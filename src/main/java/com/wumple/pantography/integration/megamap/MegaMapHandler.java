package com.wumple.pantography.integration.megamap;

import net.minecraftforge.fml.common.Loader;

public class MegaMapHandler
{
    public static void register()
    {
        if (Loader.isModLoaded("megamap"))
        {
            MegaMapIntegration.register();
        }
    }

}
