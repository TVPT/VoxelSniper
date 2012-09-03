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

    public Voxel() {
        this.setName("Voxel");
    }

    @Override
    public final int getTimesUsed() {
        return Voxel.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Voxel.timesUsed = tUsed;
    }

    public final void voxel(final SnipeData v) {
        final int bsize = v.getBrushSize();

        for (int z = bsize; z >= -bsize; z--) {
            for (int x = bsize; x >= -bsize; x--) {
                for (int y = bsize; y >= -bsize; y--) {
                    this.current.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + z, this.getBlockPositionZ() + y));
                }
            }
        }
        v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.voxel(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.arrow(v);
    }
}
