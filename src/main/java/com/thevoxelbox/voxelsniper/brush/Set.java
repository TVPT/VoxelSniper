package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Voxel
 */
public class Set extends PerformBrush {

    protected int i;
    protected Block b = null;

    private static int timesUsed = 0;

    public Set() {
        this.name = "Set";
    }

    @Override
    public final int getTimesUsed() {
        return Set.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        this.b = null;
        vm.brushName(this.name);
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        super.parameters(par, v);
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Set.timesUsed = tUsed;
    }

    private boolean set(final Block bl, final vData v) {
        if (this.b == null) {
            this.b = bl;
            return true;
        } else {
            if (!this.b.getWorld().getName().equals(bl.getWorld().getName())) {
                v.sendMessage(ChatColor.RED + "You selected points in different worlds!");
                this.b = null;
                return true;
            }
            final int lowx = (this.b.getX() <= bl.getX()) ? this.b.getX() : bl.getX();
            final int lowy = (this.b.getY() <= bl.getY()) ? this.b.getY() : bl.getY();
            final int lowz = (this.b.getZ() <= bl.getZ()) ? this.b.getZ() : bl.getZ();
            final int highx = (this.b.getX() >= bl.getX()) ? this.b.getX() : bl.getX();
            final int highy = (this.b.getY() >= bl.getY()) ? this.b.getY() : bl.getY();
            final int highz = (this.b.getZ() >= bl.getZ()) ? this.b.getZ() : bl.getZ();
            if (Math.abs(highx - lowx) * Math.abs(highz - lowz) * Math.abs(highy - lowy) > 5000000) {
                v.sendMessage(ChatColor.RED + "Selection size above hardcoded limit, please use a smaller selection.");
            } else {
                for (int y = lowy; y <= highy; y++) {
                    for (int x = lowx; x <= highx; x++) {
                        for (int z = lowz; z <= highz; z++) {
                            this.current.perform(this.clampY(x, y, z));
                        }
                    }
                }
            }

            this.b = null;
            return false;
        }
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) { // Derp
        this.i = v.voxelId;
        if (this.set(this.tb, v)) {
            v.sendMessage(ChatColor.GRAY + "Point one");
        } else {
            v.storeUndo(this.current.getUndo());
        }
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.i = v.voxelId;
        if (this.set(this.lb, v)) {
            v.sendMessage(ChatColor.GRAY + "Point one");
        } else {
            v.storeUndo(this.current.getUndo());
        }
    }
}
