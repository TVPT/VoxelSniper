package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * 
 * @author Voxel
 */
public class Eraser extends Brush {

    protected String werasemode;

    private static int timesUsed = 0;

    public Eraser() {
        this.name = "Eraser";
    }

    public final void doerase(final vData v) {
        final int bsize = v.brushSize;

        final vUndo h = new vUndo(this.tb.getWorld().getName());
        int temp;
        for (int x = 2 * bsize; x >= 0; x--) {
            for (int y = 0; y <= 2 * bsize; y++) {
                for (int z = 2 * bsize; z >= 0; z--) {
                    temp = this.getBlockIdAt(this.bx - bsize + x, this.by - bsize + y, this.bz - bsize + z);
                    if (temp > 3 && temp != 12 && temp != 13) {
                        if (!(this.werasemode.equalsIgnoreCase("keep") && (temp == 8 || temp == 9))) {
                            h.put(this.clampY(this.bx - bsize + x, this.by - bsize + y, this.bz - bsize + z));
                            this.setBlockIdAt(0, this.bx - bsize + x, this.by - bsize + y, this.bz - bsize + z);
                        }
                    }
                }
            }
        }
        v.storeUndo(h);
    }

    @Override
    public final int getTimesUsed() {
        return Eraser.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        vm.size();
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Eraser.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.werasemode = "nuke";
        this.doerase(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.werasemode = "keep";
        this.doerase(v);
    }
}
