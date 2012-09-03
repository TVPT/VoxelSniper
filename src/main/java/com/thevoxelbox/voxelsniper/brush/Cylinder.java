package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Kavutop
 */
public class Cylinder extends PerformBrush {

    /**
     * The starring Y position At the bottom of the cylinder
     */
    protected int st;
    /**
     * End Y position At the top of the cylinder
     */
    protected int en;

    double trueCircle = 0;

    private static int timesUsed = 0;

    public Cylinder() {
        this.setName("Cylinder");
    }

    public final void cylinder(final vData v) {
        this.st = this.getBlockPositionY() + v.cCen;
        this.en = this.getBlockPositionY() + v.voxelHeight + v.cCen;
        if (this.en < this.st) {
            this.en = this.st;
        }
        if (this.st < 0) {
            this.st = 0;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
        } else if (this.st > 127) {
            this.st = 127;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
        }
        if (this.en < 0) {
            this.en = 0;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
        } else if (this.en > 127) {
            this.en = 127;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
        }
        final int bsize = v.brushSize;

        final double bpow = Math.pow(bsize + this.trueCircle, 2);

        for (int z = this.en; z >= this.st; z--) {
            for (int x = bsize; x >= 0; x--) {
                final double xpow = Math.pow(x, 2);
                for (int y = bsize; y >= 0; y--) {
                    if ((xpow + Math.pow(y, 2)) <= bpow) {
                        this.current.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() + y));
                        this.current.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() - y));
                        this.current.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() + y));
                        this.current.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() - y));
                    }
                }
            }
        }
        v.storeUndo(this.current.getUndo());
    }

    @Override
    public final int getTimesUsed() {
        return Cylinder.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.height();
        vm.center();
        // vm.voxel();
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Cylinder Brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b c h[number] -- set the cylinder v.voxelHeight.  Default is 1.");
            v.sendMessage(ChatColor.DARK_AQUA
                    + "/b c true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
            v.sendMessage(ChatColor.DARK_BLUE
                    + "/b c c[number] -- set the origin of the cylinder compared to the target block. Positive numbers will move the cylinder upward, negative will move it downward.");
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
            } else if (par[x].startsWith("h")) {
                v.voxelHeight = (int) Double.parseDouble(par[x].replace("h", ""));
                v.sendMessage(ChatColor.AQUA + "Cylinder v.voxelHeight set to: " + v.voxelHeight);
                continue;
            } else if (par[x].startsWith("c")) {
                v.cCen = (int) Double.parseDouble(par[x].replace("c", ""));
                v.sendMessage(ChatColor.AQUA + "Cylinder origin set to: " + v.cCen);
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Cylinder.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.cylinder(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getLastBlock().getX());
        this.setBlockPositionY(this.getLastBlock().getY());
        this.setBlockPositionZ(this.getLastBlock().getZ());
        this.cylinder(v);
    }
}
