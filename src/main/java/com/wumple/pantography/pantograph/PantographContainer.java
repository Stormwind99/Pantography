package com.wumple.pantography.pantograph;

import com.wumple.pantography.ModBlocks;
import com.wumple.util.map.MapTranscription;
import com.wumple.util.xcartography.XCartographyContainer;
import com.wumple.util.xmap.XMapAPI;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class PantographContainer extends XCartographyContainer
{
	private TileEntity tileEntity;

	public PantographContainer(int windowId, PlayerInventory inv)
	{
		this(windowId, inv, IWorldPosCallable.DUMMY);
	}

	public PantographContainer(int windowId, PlayerInventory inv, final IWorldPosCallable worldpos)
	{
		super(ModBlocks.PantographBlock_Container, windowId, inv, worldpos);
		worldpos.apply((x,y) -> tileEntity = x.getTileEntity(y) );
	}

	public PantographContainer(int windowId, World world, BlockPos pos, PlayerInventory inv, PlayerEntity entity)
	{
		this(windowId, inv, IWorldPosCallable.of(world, pos));
	}

	// ------------------------------------------------------------------------
	// Interaction
	
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
	
	// ------------------------------------------------------------------------
	// Util
	
	protected boolean isTranscribingInput(ItemStack stack)
	{
		return XMapAPI.getInstance().isFilledMap(stack);
	}
	
	// ------------------------------------------------------------------------
	// from XCartographyContainer

	@Override
	protected boolean isAllowedInSlot1In(ItemStack stack)
	{
		return super.isAllowedInSlot1In(stack) || isTranscribingInput(stack);
	}

	@Override
	protected void processInputs(ItemStack itemStackIn0, ItemStack itemStackIn1, ItemStack itemStackOut2)
	{
		this.worldpos.consume((worldIn, posIn) -> {
			MapData mapdata0 = XMapAPI.getInstance().getMapDataIfExists(itemStackIn0, worldIn);
			if (mapdata0 != null)
			{
				ItemStack newItemStackOut = null;
				
				if (isTranscribingInput(itemStackIn1) && !isMapDataLocked(mapdata0))
				{
					// PROBLEM these only work on server because client MapData is incomplete
					// boolean canTranscribe = MapTranscription.canTranscribeMap(itemStackIn0, itemStackIn1, worldIn);
					// MAYBE boolean doesMatch = (!ConfigManager.General.matchOnlyIntersectingMaps.get()) || canTranscribe);
					// if (canTranscribe)
					
					{
						newItemStackOut = itemStackIn0.copy();
						newItemStackOut.setCount(1);
						
						this.detectAndSendChanges();
					}
				}
				
				if (newItemStackOut != null)
				{	
					if (!ItemStack.areItemStacksEqual(newItemStackOut, itemStackOut2))
					{
						this.inventoryOut.setInventorySlotContents(2, newItemStackOut);
						this.detectAndSendChanges();
					}
				}
				else
				{
					super.processInputs(itemStackIn0, itemStackIn1, itemStackOut2);
				}
			}
		});
	}
	
	@Override
	protected ItemStack transferCraftedStack(ItemStack itemstack2)
	{
		ItemStack itemstackReturn = itemstack2;
		ItemStack itemstack0 = this.inventoryIn.getStackInSlot(0);
		ItemStack itemstack1 = this.inventoryIn.getStackInSlot(1);

		if (isTranscribingInput(this.inventoryIn.getStackInSlot(1)))
		{
			itemstackReturn = this.worldpos.apply((worldIn, posIn) -> {
				
				ItemStack itemstack3 = null;
				if (!worldIn.isRemote())
				{
					itemstack3 = MapTranscription.transcribeMapWithCopy(itemstack0, itemstack1, worldIn);
				}
				else
				{
					itemstack3 = itemstack0.copy();
				}
				
				if (itemstack3 != null)
				{
					itemstack3.setCount(1);
					return itemstack3;
				}
				else
				{
					return itemstack1;
				}
			}).orElse(itemstack0);
		}
		else
		{
			itemstackReturn = super.transferCraftedStack(itemstack2);
		}
		
		return itemstackReturn;
	}
	
	@Override
	protected ItemStack doIt(ItemStack itemstack)
	{
		boolean skipDecr = (!this.onTaking) && isTranscribingInput(this.inventoryIn.getStackInSlot(1));
				
		ItemStack itemstack1 = (!this.onTaking) ? transferCraftedStack(itemstack) : itemstack;

		// decrease inputs
		this.inventoryIn.decrStackSize(0, 1);
		if (!skipDecr)
		{
			this.inventoryIn.decrStackSize(1, 1);
		}

		return itemstack1;
	}

}
