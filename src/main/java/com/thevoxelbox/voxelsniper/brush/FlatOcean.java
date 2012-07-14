/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;

/**
 *
 * @author GavJenks
 */
public class FlatOcean extends Brush {

    protected int yoLevel = 29;
    protected int ylLevel = 8;

    public FlatOcean() {
        name = "FlatOcean";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        bz = tb.getZ();

        flatOcean(w.getChunkAt(tb));
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        bz = tb.getZ();

        flatOcean(w.getChunkAt(tb));
        flatOcean(w.getChunkAt(clampY(bx + 16, 63, bz)));
        flatOcean(w.getChunkAt(clampY(bx + 16, 63, bz + 16)));
        flatOcean(w.getChunkAt(clampY(bx, 63, bz + 16)));
        flatOcean(w.getChunkAt(clampY(bx - 16, 63, bz + 16)));
        flatOcean(w.getChunkAt(clampY(bx - 16, 63, bz)));
        flatOcean(w.getChunkAt(clampY(bx - 16, 63, bz - 16)));
        flatOcean(w.getChunkAt(clampY(bx, 63, bz - 16)));
        flatOcean(w.getChunkAt(clampY(bx + 16, 63, bz - 16)));
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.custom(ChatColor.RED + "THIS BRUSH DOES NOT UNDO");
        vm.custom(ChatColor.GREEN + "Water level set to " + yoLevel);
        vm.custom(ChatColor.GREEN + "Ocean floor level set to " + ylLevel);
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {

        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GREEN + "yo[number] to set the Level to which the water will rise.");
            v.sendMessage(ChatColor.GREEN + "yl[number] to set the Level to which the ocean floor will rise.");
        }
        for (int x = 1; x < par.length; x++) {

            if (par[x].startsWith("yo")) {
                int i = Integer.parseInt(par[x].replace("yo", ""));
                if (i < ylLevel) {
                    i = ylLevel + 1;
                }
                yoLevel = i;
                v.sendMessage(ChatColor.GREEN + "Water Level set to " + yoLevel);
                continue;
            }
            else if (par[x].startsWith("yl")) {
                int i = Integer.parseInt(par[x].replace("yl", ""));
                if (i > yoLevel) {
                    i = yoLevel - 1;
                    if (i == 0) {
                        i = 1;
                        yoLevel = 2;
                    }
                }
                ylLevel = i;
                v.sendMessage(ChatColor.GREEN + "Ocean floor Level set to " + ylLevel);
                continue;
            }
        }
    }

    private void flatOcean(Chunk c) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 256; y++) {
                    if (y <= ylLevel) {
                        c.getBlock(x, y, z).setTypeId(3, true);
                    } else if (y <= yoLevel) {
                        c.getBlock(x, y, z).setTypeId(9, false);
                    } else {
                        c.getBlock(x, y, z).setTypeId(0, false);
                    }
                }
            }
        }
    }
}
