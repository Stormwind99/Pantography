package com.wumple.pantography;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

// See
// https://github.com/McJty/YouTubeModding14/blob/master/src/main/java/com/mcjty/mytutorial/Config.java
// https://wiki.mcjty.eu/modding/index.php?title=Tut14_Ep6

@Mod.EventBusSubscriber
public class ConfigManager
{
	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

	public static ForgeConfigSpec COMMON_CONFIG;
	public static ForgeConfigSpec CLIENT_CONFIG;

	public static final String CATEGORY_GENERAL = "General";
	public static final String CATEGORY_DEBUGGING = "Debugging";

	public static class General
	{
		public static ForgeConfigSpec.BooleanValue matchOnlyIntersectingMaps;

		private static void setupConfig()
		{
			COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
	
			// @Name("Match only intersecting maps")
			matchOnlyIntersectingMaps = COMMON_BUILDER
					.comment("Crafting table will only allow combining maps if maps have an intersection")
					.define("matchOnlyIntersectingMaps", true);
	
			COMMON_BUILDER.pop();
		}
	}

	public static class Debugging
	{
		public static ForgeConfigSpec.BooleanValue debug;

		private static void setupConfig()
		{
			// @Config.Comment("Debugging options")
			COMMON_BUILDER.comment("Debugging settings").push(CATEGORY_DEBUGGING);

			//@Name("Debug mode")
			debug = COMMON_BUILDER.comment("Enable general debug features, display extra debug info").define("debug",
					false);

			COMMON_BUILDER.pop();
		}
	}
	
	/*
	// TODO

	@Name("Items and Blocks")
	@Config.Comment("Configure items and blocks for mod") 
	public static class ItemsAndBlocks
	{
	    @Name("Pantographs")
	    @Config.Comment("Things that are pantographs have a non-zero value")
	    public Map<String, Integer> pantographs = new HashMap<String, Integer>();
	}
	
	public static final MatchingConfig<Integer> pantographs = new MatchingConfig<Integer>(ConfigManager.itemsAndBlocks.pantographs, 0);
	pantographs.addDefaultProperty("pantography:pantograph", 1);
	*/

	static
	{
		General.setupConfig();
		Debugging.setupConfig();

		COMMON_CONFIG = COMMON_BUILDER.build();
		CLIENT_CONFIG = CLIENT_BUILDER.build();
	}

	public static void loadConfig(ForgeConfigSpec spec, Path path)
	{

		final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave()
				.writingMode(WritingMode.REPLACE).build();

		configData.load();
		spec.setConfig(configData);
	}

	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading configEvent)
	{
	}

	@SubscribeEvent
	public static void onReload(final ModConfig.ConfigReloading configEvent)
	{
	}

	public static void register(final ModLoadingContext context)
	{
		context.registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG);
		context.registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);

		loadConfig(ConfigManager.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(Reference.MOD_ID + "-client.toml"));
		loadConfig(ConfigManager.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(Reference.MOD_ID + "-common.toml"));
	}
}

