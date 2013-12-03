package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelpacket.common.VoxelMessage;
import com.thevoxelbox.voxelpacket.common.interfaces.IVoxelMessagePublisher;
import com.thevoxelbox.voxelpacket.common.interfaces.IVoxelMessageSubscriber;
import com.thevoxelbox.voxelpacket.exceptions.InvalidPacketDataException;
import com.thevoxelbox.voxelpacket.exceptions.InvalidShortcodeException;
import com.thevoxelbox.voxelpacket.exceptions.MissingEncoderException;
import com.thevoxelbox.voxelpacket.server.VoxelPacketServer;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.common.BrushInfo;
import com.thevoxelbox.voxelsniper.common.SendableItemInfo;
import com.thevoxelbox.voxelsniper.common.VoxelSniperCommon;
import com.thevoxelbox.voxelsniper.common.VoxelSniperPacket1LoginPayload;
import com.thevoxelbox.voxelsniper.common.VoxelSniperPacket2BrushUpdateRequest;
import com.thevoxelbox.voxelsniper.event.SniperBrushChangedEvent;
import com.thevoxelbox.voxelsniper.event.SniperBrushSizeChangedEvent;
import com.thevoxelbox.voxelsniper.event.SniperMaterialChangedEvent;
import com.thevoxelbox.voxelsniper.event.SniperReplaceMaterialChangedEvent;
import com.thevoxelbox.voxelsniper.util.BrushInfoFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

/**
 *
 */
public class VoxelSniperGuiListener implements Listener, IVoxelMessageSubscriber
{
    VoxelPacketServer voxelPacketServer = VoxelPacketServer.getInstance();
    private VoxelSniper plugin;

    public VoxelSniperGuiListener(VoxelSniper plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        plugin.getLogger().info("Prepare for play login payload.");
        Player player = event.getPlayer();

        int sniperLevel = 0;
        ArrayList<BrushInfo> availableBrushes = new ArrayList<BrushInfo>();
        BrushInfo currentBrush = null;

        Sniper sniperInstance = plugin.getSniperPermissionHelper().getSniperInstance(player);

        if (plugin.getSniperPermissionHelper().isSniper(player))
        {
            sniperLevel = 2;
        }
        else if (plugin.getSniperPermissionHelper().isLiteSniper(player))
        {
            sniperLevel = 1;
        }

        if (sniperInstance != null)
        {
            Map<String, IBrush> brushes = sniperInstance.getMyBrushes();
            for (IBrush brush : new HashSet<IBrush>(brushes.values()))
            {
                BrushInfo brushInfo = BrushInfoFactory.createBrushInfo(brush);
                availableBrushes.add(brushInfo);
                if (brush.getClass() == sniperInstance.getCurrent().getClass())
                {
                    currentBrush = brushInfo;
                }
            }
        }

        try
        {
            VoxelPacketServer.getInstance().sendMessageTo(player, VoxelSniperCommon.LOGIN_CHANNEL_SHORTCODE, new VoxelSniperPacket1LoginPayload(sniperLevel, availableBrushes), null);
        }
        catch (MissingEncoderException e)
        {
            plugin.getLogger().warning("No Encoder found to send Login Payload to " + player.getName() + " (" + e.getMessage() + ")");
        }
        catch (InvalidPacketDataException e)
        {
            plugin.getLogger().warning("Trying to send invalid packet data as a Login Payload to " + player.getName() + " (" + e.getMessage() + ")");
        }
        catch (InvalidShortcodeException e)
        {
            plugin.getLogger().warning("Invalid shortcode was supplied while trying to send the Login Payload to " + player.getName() + " (" + e.getMessage() + ")");
        }

        if (sniperLevel > 0 && sniperInstance != null)
        {
            SnipeData snipeData = sniperInstance.getData();
            SendableItemInfo material = new SendableItemInfo(snipeData.getVoxelId(), snipeData.getData());
            SendableItemInfo mask = new SendableItemInfo(snipeData.getReplaceId(), snipeData.getReplaceData());

            sendBrushUpdatePayload(player, new VoxelSniperPacket2BrushUpdateRequest(currentBrush, snipeData.getBrushSize(), material, mask));
        }
    }

