package com.wumple.pantography.pantograph;

import com.wumple.util.nameable.NameableTileEntity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityPantograph extends NameableTileEntity
{
    /**
     * This controls whether the tile entity gets replaced whenever the block state is changed. Normally only want this when block actually is replaced.
     */
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return (oldState.getBlock() != newState.getBlock());
    }

    /// CustomNamedTileEntity

    @Override
    public String getRealName()
    {
        return "container.pantography.pantograph";
    }
}
