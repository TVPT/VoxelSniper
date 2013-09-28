package com.thevoxelbox.voxelsniper.event;

import com.thevoxelbox.voxelsniper.Sniper;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 */
public class SniperBrushSizeChangedEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private final Sniper sniper;
    private final int originalSize;
    private final int newSize;

    public SniperBrushSizeChangedEvent(Sniper sniper, int originalSize, int newSize)
    {
        this.sniper = sniper;
        this.originalSize = originalSize;
        this.newSize = newSize;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    public int getOriginalSize()
    {
        return originalSize;
    }

    public int getNewSize()
    {
        return newSize;
    }

    public Sniper getSniper()
    {
        return sniper;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
}
