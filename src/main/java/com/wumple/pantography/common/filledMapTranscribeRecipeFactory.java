package com.wumple.pantography.common;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
//import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multisets;
import com.google.gson.JsonObject;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * RecipeFactory to copy map data from one filled map into another filled map
 */
public class filledMapTranscribeRecipeFactory implements IRecipeFactory
{

    /**
     * hook for JSON to be able to use this recipe
     * 
     * @see _factories.json
     * @see filled_map_transcribe.json
     */
    @Override
    public IRecipe parse(JsonContext context, JsonObject json)
    {
        ShapelessOreRecipe recipe = ShapelessOreRecipe.factory(context, json);

        return new filledMapTranscribeRecipe(new ResourceLocation(Reference.MOD_ID, "filled_map_transcribe_crafting"), recipe.getRecipeOutput());
    }

    /**
     * The actual recipe to copy map data from one filled map into another filled map
     */
    public static class filledMapTranscribeRecipe extends ShapelessOreRecipe
    {

        public filledMapTranscribeRecipe(ResourceLocation group, @Nonnull ItemStack result, Object... recipe)
        {
            super(group, result, recipe);
            // register for events so received onCrafting event can handle map transcription
            MinecraftForge.EVENT_BUS.register(this);
        }

        /**
         * Magic number for width and height of map data - not a constant elsewhere
         * 
         * @see MapData
         */
        private static final int pixLength = 128;

        // Basic logging for debugging
        public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

        /**
         * // Enable for primitive live debugging
         * private EntityPlayer player;
         * private World world;
         */

        /**
         * debug logging
         * 
         * @param msg
         *            message to log
         */
        private void log(final String msg)
        {
            logger.debug(msg);

            /**
             * // Enable for primitive debugging 
             * if (player != null) 
             * { player.addChatMessage(new ChatComponentText(msg)); }
             */
        }

