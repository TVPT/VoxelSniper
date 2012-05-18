/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
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
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        powder(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        if (first) {
            Chunk c = w.getChunkAt(tb);
            fx = c.getX();
            fz = c.getZ();
            v.sendMessage(ChatColor.YELLOW + "First point selected!");
            first = !first;
        } else {
            Chunk c = w.getChunkAt(tb);
            bx = c.getX();
            bz = c.getZ();
            v.sendMessage(ChatColor.YELLOW + "Second point selected!");
            selection(fx < bx ? fx : bx, fz < bz ? fz : bz, fx > bx ? fx : bx, fz > bz ? fz : bz, v);
            first = !first;
        }
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.custom(ChatColor.GREEN + "Shift Level set to " + yLevel);
    }

    private void selection(int lowX, int lowZ, int highX, int highZ, vData v) {
        m = new vUndo(w.getChunkAt(tb).getWorld().getName());

        for (int x = lowX; x <= highX; x++) {
            for (int z = lowZ; z <= highZ; z++) {
                multiCanyon(w.getChunkAt(x, z), v);
            }
        }

        v.storeUndo(m);
    }
}
