/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;

/**
 *
 * @author Voxel
 */
public class DiscFace extends PerformBrush {
    
    public DiscFace() {
        name = "Disc Face";
    }

    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        pre(v, tb.getFace(lb));
    }

    @Override
    public void powder(vSniper v) {
        bx = lb.getX();
        by = lb.getY();
        bz = lb.getZ();
        pre(v, tb.getFace(lb));
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
        //vm.voxel();
    }

    double trueCircle = 0;
    @Override
    public void parameters(String[] par, vSniper v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.p.sendMessage(ChatColor.GOLD + "Disc Face brush Parameters:");
            v.p.sendMessage(ChatColor.AQUA + "/b df true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
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

    private void pre(vSniper v, BlockFace bf) {
        if(bf == null) {
            return;
        }
        switch (bf) {
            case NORTH:
            case SOUTH:
                discNS(v);
                break;

            case EAST:
            case WEST:
                discEW(v);
                break;

            case UP:
            case DOWN:
                disc(v);
                break;

            default:
                break;
        }
    }

    public void disc(vSniper v) {
        int bsize = v.brushSize;

        double bpow = Math.pow(bsize + trueCircle, 2);
        for (int x = bsize; x >= 0; x--) {
            double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                    current.perform(clampY(bx + x, by, bz + y));
                    current.perform(clampY(bx + x, by, bz - y));
                    current.perform(clampY(bx - x, by, bz + y));
                    current.perform(clampY(bx - x, by, bz - y));
                }
            }
        }
        
        v.hashUndo.put(v.hashEn, current.getUndo());
        v.hashEn++;
    }

    public void discEW(vSniper v) {
        int bsize = v.brushSize;

        double bpow = Math.pow(bsize + trueCircle, 2);
        for (int x = bsize; x >= 0; x--) {
            double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                    current.perform(clampY(bx + x, by + y, bz));
                    current.perform(clampY(bx + x, by - y, bz));
                    current.perform(clampY(bx - x, by + y, bz));
                    current.perform(clampY(bx - x, by - y, bz));
                }
            }
        }
        
        v.hashUndo.put(v.hashEn, current.getUndo());
        v.hashEn++;
    }

    public void discNS(vSniper v) {
        int bsize = v.brushSize;

        double bpow = Math.pow(bsize + trueCircle, 2);
        for (int x = bsize; x >= 0; x--) {
            double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                    current.perform(clampY(bx, by + x, bz + y));
                    current.perform(clampY(bx, by + x, bz - y));
                    current.perform(clampY(bx, by - x, bz + y));
                    current.perform(clampY(bx, by - x, bz - y));
                }
            }
        }
        
        v.hashUndo.put(v.hashEn, current.getUndo());
        v.hashEn++;
    }
}