    private void sendBrushUpdatePayload(Player player, VoxelSniperPacket2BrushUpdateRequest payload)
    {
        try
        {
            VoxelPacketServer.getInstance().sendMessageTo(player, VoxelSniperCommon.BRUSH_UPDATE_CHANNEL_SHORTCODE, payload, null);
        }
        catch (MissingEncoderException e)
        {
            plugin.getLogger().warning("No Encoder found to send Brush Update Payload to " + player.getName() + " (" + e.getMessage() + ")");
        }
        catch (InvalidPacketDataException e)
        {
            plugin.getLogger().warning("Trying to send invalid packet data as a Brush Update Payload to " + player.getName() + " (" + e.getMessage() + ")");
        }
        catch (InvalidShortcodeException e)
        {
            plugin.getLogger().warning("Invalid shortcode was supplied while trying to send the Brush Update Payload to " + player.getName() + " (" + e.getMessage() + ")");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSniperBrushChange(SniperBrushChangedEvent event)
    {
        SnipeData sniperData = event.getSniper().getData();
        IBrush brush = event.getNewBrush();
        boolean usesMask = (brush instanceof PerformBrush && ((PerformBrush) brush).getCurrentPerformer().isUsingReplaceMaterial());
        SendableItemInfo material = new SendableItemInfo(sniperData.getVoxelId(), sniperData.getData());
        SendableItemInfo mask = new SendableItemInfo(usesMask ? sniperData.getReplaceId() : -1, sniperData.getReplaceData());
        VoxelSniperPacket2BrushUpdateRequest payload = new VoxelSniperPacket2BrushUpdateRequest(BrushInfoFactory.createBrushInfo(brush), sniperData.getBrushSize(), material, mask);
        sendBrushUpdatePayload(event.getSniper().getPlayer(), payload);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSniperBrushSizeChange(SniperBrushSizeChangedEvent event)
    {
        SnipeData sniperData = event.getSniper().getData();
        IBrush brush = event.getSniper().getCurrent();
        boolean usesMask = (brush instanceof PerformBrush && ((PerformBrush) brush).getCurrentPerformer().isUsingReplaceMaterial());
        SendableItemInfo material = new SendableItemInfo(sniperData.getVoxelId(), sniperData.getData());
        SendableItemInfo mask = new SendableItemInfo(usesMask ? sniperData.getReplaceId() : -1, sniperData.getReplaceData());
        VoxelSniperPacket2BrushUpdateRequest payload = new VoxelSniperPacket2BrushUpdateRequest(BrushInfoFactory.createBrushInfo(brush), event.getNewSize(), material, mask);
        sendBrushUpdatePayload(event.getSniper().getPlayer(), payload);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSniperMaterialChange(SniperMaterialChangedEvent event)
    {
        SnipeData sniperData = event.getSniper().getData();
        IBrush brush = event.getSniper().getCurrent();
        boolean usesMask = (brush instanceof PerformBrush && ((PerformBrush) brush).getCurrentPerformer().isUsingReplaceMaterial());
        SendableItemInfo material = new SendableItemInfo(event.getNewMaterial().getItemTypeId(), event.getNewMaterial().getData());
        SendableItemInfo mask = new SendableItemInfo(usesMask ? sniperData.getReplaceId() : -1, sniperData.getReplaceData());
        VoxelSniperPacket2BrushUpdateRequest payload = new VoxelSniperPacket2BrushUpdateRequest(BrushInfoFactory.createBrushInfo(brush), sniperData.getBrushSize(), material, mask);
        sendBrushUpdatePayload(event.getSniper().getPlayer(), payload);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSniperReplaceMaterialChange(SniperReplaceMaterialChangedEvent event)
    {
        SnipeData sniperData = event.getSniper().getData();
        IBrush brush = event.getSniper().getCurrent();
        boolean usesMask = (brush instanceof PerformBrush && ((PerformBrush) brush).getCurrentPerformer().isUsingReplaceMaterial());
        SendableItemInfo material = new SendableItemInfo(sniperData.getVoxelId(), sniperData.getData());
        SendableItemInfo mask = new SendableItemInfo(usesMask ? event.getNewMaterial().getItemTypeId() : -1, event.getNewMaterial().getData());
        VoxelSniperPacket2BrushUpdateRequest payload = new VoxelSniperPacket2BrushUpdateRequest(BrushInfoFactory.createBrushInfo(brush), sniperData.getBrushSize(), material, mask);
        sendBrushUpdatePayload(event.getSniper().getPlayer(), payload);
    }

    @Override
    public void receiveMessage(IVoxelMessagePublisher iVoxelMessagePublisher, VoxelMessage voxelMessage)
    {
        if (voxelMessage.hasShortCode(VoxelSniperCommon.BRUSH_UPDATE_REQUEST_CHANNEL_SHORTCODE) && voxelMessage.dataInstanceOf(VoxelSniperPacket2BrushUpdateRequest.class))
        {
            VoxelSniperPacket2BrushUpdateRequest payload = voxelMessage.data();
            Sniper sniper = plugin.getSniperPermissionHelper().getSniperInstance(voxelMessage.getSender());


            if (payload.getSize() > -1)
            {
                SniperBrushSizeChangedEvent event = new SniperBrushSizeChangedEvent(sniper, sniper.getData().getBrushSize(), payload.getSize());
                sniper.getData().setBrushSize(payload.getSize());
                Bukkit.getPluginManager().callEvent(event);
                plugin.getLogger().info(sniper.getPlayer().getName() + " set Brushsize to " + payload.getSize());
                //sniper.getVoxelMessage().size();
            }

            if (payload.getMaterial() != null)
            {
                MaterialData originalMaterial = new MaterialData(sniper.getData().getVoxelId(), sniper.getData().getData());
                MaterialData newMaterial = new MaterialData(payload.getMaterial().getID(), (byte) payload.getMaterial().getMetaData());
                SniperMaterialChangedEvent event = new SniperMaterialChangedEvent(sniper, originalMaterial, newMaterial);
                sniper.getData().setVoxelId(payload.getMaterial().getID());
                sniper.getData().setData((byte) payload.getMaterial().getMetaData());
                Bukkit.getPluginManager().callEvent(event);
                plugin.getLogger().info(sniper.getPlayer().getName() + " set Material to " + newMaterial.toString());
                //sniper.getVoxelMessage().voxel();
                //sniper.getVoxelMessage().data();
            }

            if (payload.getMask() != null && !payload.noMask())
            {
                MaterialData originalMaterial = new MaterialData(sniper.getData().getVoxelId(), sniper.getData().getData());
                MaterialData newMaterial = new MaterialData(payload.getMask().getID(), (byte) payload.getMask().getMetaData());
                SniperReplaceMaterialChangedEvent event = new SniperReplaceMaterialChangedEvent(sniper, originalMaterial, newMaterial);
                sniper.getData().setReplaceId(payload.getMask().getID());
                sniper.getData().setReplaceData((byte) payload.getMask().getMetaData());
                Bukkit.getPluginManager().callEvent(event);
                plugin.getLogger().info(sniper.getPlayer().getName() + " set Replace to " + newMaterial.toString());
                //sniper.getVoxelMessage().replace();
                //sniper.getVoxelMessage().replaceData();
            }

            if (payload.getBrushInfo() != null)
            {
                IBrush brush = sniper.getCurrent();
                sniper.setBrush(new String[]{payload.getBrushInfo().getBrushCode()});
                SniperBrushChangedEvent event = new SniperBrushChangedEvent(sniper, brush, sniper.getCurrent());
                Bukkit.getPluginManager().callEvent(event);
                plugin.getLogger().info(sniper.getPlayer().getName() + " set Replace to " + sniper.getCurrent().getName());
            }
        }
    }

    @Override
    public void receiveMessageClassCastFailure(IVoxelMessagePublisher iVoxelMessagePublisher, VoxelMessage voxelMessage, ClassCastException e)
    {

    }
}
