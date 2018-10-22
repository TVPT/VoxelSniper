package com.thevoxelbox.voxelsniper.event;

import com.thevoxelbox.voxelsniper.Sniper;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;

/**
 *
 */
public class SniperReplaceMaterialChangedEvent extends SniperMaterialChangedEvent
{
    private static final HandlerList handlers = new HandlerList();

    public SniperReplaceMaterialChangedEvent(Sniper sniper, String toolId, Material originalMaterial, String originalInk, Material newMaterial, String newInk)
    {
        super(sniper, toolId, originalMaterial, originalInk, newMaterial, newInk);
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
}
