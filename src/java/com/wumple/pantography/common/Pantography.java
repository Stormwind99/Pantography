package com.wumple.pantography.common;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import com.wumple.pantography.common.LibMisc;

@Mod(modid = LibMisc.MOD_ID, name = LibMisc.MOD_NAME, version = LibMisc.VERSION, dependencies = LibMisc.DEPENDENCIES, updateJSON=LibMisc.UPDATEJSON)
public class Pantography {

    @Instance
    public static Pantography instance = new Pantography();
        
     
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
