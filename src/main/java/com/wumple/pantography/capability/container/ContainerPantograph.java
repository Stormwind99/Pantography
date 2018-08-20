package com.wumple.pantography.capability.container;

import java.util.ArrayList;
import java.util.List;

import com.wumple.pantography.capability.IPantographCap;
import com.wumple.util.base.misc.Util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;

public class ContainerPantograph extends Container
{
    private static final int InventoryX = 8;
    private static final int InventoryY = 84;
    private static final int HotbarY = 142;

    private IPantographCap owner;

    @SuppressWarnings("unused")
    private Slot targetSlot;
    private Slot outputSlot;
    private List<Slot> ourSlots;
    private List<Slot> playerSlots;
    private List<Slot> hotbarSlots;

    protected InventoryPlayer inventory;
    
    public ContainerPantograph(InventoryPlayer inventoryIn, IPantographCap cap)
    {
        owner = cap;
        inventory = inventoryIn;
        
        ourSlots = new ArrayList<Slot>();
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 3; x++)
                ourSlots.add(addSlotToContainer(new SlotInput(this, inventory.player, cap, x + y * 3, 18 + x * 18, 17 + y * 18)));
        }

        targetSlot = addSlotToContainer(new SlotInput(this, inventory.player, cap, 9, 123-54+12-1, 34+1) );
        
        outputSlot = addSlotToContainer(new SlotOutput(this, inventory.player, cap, 10, 123+14+1, 34+1));

        playerSlots = new ArrayList<Slot>();
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
                playerSlots.add(addSlotToContainer(
                        new Slot(inventory, j + i * 9 + 9, InventoryX + j * 18, InventoryY + i * 18)));
        }

        hotbarSlots = new ArrayList<Slot>();
        for (int i = 0; i < 9; i++)
            hotbarSlots.add(addSlotToContainer(new Slot(inventory, i, InventoryX + i * 18, HotbarY)));
    }

    @Override
    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.owner);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return owner.isUsableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot) inventorySlots.get(slotIndex);

        int ourStart = ourSlots.get(0).slotNumber;
        int ourEnd = ourSlots.get(ourSlots.size() - 1).slotNumber + 1;

        // Assume inventory and hotbar slot IDs are contiguous
        int inventoryStart = playerSlots.get(0).slotNumber;
        int hotbarStart = hotbarSlots.get(0).slotNumber;
        int hotbarEnd = hotbarSlots.get(hotbarSlots.size() - 1).slotNumber + 1;

        if (slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            itemStack = slotStack.copy();

            // Try merge output into inventory and signal change
            if (slotIndex == outputSlot.slotNumber)
            {
                if (!mergeItemStack(slotStack, inventoryStart, hotbarEnd, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(slotStack, itemStack);
                //owner.updateBlockState();
            }

            // Try merge stacks within inventory and hotbar spaces
            else if (slotIndex >= inventoryStart && slotIndex < hotbarEnd)
            {
                if (/*!CompostBinCap.isItemDecomposable(slotStack)
                        ||*/ !mergeItemStack(slotStack, ourStart, ourEnd, false))
                {
                    if (slotIndex >= inventoryStart && slotIndex < hotbarStart)
                    {
                        if (!mergeItemStack(slotStack, hotbarStart, hotbarEnd, false))
                        {
                            // return null;
                            return ItemStack.EMPTY;
                        }
                    }
                    else if (slotIndex >= hotbarStart && slotIndex < hotbarEnd
                            && !this.mergeItemStack(slotStack, inventoryStart, hotbarStart, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
            }

            // Try merge stack into inventory
            else if (!mergeItemStack(slotStack, inventoryStart, hotbarEnd, false))
            {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (slotStack.getCount() == itemStack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return itemStack;
    }
    
    
    
    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        owner.onCraftMatrixChanged(inventoryIn, (slot, itemstack) -> {
            EntityPlayerMP player = Util.as(inventory.player, EntityPlayerMP.class);
            if (player != null)
            {
                player.connection.sendPacket(new SPacketSetSlot(windowId, slot, itemstack));
            }
        } );
    }
}