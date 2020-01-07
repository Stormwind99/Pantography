package com.wumple.pantography;

import com.wumple.pantography.pantograph.PantographScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements IProxy
{

	@Override
	public void init()
	{
		ScreenManager.registerFactory(ModBlocks.PantographBlock_Container, PantographScreen::new);
	}

	@Override
	public World getClientWorld()
	{
		return Minecraft.getInstance().world;
	}

	@Override
	public PlayerEntity getClientPlayer()
	{
		return Minecraft.getInstance().player;
	}
}