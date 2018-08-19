package com.wumple.pantography.config;

import java.util.HashMap;
import java.util.Map;

import com.wumple.pantography.Reference;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Reference.MOD_ID)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModConfig
{
    @Name("Match only intersecting maps")
    @Config.Comment("Crafting table will only allow combining maps if maps have an intersection") 
    public static boolean matchOnlyIntersectingMaps = true;
   
    public static class ItemsAndBlocks
    {
        @Name("Pantographs")
        @Config.Comment("Things that are pantographs have a non-zero value")
        public Map<String, Integer> pantographs = new HashMap<String, Integer>();
    }

    @Name("Items and Blocks")
    @Config.Comment("Configure items and blocks for mod") 
    public static ItemsAndBlocks itemsAndBlocks = new ItemsAndBlocks();
    
    @Name("Debugging")
    @Config.Comment("Debugging options")
    public static Debugging zdebugging = new Debugging();

    public static class Debugging
    {
        @Name("Debug mode")
        @Config.Comment("Enable debug features on this menu, display extra debug info.")
        public boolean debug = false;
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    private static class EventHandler
    {
        /**
         * Inject the new values and save to the config file when the config has been changed from the GUI.
         *
         * @param event
         *            The event
         */
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equals(Reference.MOD_ID))
            {
                ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }
}
