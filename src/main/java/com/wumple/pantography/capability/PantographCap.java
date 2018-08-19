package com.wumple.pantography.capability;

import java.util.List;
import java.util.function.BiConsumer;

import com.wumple.pantography.Pantography;
import com.wumple.pantography.Reference;
import com.wumple.pantography.capability.container.ContainerPantograph;
import com.wumple.pantography.capability.container.GuiHandlerPantograph;
import com.wumple.util.adapter.IThing;
import com.wumple.util.base.misc.Util;
import com.wumple.util.capability.thing.ThingCap;
import com.wumple.util.map.MapTranscription;
import com.wumple.util.map.MapUtil;
import com.wumple.util.map.Rect;
import com.wumple.util.misc.SUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

public class PantographCap extends ThingCap<IThing> implements IPantographCap, IInteractionObject
{
    // The {@link Capability} instance
    @CapabilityInject(IPantographCap.class)
    public static final Capability<IPantographCap> CAPABILITY = null;
    public static final EnumFacing DEFAULT_FACING = null;

    // IDs of the capability
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "pantograph");

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IPantographCap.class, new PantographCapStorage(), () -> new PantographCap());
    }

    public static final int INPUT_SLOTS = 9;
    public static final int TARGET_SLOTS = 1;
    public static final int OUTPUT_SLOTS = 1;
    public static final int TOTAL_SLOTS = INPUT_SLOTS + TARGET_SLOTS + OUTPUT_SLOTS;
    public static final int OUTPUT_SLOT = TOTAL_SLOTS - 1;
    public static final int TARGET_SLOT = OUTPUT_SLOT - 1;
    
    public static final int STACK_LIMIT = 64;
    public static final int NO_SLOT = -1;
    public static final double USE_RANGE = 64.0D;

    private NonNullList<ItemStack> itemStacks = NonNullList.<ItemStack> withSize(TOTAL_SLOTS, ItemStack.EMPTY);

    protected int getFilledSlots()
    {
        int filledSlotCount = 0;
        for (int i = 0; i < INPUT_SLOTS; i++)
        {
            // MAYBE assume all itemstacks that made it into compost bin are compostable
            // reduces expense by eliminating config lookup on itemstack
            filledSlotCount += (isItemMap(itemStacks.get(i))) ? 1 : 0;
        }

        return filledSlotCount;
    }

    public boolean hasInputItems()
    {
        return getFilledSlots() > 0;
    }
    
    public boolean isActive()
    {
        return hasInputItems();
    }

    public boolean hasOutputItems()
    {
        return !SUtil.isEmpty(itemStacks.get(OUTPUT_SLOT));
    }

    public int getOutputItemCount()
    {
        ItemStack output = itemStacks.get(OUTPUT_SLOT);
        return (output != null) ? output.getCount() : 0;
    }
    
    public static boolean isItemMap(ItemStack itemStack)
    {
        return itemStack.getItem() == Items.FILLED_MAP;
    }

    public static boolean isItemEmptyMap(ItemStack itemStack)
    {
        return itemStack.getItem() == Items.MAP;
    }
    
    public boolean isEmpty()
    {
        for (ItemStack itemstack : this.itemStacks)
        {
            if (!SUtil.isEmpty(itemstack))
            {
                return false;
            }
        }

        return true;
    }

    protected World getWorld()
    {
        return owner.getWorld();
    }

    protected BlockPos getPos()
    {
        return owner.getPos();
    }

    // ----------------------------------------------------------------------
    /// IPantographCap

    @Override
    public NBTBase serializeNBT()
    {
        NBTTagCompound compound = new NBTTagCompound();

        ItemStackHelper.saveAllItems(compound, itemStacks);

        return compound;
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        NBTTagCompound compound = Util.as(nbt, NBTTagCompound.class);

        if (compound == null)
        {
            return;
        }

        ItemStackHelper.loadAllItems(compound, itemStacks);
    }

    // ----------------------------------------------------------------------
    /// Event handling via PantographyHandler

    @Override
    public void onBlockBreak(World worldIn, BlockPos pos)
    {
        setInventorySlotContents(OUTPUT_SLOT, null, false);
        InventoryHelper.dropInventoryItems(worldIn, pos, this);
        worldIn.updateComparatorOutputLevel(pos, worldIn.getBlockState(pos).getBlock());
    }

    @Override
    public void onRightBlockClicked(PlayerInteractEvent.RightClickBlock event)
    {
        World worldIn = event.getWorld();
        BlockPos pos = event.getPos();
        EntityPlayer playerIn = event.getEntityPlayer();

        if (worldIn.getBlockState(pos.up()).doesSideBlockChestOpening(worldIn, pos.up(), EnumFacing.DOWN))
        {
            event.setCancellationResult(EnumActionResult.FAIL);
            event.setCanceled(true);
        }
        else if (worldIn.isRemote)
        {
            event.setCanceled(true);
        }
        else
        {
            // custom display names are updated on client via CustomNamedTileEntity.getUpdateTag() and friends
            playerIn.openGui(Pantography.instance, GuiHandlerPantograph.myGuiID, worldIn, pos.getX(), pos.getY(), pos.getZ());
            event.setCanceled(true);
            event.setCancellationResult(EnumActionResult.SUCCESS);
        }
    }

    // ----------------------------------------------------------------------
    // TileEntity

    /**
     * invalidates a tile entity
     */
    public void invalidate()
    {
        TileEntity te = owner.as(TileEntity.class);
        if (te != null)
        {
            te.updateContainingBlockInfo();
        }
        owner.invalidate();
    }

    // ----------------------------------------------------------------------
    // IWorldNameable

    /**
     * Get the name of this object. For players this returns their username
     */
    @Override
    public String getName()
    {
        IWorldNameable i = owner.as(IWorldNameable.class);
        return (i != null) ? i.getName() : "container.pantography.pantogragh";
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName()
    {
        IWorldNameable i = owner.as(IWorldNameable.class);
        return (i != null) ? i.hasCustomName() : false;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        IWorldNameable i = owner.as(IWorldNameable.class);
        return (i != null) ? i.getDisplayName() : null;
    }

    // ----------------------------------------------------------------------
    // IInventory

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory()
    {
        return this.itemStacks.size();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack item)
    {
        if ((slot >= 0) && (slot < TARGET_SLOT))
        {
            return isItemMap(item);
        }
        
        if (slot == TARGET_SLOT)
        {
            return isItemMap(item) || isItemEmptyMap(item);
        }

        return false;
    }

    /**
     * Returns the stack in the given slot.
     */
    @Override
    public ItemStack getStackInSlot(int index)
    {
        return (index >= 0) && (index < this.itemStacks.size()) ? (ItemStack) this.itemStacks.get(index) : ItemStack.EMPTY;
    }
    
    protected void updateInternalState(int index)
    { }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack itemstack = ItemStackHelper.getAndSplit(this.itemStacks, index, count);
        updateInternalState(index);
        this.onCraftMatrixChanged(this, null);
        return itemstack;
    }
    
    /**
     * Removes a stack from the given slot and returns it.
     */
    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        ItemStack stack = ItemStackHelper.getAndRemove(this.itemStacks, index);
        updateInternalState(index);
        return stack;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    
    {
        setInventorySlotContents(index, stack, true);
    }

    public void setInventorySlotContents(int index, ItemStack stack, boolean notify)
    {
        if (stack == null)
        {
            stack = ItemStack.EMPTY;
        }

        if (index >= 0 && index < this.itemStacks.size())
        {
            this.itemStacks.set(index, stack);
            markDirty();
            if (notify)
            {
                this.onCraftMatrixChanged(this, null);
            }
        }
    }

    
    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    @Override
    public int getInventoryStackLimit()
    {
        return STACK_LIMIT;
    }

    /**
     * Don't rename this method to canInteractWith due to conflicts with Container
     */
    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        BlockPos pos = getPos();

        return player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= USE_RANGE;
    }

    @Override
    public void openInventory(EntityPlayer player)
    {
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
    }

    @Override
    public int getField(int id)
    {
        return 0;
    }

    @Override
    public void setField(int id, int value)
    {
    }

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public void clear()
    {
        this.itemStacks.clear();
    }

    @Override
    public void markDirty()
    {
        owner.markDirty();
    }

    
    // ----------------------------------------------------------------------
    // IItemHandler

    // this avoids a lot of boilerplate code, at expense of another object and indirection
    protected IItemHandlerModifiable itemHandler;

    @Override
    public IItemHandlerModifiable handler()
    {
        if (itemHandler == null)
        {
            itemHandler = new InvWrapper(this);
        }

        return itemHandler;
    }

    // ----------------------------------------------------------------------
    /// IInteractionObject

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new ContainerPantograph(playerInventory, this);
    }

    public String getGuiID()
    {
        return Integer.toString(GuiHandlerPantograph.myGuiID);
    }   
    
    // ----------------------------------------------------------------------
    /// ITooltipProvider

    @Override
    public void doTooltip(ItemStack stack, EntityPlayer entity, boolean advanced, List<String> tips)
    {
        String key = "misc.pantography.tooltip.pantograph.inactive"; // Inactive
        
        if (isActive())
        {
            key = "misc.pantography.tooltip.pantograph.active"; // Active
        }
        
        tips.add(new TextComponentTranslation(key, getOutputItemCount()).getUnformattedText());
        
        if (advanced)
        {
            //tips.add(new TextComponentTranslation("misc.pantography.tooltip.advanced.pantograph.item").getUnformattedText());
            //tips.add(new TextComponentTranslation("misc.pantography.tooltip.advanced.pantograph.target").getUnformattedText());
            //tips.add(new TextComponentTranslation("misc.pantography.tooltip.advanced.pantograph.output").getUnformattedText());
        }
    }
    
    // ----------------------------------------------------------------------
    // LALA
    
    public boolean checkTranscribe(World worldIn, ItemStack targetStack, List<ItemStack> inputs)
    {
        // first, must have a map in target slot
        if ( ! isItemMap(targetStack) )
        {
            return false;
        }
        
        int count = 0;
        for (ItemStack inputStack : inputs)
        {
            if (isItemMap(inputStack))
            {
                if ( MapTranscription.canTranscribeMap(targetStack, inputStack, worldIn)  )
                {
                    count++;
                }
                else
                {
                    return false;
                }
            }
        } 
        
        return (count > 0);
    }
    
    void doTranscribe(World worldIn, ItemStack targetStack, List<ItemStack> inputs)
    {
        for (ItemStack inputStack : inputs)
        {
            if (isItemMap(inputStack))
            {
                MapTranscription.transcribeMap(targetStack, inputStack, worldIn);
            }
        }
    }
    
    public static double log2(double num)
    {
        return (Math.log(num)/Math.log(2));
    }
    
    Rect checkCreateRect(World worldIn, ItemStack targetStack, List<ItemStack> inputs)
    {       
        // first, must have a empty map in target slot
        if ( ! isItemEmptyMap(targetStack) )
        {
            return null;
        }
        
        Rect overallRect = null;
        
        for (ItemStack inputStack: inputs)
        {
            if (isItemMap(inputStack))
            {
                MapData inputMapData = MapUtil.getMapData(inputStack, worldIn);
                Rect inputMapRect = MapUtil.getMapRect(inputMapData);
                if (overallRect == null)
                {
                    overallRect = inputMapRect.clone();
                }
                else
                {
                    overallRect = Rect.union(overallRect, inputMapRect);
                }
            }
            else if ((inputStack != null) && !inputStack.isEmpty())
            {
                return null;
            }
        }
        
        return overallRect; 
    }
        
    ItemStack doCreate(World worldIn, ItemStack targetStack, List<ItemStack> inputs)
    {        
        Rect overallRect = checkCreateRect(worldIn, targetStack, inputs);
        
        if (overallRect == null)
        {
            return null;
        }
        
        // find largest dimension
        int width = overallRect.x2 - overallRect.x1;
        int height = overallRect.z2 - overallRect.z1;
        int max = Math.max(width, height);

        // semi-reverse of MapData.calculateMapCenter()
        int max128 = max / 128;
        double logMax128 = log2(max128);
        int rawScale = (int)Math.ceil(logMax128);
        int scale = MathHelper.clamp(rawScale, 0, 4);
        
        // find center
        int worldX = overallRect.x1 + width/2;
        int worldZ = overallRect.z1 + height/2;
        
        // copy input maps onto new map
        ItemStack newStack = ItemMap.setupNewMap(worldIn, (double)worldX, (double)worldZ, (byte)scale, false, false);
        doTranscribe(worldIn, newStack, inputs);
        
        return newStack;
    }
    
    ItemStack copyMap(ItemStack srcStack)
    {
        if ( ! isItemMap(srcStack) )
        {
            return null;
        }
        
        ItemStack destStack = new ItemStack(Items.FILLED_MAP, 1, srcStack.getMetadata());

        if (srcStack.hasDisplayName())
        {
            destStack.setStackDisplayName(srcStack.getDisplayName());
        }

        if (srcStack.hasTagCompound())
        {
            destStack.setTagCompound(srcStack.getTagCompound());
        }
        
        return destStack;
    }
    
    // ---
    
    public boolean checkTranscribe(World worldIn)
    {
        ItemStack targetStack = itemStacks.get(TARGET_SLOT);
        List<ItemStack> inputs = itemStacks.subList(0, INPUT_SLOTS);

        return checkTranscribe(worldIn, targetStack, inputs);
    }
    
    boolean checkCreate(World worldIn)
    {
        ItemStack targetStack = itemStacks.get(TARGET_SLOT);
        List<ItemStack> inputs = itemStacks.subList(0, INPUT_SLOTS);
        
        return checkCreateRect(worldIn, targetStack, inputs) != null;
    }
    
    ItemStack doTranscribe(World worldIn)
    {
        ItemStack targetStack = itemStacks.get(TARGET_SLOT);
        List<ItemStack> inputs = itemStacks.subList(0, INPUT_SLOTS);
        
        ItemStack newStack = copyMap(targetStack);
        
        doTranscribe(worldIn, newStack, inputs);
        
        return newStack;
    }
    
    ItemStack doCreate(World worldIn)
    {
        ItemStack targetStack = itemStacks.get(TARGET_SLOT);
        List<ItemStack> inputs = itemStacks.subList(0, INPUT_SLOTS);

        return doCreate(worldIn, targetStack, inputs);
    }
    
    
    // do it
    
    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn, BiConsumer<Integer, ItemStack> updater)
    {
        World world = getWorld();
        if (!world.isRemote)
        {
            ItemStack newMap = null;
            if (checkTranscribe(world))
            {
                 newMap = doTranscribe(world);
            }
            else if (checkCreate(world))
            {
                newMap = doCreate(world);
            }
            
            if (newMap == null)
            {
                newMap = ItemStack.EMPTY;
            }
            
            setInventorySlotContents(OUTPUT_SLOT, newMap, false);
            
            // Note: SlotInput will force a SlotOutput.onSlotChanged() to send output slot update to client

            // Another update method:
            if (updater != null)
            {
                updater.accept(OUTPUT_SLOT, newMap);
            }
        }
    }
}
