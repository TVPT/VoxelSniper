package com.thevoxelbox.voxelsniper.event;

import com.thevoxelbox.voxelsniper.Sniper;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 */
public class SniperMaterialChangedEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private final Sniper sniper;
    private final Material originalMaterial;
    private final String originalInk;
    private final Material newMaterial;
    private final String newInk;
    private final String toolId;

    public SniperMaterialChangedEvent(Sniper sniper, String toolId, Material originalMaterial, String originalInk, Material newMaterial, String newInk)
    {
        this.sniper = sniper;
        this.originalMaterial = originalMaterial;
        this.originalInk = originalInk;
        this.newMaterial = newMaterial;
        this.newInk = newInk;
        this.toolId = toolId;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    public Material getOriginalMaterial()
    {
        return originalMaterial;
    }

    public String getOriginalInk()
    {
        return originalInk;
    }

    public Material getNewMaterial()
    {
        return newMaterial;
    }

    public String getNewInk()
    {
        return newInk;
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
