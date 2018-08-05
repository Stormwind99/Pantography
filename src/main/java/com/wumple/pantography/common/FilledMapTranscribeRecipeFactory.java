package com.wumple.pantography.common;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import com.wumple.util.map.MapTranscription;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * RecipeFactory to copy map data from one filled map into another filled map
 */
public class FilledMapTranscribeRecipeFactory implements IRecipeFactory
{
    /**
     * debug logging
     * 
     * @param msg
     *            message to log
     */
    public static void log(final String msg)
    {
        if (ModConfig.zdebugging.debug)
        {
            Pantography.logger.info(msg);
        }
    }
    
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

        return new FilledMapTranscribeRecipe(new ResourceLocation(Reference.MOD_ID, "filled_map_transcribe_crafting"), recipe.getRecipeOutput());
    }

    /**
     * The actual recipe to copy map data from one filled map into another filled map
     */
    public static class FilledMapTranscribeRecipe extends ShapelessOreRecipe
    {

        public FilledMapTranscribeRecipe(ResourceLocation group, @Nonnull ItemStack result, Object... recipe)
        {
            super(group, result, recipe);
            // register for events so received onCrafting event can handle map transcription
            MinecraftForge.EVENT_BUS.register(this);
        }   

        /**
         * return the items involved in this recipe, or null if not present
         * 
         * @param inv
         *            inventory to check for tems
         * @return dest and src items found, or null if not found
         */
        protected CraftingSearchResults getStuff(InventoryCrafting inv)
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
                        if (destItemStack.isEmpty())
                        {
                            destItemStack = itemstack1;
                        }
                        else if (srcItemStack.isEmpty())
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
            log("recipeMatches begin");
            final CraftingSearchResults results = this.getStuff(inv);

            // Currently disallowing copy of unrelated maps since no loss for player, but to allow it remove:
            // canTranscribeMap(results.destItemStack(), results.srcItemStack(), worldIn);

            boolean mapsValid = (results != null) && 
                    (!results.srcItemStack().isEmpty()) && 
                    (!results.destItemStack().isEmpty());
            
            boolean canTranscribe = false;
            if (mapsValid)
            {
                canTranscribe = MapTranscription.canTranscribeMap(results.destItemStack(), results.srcItemStack(), worldIn);
            }
            boolean doesMatch = mapsValid && ((!ModConfig.matchOnlyIntersectingMaps) || canTranscribe);

            log("doesMatch " + doesMatch + " mapsValid " + mapsValid + " canTranscribe " + canTranscribe + " matchOnlyIntersectingMaps " + ModConfig.matchOnlyIntersectingMaps);
            log("recipeMatches end");

            return doesMatch;
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv)
        {
            final CraftingSearchResults results = this.getStuff(inv);

            if (results != null && !results.srcItemStack().isEmpty() && !results.destItemStack().isEmpty())
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
                    MapTranscription.transcribeMap(event.crafting, results.srcItemStack(), event.player.world);
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