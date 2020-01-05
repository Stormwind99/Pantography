package com.wumple.pantography.capability.container;

import com.wumple.pantography.capability.IPantographCap;

import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class GuiPantograph extends CraftingScreen
{
    protected static final ResourceLocation guiTextures = new ResourceLocation("pantography", "textures/gui/pantograph.png");

    public GuiPantograph (PlayerInventory inventory, IPantographCap ownerIn)
    {
    	// PORT public CraftingScreen(WorkbenchContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(null, inventory, null);
    }
    
    protected ResourceLocation getGuiTextures()
    {
        return guiTextures;
    }
}