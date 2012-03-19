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
import org.bukkit.block.Block;

/**
 *
 * @author Voxel
 */
public class Canyon extends Brush {

    protected int yLevel = 10;
    protected vUndo m;

    public Canyon() {
        name = "Canyon";
    }

    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        bz = tb.getZ();

        canyon(w.getChunkAt(tb), v);
    }

    @Override
    public void powder(vSniper v) {
        bx = tb.getX();
        bz = tb.getZ();

        m = new vUndo(w.getChunkAt(tb).getWorld().getName());

        multiCanyon(w.getChunkAt(tb), v);
        multiCanyon(w.getChunkAt(clampY(bx + 16, 63, bz)), v);
        multiCanyon(w.getChunkAt(clampY(bx + 16, 63, bz + 16)), v);
        multiCanyon(w.getChunkAt(clampY(bx, 63, bz + 16)), v);
        multiCanyon(w.getChunkAt(clampY(bx - 16, 63, bz + 16)), v);
        multiCanyon(w.getChunkAt(clampY(bx - 16, 63, bz)), v);
        multiCanyon(w.getChunkAt(clampY(bx - 16, 63, bz - 16)), v);
        multiCanyon(w.getChunkAt(clampY(bx, 63, bz - 16)), v);
        multiCanyon(w.getChunkAt(clampY(bx + 16, 63, bz - 16)), v);

        v.hashUndo.put(v.hashEn, m);
        v.hashEn++;
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.custom(ChatColor.GREEN + "Shift Level set to " + yLevel);
    }

    @Override
    public void parameters(String[] par, vSniper v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.p.sendMessage(ChatColor.GREEN + "y[number] to set the Level to which the land will be shifted down");
        }
        if (par[1].startsWith("y")) {
            int i = Integer.parseInt(par[1].replace("y", ""));
            if (i < 10) {
                i = 10;
            } else if (i > 60) {
                i = 60;
            }
            yLevel = i;
            v.p.sendMessage(ChatColor.GREEN + "Shift Level set to " + yLevel);
        }
    }

    private void canyon(Chunk c, vSniper v) {
        int yy;

        vUndo h = new vUndo(c.getWorld().getName());

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                yy = yLevel;
                for (int y = 63; y < 128; y++) {
                    Block b = c.getBlock(x, y, z);
                    h.put(b);
                    Block bb = c.getBlock(x, yy, z);
                    h.put(bb);
                    bb.setTypeId(b.getTypeId(), false);
                    b.setTypeId(0);
                    yy++;
                }
                Block b = c.getBlock(x, 0, z);
                h.put(b);
                b.setTypeId(7);
                for (int y = 1; y < 10; y++) {
                    Block bb = c.getBlock(x, y, z);
                    h.put(bb);
                    bb.setTypeId(1);
                }
            }
        }

        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }

    protected void multiCanyon(Chunk c, vSniper v) {
        int yy;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                yy = yLevel;
                for (int y = 63; y < 128; y++) {
                    Block b = c.getBlock(x, y, z);
                    m.put(b);
                    Block bb = c.getBlock(x, yy, z);
                    m.put(bb);
                    bb.setTypeId(b.getTypeId(), false);
                    b.setTypeId(0);
                    yy++;
                }
                Block b = c.getBlock(x, 0, z);
                m.put(b);
                b.setTypeId(7);
                for (int y = 1; y < 10; y++) {
                    Block bb = c.getBlock(x, y, z);
                    m.put(bb);
                    bb.setTypeId(1);
                }
            }
        }
    }
}
