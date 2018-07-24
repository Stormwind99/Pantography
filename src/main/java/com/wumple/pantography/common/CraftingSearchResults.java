package com.wumple.pantography.common;

import net.minecraft.item.ItemStack;

/**
 * Crafting items for this recipe, if present
 */
class CraftingSearchResults
{
    /**
     * Crafting destination item stack - the stack to be modified null if none found
     */
    private final ItemStack m_destItemStack;

    /**
     * Crafting source item stack - the stack to be copied from null if none found
     */
    private final ItemStack m_srcItemStack;

    public CraftingSearchResults()
    {
        m_destItemStack = null;
        m_srcItemStack = null;
    }

    public CraftingSearchResults(ItemStack destItemStack, ItemStack srcItemStack)
    {
        m_destItemStack = destItemStack;
        m_srcItemStack = srcItemStack;
    }

    public ItemStack destItemStack()
    {
        return m_destItemStack;
    }

    public ItemStack srcItemStack()
    {
        return m_srcItemStack;
    }
}
