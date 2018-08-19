package com.wumple.pantography;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wumple.pantography.capability.PantographCap;
import com.wumple.pantography.config.ConfigHandler;
import com.wumple.pantography.config.ModConfig;
import com.wumple.util.map.MapUtil;
import com.wumple.util.mod.ModBase;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Pantography mod main class
 */
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, dependencies = Reference.DEPENDENCIES,
        updateJSON = Reference.UPDATEJSON, certificateFingerprint = Reference.FINGERPRINT)
public class Pantography extends ModBase
{
    @Mod.Instance(Reference.MOD_ID)
    public static Pantography instance;

    @EventHandler
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        ConfigHandler.init();
        PantographCap.register();
        MapUtil.setLogger((msg) -> { if (ModConfig.zdebugging.debug) { Pantography.logger.info(msg); } });
    }
    
    @EventHandler
    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        ObjectHolder.RegistrationHandler.registerGuiHandlers();
    }
    
    @EventHandler
    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
    }    

    @EventHandler
    @Override
    public void onFingerprintViolation(FMLFingerprintViolationEvent event)
    {
        super.onFingerprintViolation(event);
    }

    @Override
    public Logger getLoggerFromManager()
    {
        return LogManager.getLogger(Reference.MOD_ID);
    }
}
