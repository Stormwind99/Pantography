package com.wumple.pantography.capability.container;

import com.wumple.pantography.capability.IPantographCap;
import com.wumple.util.capability.CapabilityUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandlerPantograph implements IGuiHandler
{
    public static int myGuiID = 1;
    
    protected IPantographCap getCap(World world, int x, int y, int z)
    {
        return CapabilityUtils.fetchCapability(world, new BlockPos(x, y, z), IPantographCap.CAPABILITY, IPantographCap.DEFAULT_FACING);
    }
    
    public int getMyGuiID()
    {
        return myGuiID;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        IPantographCap cap = getCap(world, x, y, z);
        return (cap != null) ? new ContainerPantograph(player.inventory, cap) : null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        IPantographCap cap = getCap(world, x, y, z);
        return (cap != null) ? new GuiPantograph(player.inventory, cap) : null;
    }

}