        /**
         * given a pixel coordinate i,j and scale, find the most common pixel within range
         * 
         * @param mapData
         *            map's pixels to search
         * @param i
         *            pixel coordinate x
         * @param j
         *            pixel coordinate y
         * @param scaleDiff
         *            scale difference of i,j
         * @return most common pixel color within (i,j) - [i+size,j+size]
         */
        private byte getMapPixel(final MapData mapData, final int i, final int j, final int scaleDiff)
        {

            // case: multiple pixels being scaled down to one, choose the most
            // common pixel color like maps do normally
            if (scaleDiff < 0)
            {
                final int width = 1 << (scaleDiff * -1);
                final HashMultiset<Integer> hashmultiset = HashMultiset.create();
                for (int x = i; x < i + width; x++)
                {
                    for (int y = j; y < j + width; y++)
                    {
                        if ((x >= 0) && (x < pixLength) && (y >= 0) && (y < pixLength))
                            if ((i >= 0) && (i < pixLength) && (j >= 0) && (j < pixLength))
                            {
                                final int index = y * pixLength + x;
                                final byte color = mapData.colors[index];
                                // don't add transparent pixels
                                if (!isUnexploredColor(color))
                                {
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
            if ((i >= 0) && (i < pixLength) && (j >= 0) && (j < pixLength))
            {
                final int index = j * pixLength + i;
                return mapData.colors[index];
            }

            // case: out of range
            return 0;
        }

        /**
         * get map pixel color given world coord x,y in mapData
         * 
         * @param mapData
         *            map's pixels to search
         * @param x
         *            world coord x
         * @param z
         *            world cord y
         * @param scaleDiff
         *            scale difference
         * @return map pixel color for x,y or 0 if not in map
         */
        private byte getPixelValueForWorldCoord(final MapData mapData, final int x, final int z, final int scaleDiff)
        {
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

        /**
         * get intersection in of two map datas
         * 
         * @param destMapData
         *            map 1
         * @param srcMapData
         *            map 2
         * @param worldIn
         *            current world
         * @return Rect of intersection in world coords, or null if no intersection
         */
        private Rect getMapDataIntersection(final MapData destMapData, final MapData srcMapData, final World worldIn)
        {
            log("getMapDataIntersection");

            if (srcMapData == null || destMapData == null)
            {
                return null;
            }

            // if maps are for different dimensions, nothing to do
            if (srcMapData.dimension != destMapData.dimension)
            {
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

        /**
         * get MapData for given ItemStack
         * 
         * @param dest
         *            Itemstack to get MapData from
         * @param worldIn
         *            current world
         * @return MapData for a dest ItemStack, or null if not correct
         */
        private MapData getMapData(final ItemStack dest, final World worldIn)
        {
            if (dest != null && dest != ItemStack.EMPTY && dest.getItem() instanceof ItemMap)
            {
                return Items.FILLED_MAP.getMapData(dest, worldIn);
            }

            return null;
        }

        /**
         * can we transcribe any map data from src to dest?
         * 
         * @param dest
         *            destination map
         * @param src
         *            source map
         * @param worldIn
         *            current world
         * @return true if any data would be copied, false if not
         */
        public Boolean canTranscribeMap(final ItemStack dest, final ItemStack src, final World worldIn)
        {
            log("canTranscribeMap");

            final MapData destMapData = getMapData(dest, worldIn);
            final MapData srcMapData = getMapData(src, worldIn);

            // find intersection
            final Rect ri = getMapDataIntersection(destMapData, srcMapData, worldIn);

            return (ri != null);
        }

        /**
         * is color an unexplored map block color?
         * 
         * @param color
         *            to check
         * @return true if color represents unexplored color, false if not
         */
        private boolean isUnexploredColor(final byte color)
        {
            // this is calc to detemine unexplored pixels from
            // net/minecraft/client/gui/MapItemRenderer.java
            return (color / 4 == 0);
        }

        /**
         * copy the map data from src to dest if possible
         * 
         * @param dest
         *            destination map
         * @param src
         *            source map
         * @param worldIn
         *            current world
         */
        public void transcribeMap(final ItemStack dest, final ItemStack src, final World worldIn)
        {
            log("transcribeMap begin");

            final MapData destMapData = getMapData(dest, worldIn);
            final MapData srcMapData = getMapData(src, worldIn);

            // find intersection
            final Rect ri = getMapDataIntersection(destMapData, srcMapData, worldIn);

            if (ri == null)
            {
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
            for (int i = 0; i < dxsize; i++)
            {
                for (int j = 0; j < dzsize; j++)
                {

                    // calculate world coord for pixel
                    // destScale = destSize / pixLength
                    final int wx = destMapData.xCenter - (destSize / 2) + ((dp.x1 + i) * destScale);
                    final int wz = destMapData.zCenter - (destSize / 2) + ((dp.z1 + j) * destScale);

                    // calculate destination index for pixel
                    final int dz = (dp.z1 + j);
                    final int dx = (dp.x1 + i);
                    final int index = dz * pixLength + dx;

                    if ((index >= 0) && (index < pixLength * pixLength))
                    {
                        // only write to blank pixels in dest
                        if (isUnexploredColor(destMapData.colors[index]))
                        {
                            destMapData.colors[index] = this.getPixelValueForWorldCoord(srcMapData, wx, wz, scaleDiff);
                            destMapData.updateMapData(dx, dz);
                        }
                    }
                    /*
                     * // debug 
                     * else 
                     * { 
                     * this.log("OOB2: ij ("+i+"," +j+") w ("+wx+","+wz+") index "+index); 
                     * destMapData.markDirty(); 
                     * return;
                     * }
                     */
                }

                // debug
                // this.log("dirty: "+(dp.x1+i)+","+dp.z1+"..."+(dp.z1+dzsize-1));

                // mark column dirty so it is resent to clients
                // in 1.7.10 was: destMapData.setColumnDirty(dp.x1 + i, dp.z1, dp.z1 + dzsize - 1);
                destMapData.markDirty();
            }

            log("transcribeMap end - done");
        }

        /**
         * return the items involved in this recipe, or null if not present
         * 
         * @param inv
         *            inventory to check for tems
         * @return dest and src items found, or null if not found
         */
        private CraftingSearchResults getStuff(InventoryCrafting inv)
        {
            ItemStack destItemStack = ItemStack.EMPTY;
            ItemStack srcItemStack = ItemStack.EMPTY;

            for (int j = 0; j < inv.getSizeInventory(); ++j)
            {
                final ItemStack itemstack1 = inv.getStackInSlot(j);

                if (!itemstack1.isEmpty())
                {
                    if (itemstack1.getItem() == Items.FILLED_MAP)
                    {
                        if (destItemStack == ItemStack.EMPTY)
                        {
                            destItemStack = itemstack1;
                        }
                        else if (srcItemStack == ItemStack.EMPTY)
                        {
                            srcItemStack = itemstack1;
                        }
                        else
                        {
                            return null;
                        }
                    }
                    else
                    {
                        return null;
                    }
                }
            }

            return new CraftingSearchResults(destItemStack, srcItemStack);
        }

        @Override
        public boolean matches(InventoryCrafting inv, World worldIn)
        {
            final CraftingSearchResults results = this.getStuff(inv);

            // Currently allowing copy of unrelated maps since no loss for player, but to prevent it add:
            // canTranscribeMap(results.destItemStack(), results.srcItemStack(), worldIn);

            return (results != null) && (results.srcItemStack() != ItemStack.EMPTY) && (results.destItemStack() != ItemStack.EMPTY);
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv)
        {
            final CraftingSearchResults results = this.getStuff(inv);

            if (results != null && results.srcItemStack() != ItemStack.EMPTY && results.destItemStack() != ItemStack.EMPTY)
            {
                ItemStack itemstack2 = new ItemStack(Items.FILLED_MAP, 1, results.destItemStack().getMetadata());

                if (results.destItemStack().hasDisplayName())
                {
                    itemstack2.setStackDisplayName(results.destItemStack().getDisplayName());
                }

                if (results.destItemStack().hasTagCompound())
                {
                    itemstack2.setTagCompound(results.destItemStack().getTagCompound());
                }

                return itemstack2;
            }
            else
            {
                return ItemStack.EMPTY;
            }
        }

        @Override
        public ItemStack getRecipeOutput()
        {
            return ItemStack.EMPTY;
        }

        @Override
        public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
        {
            NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack> withSize(inv.getSizeInventory(), ItemStack.EMPTY);

            // return all unused items
            for (int i = 0; i < nonnulllist.size(); ++i)
            {
                ItemStack itemstack = inv.getStackInSlot(i);
                nonnulllist.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
            }

            return nonnulllist;
        }

        @Override
        public boolean isDynamic()
        {
            return true;
        }

        /**
         * Used to determine if this recipe can fit in a grid of the given width/height
         */
        @Override
        public boolean canFit(int width, int height)
        {
            return width >= 3 && height >= 3;
        }

        /**
         * handle returning src maps to player and transcribing onto dest also dirties map so server sends new data to clients
         */
        @SubscribeEvent
        public void onCrafting(final ItemCraftedEvent event)
        {

            // debug log
            // player = event.player;

            final IInventory craftMatrix = event.craftMatrix;
            if (!(craftMatrix instanceof InventoryCrafting)
                    || !craftMatrix.getName().equals("container.crafting"))
            {
                return;
            }

            final InventoryCrafting inv = (InventoryCrafting) craftMatrix;

            final CraftingSearchResults results = this.getStuff(inv);

            if (this.matches(inv, event.player.world))
            {
                // only transcribe on server, and let it send updated map data to client
                if (!event.player.world.isRemote)
                {
                    this.transcribeMap(event.crafting, results.srcItemStack(), event.player.world);
                }

                for (int i = craftMatrix.getSizeInventory() - 1; i >= 0; i--)
                {
                    final ItemStack slot = craftMatrix.getStackInSlot(i);

                    if (slot == null)
                    {
                        continue;
                    }
                    else if (slot == results.srcItemStack())
                    {
                        // increment stack size by 1 so when decreased automatically by 1 there is still 1 there
                        slot.grow(1);
                    }
                }
            }

        }
    }
}