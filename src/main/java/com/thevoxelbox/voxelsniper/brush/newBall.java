/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import com.thevoxelbox.voxelsniper.undo.vUndo;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 *
 * @author Piotr
 */
public class newBall extends Brush {

    protected vUndo h;
    protected double trueCircle = 0;
    protected int bsize;
    protected int voxelId;

    public newBall() {
        name = "Ball";
    }

    @Override
    public void parameters(String[] par, vSniper v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.p.sendMessage(ChatColor.GOLD + "Ball Brush Parameters:");
            v.p.sendMessage(ChatColor.AQUA + "/b b true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("true")) {
                trueCircle = 0.5;
                v.p.sendMessage(ChatColor.AQUA + "True circle mode ON.");
                continue;
            } else if (par[x].startsWith("false")) {
                trueCircle = 0;
                v.p.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
                continue;
            } else {
                v.p.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
        vm.voxel();
    }

    @Override
    protected void arrow(vSniper v) {
        h = new vUndo(w.getName());
        bsize = v.brushSize;
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        initArrow(v);
        double bpow = Math.pow(bsize + trueCircle, 2);
        for (int i = bsize; i >= -bsize; i--) {
            performArrow(clampY(bx, by + i, bz));
        }
        for (int i = bsize; i >= 1; i--) {
            performArrow(clampY(bx + i, by, bz));
            performArrow(clampY(bx - i, by, bz));
            performArrow(clampY(bx, by, bz + i));
            performArrow(clampY(bx, by, bz - i));
        }
        for (int y = bsize; y >= 1; y--) {
            for (int x = bsize; x >= 1; x--) {
                double xpow = Math.pow(x, 2);
                for (int z = bsize; z >= 1; z--) {
                    double zpow = Math.pow(z, 2);
                    if ((xpow + Math.pow(y, 2) + zpow) <= bpow) {
                        performArrow(clampY(bx + x, by + z, bz + y));
                        performArrow(clampY(bx + x, by + z, bz - y));
                        performArrow(clampY(bx - x, by + z, bz + y));
                        performArrow(clampY(bx - x, by + z, bz - y));
                        performArrow(clampY(bx + x, by - z, bz + y));
                        performArrow(clampY(bx + x, by - z, bz - y));
                        performArrow(clampY(bx - x, by - z, bz + y));
                        performArrow(clampY(bx - x, by - z, bz - y));
                    }
                }
            }
        }
        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }

    @Override
    protected void powder(vSniper v) {
        h = new vUndo(w.getName());
        bsize = v.brushSize;
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        initPowder(v);
        double bpow = Math.pow(bsize + trueCircle, 2);
        for (int i = bsize; i >= -bsize; i--) {
            performPowder(clampY(bx, by + i, bz));
        }
        for (int i = bsize; i >= 1; i--) {
            performPowder(clampY(bx + i, by, bz));
            performPowder(clampY(bx - i, by, bz));
            performPowder(clampY(bx, by, bz + i));
            performPowder(clampY(bx, by, bz - i));
        }
        for (int z = bsize; z >= 1; z--) {
            double zpow = Math.pow(z, 2);
            for (int x = bsize; x >= 1; x--) {
                double xpow = Math.pow(x, 2);
                for (int y = bsize; y >= 1; y--) {
                    if ((xpow + Math.pow(y, 2) + zpow) <= bpow) {
                        performPowder(clampY(bx + x, by + z, bz + y));
                        performPowder(clampY(bx + x, by + z, bz - y));
                        performPowder(clampY(bx - x, by + z, bz + y));
                        performPowder(clampY(bx - x, by + z, bz - y));
                        performPowder(clampY(bx + x, by - z, bz + y));
                        performPowder(clampY(bx + x, by - z, bz - y));
                        performPowder(clampY(bx - x, by - z, bz + y));
                        performPowder(clampY(bx - x, by - z, bz - y));
                    }
                }
            }
        }
        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }

    protected void initArrow(vSniper v) {
        voxelId = v.voxelId;
    }

    protected void initPowder(vSniper v) {
        voxelId = v.voxelId;
    }

    protected void performArrow(Block b) {
        if (b.getTypeId() != voxelId) {
            h.put(b);
            b.setTypeId(voxelId);
        }
    }

    protected void performPowder(Block b) {
        if (b.getTypeId() != voxelId) {
            h.put(b);
            b.setTypeId(voxelId);
        }
    }
}
