package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Voxel
 */
public class VoxelDisc extends PerformBrush {

    private static int timesUsed = 0;

    public VoxelDisc() {
        this.name = "Voxel Disc";
    }

    public final void disc(final vData v) {
        final int bsize = v.brushSize;

        for (int x = bsize; x >= -bsize; x--) {
            for (int y = bsize; y >= -bsize; y--) {
                this.current.perform(this.clampY(this.bx + x, this.by, this.bz + y));
            }
        }
        v.storeUndo(this.current.getUndo());
    }

    @Override
    public final int getTimesUsed() {
        return VoxelDisc.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        vm.size();
        // vm.voxel();
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        VoxelDisc.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.disc(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.lb.getX();
        this.by = this.lb.getY();
        this.bz = this.lb.getZ();
        this.disc(v);
    }
}
