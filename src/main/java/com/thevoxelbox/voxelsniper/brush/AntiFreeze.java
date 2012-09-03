package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;

/**
 * 
 * @author Gavjenks
 */
public class AntiFreeze extends Brush {

    boolean bool = true;

    private static int timesUsed = 0;

    public AntiFreeze() {
        this.name = "AntiFreeze";
    }

    public final void AF(final vData v) {
        final int bsize = v.brushSize;

        final double bpow = Math.pow(bsize + 0.5, 2);
        for (int x = bsize; x >= 0; x--) {
            final double xpow = Math.pow(x, 2);
            for (int z = bsize; z >= 0; z--) {
                final double zpow = Math.pow(z, 2);
                if (xpow + zpow <= bpow) {
                    for (int y = 1; y < 127; y++) {
                        if (this.getBlockIdAt(this.bx + x, y, this.bz + z) == 79 && this.getBlockIdAt(this.bx + x, y + 1, this.bz + z) == 0) {
                            if (this.bool) {
                                this.setBlockIdAt(53, this.bx + x, y + 1, this.bz + z);
                                if (this.getBlockIdAt(this.bx + x, y, this.bz + z) == 53) {
                                    this.setBlockIdAt(9, this.bx + x, y, this.bz + z);
                                }
                                this.setBlockIdAt(53, this.bx + x, y + 1, this.bz - z);
                                if (this.getBlockIdAt(this.bx + x, y, this.bz - z) == 53) {
                                    this.setBlockIdAt(9, this.bx + x, y, this.bz - z);
                                }
                                this.setBlockIdAt(53, this.bx - x, y + 1, this.bz + z);
                                if (this.getBlockIdAt(this.bx - x, y, this.bz + z) == 53) {
                                    this.setBlockIdAt(9, this.bx - x, y, this.bz + z);
                                }
                                this.setBlockIdAt(53, this.bx - x, y + 1, this.bz - z);
                                if (this.getBlockIdAt(this.bx - x, y, this.bz - z) == 53) {
                                    this.setBlockIdAt(9, this.bx - x, y, this.bz - z);
                                }
                                this.w.getBlockAt(this.bx + x, y + 1, this.bz + z).setData((byte) 6);
                                this.w.getBlockAt(this.bx + x, y + 1, this.bz - z).setData((byte) 6);
                                this.w.getBlockAt(this.bx - x, y + 1, this.bz + z).setData((byte) 6);
                                this.w.getBlockAt(this.bx - x, y + 1, this.bz - z).setData((byte) 6);
                                this.setBlockIdAt(9, this.bx + x, y, this.bz + z);
                                this.setBlockIdAt(9, this.bx + x, y, this.bz - z);
                                this.setBlockIdAt(9, this.bx - x, y, this.bz + z);
                                this.setBlockIdAt(9, this.bx - x, y, this.bz - z);
                                this.setBlockIdAt(9, this.bx + x, y, this.bz + z);
                                this.setBlockIdAt(9, this.bx + x, y, this.bz - z);
                                this.setBlockIdAt(9, this.bx - x, y, this.bz + z);
                                this.setBlockIdAt(9, this.bx - x, y, this.bz - z);
                            } else {

                                this.setBlockIdAt(67, this.bx + x, y + 1, this.bz + z);
                                if (this.getBlockIdAt(this.bx + x, y, this.bz + z) == 67) {
                                    this.setBlockIdAt(9, this.bx + x, y, this.bz + z);
                                }
                                this.setBlockIdAt(67, this.bx + x, y + 1, this.bz - z);
                                if (this.getBlockIdAt(this.bx + x, y, this.bz - z) == 67) {
                                    this.setBlockIdAt(9, this.bx + x, y, this.bz - z);
                                }
                                this.setBlockIdAt(67, this.bx - x, y + 1, this.bz + z);
                                if (this.getBlockIdAt(this.bx - x, y, this.bz + z) == 67) {
                                    this.setBlockIdAt(9, this.bx - x, y, this.bz + z);
                                }
                                this.setBlockIdAt(67, this.bx - x, y + 1, this.bz - z);
                                if (this.getBlockIdAt(this.bx - x, y, this.bz - z) == 67) {
                                    this.setBlockIdAt(9, this.bx - x, y, this.bz - z);
                                }
                                this.w.getBlockAt(this.bx + x, y + 1, this.bz + z).setData((byte) 6);
                                this.w.getBlockAt(this.bx + x, y + 1, this.bz - z).setData((byte) 6);
                                this.w.getBlockAt(this.bx - x, y + 1, this.bz + z).setData((byte) 6);
                                this.w.getBlockAt(this.bx - x, y + 1, this.bz - z).setData((byte) 6);
                                this.setBlockIdAt(9, this.bx + x, y, this.bz + z);
                                this.setBlockIdAt(9, this.bx + x, y, this.bz - z);
                                this.setBlockIdAt(9, this.bx - x, y, this.bz + z);
                                this.setBlockIdAt(9, this.bx - x, y, this.bz - z);
                                this.setBlockIdAt(9, this.bx + x, y, this.bz + z); // double set, in case it tries to do wonky stuff just because it'w ice and
                                                                                   // it is a paint in
                                // the ass.
                                this.setBlockIdAt(9, this.bx + x, y, this.bz - z);
                                this.setBlockIdAt(9, this.bx - x, y, this.bz + z);
                                this.setBlockIdAt(9, this.bx - x, y, this.bz - z);

                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public final int getTimesUsed() {
        return AntiFreeze.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushMessage(ChatColor.GOLD
                + "Arrow overlays insible wood stairs, powder is cobble stairs.  Use whichever one you have less of in your build for easier undoing later.  This may ruin builds with ice as a structural component.  DOES NOT UNDO DIRECTLY.");
        vm.brushName(this.name);
        vm.size();
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        AntiFreeze.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.AF(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.bool = false;
        this.arrow(v);
        this.bool = true;
    }
}
