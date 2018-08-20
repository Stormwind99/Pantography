package com.wumple.pantography.capability;

import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import com.wumple.util.adapter.IThing;
import com.wumple.util.capability.thing.IThingCap;
import com.wumple.util.container.capabilitylistener.CapabilityUtils;
import com.wumple.util.tooltip.ITooltipProvider;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface IPantographCap extends IInventory, INBTSerializable<NBTBase>, IThingCap<IThing>, ITooltipProvider
{
    // The {@link Capability} instance
    @CapabilityInject(IPantographCap.class)
    public static final Capability<IPantographCap> CAPABILITY = null;
    public static final EnumFacing DEFAULT_FACING = null;
    
    default boolean hasCustomName() { return false; }
    default String getName() { return "none"; }
    
    boolean isActive();
    
    void onBlockBreak(World worldIn, BlockPos pos);

    void onRightBlockClicked(PlayerInteractEvent.RightClickBlock event);
    
    IItemHandlerModifiable handler();
    
    void onCraftMatrixChanged(IInventory inventoryIn, BiConsumer<Integer, ItemStack> updater);
    
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
