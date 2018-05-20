package com.wumple.pantography.common;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = LibMisc.MOD_ID, name = LibMisc.MOD_NAME, version = LibMisc.VERSION, dependencies = LibMisc.DEPENDENCIES, updateJSON=LibMisc.UPDATEJSON)
public class Pantography {

    @Instance
    public static Pantography instance = new Pantography();
        
     
    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
    	///filledMapTranscribeRecipeFactory.init();
    }
    
	@EventHandler
	public void load(FMLInitializationEvent event) {		
		///RecipeSorter.register("filled_map_transcribe", filledMapTranscribeRecipe.class, Category.SHAPELESS, "");
	}
        
    @EventHandler
    public void init(FMLInitializationEvent e) { 
    }
        
    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
    }
}
