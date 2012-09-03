package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Voxel
 */
public class Eraser extends Brush {

    protected String werasemode;

    private static int timesUsed = 0;

    public Eraser() {
        this.setName("Eraser");
    }

    public final void doerase(final SnipeData v) {
        final int bsize = v.getBrushSize();

        final Undo h = new Undo(this.getTargetBlock().getWorld().getName());
        int temp;
        for (int x = 2 * bsize; x >= 0; x--) {
            for (int y = 0; y <= 2 * bsize; y++) {
                for (int z = 2 * bsize; z >= 0; z--) {
                    temp = this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - bsize + y, this.getBlockPositionZ() - bsize + z);
                    if (temp > 3 && temp != 12 && temp != 13) {
                        if (!(this.werasemode.equalsIgnoreCase("keep") && (temp == 8 || temp == 9))) {
                            h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - bsize + y, this.getBlockPositionZ() - bsize + z));
                            this.setBlockIdAt(0, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - bsize + y, this.getBlockPositionZ() - bsize + z);
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
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Eraser.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.werasemode = "nuke";
        this.doerase(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.werasemode = "keep";
        this.doerase(v);
    }
}
