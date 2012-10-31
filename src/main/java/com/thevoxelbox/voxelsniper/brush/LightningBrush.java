package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

/**
 * 
 * @author Gavjenks
 */
public class LightningBrush extends Brush {

    private static int timesUsed = 0;

    /**
     * 
     */
    public LightningBrush() {
        this.setName("Lightning");
    }

    @Override
    public final int getTimesUsed() {
        return LightningBrush.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.brushMessage("Lightning Brush!  Please use in moderation.");
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        LightningBrush.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.getWorld().strikeLightning(this.getTargetBlock().getLocation());
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.getWorld().strikeLightning(this.getTargetBlock().getLocation());
    }
}
