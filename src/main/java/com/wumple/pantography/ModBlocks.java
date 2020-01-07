package com.wumple.pantography;

import com.wumple.pantography.pantograph.PantographBlock;
import com.wumple.pantography.pantograph.PantographContainer;
import com.wumple.pantography.pantograph.PantographTileEntity;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks
{
    @ObjectHolder("pantography:pantograph")
    public static PantographBlock PantographBlock;
    
    @ObjectHolder("pantography:pantograph")
    public static TileEntityType<PantographTileEntity> PantographBlock_Tile;
    
    @ObjectHolder("pantography:pantograph")
    public static ContainerType<PantographContainer> PantographBlock_Container;
}
