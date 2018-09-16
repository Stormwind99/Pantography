package com.wumple.pantography.integration;

import com.wumple.util.map.MapUtil;

import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MapCompatibilityVanilla implements IMapCompatibility
{
    public static void register()
    {
        MapCompatibilityHandler.setFactory(MapCompatibilityVanilla::new);
    }
    
    @Override
    public boolean isItemMap(ItemStack itemstack1)
    {
        return MapUtil.isItemMap(itemstack1);
    }
    
    @Override
    public boolean isItemEmptyMap(ItemStack itemstack1)
    {
        return MapUtil.isItemEmptyMap(itemstack1);
    }
    
    @Override
    public ItemStack setupNewMap(World worldIn, double worldX, double worldZ, byte scale, boolean one, boolean two)
    {
        return ItemMap.setupNewMap(worldIn, worldX, worldZ, scale, one, two);
    }

}
