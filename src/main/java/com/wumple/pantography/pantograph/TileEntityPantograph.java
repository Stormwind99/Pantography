package com.wumple.pantography.pantograph;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityPantograph extends TileEntity // PORT extends OrientableTileEntity
{
    /// CustomNamedTileEntity

	// PORT
    public TileEntityPantograph(TileEntityType<?> tileEntityTypeIn)
	{
		super(tileEntityTypeIn);
		// TODO Auto-generated constructor stub
	}

	// PORT @Override
    public String getRealName()
    {
        return "container.pantography.pantograph";
    }
}
