/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 *
 * @author psanker
 */
public class CleanSnow extends Brush {

    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        cleanSnow(v);
    }

    @Override
    public void powder(vSniper v) {
        arrow(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName("Clean Snow");
        vm.size();
    }
    double trueCircle = 0;

    @Override
    public void parameters(String[] par, vSniper v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.p.sendMessage(ChatColor.GOLD + "Clean Snow Brush Parameters:");
            v.p.sendMessage(ChatColor.AQUA + "/b cls true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b cls false will switch back. (false is default)");
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

    public void cleanSnow(vSniper v) {
        int bsize = v.brushSize;

        vUndo h = new vUndo(tb.getWorld().getName());

        double bpow = Math.pow(bsize + trueCircle, 2);

        for (int y = (bsize + 1) * 2; y >= 0; y--) {
            double ypow = Math.pow(y-bsize, 2);
            for (int x = (bsize + 1) * 2; x >= 0; x--) {
                double xpow = Math.pow(x-bsize, 2);
                for (int z = (bsize + 1) * 2; z >= 0; z--) {
                    if ((xpow + Math.pow(z-bsize, 2) + ypow) <= bpow) {
                        if ((clampY(bx + x - bsize, by + z - bsize, bz + y - bsize).getType() == Material.SNOW) && ((clampY(bx + x - bsize, by + z - bsize - 1, bz + y - bsize).getType() == Material.SNOW) || (clampY(bx + x - bsize, by + z - bsize - 1, bz + y - bsize).getType() == Material.AIR))) {
                            h.put(clampY(bx + x, by + z, bz + y));
                            setBlockIdAt(0, bx + x - bsize, by + z - bsize, bz + y - bsize);
                        }

                    }
                }
            }
        }
        
        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }
}
