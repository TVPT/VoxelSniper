package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Voxel
 */
public class DiscFace extends PerformBrush {

    double trueCircle = 0;

    private static int timesUsed = 0;

    public DiscFace() {
        this.setName("Disc Face");
    }

    public final void disc(final vData v) {
        final int bsize = v.brushSize;

        final double bpow = Math.pow(bsize + this.trueCircle, 2);
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

        v.storeUndo(this.current.getUndo());
    }

    public final void discEW(final vData v) {
        final int bsize = v.brushSize;

        final double bpow = Math.pow(bsize + this.trueCircle, 2);
        for (int x = bsize; x >= 0; x--) {
            final double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                    this.current.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ()));
                    this.current.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ()));
                    this.current.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ()));
                    this.current.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ()));
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    public final void discNS(final vData v) {
        final int bsize = v.brushSize;

        final double bpow = Math.pow(bsize + this.trueCircle, 2);
        for (int x = bsize; x >= 0; x--) {
            final double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + x, this.getBlockPositionZ() + y));
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + x, this.getBlockPositionZ() - y));
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - x, this.getBlockPositionZ() + y));
                    this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - x, this.getBlockPositionZ() - y));
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    @Override
    public final int getTimesUsed() {
        return DiscFace.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.getName());
        vm.size();
        // vm.voxel();
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Disc Face brush Parameters:");
            v.sendMessage(ChatColor.AQUA
                    + "/b df true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
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
        DiscFace.timesUsed = tUsed;
    }

    private void pre(final vData v, final BlockFace bf) {
        if (bf == null) {
            return;
        }
        switch (bf) {
        case NORTH:
        case SOUTH:
            this.discNS(v);
            break;

        case EAST:
        case WEST:
            this.discEW(v);
            break;

        case UP:
        case DOWN:
            this.disc(v);
            break;

        default:
            break;
        }
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.pre(v, this.getTargetBlock().getFace(this.getLastBlock()));
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getLastBlock().getX());
        this.setBlockPositionY(this.getLastBlock().getY());
        this.setBlockPositionZ(this.getLastBlock().getZ());
        this.pre(v, this.getTargetBlock().getFace(this.getLastBlock()));
    }
}
