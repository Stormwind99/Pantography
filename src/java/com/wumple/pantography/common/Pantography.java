package com.wumple.pantography.common;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Pantography mod main class
 */
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = Reference.DEPENDENCIES, updateJSON=Reference.UPDATEJSON)
public class Pantography {

    @Instance(Reference.MOD_ID)
    public static Pantography instance = new Pantography();
     
    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
    }
    
    @EventHandler
    public void load(FMLInitializationEvent event) {
    }
        
    @EventHandler
    public void init(FMLInitializationEvent e) {
    }
        
    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
    }
}
