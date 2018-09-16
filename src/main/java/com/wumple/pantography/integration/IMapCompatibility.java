package com.wumple.pantography.integration;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IMapCompatibility
{

    boolean isItemMap(ItemStack itemstack1);

    boolean isItemEmptyMap(ItemStack itemstack1);

    ItemStack setupNewMap(World worldIn, double worldX, double worldZ, byte scale, boolean one, boolean two);

}