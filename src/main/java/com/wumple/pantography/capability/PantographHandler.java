package com.wumple.pantography.capability;

import com.wumple.megamap.ConfigManager;
import com.wumple.util.adapter.EntityThing;
import com.wumple.util.adapter.ItemStackThing;
import com.wumple.util.adapter.TileEntityThing;
import com.wumple.util.capability.targetcrafting.CapHandler;
import com.wumple.util.capability.targetcrafting.IContainerCraftingOwner;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PantographHandler extends CapHandler
{
    public static PantographHandler INSTANCE = new PantographHandler();

    public static PantographHandler getInstance()
    {
        return INSTANCE;
    }

    public PantographHandler()
    {
        super();
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    protected boolean isDebugging()
    {
        return ConfigManager.Debugging.debug.get();
    }
    
    protected LazyOptional<IPantographCap> getCap(ICapabilityProvider provider)
    {
        return IPantographCap.getCap(provider);
    }

    protected IContainerCraftingOwner getTCCap(ICapabilityProvider provider)
    {
        return null; // PORT getCap(provider);
    }
    
    /**
     * Attach the capability to relevant items.
     *
     * @param event
     *            The event
     */
    @SubscribeEvent
    public static void attachCapabilitiesTileEntity(AttachCapabilitiesEvent<TileEntity> event)
    {
        TileEntity entity = event.getObject();

        if (false) // PORT (ConfigHandler.pantographs.doesIt(entity))
        {
            PantographCapProvider provider = PantographCapProvider.createProvider(new TileEntityThing(entity));
            event.addCapability(PantographCap.ID, provider);
        }
    }

    @SubscribeEvent
    public static void attachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event)
    {
        Entity entity = event.getObject();

        if (false) // PORT (ConfigHandler.pantographs.doesIt(entity))
        {
            PantographCapProvider provider = PantographCapProvider.createProvider(new EntityThing(entity));
            event.addCapability(PantographCap.ID, provider);
        }
    }
    
    @SubscribeEvent
    public static void attachCapabilitiesItemStack(AttachCapabilitiesEvent<ItemStack> event)
    {
        ItemStack stack = event.getObject();

        if (false) // PORT (ConfigHandler.pantographs.doesIt(stack))
        {
            PantographCapProvider provider = PantographCapProvider.createProvider(new ItemStackThing(stack));
            event.addCapability(PantographCap.ID, provider);
        }
    }
    
    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        super.onRightClickBlock(event);
    }
    
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event)
    {
        super.onBlockBreak(event);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onDrawOverlay(final RenderGameOverlayEvent.Text e)
    {
        super.onDrawOverlay(e);
    }
}
