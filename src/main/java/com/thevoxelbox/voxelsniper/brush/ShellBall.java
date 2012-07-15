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
public class ShellBall extends Brush {

    public ShellBall() {
        name = "Shell Ball";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        bshell(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = lb.getX();
        by = lb.getY();
        bz = lb.getZ();
        bshell(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
        vm.voxel();
        vm.replace();

    }

    //parameters isn't an abstract method, gilt.  You can just leave it out if there are none.
    public void bshell(vData v) {
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
        double rpow = Math.pow(bsize + 0.5, 2);
        for (int x = 2 * bsize; x >= 0; x--) {
            double xpow = Math.pow(x - bsize, 2);
            for (int y = 0; y <= 2 * bsize; y++) {
                double ypow = Math.pow(y - bsize, 2);
                for (int z = 2 * bsize; z >= 0; z--) {
                    if (xpow + ypow + Math.pow(z - bsize, 2) <= rpow) {

                        if (getBlockIdAt(bx - bsize + x, by - bsize + y, bz - bsize + z) != newmats[x][y][z]) {
                            h.put(clampY(bx - bsize + x, by - bsize + y, bz - bsize + z));
                        }
                        setBlockIdAt(newmats[x][y][z], bx - bsize + x, by - bsize + y, bz - bsize + z);
                    }
                }
            }
        }
        v.storeUndo(h);

        v.owner().getPlayer().sendMessage(ChatColor.AQUA + "Shell complete.");  //This is needed because most uses of this brush will not be sible to the sniper.
    }
}
