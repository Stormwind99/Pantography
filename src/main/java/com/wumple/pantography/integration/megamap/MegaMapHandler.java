package com.wumple.pantography.integration.megamap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wumple.pantography.Reference;

import net.minecraftforge.fml.ModList;

public class MegaMapHandler
{
    public static void register()
    {
    	Logger logger = LogManager.getLogger(Reference.MOD_ID);

        if (ModList.get().isLoaded("megamap"))
        {
        	logger.info("Registering MegaMapIntegration");
            // PORT MegaMapIntegration.register();
        }
        else
        {
        	logger.info("Skipping MegaMapIntegration");
        }
    }

}
