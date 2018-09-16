package com.wumple.pantography.integration.megamap;

public class MegaMapIntegration 
{
    private static boolean registered;

    public static void register()
    {
        if (registered) { return; }
        MapCompatibilityMegaMap.register();
        registered = true;
    }
    
    public static boolean getRegistered()
    {
        return registered;
    }
}