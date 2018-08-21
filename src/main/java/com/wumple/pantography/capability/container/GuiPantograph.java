package com.wumple.pantography.capability.container;

import com.wumple.pantography.capability.IPantographCap;
import com.wumple.util.capability.targetcrafting.container.GuiCrafting;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiPantograph extends GuiCrafting
{
    protected static final ResourceLocation guiTextures = new ResourceLocation("pantography", "textures/gui/pantograph.png");

    public GuiPantograph (InventoryPlayer inventory, IPantographCap ownerIn)
    {
        super(inventory, ownerIn);
    }
    
    protected ResourceLocation getGuiTextures()
    {
        return guiTextures;
    }
}