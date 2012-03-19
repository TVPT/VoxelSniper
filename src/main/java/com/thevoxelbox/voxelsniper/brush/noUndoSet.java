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
 * @author Voxel
 */
public class noUndoSet extends Brush {

    protected int i;
    protected Block b = null;
    protected vUndo h;

    public noUndoSet() {
        name = "noUndo Set";
    }

    @Override
    protected void arrow(vSniper v) { // Derp
        i = v.voxelId;
        if (set(tb)) {
            v.p.sendMessage(ChatColor.GRAY + "Point one");
        }
    }

    @Override
    protected void powder(vSniper v) {
        i = v.voxelId;
        if (set(lb)) {
            v.p.sendMessage(ChatColor.GRAY + "Point one");
        }
    }

    @Override
    public void info(vMessage vm) {
        b = null;
        vm.brushName(name);
        vm.voxel();
    }

    @Override
    public void parameters(String[] par, vSniper v) {
        super.parameters(par, v);
    }

    private boolean set(Block bl) {
        if (b == null) {
            b = bl;
            return true;
        } else {
            int lowx = (b.getX() <= bl.getX()) ? b.getX() : bl.getX();
            int lowy = (b.getY() <= bl.getY()) ? b.getY() : bl.getY();
            int lowz = (b.getZ() <= bl.getZ()) ? b.getZ() : bl.getZ();
            int highx = (b.getX() >= bl.getX()) ? b.getX() : bl.getX();
            int highy = (b.getY() >= bl.getY()) ? b.getY() : bl.getY();
            int highz = (b.getZ() >= bl.getZ()) ? b.getZ() : bl.getZ();
            for (int y = lowy; y <= highy; y++) {
                for (int x = lowx; x <= highx; x++) {
                    for (int z = lowz; z <= highz; z++) {
                        perform(clampY(x, y, z));
                    }
                }
            }
            b = null;
            return false;
        }
    }

    protected void perform(Block bl) {
        if (bl.getTypeId() != i) {
            bl.setTypeId(i);
        }
    }
}
