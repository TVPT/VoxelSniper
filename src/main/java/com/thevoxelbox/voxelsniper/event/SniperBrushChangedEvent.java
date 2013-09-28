package com.thevoxelbox.voxelsniper.event;

import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 */
public class SniperBrushChangedEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private final Sniper sniper;
    private final IBrush originalBrush;
    private final IBrush newBrush;

    public SniperBrushChangedEvent(Sniper sniper, IBrush originalBrush, IBrush newBrush)
    {
        this.sniper = sniper;
        this.originalBrush = originalBrush;
        this.newBrush = newBrush;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    public IBrush getOriginalBrush()
    {
        return originalBrush;
    }

    public IBrush getNewBrush()
    {
        return newBrush;
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
