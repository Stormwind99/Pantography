package com.wumple.pantography.pantograph;

import javax.annotation.Nullable;

import com.wumple.pantography.ModBlocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class PantographTileEntity extends TileEntity implements INamedContainerProvider // PORT extends OrientableTileEntity
{
	public PantographTileEntity()
	{
		super(ModBlocks.PantographBlock_Tile);
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new StringTextComponent(getType().getRegistryName().getPath());
	}

	@Nullable
	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity)
	{
		return new PantographContainer(i, world, pos, playerInventory, playerEntity);
	}

	/*
	// PORT
	  
	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity)
    {
        IPantographCap cap = getCap(world, x, y, z).orElse(null); // PORT
        // PORT return (cap != null) ? new ContainerPantograph(player.inventory, cap) : null;
        return null;
    }
	
	/// CustomNamedTileEntity
	
	// PORT 
	@Override
	public String getRealName()
	{
	    return "container.pantography.pantograph";
	}
	*/
	
	
}
