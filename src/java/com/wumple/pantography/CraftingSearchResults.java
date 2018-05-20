package com.wumple.pantography;

import net.minecraft.item.ItemStack;

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