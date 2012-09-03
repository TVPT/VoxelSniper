package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vPainting;

/**
 * Painting scrolling Brush
 * 
 * @author Voxel
 */
public class Painting extends Brush {

    private static int timesUsed = 0;

    public Painting() {
        this.name = "Painting";
    }

    @Override
    public final int getTimesUsed() {
        return Painting.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Painting.timesUsed = tUsed;
    }

    /**
     * Scroll painting forward
     * 
     * @param v
     *            vSniper caller
     */
    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        vPainting.paint(v.owner().getPlayer(), true, false, 0);
    }

    /**
     * Scroll painting backwards
     * 
     * @param v
     *            vSniper caller
     */
    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        vPainting.paint(v.owner().getPlayer(), true, true, 0);
    }
}
