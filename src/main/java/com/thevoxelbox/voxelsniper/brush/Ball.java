package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Piotr
 */
public class Ball extends PerformBrush {

    private double trueCircle = 0;

    private static int timesUsed = 0;

    public Ball() {
        this.name = "Ball";
    }

    public final void ball(final vData v) {
        final int bsize = v.brushSize;

        final double bpow = Math.pow(bsize + this.trueCircle, 2);
        double zpow;
        double xpow;

        this.current.perform(this.clampY(this.bx, this.by, this.bz));

        for (int i = 1; i <= bsize; i++) {
            this.current.perform(this.clampY(this.bx + i, this.by, this.bz));
            this.current.perform(this.clampY(this.bx - i, this.by, this.bz));
            this.current.perform(this.clampY(this.bx, this.by + i, this.bz));
            this.current.perform(this.clampY(this.bx, this.by - i, this.bz));
            this.current.perform(this.clampY(this.bx, this.by, this.bz + i));
            this.current.perform(this.clampY(this.bx, this.by, this.bz - i));
        }

        for (int i = 1; i <= bsize; i++) {
            zpow = Math.pow(i, 2);
            for (int j = 1; j <= bsize; j++) {
                if (zpow + Math.pow(j, 2) <= bpow) {
                    this.current.perform(this.clampY(this.bx + i, this.by, this.bz + j));
                    this.current.perform(this.clampY(this.bx + i, this.by, this.bz - j));
                    this.current.perform(this.clampY(this.bx - i, this.by, this.bz + j));
                    this.current.perform(this.clampY(this.bx - i, this.by, this.bz - j));
                    this.current.perform(this.clampY(this.bx + i, this.by + j, this.bz));
                    this.current.perform(this.clampY(this.bx + i, this.by - j, this.bz));
                    this.current.perform(this.clampY(this.bx - i, this.by + j, this.bz));
                    this.current.perform(this.clampY(this.bx - i, this.by - j, this.bz));
                    this.current.perform(this.clampY(this.bx, this.by + i, this.bz + j));
                    this.current.perform(this.clampY(this.bx, this.by + i, this.bz - j));
                    this.current.perform(this.clampY(this.bx, this.by - i, this.bz + j));
                    this.current.perform(this.clampY(this.bx, this.by - i, this.bz - j));
                }
            }
        }

        for (int z = 1; z <= bsize; z++) {
            zpow = Math.pow(z, 2);
            for (int x = 1; x <= bsize; x++) {
                xpow = Math.pow(x, 2);
                for (int y = 1; y <= bsize; y++) {
                    if ((xpow + Math.pow(y, 2) + zpow) <= bpow) {
                        this.current.perform(this.clampY(this.bx + x, this.by + y, this.bz + z));
                        this.current.perform(this.clampY(this.bx + x, this.by + y, this.bz - z));
                        this.current.perform(this.clampY(this.bx - x, this.by + y, this.bz + z));
                        this.current.perform(this.clampY(this.bx - x, this.by + y, this.bz - z));
                        this.current.perform(this.clampY(this.bx + x, this.by - y, this.bz + z));
                        this.current.perform(this.clampY(this.bx + x, this.by - y, this.bz - z));
                        this.current.perform(this.clampY(this.bx - x, this.by - y, this.bz + z));
                        this.current.perform(this.clampY(this.bx - x, this.by - y, this.bz - z));
                    }
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    @Override
    public final int getTimesUsed() {
        return Ball.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        vm.size();
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Ball Brush Parameters:");
            v.sendMessage(ChatColor.AQUA
                    + "/b b true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("true")) {
                this.trueCircle = 0.5;
                v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
                continue;
            } else if (par[x].startsWith("false")) {
                this.trueCircle = 0;
                v.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Ball.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.ball(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.arrow(v);
    }
}
