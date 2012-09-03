package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Gavjenks
 * @author psanker
 */
public class Drain extends Brush {

    double trueCircle = 0;
    boolean disc = false;

    private static int timesUsed = 0;

    public Drain() {
        this.setName("Drain");
    }

    public final void drain(final SnipeData v) {
        final int bsize = v.getBrushSize();

        final Undo h = new Undo(this.getTargetBlock().getWorld().getName());

        final double bpow = Math.pow(bsize + this.trueCircle, 2);

        if (this.disc) {
            for (int x = bsize; x >= 0; x--) {
                final double xpow = Math.pow(x, 2);
                for (int y = bsize; y >= 0; y--) {
                    if ((xpow + Math.pow(y, 2)) <= bpow) {
                        if (this.getBlockIdAt(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() + y) == 8 || this.getBlockIdAt(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() + y) == 9
                                || this.getBlockIdAt(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() + y) == 10 || this.getBlockIdAt(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() + y) == 11) {
                            h.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() + y));
                            this.setBlockIdAt(0, this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() + y);
                        }

                        if (this.getBlockIdAt(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() - y) == 8 || this.getBlockIdAt(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() - y) == 9
                                || this.getBlockIdAt(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() - y) == 10 || this.getBlockIdAt(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() - y) == 11) {
                            h.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() - y));
                            this.setBlockIdAt(0, this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() - y);
                        }

                        if (this.getBlockIdAt(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() + y) == 8 || this.getBlockIdAt(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() + y) == 9
                                || this.getBlockIdAt(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() + y) == 10 || this.getBlockIdAt(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() + y) == 11) {
                            h.put(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() + y));
                            this.setBlockIdAt(0, this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() + y);
                        }

                        if (this.getBlockIdAt(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() - y) == 8 || this.getBlockIdAt(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() - y) == 9
                                || this.getBlockIdAt(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() - y) == 10 || this.getBlockIdAt(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() - y) == 11) {
                            h.put(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() - y));
                            this.setBlockIdAt(0, this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() - y);
                        }
                    }
                }
            }
        } else {
            for (int y = (bsize + 1) * 2; y >= 0; y--) {
                final double ypow = Math.pow(y - bsize, 2);
                for (int x = (bsize + 1) * 2; x >= 0; x--) {
                    final double xpow = Math.pow(x - bsize, 2);
                    for (int z = (bsize + 1) * 2; z >= 0; z--) {
                        if ((xpow + Math.pow(z - bsize, 2) + ypow) <= bpow) {
                            if (this.getBlockIdAt(this.getBlockPositionX() + x - bsize, this.getBlockPositionY() + z - bsize, this.getBlockPositionZ() + y - bsize) == 8
                                    || this.getBlockIdAt(this.getBlockPositionX() + x - bsize, this.getBlockPositionY() + z - bsize, this.getBlockPositionZ() + y - bsize) == 9
                                    || this.getBlockIdAt(this.getBlockPositionX() + x - bsize, this.getBlockPositionY() + z - bsize, this.getBlockPositionZ() + y - bsize) == 10
                                    || this.getBlockIdAt(this.getBlockPositionX() + x - bsize, this.getBlockPositionY() + z - bsize, this.getBlockPositionZ() + y - bsize) == 11) {
                                h.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + z, this.getBlockPositionZ() + y));
                                this.setBlockIdAt(0, this.getBlockPositionX() + x - bsize, this.getBlockPositionY() + z - bsize, this.getBlockPositionZ() + y - bsize);
                            }
                        }
                    }
                }
            }
        }

        v.storeUndo(h);
    }

    @Override
    public final int getTimesUsed() {
        return Drain.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();

        if (this.trueCircle == 0.5) {
            vm.custom(ChatColor.AQUA + "True circle mode ON");
        } else {
            vm.custom(ChatColor.AQUA + "True circle mode OFF");
        }

        if (this.disc) {
            vm.custom(ChatColor.AQUA + "Disc drain mode ON");
        } else {
            vm.custom(ChatColor.AQUA + "Disc drain mode OFF");
        }
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Drain Brush Parameters:");
            v.sendMessage(ChatColor.AQUA
                    + "/b drain true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b drain false will switch back. (false is default)");
            v.sendMessage(ChatColor.AQUA + "/b drain d -- toggles disc drain mode, as opposed to a ball drain mode");
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
            } else if (par[x].equalsIgnoreCase("d")) {
                if (this.disc) {
                    this.disc = false;
                    v.sendMessage(ChatColor.AQUA + "Disc drain mode OFF");
                } else {
                    this.disc = true;
                    v.sendMessage(ChatColor.AQUA + "Disc drain mode ON");
                }
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Drain.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.drain(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.arrow(v);
    }
}
