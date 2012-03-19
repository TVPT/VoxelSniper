/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import com.thevoxelbox.voxelsniper.undo.vUndo;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 *
 * @author Voxel
 */
public class Set extends PerformBrush {

    protected int i;
    protected Block b = null;
    protected vUndo h;

    public Set() {
        name = "Set";
    }

    @Override
    protected void arrow(vSniper v) { // Derp
        i = v.voxelId;
        if (set(tb, v)) {
            v.p.sendMessage(ChatColor.GRAY + "Point one");
        } else {
            v.hashUndo.put(v.hashEn, current.getUndo());
            v.hashEn++;
        }
    }

    @Override
    protected void powder(vSniper v) {
        i = v.voxelId;
        if (set(lb, v)) {
            v.p.sendMessage(ChatColor.GRAY + "Point one");
        } else {
            v.hashUndo.put(v.hashEn, current.getUndo()); //was still on h -Giltwist
            v.hashEn++;
        }
    }

    @Override
    public void info(vMessage vm) {
        b = null;
        vm.brushName(name);
    }

    @Override
    public void parameters(String[] par, vSniper v) {
        super.parameters(par, v);
    }

    private boolean set(Block bl, vSniper v) {
        if (b == null) {
            b = bl;
            return true;
        } else {
            if (!b.getWorld().getName().equals(bl.getWorld().getName())) {
                v.p.sendMessage(ChatColor.RED + "You selected points in different worlds!");
                b = null;
                return true;
            }
            int lowx = (b.getX() <= bl.getX()) ? b.getX() : bl.getX();
            int lowy = (b.getY() <= bl.getY()) ? b.getY() : bl.getY();
            int lowz = (b.getZ() <= bl.getZ()) ? b.getZ() : bl.getZ();
            int highx = (b.getX() >= bl.getX()) ? b.getX() : bl.getX();
            int highy = (b.getY() >= bl.getY()) ? b.getY() : bl.getY();
            int highz = (b.getZ() >= bl.getZ()) ? b.getZ() : bl.getZ();
            if (Math.abs(highx - lowx) * Math.abs(highz - lowz) * Math.abs(highy - lowy) > 5000000) {
                v.p.sendMessage(ChatColor.RED + "Selection size above hardcoded limit, please use a smaller selection.");
            } else {
                for (int y = lowy; y <= highy; y++) {
                    for (int x = lowx; x <= highx; x++) {
                        for (int z = lowz; z <= highz; z++) {
                            current.perform(clampY(x, y, z));
                        }
                    }
                }
            }

            b = null;
            return false;
        }
    }
}
