package com.wumple.pantography.capability.container;

import com.wumple.pantography.capability.PantographCap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotOutput extends SlotBase
{
    public SlotOutput(Container containerIn, EntityPlayer playerIn, IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition)
    {
        super(containerIn, playerIn, inventory, slotIndex, xDisplayPosition, yDisplayPosition);
    }
    
    @Override
    public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack)
    {
        // FMLCommonHandler.instance().firePlayerCraftingEvent(thePlayer, stack, inputInventory);

        onCrafting(stack);
        
        inventory.decrStackSize(PantographCap.TARGET_SLOT, 1);

        return super.onTake(thePlayer, stack);
    }
    
    // See hack in SlotInput:
    // HACK: force send SlotOutput when any SlotInput changes, so that update caused by SlotInput update will get sent to client
    
    /*
    private int amountCrafted;

    @Override
    public boolean isItemValid(ItemStack itemStack)
    {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int count)
    {
        if (getHasStack())
        {
            amountCrafted += Math.min(count, getStack().getCount());
        }

        return super.decrStackSize(count);
    }

    @Override
    protected void onCrafting(ItemStack itemStack, int count)
    {
        amountCrafted += count;
        super.onCrafting(itemStack, count);
    }

    @Override
    protected void onCrafting(ItemStack itemStack)
    {
        itemStack.onCrafting(player.getEntityWorld(), player, amountCrafted);
        amountCrafted = 0;

        // FMLCommonHandler.instance().firePlayerCraftingEvent(thePlayer, stack, inputInventory);
    }
    */
}