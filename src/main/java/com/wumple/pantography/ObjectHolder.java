package com.wumple.pantography;

import com.wumple.pantography.capability.container.GuiHandlerPantograph;
import com.wumple.pantography.pantograph.BlockPantograph;
import com.wumple.pantography.pantograph.TileEntityPantograph;
import com.wumple.util.misc.RegistrationHelpers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@GameRegistry.ObjectHolder("pantography")
public class ObjectHolder
{
    @GameRegistry.ObjectHolder("pantography:pantograph")
    public static /* final */ Block pantograph = null;

    // @GameRegistry.ObjectHolder("pantography:pantograph_item")
    public static /* final */ Item pantograph_item = null;
    
    // @ObjectHolder("pantography:pantograph_use")
    public static SoundEvent pantograph_use = null;
    
    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static class RegistrationHandler
    {
        public static class Ids
        {
            protected final static String[] pantographs = { "pantographs" };
        }
        
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            final IForgeRegistry<Item> registry = event.getRegistry();

            pantograph_item = RegistrationHelpers.registerItemBlockOre(registry, pantograph, Ids.pantographs);
            
            registerTileEntities();
        }

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            final IForgeRegistry<Block> registry = event.getRegistry();

            pantograph = RegistrationHelpers.regHelper(registry, new BlockPantograph());
        }
        
        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public static void registerRenders(ModelRegistryEvent event)
        {
            RegistrationHelpers.registerRender(pantograph, pantograph_item);
        }

        public static void registerTileEntities()
        {
            RegistrationHelpers.registerTileEntity(TileEntityPantograph.class, BlockPantograph.ID);
        }
        
        public static void registerGuiHandlers()
        {
            NetworkRegistry.INSTANCE.registerGuiHandler(Pantography.instance, new GuiHandlerPantograph());
        }
        
        @SubscribeEvent
        public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event)
        {
            final IForgeRegistry<SoundEvent> registry = event.getRegistry();

            pantograph_use = RegistrationHelpers.registerSound(registry, "pantography:pantograph_use");
        }

    }
}
