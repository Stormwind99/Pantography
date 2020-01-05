package com.wumple.pantography.integration;

import com.wumple.util.map.MapUtil;

import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MapCompatibilityVanilla implements IMapCompatibility
{
    public static void register()
    {
        MapCompatibilityHandler.setFactory(MapCompatibilityVanilla::new);
    }
    
    @Override
    public boolean isItemFilledMap(ItemStack itemstack1)
    {
        return MapUtil.isFilledMapItem(itemstack1);
    }
    
    @Override
    public boolean isItemEmptyMap(ItemStack itemstack1)
    {
        return MapUtil.isItemEmptyMap(itemstack1);
    }
    
    @Override
    public ItemStack setupNewMap(World worldIn, int worldX, int worldZ, byte scale, boolean one, boolean two)
    {
        return FilledMapItem.setupNewMap(worldIn, worldX, worldZ, scale, one, two);
    }

}
