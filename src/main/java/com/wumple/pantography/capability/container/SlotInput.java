package com.wumple.pantography.capability.container;

import com.wumple.pantography.capability.PantographCap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotInput extends SlotBase
{
    public SlotInput(Container containerIn, EntityPlayer playerIn, IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition)
    {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
        container = containerIn;
        player = playerIn;
    }
    
    /**
     * Helper method to put a stack in the slot.
     */
    public void putStack(ItemStack stack)
    {
        super.putStack(stack);
        // HACK: force send SlotOutput when any SlotInput changes, so that update caused by SlotInput update will get sent to client
        container.inventorySlots.get(PantographCap.OUTPUT_SLOT).onSlotChanged();
    }
}