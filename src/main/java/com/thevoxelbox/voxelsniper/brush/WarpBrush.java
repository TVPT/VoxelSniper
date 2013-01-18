package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

import org.bukkit.Location;

/**
 * @author MikeMatrix
 */
public class WarpBrush extends Brush
{

    private static int timesUsed = 0;

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
    }

    /**
     *
     */
    public WarpBrush()
    {
        this.setName("Warp");
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        v.owner().getPlayer().teleport(this.getLastBlock().getLocation());
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        final Location _targetLocation = this.getLastBlock().getLocation();
        this.getWorld().strikeLightning(_targetLocation);
        v.owner().getPlayer().teleport(_targetLocation);
        this.getWorld().strikeLightning(_targetLocation);
    }

    @Override
    public final int getTimesUsed()
    {
        return timesUsed;
    }

    @Override
    public final void setTimesUsed(final int timesUsed)
    {
        WarpBrush.timesUsed = timesUsed;
    }

}
