package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Voxel
 */
public class Ring extends PerformBrush {

    private double trueCircle = 0;
    private double innerSize = 0;

    private static int timesUsed = 0;

    public Ring() {
        this.setName("Ring");
    }

    @Override
    public final int getTimesUsed() {
        return Ring.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.custom(ChatColor.AQUA + "The inner radius is " + ChatColor.RED + this.innerSize);
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Ring Brush Parameters:");
            v.sendMessage(ChatColor.AQUA
                    + "/b ri true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b ri false will switch back. (false is default)");
            v.sendMessage(ChatColor.AQUA + "/b ri ir2.5 -- will set the inner radius to 2.5 units");
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
            } else if (par[x].startsWith("ir")) {
                try {
                    final double d = Double.parseDouble(par[x].replace("ir", ""));
                    this.innerSize = d;
                    v.sendMessage(ChatColor.AQUA + "The inner radius has been set to " + ChatColor.RED + this.innerSize);
                } catch (final Exception e) {
                    v.sendMessage(ChatColor.RED + "The parameters included are invalid");
                }
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    public final void ring(final vData v) {
        final int bsize = v.brushSize;
        final double outerpow = Math.pow(bsize + this.trueCircle, 2);
        final double innerpow = Math.pow(this.innerSize, 2);
        for (int x = bsize; x >= 0; x--) {
            final double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                final double ypow = Math.pow(y, 2);
                if ((xpow + ypow) <= outerpow && (xpow + ypow) >= innerpow) {
                    this.current.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() + y));
                    this.current.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() - y));
                    this.current.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() + y));
                    this.current.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() - y));
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Ring.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.ring(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getLastBlock().getX());
        this.setBlockPositionY(this.getLastBlock().getY());
        this.setBlockPositionZ(this.getLastBlock().getZ());
        this.ring(v);
    }
}
