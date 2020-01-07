package com.wumple.pantography.pantograph;

import com.wumple.util.xcartography.XCartographyContainer;
import com.wumple.util.xcartography.XCartographyScreen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class PantographScreen extends XCartographyScreen
{
	public PantographScreen(XCartographyContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
	{
		super(screenContainer, inv, titleIn);
	}
}
