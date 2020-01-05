package com.wumple.pantography.integration.megamap;

import com.wumple.megamap.api.MegaMapAPI;
import com.wumple.pantography.integration.IMapCompatibility;
import com.wumple.pantography.integration.MapCompatibilityHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MapCompatibilityMegaMap implements IMapCompatibility
{
    public static void register()
    {
        MapCompatibilityHandler.setFactory(MapCompatibilityMegaMap::new);
    }
    
    @Override
    public boolean isItemFilledMap(ItemStack itemstack1)
    {
        return MegaMapAPI.getInstance().isFilledMap(itemstack1);
    }
    
    @Override
    public boolean isItemEmptyMap(ItemStack itemstack1)
    {
        return MegaMapAPI.getInstance().isEmptyMap(itemstack1);
    }
    
    @Override
    public ItemStack setupNewMap(World worldIn, int worldX, int worldZ, byte scale, boolean one, boolean two)
    {
        return MegaMapAPI.getInstance().setupNewMap(worldIn, worldX, worldZ, scale, one, two);
    }

}
