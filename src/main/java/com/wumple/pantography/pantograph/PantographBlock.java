package com.wumple.pantography.pantograph;

import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.EnumUtils;

import com.wumple.util.misc.VoxelShapeUtils;

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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class PantographBlock extends Block // PORT extends HorizontalOrientableBlock
{
	protected VoxelShape shape;

	// ----------------------------------------------------------------------
	// BlockPantograph

	public static final String ID = "pantography:pantograph";

	public PantographBlock()
	{
		super(Block.Properties.create(Material.WOOD).hardnessAndResistance(1.5f).tickRandomly());

		buildVoxelShapes();

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

	// for partial block

	protected VoxelShape buildShape()
	{
		Optional<VoxelShape> a = Stream.of(Block.makeCuboidShape(1, 2, 8, 2, 3, 9), Block.makeCuboidShape(1, 2, 2, 2, 3, 3),
				Block.makeCuboidShape(8, 2, 9, 9, 3, 10), Block.makeCuboidShape(8, 2, 1, 9, 3, 2),
				Block.makeCuboidShape(3, 2, 8, 10, 3, 9), Block.makeCuboidShape(8, 2, 3, 9, 3, 8),
				Block.makeCuboidShape(2, 2, 1, 3, 3, 15), Block.makeCuboidShape(3, 2, 2, 15, 3, 3))
				.reduce((v1, v2) -> {
					return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
				});
		Optional<VoxelShape> b = Stream.of(Block.makeCuboidShape(8, 1, 8, 9, 2, 9), Block.makeCuboidShape(13, 1, 2, 14, 2, 3),
				Block.makeCuboidShape(2, 1, 2, 3, 2, 3), Block.makeCuboidShape(2, 1, 13, 3, 2, 14))
				.reduce((v1, v2) -> {
					return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
				});
		
		// exported from Blockbench via Mod Utils plugins
		Optional<VoxelShape> object = Stream.of(
				Block.makeCuboidShape(0, 0, 0, 16, 1, 16),
				a.get(),
				b.get())
				.reduce((v1, v2) -> {
					return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
				});

		return object.get();		
	}
	
	public static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
	
	private static final VoxelShape[] bounds = new VoxelShape[HORIZONTAL_DIRECTIONS.length];
	
	protected void buildVoxelShapes()
	{
        VoxelShape pump = VoxelShapeUtils.rotate(buildShape(), Rotation.CLOCKWISE_180);
        for (Direction side : HORIZONTAL_DIRECTIONS) {
            bounds[side.ordinal() - 2] = VoxelShapeUtils.rotateHorizontal(pump, side);
        }
	}
	
    @Nonnull
    @Override
    @Deprecated
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
    	int index = state.get(BlockStateProperties.HORIZONTAL_FACING).ordinal();
        return bounds[index - 2];
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
