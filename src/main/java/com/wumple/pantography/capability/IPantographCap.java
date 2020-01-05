package com.wumple.pantography.capability;

import javax.annotation.Nullable;

import com.wumple.pantography.capability.container.ContainerPantograph;
import com.wumple.util.capability.targetcrafting.IContainerCraftingOwner;
import com.wumple.util.capability.CapabilityUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public interface IPantographCap extends IContainerCraftingOwner
{
    // The {@link Capability} instance
    @CapabilityInject(IPantographCap.class)
    public static final Capability<IPantographCap> CAPABILITY = null;
    public static final Direction DEFAULT_FACING = null;
    
    default boolean hasCustomName() { return false; }
    default String getName() { return "none"; }
    
    boolean isActive();
    
    default public Container createContainer(PlayerInventory inventory, PlayerEntity playerIn)
    {
        return new ContainerPantograph(inventory, this);
    }
    
    static LazyOptional<IPantographCap> getCap(@Nullable ICapabilityProvider provider)
    {
       return CapabilityUtils.fetchCapability(provider, PantographCap.CAPABILITY, PantographCap.DEFAULT_FACING);
    }

    static LazyOptional<IPantographCap> getCap(World worldIn, @Nullable BlockPos pos)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return IPantographCap.getCap(tileentity);
    } 
}
