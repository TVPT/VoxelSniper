package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author psanker
 */
public class CleanSnow extends Brush {

    double trueCircle = 0;

    private static int timesUsed = 0;

    public CleanSnow() {
        this.setName("Clean Snow");
    }

    public final void cleanSnow(final SnipeData v) {
        final int bsize = v.getBrushSize();

        final Undo h = new Undo(this.getTargetBlock().getWorld().getName());

        final double bpow = Math.pow(bsize + this.trueCircle, 2);

        for (int y = (bsize + 1) * 2; y >= 0; y--) {
            final double ypow = Math.pow(y - bsize, 2);
            for (int x = (bsize + 1) * 2; x >= 0; x--) {
                final double xpow = Math.pow(x - bsize, 2);
                for (int z = (bsize + 1) * 2; z >= 0; z--) {
                    if ((xpow + Math.pow(z - bsize, 2) + ypow) <= bpow) {
                        if ((this.clampY(this.getBlockPositionX() + x - bsize, this.getBlockPositionY() + z - bsize, this.getBlockPositionZ() + y - bsize).getType() == Material.SNOW)
                                && ((this.clampY(this.getBlockPositionX() + x - bsize, this.getBlockPositionY() + z - bsize - 1, this.getBlockPositionZ() + y - bsize).getType() == Material.SNOW) || (this
                                        .clampY(this.getBlockPositionX() + x - bsize, this.getBlockPositionY() + z - bsize - 1, this.getBlockPositionZ() + y - bsize).getType() == Material.AIR))) {
                            h.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + z, this.getBlockPositionZ() + y));
                            this.setBlockIdAt(0, this.getBlockPositionX() + x - bsize, this.getBlockPositionY() + z - bsize, this.getBlockPositionZ() + y - bsize);
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
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
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
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.cleanSnow(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.arrow(v);
    }
}
