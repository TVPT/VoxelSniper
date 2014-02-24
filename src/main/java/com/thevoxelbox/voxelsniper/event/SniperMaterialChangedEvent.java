package com.thevoxelbox.voxelsniper.event;

import com.thevoxelbox.voxelsniper.Sniper;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.material.MaterialData;

/**
 *
 */
public class SniperMaterialChangedEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private final Sniper sniper;
    private final MaterialData originalMaterial;
    private final MaterialData newMaterial;
    private final String toolId;

    public SniperMaterialChangedEvent(Sniper sniper, String toolId, MaterialData originalMaterial, MaterialData newMaterial)
    {
        this.sniper = sniper;
        this.originalMaterial = originalMaterial;
        this.newMaterial = newMaterial;
        this.toolId = toolId;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    public MaterialData getOriginalMaterial()
    {
        return originalMaterial;
    }

    public MaterialData getNewMaterial()
    {
        return newMaterial;
    }

    public Sniper getSniper()
    {
        return sniper;
    }

    public String getToolId()
    {
        return toolId;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
}
