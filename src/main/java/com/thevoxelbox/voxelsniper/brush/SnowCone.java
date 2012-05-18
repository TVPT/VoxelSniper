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
public class SnowCone extends Brush {

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        //delsnow(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        switch (getBlockIdAt(bx, by, bz)) {
            case 78:
                addsnow(v);
                break;
            default:
                //Move up one if target is not snowtile
                if (getBlockIdAt(bx, by + 1, bz) == 0) {
                    by++;
                    addsnow(v);
                } else {
                    v.owner().p.sendMessage(ChatColor.RED + "Error: Center block neither snow nor air.");
                }
                break;
        }
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName("Snow Cone");
    }
    double trueCircle = 0;

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Snow Cone Parameters:");
        }
    }

    public void addsnow(vData v) {
        int bsize;

        if (getBlockIdAt(bx, by, bz) == 0) {
            bsize = 0;
        } else {
            bsize = clampY(bx, by, bz).getData() + 1;
        }
        int[][] snowcone = new int[2 * bsize + 1][2 * bsize + 1]; //Will hold block IDs
        int[][] snowconedata = new int[2 * bsize + 1][2 * bsize + 1]; // Will hold data values for snowcone
        int[][] yoffset = new int[bsize * 2 + 1][bsize * 2 + 1];
        //prime the arrays

        for (int x = 0; x <= 2 * bsize; x++) {
            for (int z = 0; z <= 2 * bsize; z++) {
                boolean flag = true;
                for (int i = 0; i < 10; i++) { //overlay
                    if (flag) {
                        if ((getBlockIdAt(bx - bsize + x, by - i, bz - bsize + z) == 0 || getBlockIdAt(bx - bsize + x, by - i, bz - bsize + z) == 78) && getBlockIdAt(bx - bsize + x, by - i - 1, bz - bsize + z) != 0 && getBlockIdAt(bx - bsize + x, by - i - 1, bz - bsize + z) != 78) {
                            flag = false;
                            yoffset[x][z] = i;
                        }
                    }
                }
                snowcone[x][z] = getBlockIdAt(bx - bsize + x, by - yoffset[x][z], bz - bsize + z);
                snowconedata[x][z] = clampY(bx - bsize + x, by - yoffset[x][z], bz - bsize + z).getData();
            }
        }

        //figure out new snowheights
        for (int x = 0; x <= 2 * bsize; x++) {
            double xpow = Math.pow(x - bsize, 2);
            for (int z = 0; z <= 2 * bsize; z++) {
                double zpow = Math.pow(z - bsize, 2);
                double dist = Math.pow(xpow + zpow, .5); //distance from center of array
                int snowdata = bsize - (int) Math.ceil(dist);

                if (snowdata >= 0) { //no funny business
                    switch (snowdata) {
                        case 0:
                            if (snowcone[x][z] == 78) {
                                //snowconedata[x][z] = 1;
                            } else if (snowcone[x][z] == 0) {
                                snowcone[x][z] = 78;
                                snowconedata[x][z] = 0;
                            }
                            break;
                        case 7: // Turn largest snowtile into snowblock
                            if (snowcone[x][z] == 78) {
                                snowcone[x][z] = 80;
                                snowconedata[x][z] = 0;
                            }
                            break;
                        default: // Increase snowtile size, if smaller than target

                            if (snowdata > snowconedata[x][z]) {
                                switch (snowcone[x][z]) {
                                    case 0:
                                        snowconedata[x][z] = snowdata;
                                        snowcone[x][z] = 78;
                                    case 78:
                                        snowconedata[x][z] = snowdata;
                                        break;
                                    default:
                                        //v.sendMessage(ChatColor.RED+"Case: "+snowcone[x][z]);
                                        break;

                                }
                            } else if (yoffset[x][z] > 0 && snowcone[x][z] == 78) {
                                snowconedata[x][z]++;
                                if (snowconedata[x][z] == 7) {
                                    snowconedata[x][z] = 0;
                                    snowcone[x][z] = 80;
                                }
                            }
                            break;
                    }
                }
            }
        }
        vUndo h = new vUndo(tb.getWorld().getName());

        for (int x = 0; x <= 2 * bsize; x++) {
            for (int z = 0; z <= 2 * bsize; z++) {

                if (getBlockIdAt(bx - bsize + x, by - yoffset[x][z], bz - bsize + z) != snowcone[x][z] || clampY(bx - bsize + x, by - yoffset[x][z], bz - bsize + z).getData() != snowconedata[x][z]) {
                    h.put(clampY(bx - bsize + x, by - yoffset[x][z], bz - bsize + z));
                }
                setBlockIdAt(snowcone[x][z], bx - bsize + x, by - yoffset[x][z], bz - bsize + z);
                clampY(bx - bsize + x, by - yoffset[x][z], bz - bsize + z).setData((byte) snowconedata[x][z]);

            }
        }
        v.storeUndo(h);
    }
}
