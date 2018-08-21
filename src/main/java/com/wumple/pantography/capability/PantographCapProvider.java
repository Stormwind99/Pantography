package com.wumple.pantography.capability;

import javax.annotation.Nullable;

import com.wumple.util.adapter.IThing;
import com.wumple.util.capability.targetcrafting.CapProvider;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class PantographCapProvider extends CapProvider<IPantographCap>
{
    public PantographCapProvider(Capability<IPantographCap> capability, @Nullable EnumFacing facing, IThing ownerIn)
    {
        super(capability, facing, ownerIn);
    }

    public PantographCapProvider(Capability<IPantographCap> capability, @Nullable EnumFacing facing, IPantographCap instance,
            IThing ownerIn)
    {
        super(capability, facing, instance, ownerIn);
    }

    public static PantographCapProvider createProvider(IThing ownerIn)
    {
        return new PantographCapProvider(IPantographCap.CAPABILITY, IPantographCap.DEFAULT_FACING, ownerIn);
    }
}
