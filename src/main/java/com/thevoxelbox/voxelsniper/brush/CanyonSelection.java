/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import com.thevoxelbox.voxelsniper.undo.vUndo;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;

/**
 *
 * @author Voxel
 */
public class CanyonSelection extends Canyon {

    private boolean first = true;
    private int fx;
    private int fz;

    public CanyonSelection() {
        name = "Canyon Selection";
    }

    @Override
    public void arrow(vSniper v) {
        powder(v);
    }

    @Override
    public void powder(vSniper v) {
        if (first) {
            Chunk c = w.getChunkAt(tb);
            fx = c.getX();
            fz = c.getZ();
            v.p.sendMessage(ChatColor.YELLOW + "First point selected!");
            first = !first;
        } else {
            Chunk c = w.getChunkAt(tb);
            bx = c.getX();
            bz = c.getZ();
            v.p.sendMessage(ChatColor.YELLOW + "Second point selected!");
            selection(fx < bx ? fx : bx, fz < bz ? fz : bz, fx > bx ? fx : bx, fz > bz ? fz : bz, v);
            first = !first;
        }
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.custom(ChatColor.GREEN + "Shift Level set to " + yLevel);
    }

    private void selection(int lowX, int lowZ, int highX, int highZ, vSniper v) {
        m = new vUndo(w.getChunkAt(tb).getWorld().getName());

        for (int x = lowX; x <= highX; x++) {
            for (int z = lowZ; z <= highZ; z++) {
                multiCanyon(w.getChunkAt(x, z), v);
            }
        }

        v.hashUndo.put(v.hashEn, m);
        v.hashEn++;
    }
}
