package com.wumple.pantography;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModSetup {

    public ItemGroup itemGroup = new ItemGroup("pantography") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.PantographBlock);
        }
    };
    
    public void init() {
        //MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        //itemGroup.Networking.registerMessages();
    }

}