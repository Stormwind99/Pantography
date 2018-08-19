package com.wumple.pantography;

import com.wumple.pantography.capability.container.GuiHandlerPantograph;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder("pantography")
public class ObjectHolder
{
    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static class RegistrationHandler
    {

        public static void registerGuiHandlers()
        {
            NetworkRegistry.INSTANCE.registerGuiHandler(Pantography.instance, new GuiHandlerPantograph());
        }
    }
}
