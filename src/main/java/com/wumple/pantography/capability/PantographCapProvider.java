package com.wumple.pantography.capability;

import javax.annotation.Nullable;

import com.wumple.util.adapter.IThing;
import com.wumple.util.capability.targetcrafting.CapProvider;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class PantographCapProvider extends CapProvider<IPantographCap>
{
    public PantographCapProvider(Capability<IPantographCap> capability, @Nullable Direction facing, IThing ownerIn)
    {
        super(capability, facing, ownerIn);
    }

    public PantographCapProvider(Capability<IPantographCap> capability, @Nullable Direction facing, IPantographCap instance,
            IThing ownerIn)
    {
        super(capability, facing, instance, ownerIn);
    }

    public static PantographCapProvider createProvider(IThing ownerIn)
    {
        return new PantographCapProvider(IPantographCap.CAPABILITY, IPantographCap.DEFAULT_FACING, ownerIn);
    }
}
