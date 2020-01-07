package com.wumple.util.xcartography;

import com.wumple.util.map.MapUtil;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.storage.MapData;

public class XCartographyContainer extends Container
{	
	public XCartographyContainer(int idIn, PlayerInventory playerInventoryIn)
	{
		this(idIn, playerInventoryIn, IWorldPosCallable.DUMMY);
	}
	
	// ------------------------------------------------------------------------
	// Added to CartographyContainer to make more extendible via inheritance
	
	protected boolean isAllowedInSlot0In(Item item)
	{
		return item == Items.FILLED_MAP;
	}
	
	protected boolean isAllowedInSlot1In(Item item)
	{
		return isScalingInput(item) || isCopyInput(item) || isLockingInput(item);
	}
	
	protected boolean isLockingInput(Item item)
	{
		return item == Items.GLASS_PANE;
	}
	
	protected boolean isScalingInput(Item item)
	{
		return item == Items.PAPER;
	}
	
	protected boolean isCopyInput(Item item)
	{
		return item == Items.MAP;
	}
	
	protected boolean isMapDataLocked(MapData mapdata)
	{
		return mapdata.locked;
	}
	
	protected boolean isMapDataScaleable(MapData mapdata)
	{
		return (mapdata.scale < 4);
	}
	
	// ------------------------------------------------------------------------
	// From CartographyContainer
	
	protected final IWorldPosCallable worldpos;
	protected boolean onTaking;
	
	public final IInventory inventoryIn=new Inventory(2){
	/**
	 * For tile entities, ensures the chunk containing the tile entity is saved to
	 * disk later - the game won't think it hasn't changed and skip it.
	 */
	public void markDirty(){XCartographyContainer.this.onCraftMatrixChanged(this);super.markDirty();}};
	
	protected final CraftResultInventory inventoryOut=new CraftResultInventory(){
	/**
	 * For tile entities, ensures the chunk containing the tile entity is saved to
	 * disk later - the game won't think it hasn't changed and skip it.
	 */
	public void markDirty(){XCartographyContainer.this.onCraftMatrixChanged(this);super.markDirty();}};
	
	public XCartographyContainer(int idIn, PlayerInventory playerInventoryIn, final IWorldPosCallable worldPosIn)
	{
		super(ContainerType.CARTOGRAPHY, idIn);
		this.worldpos = worldPosIn;
		init(idIn, playerInventoryIn, worldPosIn);
	}
	
	protected void init(int idIn, PlayerInventory playerInventoryIn, final IWorldPosCallable worldPosIn)
	{
		addSlot0In(idIn, playerInventoryIn, worldPosIn);
		addSlot1In(idIn, playerInventoryIn, worldPosIn);
		addSlot2Out(idIn, playerInventoryIn, worldPosIn);
		addSlotsInventory(idIn, playerInventoryIn, worldPosIn);
	}
	
	protected void addSlot0In(int idIn, PlayerInventory playerInventoryIn, final IWorldPosCallable worldPosIn)
	{
		this.addSlot(new Slot(this.inventoryIn, 0, 15, 15)
		{
			/**
			 * Check if the stack is allowed to be placed in this slot, used for armor slots
			 * as well as furnace fuel.
			 */
			public boolean isItemValid(ItemStack stack)
			{
				return isAllowedInSlot0In(stack.getItem());
			}
		});
	}
	
	protected void addSlot1In(int idIn, PlayerInventory playerInventoryIn, final IWorldPosCallable worldPosIn)
	{
		this.addSlot(new Slot(this.inventoryIn, 1, 15, 52)
		{
			/**
			 * Check if the stack is allowed to be placed in this slot, used for armor slots
			 * as well as furnace fuel.
			 */
			public boolean isItemValid(ItemStack stack)
			{
				return isAllowedInSlot1In(stack.getItem());
			}
		});
	}
	
	protected void addSlot2Out(int idIn, PlayerInventory playerInventoryIn, final IWorldPosCallable worldPosIn)
	{
		this.addSlot(new Slot(this.inventoryOut, 2, 145, 39)
		{
			/**
			 * Check if the stack is allowed to be placed in this slot, used for armor slots
			 * as well as furnace fuel.
			 */
			public boolean isItemValid(ItemStack stack)
			{
				return false;
			}

			/**
			 * Decrease the size of the stack in slot (first int arg) by the amount of the
			 * second int arg. Returns the new stack.
			 */
			public ItemStack decrStackSize(int amount)
			{
				ItemStack itemstack = super.decrStackSize(amount);
				ItemStack itemstack1 = worldPosIn.apply((worldIn, posIn) -> {
					if (!XCartographyContainer.this.onTaking && isLockingInput(XCartographyContainer.this.inventoryIn
							.getStackInSlot(1).getItem()))
					{
						// duplicate map ItemStack and copy mapdata
						ItemStack itemstack2 = FilledMapItem.func_219992_b(worldIn,
								XCartographyContainer.this.inventoryIn.getStackInSlot(0));
						
						if (itemstack2 != null)
						{
							itemstack2.setCount(1);
							return itemstack2;
						}
					}

					return itemstack;
				}).orElse(itemstack);
				
				// decrease inputs
				XCartographyContainer.this.inventoryIn.decrStackSize(0, 1);
				XCartographyContainer.this.inventoryIn.decrStackSize(1, 1);
				
				return itemstack1;
			}

			/**
			 * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not
			 * ore and wood. Typically increases an internal count then calls
			 * onCrafting(item).
			 */
			protected void onCrafting(ItemStack stack, int amount)
			{
				this.decrStackSize(amount);
				super.onCrafting(stack, amount);
			}

			public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack)
			{
				stack.getItem().onCreated(stack, thePlayer.world, thePlayer);
				worldPosIn.consume((worldIn, posIn) -> {
					worldIn.playSound((PlayerEntity) null, posIn,
							SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
				});
				return super.onTake(thePlayer, stack);
			}
		});
	}
	
