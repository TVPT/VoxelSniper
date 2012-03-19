/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;

/**
 *
 * @author Voxel
 */
public class OceanSelection extends Ocean {

    protected boolean sel = true;
    
    public OceanSelection() {
        name = "Ocean Selection";
    }

    @Override
    public void arrow(vSniper v) {
        oceanSelection(v);
    }

    @Override
    public void powder(vSniper v) {
        oceanSelection(v);
    }

    @Override
    public void info(vMessage vm) {
            vm.brushName(name);
    }

    public void oceanSelection(vSniper v) {
        if (sel) {
            h = new vUndo(tb.getWorld().getName());
            oceanator(v);
            v.hashUndo.put(v.hashEn, h);
            v.hashEn++;
            s1x = tb.getX();
            s1z = tb.getZ();
            v.p.sendMessage(ChatColor.DARK_PURPLE + "Chunk one selected");
            sel = !sel;
        } else {
            v.p.sendMessage(ChatColor.DARK_PURPLE + "Chunk two selected");
            h = new vUndo(tb.getWorld().getName());
            oceanator(v);
            v.hashUndo.put(v.hashEn, h);
            v.hashEn++;
            s2x = tb.getX();
            s2z = tb.getZ();
            oceanate(v, ((s1x <= s2x) ? s1x : s2x), ((s2x >= s1x) ? s2x : s1x), ((s1z <= s2z) ? s1z : s2z), ((s2z >= s1z) ? s2z : s1z));
            sel = !sel;
        }
    }

    public void oceanate(vSniper v, int lowx, int highx, int lowz, int highz) {
        h = new vUndo(tb.getWorld().getName());
        for (int x = lowx; x <= highx; x += 16) {
            tb = setX(tb, x);
            for (int z = lowz; z <= highz; z += 16) {
                tb = setZ(tb, z);
                oceanator(v);
            }
        }
        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }
}
