package com.wumple.pantography;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multisets;

import net.minecraft.world.storage.MapData;
import net.minecraft.world.World;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemMap;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.init.Items;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.registry.GameRegistry;
// debug
import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

// recipe to copy map data from one filled map into another filled map
public class filledMapTranscribeRecipe implements IRecipe {

	public static final Logger logger = LogManager.getLogger("Pantography");
	private static final int pixLength = 128;
	// private EntityPlayer player;
	// private World world;

	public static void init() {
		final filledMapTranscribeRecipe recipe = new filledMapTranscribeRecipe();
		GameRegistry.addRecipe(recipe);
		FMLCommonHandler.instance().bus().register(recipe);
	}

	public filledMapTranscribeRecipe() {
	}

	// debug logging
	private void log(final String msg) {
		logger.debug(msg);

		/*
		 * if (player != null) { player.addChatMessage(new
		 * ChatComponentText(msg)); }
		 */
	}

	// return the items involved in this recipe, or null if not present
	private CraftingSearchResults getStuff(final InventoryCrafting inv) {
		ItemStack destItemStack = null;
		ItemStack srcItemStack = null;

		for (int j = 0; j < inv.getSizeInventory(); ++j) {
			final ItemStack itemstack1 = inv.getStackInSlot(j);

			if (itemstack1 != null) {
				if (itemstack1.getItem() == Items.filled_map) {
					if (destItemStack == null) {
						destItemStack = itemstack1;
					} else if (srcItemStack == null) {
						srcItemStack = itemstack1;
					} else {
						return null;
					}
				} else {
					return null;
				}
			}
		}

		return new CraftingSearchResults(destItemStack, srcItemStack);
	}

	// given a pixel coordinate i,j and scale, find the most common pixel within
	// (i,j) - [i+size,j+size].
	private byte getMapPixel(final MapData mapData, final int i, final int j, final int scaleDiff) {

		// case: multiple pixels being scaled down to one, choose the most
		// common pixel color like maps do normally
		if (scaleDiff < 0) {
			final int width = 1 << (scaleDiff * -1);
			final HashMultiset<Integer> hashmultiset = HashMultiset.create();
			for (int x = i; x < i + width; x++) {
				for (int y = j; y < j + width; y++) {
					if ((x >= 0) && (x < pixLength) && (y >= 0) && (y < pixLength))
						if ((i >= 0) && (i < pixLength) && (j >= 0) && (j < pixLength)) {
							final int index = y * pixLength + x;
							final byte color = mapData.colors[index];
							// don't add transparent pixels
							if (!isUnexploredColor(color)) {
								hashmultiset.add(new Integer(color));
							}
						}
				}
			}

			final Integer pixelValue = Iterables.getFirst(Multisets.copyHighestCountFirst(hashmultiset), 0);
			return pixelValue.byteValue();
		}

		// MAYBETODO if scale > 1, blend between pixel values to avoid blockiness

		// case: return one pixel for one or fewer pixels
		if ((i >= 0) && (i < pixLength) && (j >= 0) && (j < pixLength)) {
			final int index = j * pixLength + i;
			return mapData.colors[index];
		}

		// case: out of range
		return 0;
	}

	// given world coord x,y in mapData, return map pixel color or 0 if not in
	// map
	private byte getPixelValueForWorldCoord(final MapData mapData, final int x, final int z, final int scaleDiff) {
		final int scale = 1 << mapData.scale;
		final int size = pixLength * scale;

		// world scale 0 .. size
		final int wsx0 = x - mapData.xCenter + (size / 2);
		final int wsz0 = z - mapData.zCenter + (size / 2);

		// pixel scale 0 .. pixLength
		final int psx0 = wsx0 / scale;
		final int psz0 = wsz0 / scale;

		// get pixel value if coord within range
		return getMapPixel(mapData, psx0, psz0, scaleDiff);
	}

