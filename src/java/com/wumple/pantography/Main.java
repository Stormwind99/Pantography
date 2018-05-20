package com.wumple.pantography;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Main.MODID, name = Main.MODNAME, version = Main.VERSION, updateJSON = "https://raw.githubusercontent.com/Stormwind99/Pantography/master/update.json")
public class Main {

    public static final String MODID = "pantography";
    public static final String MODNAME = "Pantography";
    public static final String VERSION = "1.1.0";
        
    @Instance
    public static Main instance = new Main();
        
     
    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
    	filledMapTranscribeRecipe.init();
    }
        
    @EventHandler
    public void init(FMLInitializationEvent e) { 
    }
        
    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
    }
}