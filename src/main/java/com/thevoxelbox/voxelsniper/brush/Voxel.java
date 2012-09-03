package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Piotr
 */
public class Voxel extends PerformBrush {

    private static int timesUsed = 0;

    public Voxel() {
        this.name = "Voxel";
    }

    @Override
    public final int getTimesUsed() {
        return Voxel.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        vm.size();
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Voxel.timesUsed = tUsed;
    }

    public final void voxel(final vData v) {
        final int bsize = v.brushSize;

        for (int z = bsize; z >= -bsize; z--) {
            for (int x = bsize; x >= -bsize; x--) {
                for (int y = bsize; y >= -bsize; y--) {
                    this.current.perform(this.clampY(this.bx + x, this.by + z, this.bz + y));
                }
            }
        }
        v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.voxel(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.arrow(v);
    }
}
