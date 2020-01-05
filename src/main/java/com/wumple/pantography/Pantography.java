package com.wumple.pantography;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wumple.pantography.integration.megamap.MegaMapHandler;
import com.wumple.pantography.recipe.TranscribeMapRecipe;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Pantography mod main class
 */
@Mod(Reference.MOD_ID)
public class Pantography /*extends ModBase*/
{
	public Logger getLogger()
	{
		return LogManager.getLogger(Reference.MOD_ID);
	}

	public Pantography()
	{
		ConfigManager.register(ModLoadingContext.get());

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addGenericListener(IRecipeSerializer.class, this::registerRecipeSerializers);
		// TODO modEventBus.addGenericListener(Item.class, this::registerItems);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}
	
    public void setup(final FMLCommonSetupEvent event)
    {
		//MegaMapHandler.register();
        //PantographCap.register();
    }

	@SubscribeEvent
	public void onFingerprintViolation(final FMLFingerprintViolationEvent event)
	{
		getLogger().warn("Invalid fingerprint detected! The file " + event.getSource().getName()
				+ " may have been tampered with. This version will NOT be supported by the author!");
		getLogger().warn("Expected " + event.getExpectedFingerprint() + " found " + event.getFingerprints().toString());
	}

	public static TranscribeMapRecipe.Serializer<TranscribeMapRecipe> CRAFTING_SPECIAL_TRANSCRIBEMAP;

	private void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event)
	{
		CRAFTING_SPECIAL_TRANSCRIBEMAP = new TranscribeMapRecipe.Serializer<TranscribeMapRecipe>(
				TranscribeMapRecipe::new);
		CRAFTING_SPECIAL_TRANSCRIBEMAP.setRegistryName("transcribemap");
		event.getRegistry().registerAll(CRAFTING_SPECIAL_TRANSCRIBEMAP);
	}

}
