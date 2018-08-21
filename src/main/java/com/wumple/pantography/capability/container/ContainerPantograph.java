package com.wumple.pantography.capability.container;

import com.wumple.util.capability.targetcrafting.IContainerCraftingOwner;
import com.wumple.util.capability.targetcrafting.container.ContainerCrafting;

import net.minecraft.entity.player.InventoryPlayer;

public class ContainerPantograph extends ContainerCrafting
{
    public ContainerPantograph(InventoryPlayer inventoryIn, IContainerCraftingOwner cap)
    {
        super(inventoryIn, cap);
    }
}
