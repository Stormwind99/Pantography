package com.wumple.pantography;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wumple.pantography.pantograph.PantographBlock;
import com.wumple.pantography.pantograph.PantographContainer;
import com.wumple.pantography.pantograph.PantographTileEntity;
import com.wumple.pantography.recipe.TranscribeMapRecipe;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
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

	public static ModSetup setup = new ModSetup();
	public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

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
		setup.init();
		proxy.init();
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

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents
	{
		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> event)
		{
			event.getRegistry().register(new PantographBlock());
		}

		@SubscribeEvent
		public static void onItemsRegistry(final RegistryEvent.Register<Item> event)
		{
			Item.Properties properties = new Item.Properties().group(setup.itemGroup);
			event.getRegistry()
					.register(new BlockItem(ModBlocks.PantographBlock, properties).setRegistryName("pantograph"));
		}

		@SubscribeEvent
		public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event)
		{
			event.getRegistry().register(TileEntityType.Builder.create(PantographTileEntity::new, ModBlocks.PantographBlock)
					.build(null).setRegistryName("pantograph"));
		}
		
        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
				return new PantographContainer(windowId, Pantography.proxy.getClientWorld(), pos, inv,
						Pantography.proxy.getClientPlayer());
            }).setRegistryName("pantograph"));
        }
	}
}
