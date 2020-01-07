package com.wumple.pantography.pantograph;

import com.wumple.pantography.ModBlocks;
import com.wumple.util.xcartography.XCartographyContainer;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PantographContainer extends XCartographyContainer
{
	private TileEntity tileEntity;

	public PantographContainer(int windowId, PlayerInventory inv)
	{
		this(windowId, inv, IWorldPosCallable.DUMMY);
	}

	public PantographContainer(int windowId, PlayerInventory inv, final IWorldPosCallable worldpos)
	{
		super(windowId, inv, worldpos);
		worldpos.apply((x,y) -> tileEntity = x.getTileEntity(y) );
	}

	public PantographContainer(int windowId, World world, BlockPos pos, PlayerInventory inv, PlayerEntity entity)
	{
		this(windowId, inv, IWorldPosCallable.of(world, pos));
	}
	
	public Block getInteractionTargetBlock()
	{
		return ModBlocks.PantographBlock;
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn)
	{
		return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()), playerIn,
				getInteractionTargetBlock());
	}
}
