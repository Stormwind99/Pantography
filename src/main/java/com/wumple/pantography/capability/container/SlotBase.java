package com.wumple.pantography.capability.container;

import com.wumple.util.base.misc.Util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;

public class SlotBase extends Slot
{

    protected Container container;
    protected EntityPlayer player;

    public SlotBase(IInventory inventoryIn, int index, int xPosition, int yPosition)
    {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack itemStack)
    {
        return inventory.isItemValidForSlot(getSlotIndex(), itemStack);
    }

    @Override
    public void onSlotChanged()
    {
        this.inventory.markDirty();
        EntityPlayerMP playermp = Util.as(player, EntityPlayerMP.class);
        if (playermp != null)
        {
            playermp.connection.sendPacket(new SPacketSetSlot(container.windowId, this.slotNumber, this.getStack()));
        }
    }

}