package com.wumple.pantography.integration;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IMapCompatibility
{

	boolean isItemFilledMap(ItemStack itemstack1);

	boolean isItemEmptyMap(ItemStack itemstack1);

	ItemStack setupNewMap(World worldIn, int worldX, int worldZ, byte scale, boolean one, boolean two);

}