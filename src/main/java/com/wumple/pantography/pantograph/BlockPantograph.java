package com.wumple.pantography.pantograph;

import com.wumple.util.misc.RegistrationHelpers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.AxisAlignedBB;

public class BlockPantograph extends Block // PORT extends HorizontalOrientableBlock
{
    // ----------------------------------------------------------------------
    // BlockPantograph

    public static final String ID = "pantography:pantograph";
    public static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D);

    public BlockPantograph(Block.Properties properties)
    {
    	super(properties);
    	/*
        super(materialIn);
        setTickRandomly(true);
        setHardness(1.5f);
        setResistance(5f);
        setCreativeTab(CreativeTabs.MISC);
        */

        RegistrationHelpers.nameHelper(this, ID);
    }
    
    public Block getThisBlock()
    { return this; }
    
    // PORT
    public void setMyDefaultState(BlockState state)
    { setDefaultState(state); }

    // ----------------------------------------------------------------------
    // Block

    /*
    // PORT
 
    @Override
    public boolean isOpaqueCube(BlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(BlockState state)
    {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos)
    {
        return BASE_AABB;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return BASE_AABB;
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, EnumFacing face)
    {
        if (face == EnumFacing.UP)
        {
            return BlockFaceShape.BOWL;
        }
        else
        {
            return face == EnumFacing.DOWN ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
        }
    }
    */

    // ----------------------------------------------------------------------
    // BlockContainer

    /*
    // PORT
    @Override
    public EnumBlockRenderType getRenderType(BlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
    */

    // ----------------------------------------------------------------------
    // ITileEntityProvider

    /*
    // PORT
     
    // from
    // http://www.minecraftforge.net/forum/topic/62067-solved-itickable-and-tes-not-ticking/
    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityPantograph();
    }
    */
}
