package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;

/**
 * 
 * @author Gavjenks
 */
public class AntiFreeze extends Brush {

    boolean bool = true;

    private static int timesUsed = 0;

    public AntiFreeze() {
        this.setName("AntiFreeze");
    }

    public final void AF(final SnipeData v) {
        final int bsize = v.getBrushSize();

        final double bpow = Math.pow(bsize + 0.5, 2);
        for (int x = bsize; x >= 0; x--) {
            final double xpow = Math.pow(x, 2);
            for (int z = bsize; z >= 0; z--) {
                final double zpow = Math.pow(z, 2);
                if (xpow + zpow <= bpow) {
                    for (int y = 1; y < 127; y++) {
                        if (this.getBlockIdAt(this.getBlockPositionX() + x, y, this.getBlockPositionZ() + z) == 79 && this.getBlockIdAt(this.getBlockPositionX() + x, y + 1, this.getBlockPositionZ() + z) == 0) {
                            if (this.bool) {
                                this.setBlockIdAt(53, this.getBlockPositionX() + x, y + 1, this.getBlockPositionZ() + z);
                                if (this.getBlockIdAt(this.getBlockPositionX() + x, y, this.getBlockPositionZ() + z) == 53) {
                                    this.setBlockIdAt(9, this.getBlockPositionX() + x, y, this.getBlockPositionZ() + z);
                                }
                                this.setBlockIdAt(53, this.getBlockPositionX() + x, y + 1, this.getBlockPositionZ() - z);
                                if (this.getBlockIdAt(this.getBlockPositionX() + x, y, this.getBlockPositionZ() - z) == 53) {
                                    this.setBlockIdAt(9, this.getBlockPositionX() + x, y, this.getBlockPositionZ() - z);
                                }
                                this.setBlockIdAt(53, this.getBlockPositionX() - x, y + 1, this.getBlockPositionZ() + z);
                                if (this.getBlockIdAt(this.getBlockPositionX() - x, y, this.getBlockPositionZ() + z) == 53) {
                                    this.setBlockIdAt(9, this.getBlockPositionX() - x, y, this.getBlockPositionZ() + z);
                                }
                                this.setBlockIdAt(53, this.getBlockPositionX() - x, y + 1, this.getBlockPositionZ() - z);
                                if (this.getBlockIdAt(this.getBlockPositionX() - x, y, this.getBlockPositionZ() - z) == 53) {
                                    this.setBlockIdAt(9, this.getBlockPositionX() - x, y, this.getBlockPositionZ() - z);
                                }
                                this.getWorld().getBlockAt(this.getBlockPositionX() + x, y + 1, this.getBlockPositionZ() + z).setData((byte) 6);
                                this.getWorld().getBlockAt(this.getBlockPositionX() + x, y + 1, this.getBlockPositionZ() - z).setData((byte) 6);
                                this.getWorld().getBlockAt(this.getBlockPositionX() - x, y + 1, this.getBlockPositionZ() + z).setData((byte) 6);
                                this.getWorld().getBlockAt(this.getBlockPositionX() - x, y + 1, this.getBlockPositionZ() - z).setData((byte) 6);
                                this.setBlockIdAt(9, this.getBlockPositionX() + x, y, this.getBlockPositionZ() + z);
                                this.setBlockIdAt(9, this.getBlockPositionX() + x, y, this.getBlockPositionZ() - z);
                                this.setBlockIdAt(9, this.getBlockPositionX() - x, y, this.getBlockPositionZ() + z);
                                this.setBlockIdAt(9, this.getBlockPositionX() - x, y, this.getBlockPositionZ() - z);
                                this.setBlockIdAt(9, this.getBlockPositionX() + x, y, this.getBlockPositionZ() + z);
                                this.setBlockIdAt(9, this.getBlockPositionX() + x, y, this.getBlockPositionZ() - z);
                                this.setBlockIdAt(9, this.getBlockPositionX() - x, y, this.getBlockPositionZ() + z);
                                this.setBlockIdAt(9, this.getBlockPositionX() - x, y, this.getBlockPositionZ() - z);
                            } else {

                                this.setBlockIdAt(67, this.getBlockPositionX() + x, y + 1, this.getBlockPositionZ() + z);
                                if (this.getBlockIdAt(this.getBlockPositionX() + x, y, this.getBlockPositionZ() + z) == 67) {
                                    this.setBlockIdAt(9, this.getBlockPositionX() + x, y, this.getBlockPositionZ() + z);
                                }
                                this.setBlockIdAt(67, this.getBlockPositionX() + x, y + 1, this.getBlockPositionZ() - z);
                                if (this.getBlockIdAt(this.getBlockPositionX() + x, y, this.getBlockPositionZ() - z) == 67) {
                                    this.setBlockIdAt(9, this.getBlockPositionX() + x, y, this.getBlockPositionZ() - z);
                                }
                                this.setBlockIdAt(67, this.getBlockPositionX() - x, y + 1, this.getBlockPositionZ() + z);
                                if (this.getBlockIdAt(this.getBlockPositionX() - x, y, this.getBlockPositionZ() + z) == 67) {
                                    this.setBlockIdAt(9, this.getBlockPositionX() - x, y, this.getBlockPositionZ() + z);
                                }
                                this.setBlockIdAt(67, this.getBlockPositionX() - x, y + 1, this.getBlockPositionZ() - z);
                                if (this.getBlockIdAt(this.getBlockPositionX() - x, y, this.getBlockPositionZ() - z) == 67) {
                                    this.setBlockIdAt(9, this.getBlockPositionX() - x, y, this.getBlockPositionZ() - z);
                                }
                                this.getWorld().getBlockAt(this.getBlockPositionX() + x, y + 1, this.getBlockPositionZ() + z).setData((byte) 6);
                                this.getWorld().getBlockAt(this.getBlockPositionX() + x, y + 1, this.getBlockPositionZ() - z).setData((byte) 6);
                                this.getWorld().getBlockAt(this.getBlockPositionX() - x, y + 1, this.getBlockPositionZ() + z).setData((byte) 6);
                                this.getWorld().getBlockAt(this.getBlockPositionX() - x, y + 1, this.getBlockPositionZ() - z).setData((byte) 6);
                                this.setBlockIdAt(9, this.getBlockPositionX() + x, y, this.getBlockPositionZ() + z);
                                this.setBlockIdAt(9, this.getBlockPositionX() + x, y, this.getBlockPositionZ() - z);
                                this.setBlockIdAt(9, this.getBlockPositionX() - x, y, this.getBlockPositionZ() + z);
                                this.setBlockIdAt(9, this.getBlockPositionX() - x, y, this.getBlockPositionZ() - z);
                                this.setBlockIdAt(9, this.getBlockPositionX() + x, y, this.getBlockPositionZ() + z); // double set, in case it tries to do wonky stuff just because it'world ice and
                                                                                   // it is a paint in
                                // the ass.
                                this.setBlockIdAt(9, this.getBlockPositionX() + x, y, this.getBlockPositionZ() - z);
                                this.setBlockIdAt(9, this.getBlockPositionX() - x, y, this.getBlockPositionZ() + z);
                                this.setBlockIdAt(9, this.getBlockPositionX() - x, y, this.getBlockPositionZ() - z);

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
    public final void info(final Message vm) {
        vm.brushMessage(ChatColor.GOLD
                + "Arrow overlays insible wood stairs, powder is cobble stairs.  Use whichever one you have less of in your build for easier undoing later.  This may ruin builds with ice as a structural component.  DOES NOT UNDO DIRECTLY.");
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        AntiFreeze.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.AF(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.bool = false;
        this.arrow(v);
        this.bool = true;
    }
}
