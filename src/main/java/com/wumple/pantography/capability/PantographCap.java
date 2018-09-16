package com.wumple.pantography.capability;

import java.util.List;

import javax.annotation.Nullable;

import com.wumple.pantography.ObjectHolder;
import com.wumple.pantography.Pantography;
import com.wumple.pantography.Reference;
import com.wumple.pantography.capability.container.ContainerPantograph;
import com.wumple.pantography.capability.container.GuiHandlerPantograph;
import com.wumple.pantography.integration.MapCompatibilityHandler;
import com.wumple.util.capability.targetcrafting.TargetCraftingCap;
import com.wumple.util.map.MapCreation;
import com.wumple.util.map.MapTranscription;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class PantographCap extends TargetCraftingCap implements IPantographCap, IInteractionObject
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

    public boolean isValidInputStack(ItemStack itemStack)
    {
        return MapCompatibilityHandler.getInstance().isItemMap(itemStack);
    }

    public boolean isValidBlankStack(ItemStack itemStack)
    {
        return MapCompatibilityHandler.getInstance().isItemEmptyMap(itemStack);
    }
    
    public boolean isValidTargetStack(ItemStack itemStack)
    {
        return (isValidInputStack(itemStack) || isValidBlankStack(itemStack));
    }

    public void openGui(EntityPlayer playerIn, World worldIn, BlockPos pos)
    {
        playerIn.openGui(Pantography.instance, GuiHandlerPantograph.myGuiID, worldIn, pos.getX(), pos.getY(), pos.getZ());
    }
    
    public String getLocalizationID()
    {
        return "container.pantography.pantogragh";
    }

    public SoundEvent getCraftingSound()
    {
        return ObjectHolder.pantograph_use;
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
    // PantographCap
    
    public boolean checkTranscribe(World worldIn)
    {
        ItemStack targetStack = itemStacks.get(TARGET_SLOT);
        List<ItemStack> inputs = itemStacks.subList(0, INPUT_SLOTS);

        return MapTranscription.checkTranscribe(worldIn, targetStack, inputs);
    }
    
    boolean checkCreate(World worldIn)
    {
        ItemStack targetStack = itemStacks.get(TARGET_SLOT);
        List<ItemStack> inputs = itemStacks.subList(0, INPUT_SLOTS);
        
        if (!isValidBlankStack(targetStack))
        { return false; }
        
        return MapCreation.checkCreateRect(worldIn, inputs) != null;
    }
     
    ItemStack doTranscribe(World worldIn)
    {
        ItemStack targetStack = itemStacks.get(TARGET_SLOT);
        List<ItemStack> inputs = itemStacks.subList(0, INPUT_SLOTS);
        
        ItemStack newStack = MapCreation.copyMap(targetStack);
        
        MapTranscription.doTranscribe(worldIn, newStack, inputs);
        
        return newStack;
    }
    
    ItemStack doCreate(World worldIn)
    {
        List<ItemStack> inputs = itemStacks.subList(0, INPUT_SLOTS);

        MapCreation.MapProps mapProps = MapCreation.getCreateMap(worldIn, inputs);
        
        if (mapProps == null)
        {
            return null;
        }

        int worldX = mapProps.worldX;
        int worldZ = mapProps.worldZ;
        int scale = mapProps.scale;
        
        // was MapCreation.doCreate(worldIn, inputs);
        
        ItemStack newStack = MapCompatibilityHandler.getInstance().setupNewMap(worldIn, (double) worldX, (double) worldZ, (byte) scale, false, false);
        MapTranscription.doTranscribe(worldIn, newStack, inputs);

        return newStack;
    }
    
    @Nullable
    protected ItemStack craftIt(World world)
    {
        if (checkTranscribe(world))
        {
            return doTranscribe(world);
        }
        else if (checkCreate(world))
        {
            return doCreate(world);
        }
        
        return null;
    }
}
