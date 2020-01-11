package com.wumple.pantography.pantograph;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.wumple.util.base.misc.Util;
import com.wumple.util.xcartography.XCartographyContainer;
import com.wumple.util.xcartography.XCartographyScreen;
import com.wumple.util.xmap.XMapAPI;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.storage.MapData;

public class PantographScreen extends XCartographyScreen
{
	public PantographScreen(XCartographyContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
	{
		super(screenContainer, inv, titleIn);
	}
	
	@Override
	protected void drawMap(@Nullable MapData mapDataIn, boolean isMap, boolean isPaper, boolean isGlassPane,
			boolean isLocked)
	{
		boolean drew = false;
		
		PantographContainer pcontainer = Util.as(this.container, PantographContainer.class);
		if (pcontainer != null)
		{
			ItemStack itemstack1 = pcontainer.getSlot(1).getStack();
			boolean isTranscribing = pcontainer.isTranscribingInput(itemstack1);
			if (isTranscribing)
			{
				MapData mapdata2 = XMapAPI.getInstance().getMapDataIfExists(itemstack1, this.minecraft.world);
				drawMapTranscribeMap(mapDataIn, mapdata2);
				drew = true;
			}
		}
		
		if (!drew)
		{
			super.drawMap(mapDataIn, isMap, isPaper, isGlassPane, isLocked);
		}
	}
	
	protected void drawMapTranscribeMap(MapData destMapData, MapData srcMapData)
	{
		int i = this.guiLeft;
		int j = this.guiTop;
				
		// draw src map
		this.blit(i + 67 + 16, j + 13, this.xSize, 132, 50, 66);
		if (srcMapData != null)
		{
			this.drawMapItem(srcMapData, i + 86, j + 16, 0.34F);
		}
		
		// draw dest map
		this.minecraft.getTextureManager().bindTexture(CONTAINER_TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translatef(0.0F, 0.0F, 1.0F);
		this.blit(i + 67, j + 13 + 16, this.xSize, 132, 50, 66);
		this.drawMapItem(destMapData, i + 70, j + 32, 0.34F);
		GlStateManager.popMatrix();
		
		/*
		// draw transcription rect or X for invalid transcription
		// PROBLEM: Client MapData is missing dimension, xCenter, and yCenter!

        // find intersection
        final Rect ri = MapTranscription.getMapDataIntersection(destMapData, srcMapData);
		if (ri != null)
		{
			int x = i + 67;
			int y = j + 13 + 16;
			int boundsWidth = this.xSize;
			int boundsHeight = 132;
			int rectWidth = 50;
			int rectHeight = 66;
			// TODO McJty said blit() and fill(), for unfilled maybe vLine() and hLine()
		}
		else
		{
			// draw X over arrow since no intersection to transcribe
			this.blit(i + 35, j + 31, this.xSize + 50, 132, 28, 21);
		}
		*/
	}
	
}
	
