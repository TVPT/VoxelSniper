/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;

/**
 *
 * @author Gavjenks
 * Disc mode by psanker
 */
public class Drain extends Brush {

    double trueCircle = 0;
    boolean disc = false;

    public Drain() {
        name = "Drain";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        drain(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        arrow(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();

        if (trueCircle == 0.5) {
            vm.custom(ChatColor.AQUA + "True circle mode ON");
        } else {
            vm.custom(ChatColor.AQUA + "True circle mode OFF");
        }

        if (disc) {
            vm.custom(ChatColor.AQUA + "Disc drain mode ON");
        } else {
            vm.custom(ChatColor.AQUA + "Disc drain mode OFF");
        }
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Drain Brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b drain true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b drain false will switch back. (false is default)");
            v.sendMessage(ChatColor.AQUA + "/b drain d -- toggles disc drain mode, as opposed to a ball drain mode");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("true")) {
                trueCircle = 0.5;
                v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
                continue;
            } else if (par[x].startsWith("false")) {
                trueCircle = 0;
                v.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
                continue;
            } else if (par[x].equalsIgnoreCase("d")) {
                if (disc) {
                    disc = false;
                    v.sendMessage(ChatColor.AQUA + "Disc drain mode OFF");
                } else {
                    disc = true;
                    v.sendMessage(ChatColor.AQUA + "Disc drain mode ON");
                }
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    public void drain(vData v) {
        int bsize = v.brushSize;

        vUndo h = new vUndo(tb.getWorld().getName());

        double bpow = Math.pow(bsize + trueCircle, 2);

        if (disc) {
            for (int x = bsize; x >= 0; x--) {
                double xpow = Math.pow(x, 2);
                for (int y = bsize; y >= 0; y--) {
                    if ((xpow + Math.pow(y, 2)) <= bpow) {
                        if (getBlockIdAt(bx + x, by, bz + y) == 8 || getBlockIdAt(bx + x, by, bz + y) == 9 || getBlockIdAt(bx + x, by, bz + y) == 10 || getBlockIdAt(bx + x, by, bz + y) == 11) {
                            h.put(clampY(bx + x, by, bz + y));
                            setBlockIdAt(0, bx + x, by, bz + y);
                        }

                        if (getBlockIdAt(bx + x, by, bz - y) == 8 || getBlockIdAt(bx + x, by, bz - y) == 9 || getBlockIdAt(bx + x, by, bz - y) == 10 || getBlockIdAt(bx + x, by, bz - y) == 11) {
                            h.put(clampY(bx + x, by, bz - y));
                            setBlockIdAt(0, bx + x, by, bz - y);
                        }

                        if (getBlockIdAt(bx - x, by, bz + y) == 8 || getBlockIdAt(bx - x, by, bz + y) == 9 || getBlockIdAt(bx - x, by, bz + y) == 10 || getBlockIdAt(bx - x, by, bz + y) == 11) {
                            h.put(clampY(bx - x, by, bz + y));
                            setBlockIdAt(0, bx - x, by, bz + y);
                        }

                        if (getBlockIdAt(bx - x, by, bz - y) == 8 || getBlockIdAt(bx - x, by, bz - y) == 9 || getBlockIdAt(bx - x, by, bz - y) == 10 || getBlockIdAt(bx - x, by, bz - y) == 11) {
                            h.put(clampY(bx - x, by, bz - y));
                            setBlockIdAt(0, bx - x, by, bz - y);
                        }
                    }
                }
            }
        } else {
            for (int y = (bsize + 1) * 2; y >= 0; y--) {
                double ypow = Math.pow(y - bsize, 2);
                for (int x = (bsize + 1) * 2; x >= 0; x--) {
                    double xpow = Math.pow(x - bsize, 2);
                    for (int z = (bsize + 1) * 2; z >= 0; z--) {
                        if ((xpow + Math.pow(z - bsize, 2) + ypow) <= bpow) {
                            if (getBlockIdAt(bx + x - bsize, by + z - bsize, bz + y - bsize) == 8 || getBlockIdAt(bx + x - bsize, by + z - bsize, bz + y - bsize) == 9 || getBlockIdAt(bx + x - bsize, by + z - bsize, bz + y - bsize) == 10 || getBlockIdAt(bx + x - bsize, by + z - bsize, bz + y - bsize) == 11) {
                                h.put(clampY(bx + x, by + z, bz + y));
                                setBlockIdAt(0, bx + x - bsize, by + z - bsize, bz + y - bsize);
                            }
                        }
                    }
                }
            }
        }

        v.storeUndo(h);
    }
}
