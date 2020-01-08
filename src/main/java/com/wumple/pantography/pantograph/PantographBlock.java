package com.wumple.pantography.pantograph;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class PantographBlock extends Block // PORT extends HorizontalOrientableBlock
{
	// ----------------------------------------------------------------------
	// BlockPantograph

	public static final String ID = "pantography:pantograph";

	public PantographBlock()
	{
		super(Block.Properties.create(Material.WOOD).hardnessAndResistance(1.5f).tickRandomly());

		setRegistryName(ID);
	}

	// ------------------------------------------------------------------------
	// for horizontal orientable block

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity,
			ItemStack stack)
	{
		world.setBlockState(pos,
				state.with(BlockStateProperties.HORIZONTAL_FACING, getFacingFromEntity(pos, entity).getOpposite()), 2);
	}

	public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity)
	{
		return (entity != null) ? entity.getHorizontalFacing() : Direction.NORTH;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}

	// ------------------------------------------------------------------------
	// for tile entity - may not be needed in 1.14
	// was ITileEntityProvider

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new PantographTileEntity();
	}

	// From McJty tutorial

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult result)
	{
		if (!world.isRemote)
		{
			TileEntity tileEntity = world.getTileEntity(pos);
			if (tileEntity instanceof INamedContainerProvider)
			{
				NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity,
						tileEntity.getPos());
			}
			else
			{
				throw new IllegalStateException("Our named container provider is missing!");
			}
			return true;
		}
		return super.onBlockActivated(state, world, pos, player, hand, result);
	}
}
