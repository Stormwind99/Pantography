package com.wumple.pantography.capability;

import javax.annotation.Nullable;

import com.wumple.pantography.capability.container.ContainerPantograph;
import com.wumple.util.capability.targetcrafting.IContainerCraftingOwner;
import com.wumple.util.container.capabilitylistener.CapabilityUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IPantographCap extends IContainerCraftingOwner
{
    // The {@link Capability} instance
    @CapabilityInject(IPantographCap.class)
    public static final Capability<IPantographCap> CAPABILITY = null;
    public static final EnumFacing DEFAULT_FACING = null;
    
    default boolean hasCustomName() { return false; }
    default String getName() { return "none"; }
    
    boolean isActive();
    
    default public Container createContainer(InventoryPlayer inventory, EntityPlayer playerIn)
    {
        return new ContainerPantograph(inventory, this);
    }
    
    static IPantographCap getCap(@Nullable ICapabilityProvider provider)
    {
        return CapabilityUtils.fetchCapability(provider, PantographCap.CAPABILITY, PantographCap.DEFAULT_FACING);
    }

    static IPantographCap getCap(World worldIn, @Nullable BlockPos pos)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return IPantographCap.getCap(tileentity);
    } 
}
