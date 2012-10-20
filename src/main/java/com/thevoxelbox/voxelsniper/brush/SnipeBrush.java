package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Snipe_Brush
 * @author Voxel
 */
public class SnipeBrush extends PerformBrush {
    private static int timesUsed = 0;

    /**
     * 
     */
    public SnipeBrush() {
        this.setName("Snipe");
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.current.perform(this.getTargetBlock());
        v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.current.perform(this.getLastBlock());
        v.storeUndo(this.current.getUndo());
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    }
    
    @Override
    public final int getTimesUsed() {
        return SnipeBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
        SnipeBrush.timesUsed = tUsed;
    }
}
