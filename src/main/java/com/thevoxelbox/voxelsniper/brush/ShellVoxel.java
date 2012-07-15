/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;

/**
 * THIS BRUSH SHOULD NOT USE PERFORMERS
 *
 * @author Voxel
 */
public class ShellVoxel extends Brush {

    public ShellVoxel() {
        name = "Shell Voxel";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        vshell(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = lb.getX();
        by = lb.getY();
        bz = lb.getZ();
        vshell(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
        vm.voxel();
        vm.replace();
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Shell Voxel Parameters:");
        } else {
            v.sendMessage(ChatColor.RED + "Invalid parameter - see the info message for help.");
        }
    }

    public void vshell(vData v) {
        int bsize = v.brushSize;
        int bId = v.voxelId;
        int brId = v.replaceId;
        int[][][] oldmats = new int[2 * (bsize + 1) + 1][2 * (bsize + 1) + 1][2 * (bsize + 1) + 1]; //Array that holds the original materials plus a buffer
        int[][][] newmats = new int[2 * bsize + 1][2 * bsize + 1][2 * bsize + 1]; //Array that holds the hollowed materials

        //Log current materials into oldmats
        for (int x = 0; x <= 2 * (bsize + 1); x++) {
            for (int y = 0; y <= 2 * (bsize + 1); y++) {
                for (int z = 0; z <= 2 * (bsize + 1); z++) {
                    oldmats[x][y][z] = getBlockIdAt(bx - bsize - 1 + x, by - bsize - 1 + y, bz - bsize - 1 + z);
                }
            }
        }

        //Log current materials into newmats
        for (int x = 0; x <= 2 * bsize; x++) {
            for (int y = 0; y <= 2 * bsize; y++) {
                for (int z = 0; z <= 2 * bsize; z++) {
                    newmats[x][y][z] = oldmats[x + 1][y + 1][z + 1];
                }
            }
        }
        int temp;

        //Hollow Brush Area
        for (int x = 0; x <= 2 * bsize; x++) {
            for (int y = 0; y <= 2 * bsize; y++) {
                for (int z = 0; z <= 2 * bsize; z++) {
                    temp = 0;

                    if (oldmats[x + 1 + 1][y + 1][z + 1] == brId) {
                        temp++;
                    }
                    if (oldmats[x + 1 - 1][y + 1][z + 1] == brId) {
                        temp++;
                    }
                    if (oldmats[x + 1][y + 1 + 1][z + 1] == brId) {
                        temp++;
                    }
                    if (oldmats[x + 1][y + 1 - 1][z + 1] == brId) {
                        temp++;
                    }
                    if (oldmats[x + 1][y + 1][z + 1 + 1] == brId) {
                        temp++;
                    }
                    if (oldmats[x + 1][y + 1][z + 1 - 1] == brId) {
                        temp++;
                    }

                    if (temp == 0) {
                        newmats[x][y][z] = bId;
                    }
                }
            }
        }

        //Make the changes
        vUndo h = new vUndo(tb.getWorld().getName());

        for (int x = 2 * bsize; x >= 0; x--) {
            for (int y = 0; y <= 2 * bsize; y++) {
                for (int z = 2 * bsize; z >= 0; z--) {

                    if (getBlockIdAt(bx - bsize + x, by - bsize + y, bz - bsize + z) != newmats[x][y][z]) {
                        h.put(clampY(bx - bsize + x, by - bsize + y, bz - bsize + z));
                    }
                    setBlockIdAt(newmats[x][y][z], bx - bsize + x, by - bsize + y, bz - bsize + z);
                }
            }
        }
        v.storeUndo(h);

        v.owner().getPlayer().sendMessage(ChatColor.AQUA + "Shell complete.");
    }
}