	protected void addSlotsInventory(int idIn, PlayerInventory playerInventoryIn, final IWorldPosCallable worldPosIn)
	{
		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlot(new Slot(playerInventoryIn, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k)
		{
			this.addSlot(new Slot(playerInventoryIn, k, 8 + k * 18, 142));
		}
	}

	/**
	 * Determines whether supplied player can use this container
	 */
	public boolean canInteractWith(PlayerEntity playerIn)
	{
		return isWithinUsableDistance(this.worldpos, playerIn, Blocks.CARTOGRAPHY_TABLE);
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	public void onCraftMatrixChanged(IInventory inventoryIn)
	{
		ItemStack itemstackIn0 = this.inventoryIn.getStackInSlot(0);
		ItemStack itemStackIn1 = this.inventoryIn.getStackInSlot(1);
		ItemStack itemStackOut = this.inventoryOut.getStackInSlot(2);
		if (itemStackOut.isEmpty() || !itemstackIn0.isEmpty() && !itemStackIn1.isEmpty())
		{
			if (!itemstackIn0.isEmpty() && !itemStackIn1.isEmpty())
			{
				this.processInputs(itemstackIn0, itemStackIn1, itemStackOut);
			}
		}
		else
		{
			this.inventoryOut.removeStackFromSlot(2);
		}

	}

	protected void processInputs(ItemStack itemStackIn0, ItemStack itemStackIn1, ItemStack itemStackOut)
	{
		this.worldpos.consume((worldIn, posIn) -> {
			Item item = itemStackIn1.getItem();
			MapData mapdata = FilledMapItem.getData(itemStackIn0, worldIn);
			if (mapdata != null)
			{
				ItemStack itemstack;
				if (isScalingInput(item) && !isMapDataLocked(mapdata) && isMapDataScaleable(mapdata))
				{
					itemstack = itemStackIn0.copy();
					itemstack.setCount(1);
					MapUtil.mapScaleDirection(itemstack, 1);
					this.detectAndSendChanges();
				}
				else if (isLockingInput(itemStackIn1.getItem()) && !isMapDataLocked(mapdata))
				{
					itemstack = itemStackIn0.copy();
					itemstack.setCount(1);
					this.detectAndSendChanges();
				}
				else
				{
					if (!isCopyInput(item))
					{
						this.inventoryOut.removeStackFromSlot(2);
						this.detectAndSendChanges();
						return;
					}

					itemstack = itemStackIn0.copy();
					itemstack.setCount(2);
					this.detectAndSendChanges();
				}

				if (!ItemStack.areItemStacksEqual(itemstack, itemStackOut))
				{
					this.inventoryOut.setInventorySlotContents(2, itemstack);
					this.detectAndSendChanges();
				}

			}
		});
	}

	/**
	 * Called to determine if the current slot is valid for the stack merging
	 * (double-click) code. The stack passed in is null for the initial slot that
	 * was double-clicked.
	 */
	public boolean canMergeSlot(ItemStack stack, Slot slotIn)
	{
		return false;
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this
	 * moves the stack between the player inventory and the other inventory(s).
	 */
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			ItemStack itemstack2 = itemstack1;
			Item item = itemstack1.getItem();
			itemstack = itemstack1.copy();
			// output slot (#2)
			if (index == 2)
			{
				if (isLockingInput(this.inventoryIn.getStackInSlot(1).getItem()))
				{
					itemstack2 = this.worldpos.apply((worldIn, posIn) -> {
						ItemStack itemstack3 = FilledMapItem.func_219992_b(worldIn,
								this.inventoryIn.getStackInSlot(0));
						if (itemstack3 != null)
						{
							itemstack3.setCount(1);
							return itemstack3;
						}
						else
						{
							return itemstack1;
						}
					}).orElse(itemstack1);
				}

				item.onCreated(itemstack2, playerIn.world, playerIn);
				if (!this.mergeItemStack(itemstack2, 3, 39, true))
				{
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(itemstack2, itemstack);
			}
			// output slot or inventory (not #0 or #1)
			else if (index != 1 && index != 0)
			{
				if (isCopyInput(item))
				{
					if (!this.mergeItemStack(itemstack1, 0, 1, false))
					{
						return ItemStack.EMPTY;
					}
				}
				else if (!isAllowedInSlot1In(item))
				{
					// inventory slot
					if (index >= 3 && index < 30)
					{
						if (!this.mergeItemStack(itemstack1, 30, 39, false))
						{
							return ItemStack.EMPTY;
						}
					}
					// hotbar slot
					else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false))
					{
						return ItemStack.EMPTY;
					}
				}
				else if (!this.mergeItemStack(itemstack1, 1, 2, false))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 3, 39, false))
			{
				return ItemStack.EMPTY;
			}

			if (itemstack2.isEmpty())
			{
				slot.putStack(ItemStack.EMPTY);
			}

			slot.onSlotChanged();
			if (itemstack2.getCount() == itemstack.getCount())
			{
				return ItemStack.EMPTY;
			}

			this.onTaking = true;
			slot.onTake(playerIn, itemstack2);
			this.onTaking = false;
			this.detectAndSendChanges();
		}

		return itemstack;
	}

	/**
    * Called when the container is closed.
    */
   public void onContainerClosed(PlayerEntity playerIn) {
      super.onContainerClosed(playerIn);
      this.inventoryOut.removeStackFromSlot(2);
      this.worldpos.consume((worldIn, posIn) -> {
         this.clearContainer(playerIn, playerIn.world, this.inventoryIn);
      });
   }
}