	// get intersection in world coords of two map datas, or null if no
	// intersection
	private Rect getMapDataIntersection(final MapData destMapData, final MapData srcMapData, final World worldIn) {
		log("getMapDataIntersection");

		if (srcMapData == null || destMapData == null) {
			return null;
		}

		// if maps are for different dimensions, nothing to do
		if (srcMapData.dimension != destMapData.dimension) {
			return null;
		}

		// calculate map corners
		final int destScale = 1 << destMapData.scale;
		final int destSize = pixLength * destScale;
		final Rect r1 = new Rect();
		r1.x1 = destMapData.xCenter - destSize / 2;
		r1.z1 = destMapData.zCenter - destSize / 2;
		r1.x2 = destMapData.xCenter + destSize / 2;
		r1.z2 = destMapData.zCenter + destSize / 2;

		final int srcScale = 1 << srcMapData.scale;
		final int srcSize = pixLength * srcScale;
		final Rect r2 = new Rect();
		r2.x1 = srcMapData.xCenter - srcSize / 2;
		r2.z1 = srcMapData.zCenter - srcSize / 2;
		r2.x2 = srcMapData.xCenter + srcSize / 2;
		r2.z2 = srcMapData.zCenter + srcSize / 2;

		log("dest: " + r1.str());
		log("src:  " + r2.str());

		// find intersection
		return Rect.intersection(r1, r2);
	}

	// get the MapData for a given ItemStack, or null if not correct
	private MapData getMapData(final ItemStack dest, final World worldIn) {
		if (dest != null && dest.getItem() instanceof ItemMap) {
			return Items.filled_map.getMapData(dest, worldIn);
		}

		return null;
	}

	// can we copy any map data from src to dest?
	public Boolean canTranscribeMap(final ItemStack dest, final ItemStack src, final World worldIn) {
		log("canTranscribeMap");

		final MapData destMapData = getMapData(dest, worldIn);
		final MapData srcMapData = getMapData(src, worldIn);

		// find intersection
		final Rect ri = getMapDataIntersection(destMapData, srcMapData, worldIn);

		return (ri != null);
	}

	// is color an unexplored map block color?
	private boolean isUnexploredColor(final byte color) {
		// this is calc to detemine unexplored pixels from
		// net/minecraft/client/gui/MapItemRenderer.java
		return (color / 4 == 0);
	}

	// copy the map data from src to dest if possible
	public void transcribeMap(final ItemStack dest, final ItemStack src, final World worldIn) {
		log("transcribeMap begin");

		final MapData destMapData = getMapData(dest, worldIn);
		final MapData srcMapData = getMapData(src, worldIn);

		// find intersection
		final Rect ri = getMapDataIntersection(destMapData, srcMapData, worldIn);

		if (ri == null) {
			log("transcribeMap end - no intersection");
			return;
		}

		log("intr: " + ri.str());

		// now convert world space intersection into dest pixel space
		// dest pixel space is byte array of 128x128 with world xCenter and
		// zCenter in middle
		final int destScale = 1 << destMapData.scale;
		final int destSize = pixLength * destScale;
		final Rect dp = new Rect();
		dp.x1 = (ri.x1 - destMapData.xCenter + destSize / 2) / destScale;
		dp.z1 = (ri.z1 - destMapData.zCenter + destSize / 2) / destScale;
		dp.x2 = (ri.x2 - destMapData.xCenter + destSize / 2) / destScale;
		dp.z2 = (ri.z2 - destMapData.zCenter + destSize / 2) / destScale;

		final int dxsize = dp.x2 - dp.x1;
		final int dzsize = dp.z2 - dp.z1;
		final int scaleDiff = srcMapData.scale - destMapData.scale;

		log("dp:   " + dp.str());
		log("size: (" + dxsize + "," + dzsize + ") scaleDiff " + scaleDiff);

		// walk dest pixels, copying appropriate pixel from src for each one
		for (int i = 0; i < dxsize; i++) {
			for (int j = 0; j < dzsize; j++) {

				// calculate world coord for pixel
				// destScale = destSize / pixLength
				final int wx = destMapData.xCenter - (destSize / 2) + ((dp.x1 + i) * destScale);
				final int wz = destMapData.zCenter - (destSize / 2) + ((dp.z1 + j) * destScale);

				// calculate destination index for pixel
				final int index = (dp.z1 + j) * pixLength + (dp.x1 + i);

				if ((index >= 0) && (index < pixLength * pixLength)) {
					// only write to blank pixels in dest
					if (isUnexploredColor(destMapData.colors[index])) {
						destMapData.colors[index] = this.getPixelValueForWorldCoord(srcMapData, wx, wz, scaleDiff);
					}
				}
				/*
				 * // debug else { this.log("OOB2: ij ("+i+","
				 * +j+") w ("+wx+","+wz+") index "+index);
				 * //destMapData.markDirty(); //return; }
				 */
			}

			// debug
			// this.log("dirty: "+(dp.x1+i)+","+dp.z1+"..."+(dp.z1+dzsize-1));

			// mark column dirty so it is resent to clients
			destMapData.setColumnDirty(dp.x1 + i, dp.z1, dp.z1 + dzsize - 1);
		}

		log("transcribeMap end - done");
	}

