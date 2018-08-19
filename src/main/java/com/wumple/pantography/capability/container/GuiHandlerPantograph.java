package com.wumple.pantography.capability.container;

import com.wumple.pantography.capability.IPantographCap;
import com.wumple.util.container.capabilitylistener.CapabilityUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandlerPantograph implements IGuiHandler
{
    public static int myGuiID = 1;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        IPantographCap cap = CapabilityUtils.fetchCapability(tileEntity, IPantographCap.CAPABILITY, IPantographCap.DEFAULT_FACING);
        return (cap != null) ? new ContainerPantograph(player.inventory, cap) : null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        IPantographCap cap = CapabilityUtils.fetchCapability(tileEntity, IPantographCap.CAPABILITY, IPantographCap.DEFAULT_FACING);
        return (cap != null) ? new GuiPantograph(player.inventory, cap) : null;
    }

}