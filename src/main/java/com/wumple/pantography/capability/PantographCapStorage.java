package com.wumple.pantography.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PantographCapStorage implements IStorage<IPantographCap>
{
    @Override
    public NBTBase writeNBT(Capability<IPantographCap> capability, IPantographCap instance, EnumFacing side)
    {
        if (instance != null)
        {
            return instance.serializeNBT();
        }

        return null;
    }

    @Override
    public void readNBT(Capability<IPantographCap> capability, IPantographCap instance, EnumFacing side, NBTBase nbt)
    {
        if ((nbt != null) && (instance != null))
        {
            instance.deserializeNBT(nbt);
        }
    }
}