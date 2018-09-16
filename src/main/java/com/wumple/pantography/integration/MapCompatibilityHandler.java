package com.wumple.pantography.integration;

import java.util.function.Supplier;

public class MapCompatibilityHandler
{
    protected static IMapCompatibility instance = null;
    
    protected static Supplier<IMapCompatibility> factory = MapCompatibilityVanilla::new;
    
    public static void setFactory(Supplier<IMapCompatibility> factoryIn)
    {
        instance = null;
        factory = factoryIn;
    }
    
    public static IMapCompatibility getInstance()
    {
        if (instance == null)
        {
            instance = factory.get();
        }
        
        return instance;
    }
}