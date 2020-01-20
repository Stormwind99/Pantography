package com.wumple.pantography.recipe;

import com.wumple.pantography.ConfigManager;
import com.wumple.pantography.Pantography;
import com.wumple.util.crafting.CraftingUtil;
import com.wumple.util.crafting.XShapelessRecipe;
import com.wumple.util.map.MapTranscription;
import com.wumple.util.xmap.XMapAPI;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Recipe to copy map data from one filled map into another filled map
 */
public class TranscribeMapRecipe extends XShapelessRecipe
{
	public TranscribeMapRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn,
			NonNullList<Ingredient> recipeItemsIn)
	{
		super(idIn, groupIn, recipeOutputIn, recipeItemsIn);

		MinecraftForge.EVENT_BUS.register(this);
	}

	private void log(String msg)
	{
		//LogManager.getLogger(Reference.MOD_ID).info(msg);
	}

	/**
	 * return the items involved in this recipe, or null if not present
	 * 
	 * @param inv inventory to check for tems
	 * @return dest and src items found, or null if not found
	 */
	protected CraftingSearchResults getStuff(CraftingInventory inv)
	{
		ItemStack destItemStack = ItemStack.EMPTY;
		ItemStack srcItemStack = ItemStack.EMPTY;

		for (int j = 0; j < inv.getSizeInventory(); ++j)
		{
			final ItemStack itemstack1 = inv.getStackInSlot(j);

			if (!itemstack1.isEmpty())
			{
				if (XMapAPI.getInstance().isFilledMap(itemstack1))
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
	public boolean matches(CraftingInventory inv, World worldIn)
	{
		log("recipeMatches begin");
		final CraftingSearchResults results = this.getStuff(inv);

		// Currently disallowing copy of unrelated maps since no loss for player, but to allow it remove:
		// canTranscribeMap(results.destItemStack(), results.srcItemStack(), worldIn);

		boolean mapsValid = (results != null) && (!results.srcItemStack().isEmpty())
				&& (!results.destItemStack().isEmpty());

		boolean canTranscribe = false;
		if (mapsValid)
		{
			canTranscribe = MapTranscription.canTranscribeMap(results.destItemStack(), results.srcItemStack(), worldIn);
		}
		boolean doesMatch = mapsValid && ((!ConfigManager.General.matchOnlyIntersectingMaps.get()) || canTranscribe);

		log("doesMatch " + doesMatch + " mapsValid " + mapsValid + " canTranscribe " + canTranscribe
				+ " matchOnlyIntersectingMaps " + ConfigManager.General.matchOnlyIntersectingMaps.get());
		log("recipeMatches end");

		return doesMatch;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv)
	{
		final CraftingSearchResults results = this.getStuff(inv);

		if (results != null && !results.srcItemStack().isEmpty() && !results.destItemStack().isEmpty())
		{
			return XMapAPI.getInstance().copyMapShallow(results.destItemStack(), 1);
		}
		else
		{
			return ItemStack.EMPTY;
		}
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
	 * If true, this recipe does not appear in the recipe book and does not respect
	 * recipe unlocking (and the doLimitedCrafting gamerule)
	 */
	@Override
	public boolean isDynamic()
	{
		return true;
	}

	public IRecipeSerializer<?> getSerializer()
	{
		return Pantography.CRAFTING_SPECIAL_TRANSCRIBEMAP;
	}

	/**
	 * handle returning src maps to player and transcribing onto dest also dirties
	 * map so server sends new data to clients
	 */
	@SubscribeEvent
	public void onCrafting(final ItemCraftedEvent event)
	{
		final IInventory craftMatrix = event.getInventory();
		if (!(craftMatrix instanceof CraftingInventory)) // || !craftMatrix.getName().equals("container.crafting"))
		{
			return;
		}

		final CraftingInventory inv = (CraftingInventory) craftMatrix;

		final CraftingSearchResults results = this.getStuff(inv);

		if (this.matches(inv, event.getEntityPlayer().getEntityWorld()))
		{
			// only transcribe on server, and let it send updated map data to client
			if (!event.getPlayer().getEntityWorld().isRemote())
			{
				// New Item: event.getCrafting()
				// Map to copy from: results.srcItemStack()
				// Original map being copied to: results.destItemStack()
				
				MapTranscription.transcribeMapWithCopy(event.getCrafting(), results.destItemStack(), results.srcItemStack(), event.getPlayer().getEntityWorld());
			}
			
			CraftingUtil.growByOne(craftMatrix, results.srcItemStack());
		}

	}
}
