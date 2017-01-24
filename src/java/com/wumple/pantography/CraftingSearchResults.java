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

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
//import net.minecraftforge.event.entity.player.PlayerEvent;

// debug
import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

// crafting items for this recipe, if present
class CraftingSearchResults {
	private final ItemStack m_destItemStack;
	private final ItemStack m_srcItemStack;

	public CraftingSearchResults() {
		m_destItemStack = null;
		m_srcItemStack = null;
	}

	public CraftingSearchResults(ItemStack destItemStack, ItemStack srcItemStack) {
		m_destItemStack = destItemStack;
		m_srcItemStack = srcItemStack;
	}

	public ItemStack destItemStack() {
		return m_destItemStack;
	}

	public ItemStack srcItemStack() {
		return m_srcItemStack;
	}
}