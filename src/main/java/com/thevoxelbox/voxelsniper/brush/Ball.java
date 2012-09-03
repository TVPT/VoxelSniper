package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Piotr
 */
public class Ball extends PerformBrush {

    private double trueCircle = 0;

    private static int timesUsed = 0;

    public Ball() {
        this.setName("Ball");
    }

    public final void ball(final SnipeData v) {
        final int bsize = v.getBrushSize();

        final double bpow = Math.pow(bsize + this.trueCircle, 2);
        double zpow;
        double xpow;

        this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()));

        for (int i = 1; i <= bsize; i++) {
            this.current.perform(this.clampY(this.getBlockPositionX() + i, this.getBlockPositionY(), this.getBlockPositionZ()));
            this.current.perform(this.clampY(this.getBlockPositionX() - i, this.getBlockPositionY(), this.getBlockPositionZ()));
            this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + i, this.getBlockPositionZ()));
            this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - i, this.getBlockPositionZ()));
            this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ() + i));
            this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ() - i));
        }

        for (int i = 1; i <= bsize; i++) {
            zpow = Math.pow(i, 2);
            for (int j = 1; j <= bsize; j++) {
                if (zpow + Math.pow(j, 2) <= bpow) {
                    this.current.perform(this.clampY(this.getBlockPositionX() + i, this.getBlockPositionY(), this.getBlockPositionZ() + j));
                    this.current.perform(this.clampY(this.getBlockPositionX() + i, this.getBlockPositionY(), this.getBlockPositionZ() - j));
                    this.current.perform(this.clampY(this.getBlockPositionX() - i, this.getBlockPositionY(), this.getBlockPositionZ() + j));
                    this.current.perform(this.clampY(this.getBlockPositionX() - i, this.getBlockPositionY(), this.getBlockPositionZ() - j));
                    this.current.perform(this.clampY(this.getBlockPositionX() + i, this.getBlockPositionY() + j, this.getBlockPositionZ()));
                    this.current.perform(this.clampY(this.getBlockPositionX() + i, this.getBlockPositionY() - j, this.getBlockPositionZ()));
                    this.current.perform(this.clampY(this.getBlockPositionX() - i, this.getBlockPositionY() + j, this.getBlockPositionZ()));
                    this.current.perform(this.clampY(this.getBlockPositionX() - i, this.getBlockPositionY() - j, this.getBlockPositionZ()));
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + i, this.getBlockPositionZ() + j));
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + i, this.getBlockPositionZ() - j));
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - i, this.getBlockPositionZ() + j));
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - i, this.getBlockPositionZ() - j));
                }
            }
        }

        for (int z = 1; z <= bsize; z++) {
            zpow = Math.pow(z, 2);
            for (int x = 1; x <= bsize; x++) {
                xpow = Math.pow(x, 2);
                for (int y = 1; y <= bsize; y++) {
                    if ((xpow + Math.pow(y, 2) + zpow) <= bpow) {
                        this.current.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z));
                        this.current.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() - z));
                        this.current.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z));
                        this.current.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() - z));
                        this.current.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ() + z));
                        this.current.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ() - z));
                        this.current.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ() + z));
                        this.current.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ() - z));
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
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
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
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.ball(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.arrow(v);
    }
}
