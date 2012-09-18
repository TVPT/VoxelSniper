package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Piotr
 */
public class Voxel extends PerformBrush {
    private static int timesUsed = 0;

    /**
     * 
     */
    public Voxel() {
        this.setName("Voxel");
    }

    private final void voxel(final SnipeData v) {
        final int _bSize = v.getBrushSize();

        for (int _z = _bSize; _z >= -_bSize; _z--) {
            for (int _x = _bSize; _x >= -_bSize; _x--) {
                for (int _y = _bSize; _y >= -_bSize; _y--) {
                    this.current.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _z, this.getBlockPositionZ() + _y));
                }
            }
        }
        v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.voxel(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
    	this.voxel(v);
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.size();
    }
    
    @Override
    public final int getTimesUsed() {
    	return Voxel.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	Voxel.timesUsed = tUsed;
    }
}
