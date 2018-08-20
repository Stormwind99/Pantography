package com.wumple.pantography.config;

import com.wumple.pantography.Reference;
import com.wumple.util.config.MatchingConfig;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;

public class ConfigHandler
{
	public static final MatchingConfig<Integer> pantographs = new MatchingConfig<Integer>(ModConfig.itemsAndBlocks.pantographs, 0);
	
	public static void init()
	{
	    pantographs.addDefaultProperty("pantography:pantograph", 1);
	    
	    ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
	}	
}
