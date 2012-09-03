package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * 
 * @author psanker
 */
public class CleanSnow extends Brush {

    double trueCircle = 0;

    private static int timesUsed = 0;

    public CleanSnow() {
        this.name = "Clean Snow";
    }

    public final void cleanSnow(final vData v) {
        final int bsize = v.brushSize;

        final vUndo h = new vUndo(this.tb.getWorld().getName());

        final double bpow = Math.pow(bsize + this.trueCircle, 2);

        for (int y = (bsize + 1) * 2; y >= 0; y--) {
            final double ypow = Math.pow(y - bsize, 2);
            for (int x = (bsize + 1) * 2; x >= 0; x--) {
                final double xpow = Math.pow(x - bsize, 2);
                for (int z = (bsize + 1) * 2; z >= 0; z--) {
                    if ((xpow + Math.pow(z - bsize, 2) + ypow) <= bpow) {
                        if ((this.clampY(this.bx + x - bsize, this.by + z - bsize, this.bz + y - bsize).getType() == Material.SNOW)
                                && ((this.clampY(this.bx + x - bsize, this.by + z - bsize - 1, this.bz + y - bsize).getType() == Material.SNOW) || (this
                                        .clampY(this.bx + x - bsize, this.by + z - bsize - 1, this.bz + y - bsize).getType() == Material.AIR))) {
                            h.put(this.clampY(this.bx + x, this.by + z, this.bz + y));
                            this.setBlockIdAt(0, this.bx + x - bsize, this.by + z - bsize, this.bz + y - bsize);
                        }

                    }
                }
            }
        }

        v.storeUndo(h);
    }

    @Override
    public final int getTimesUsed() {
        return CleanSnow.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        vm.size();
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Clean Snow Brush Parameters:");
            v.sendMessage(ChatColor.AQUA
                    + "/b cls true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b cls false will switch back. (false is default)");
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
        CleanSnow.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.cleanSnow(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.arrow(v);
    }
}
