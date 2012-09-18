package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.PaintingWrapper;
import com.thevoxelbox.voxelsniper.SnipeData;

/**
 * Painting scrolling Brush
 * 
 * @author Voxel
 */
public class Painting extends Brush {
    private static int timesUsed = 0;

    /**
     * 
     */
    public Painting() {
        this.setName("Painting");
    }

    /**
     * Scroll painting forward
     * 
     * @param v
     *            Sniper caller
     */
    @Override
    protected final void arrow(final SnipeData v) {
        PaintingWrapper.paint(v.owner().getPlayer(), true, false, 0);
    }

    /**
     * Scroll painting backwards
     * 
     * @param v
     *            Sniper caller
     */
    @Override
    protected final void powder(final SnipeData v) {
        PaintingWrapper.paint(v.owner().getPlayer(), true, true, 0);
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    }
    
    @Override
    public final int getTimesUsed() {
    	return Painting.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	Painting.timesUsed = tUsed;
    }
}
