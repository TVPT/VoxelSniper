package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Voxel
 */
public class VoxelDisc extends PerformBrush {
    private static int timesUsed = 0;

    public VoxelDisc() {
        this.setName("Voxel Disc");
    }

    private final void disc(final SnipeData v) {
        final int bsize = v.getBrushSize();

        for (int x = bsize; x >= -bsize; x--) {
            for (int y = bsize; y >= -bsize; y--) {
                this.current.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() + y));
            }
        }
        v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.disc(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.setBlockPositionX(this.getLastBlock().getX());
        this.setBlockPositionY(this.getLastBlock().getY());
        this.setBlockPositionZ(this.getLastBlock().getZ());
        this.disc(v);
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.size();
    	// voxelMessage.voxel();
    }
    
    @Override
    public final int getTimesUsed() {
    	return VoxelDisc.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	VoxelDisc.timesUsed = tUsed;
    }
}
