/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 *
 * @author Piotr
 */
public class ShellSet extends Brush {

    protected Block b = null;

    public ShellSet() {
        name = "Shell Set";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) { // Derp
        if (set(tb, v)) {
            v.owner().getPlayer().sendMessage(ChatColor.GRAY + "Point one");
        }
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        if (set(lb, v)) {
            v.owner().getPlayer().sendMessage(ChatColor.GRAY + "Point one");
        }
    }

    @Override
    public void info(vMessage vm) {
        b = null;
        vm.brushName(name);
        vm.size();
        vm.voxel();
        vm.replace();
    }

    private boolean set(Block bl, vData v) {
        if (b == null) {
            b = bl;
            return true;
        } else {
            if (!b.getWorld().getName().equals(bl.getWorld().getName())) {
                v.sendMessage(ChatColor.RED + "You selected points in different worlds!");
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
                v.sendMessage(ChatColor.RED + "Selection size above hardcoded limit, please use a smaller selection.");
            } else {
                int bId = v.voxelId;
                int brId = v.replaceId;
                ArrayList<Block> blocks = new ArrayList<Block>(((Math.abs(highx - lowx) * Math.abs(highz - lowz) * Math.abs(highy - lowy)) / 2));
                for (int y = lowy; y <= highy; y++) {
                    for (int x = lowx; x <= highx; x++) {
                        for (int z = lowz; z <= highz; z++) {
                            if (w.getBlockTypeIdAt(x, y, z) == brId) {
                                continue;
                            } else if (w.getBlockTypeIdAt(x + 1, y, z) == brId) {
                                continue;
                            } else if (w.getBlockTypeIdAt(x - 1, y, z) == brId) {
                                continue;
                            } else if (w.getBlockTypeIdAt(x, y, z + 1) == brId) {
                                continue;
                            } else if (w.getBlockTypeIdAt(x, y, z - 1) == brId) {
                                continue;
                            } else if (w.getBlockTypeIdAt(x, y + 1, z) == brId) {
                                continue;
                            } else if (w.getBlockTypeIdAt(x, y - 1, z) == brId) {
                                continue;
                            } else {
                                blocks.add(w.getBlockAt(x, y, z));
                            }
                        }
                    }
                }
                
                vUndo h = new vUndo(tb.getWorld().getName());
                for(Block blo : blocks) {
                    if(blo.getTypeId() != bId) {
                        h.put(blo);
                        blo.setTypeId(bId);
                    }
                }
                v.storeUndo(h);
                v.sendMessage(ChatColor.AQUA + "Shell complete.");
            }

            b = null;
            return false;
        }
    }
}