	@Override
	public boolean matches(final InventoryCrafting inv, final World worldIn) {

		// debug
		// this.world = worldIn;

		final CraftingSearchResults results = this.getStuff(inv);

		return (results != null) && (results.srcItemStack() != null);
		// && canTranscribeMap(results.destItemStack(), results.srcItemStack(),
		// worldIn);
	}

	@Override
	public ItemStack getCraftingResult(final InventoryCrafting inv) {

		final CraftingSearchResults results = this.getStuff(inv);

		if (results != null && results.srcItemStack() != null) {
			// did not work: seems to cause map transcription to be missing
			// sometimes
			// ItemStack itemstack = results.destItemStack().copy();

			// FUTURE: ItemStack.getItemDamage becomes ItemStack.getMetaData
			// after 1.7.10
			final ItemStack itemstack = new ItemStack(Items.filled_map, 1, results.destItemStack().getItemDamage());

			if (results.destItemStack().hasDisplayName()) {
				itemstack.setStackDisplayName(results.destItemStack().getDisplayName());
			}

			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

	// handle returning src maps to player and transcribing onto dest
	@SubscribeEvent
	public void onCrafting(final PlayerEvent.ItemCraftedEvent event) {

		// debug log
		// player = event.player;

		final IInventory craftMatrix = event.craftMatrix;
		if (!(craftMatrix instanceof InventoryCrafting)
				|| !craftMatrix.getInventoryName().equals("container.crafting")) {
			return;
		}

		final InventoryCrafting inv = (InventoryCrafting) craftMatrix;

		final CraftingSearchResults results = this.getStuff(inv);

		if (this.matches(inv, event.player.worldObj)) {
			// only transcribe on server, and let it send updated map data to
			// client
			if (!event.player.worldObj.isRemote) {
				this.transcribeMap(event.crafting, results.srcItemStack(), event.player.worldObj);
			}

			for (int i = craftMatrix.getSizeInventory() - 1; i >= 0; i--) {
				final ItemStack slot = craftMatrix.getStackInSlot(i);

				if (slot == null) {
					continue;
				} else if (slot == results.srcItemStack()) {
					// increment stack size by 1 so when decreased automatically
					// by 1 there is still 1 there
					slot.stackSize += 1;
				}
			}
		}

	}

	/*
	 * // For future Forge/MC versions:
	 * 
	 * @Override public ItemStack[] getRemainingItems(InventoryCrafting inv) {
	 * CraftingSearchResults results = this.getStuff(inv);
	 * 
	 * ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];
	 * 
	 * for (int i = 0; i < aitemstack.length; ++i) { ItemStack itemstack =
	 * inv.getStackInSlot(i);
	 * 
	 * if (itemstack != null) && itemstack.getItem().hasContainerItem()) {
	 * aitemstack[i] = new ItemStack(itemstack.getItem().getContainerItem()); }
	 * 
	 * }
	 * 
	 * return aitemstack; }
	 */
}