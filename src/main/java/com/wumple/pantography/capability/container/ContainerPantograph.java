package com.wumple.pantography.capability.container;

import com.wumple.util.capability.targetcrafting.IContainerCraftingOwner;
// PORT import com.wumple.util.capability.targetcrafting.container.ContainerCrafting;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

public class ContainerPantograph extends Container // PORT extends ContainerCrafting
{
    public ContainerPantograph(PlayerInventory inventoryIn, IContainerCraftingOwner cap)
    {
        // PORT super(inventoryIn, cap);
    	super(null, 0); // TEMP
    }

    // PORT
	@Override
	public boolean canInteractWith(PlayerEntity playerIn)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
