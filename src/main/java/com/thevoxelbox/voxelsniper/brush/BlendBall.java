/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import com.thevoxelbox.voxelsniper.undo.vUndo;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 *
 * BLEND BRUSHES SHOULD NOT USE PERFORMERS
 */
public class BlendBall extends Brush {

    protected String ablendmode = "exclude";
    protected String wblendmode = "exclude";

    public BlendBall() {
        name = "Blend Ball";
    }

    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        
        ablendmode = "include";
        bblend(v);
    }

    @Override
    public void powder(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        
        ablendmode = "exclude";
        bblend(v);
    }

    @Override
    public void info(vMessage vm) {
        /*if (!ablendmode.equalsIgnoreCase("exclude") && !ablendmode.equalsIgnoreCase("include")) {
            ablendmode = "exclude";
        }*/
        if (!wblendmode.equalsIgnoreCase("exclude") && !wblendmode.equalsIgnoreCase("include")) {
            wblendmode = "exclude";
        }
        vm.brushName(name);
        vm.size();
        vm.voxel();
        //vm.custom(ChatColor.BLUE + "Air Mode: " + ablendmode);
        vm.custom(ChatColor.BLUE + "Water Mode: " + wblendmode);
    }

    @Override
    public void parameters(String[] par, vSniper v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.p.sendMessage(ChatColor.GOLD + "Blend Ball Parameters:");
            //v.p.sendMessage(ChatColor.AQUA + "/b bb air -- toggle include or exclude (default) air");
            v.p.sendMessage(ChatColor.AQUA + "/b bb water -- toggle include or exclude (default) water");
            return;
        }
       /* if (par[1].equalsIgnoreCase("air")) {
            if (ablendmode.equalsIgnoreCase("exclude")){
            ablendmode="include";
            }
            else
            {
            ablendmode="exclude";
            }
            v.p.sendMessage(ChatColor.AQUA + "Air Mode: " + ablendmode);
            
            return;
        } */
        if (par[1].equalsIgnoreCase("water")) {
            if (wblendmode.equalsIgnoreCase("exclude")){
            wblendmode="include";
            }
            else
            {
            wblendmode="exclude";
            }
            v.p.sendMessage(ChatColor.AQUA + "Water Mode: " + wblendmode);
            
            return;
        }
    }

    public void bblend(vSniper v) {
        int bsize = v.brushSize;
        //int bId = v.voxelId;
        int[][][] oldmats = new int[2 * (bsize + 1) + 1][2 * (bsize + 1) + 1][2 * (bsize + 1) + 1]; //Array that holds the original materials plus a buffer
        int[][][] newmats = new int[2 * bsize + 1][2 * bsize + 1][2 * bsize + 1]; //Array that holds the blended materials
        int maxblock = 0;  //What is the highest material ID that is a block?

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

        //Find highest placeable block ID         
        for (int i = 0; i < Material.values().length; i++) {
            if (Material.values()[i].isBlock() && Material.values()[i].getId() > maxblock) {
                maxblock = Material.values()[i].getId();
            }
        }


        //Blend materials
        for (int x = 0; x <= 2 * bsize; x++) {
            for (int y = 0; y <= 2 * bsize; y++) {
                for (int z = 0; z <= 2 * bsize; z++) {
                    int[] matfreq = new int[maxblock + 1]; //Array that tracks frequency of materials neighboring given block
                    int modematcount = 0;
                    int modematid = 0;
                    boolean tiecheck = true;

                    for (int m = -1; m <= 1; m++) {
                        for (int n = -1; n <= 1; n++) {
                            for (int o = -1; o <= 1; o++) {
                                if (!(m == 0 && n == 0 && o == 0)) {
                                    matfreq[oldmats[x + 1 + m][y + 1 + n][z + 1 + o]]++;
                                }
                            }
                        }
                    }



                    //Find most common neighboring material.
                    for (int i = 0; i <= maxblock; i++) {
                        if (matfreq[i] > modematcount && !(ablendmode.equalsIgnoreCase("exclude") && i == 0)&& !(wblendmode.equalsIgnoreCase("exclude") && (i == 8||i==9))) {
                            modematcount = matfreq[i];
                            modematid = i;
                        }
                    }
                    //Make sure there'w not a tie for most common
                    for (int i = 0; i < modematid; i++) {
                        if (matfreq[i] == modematcount && !(ablendmode.equalsIgnoreCase("exclude") && i == 0)&& !(wblendmode.equalsIgnoreCase("exclude") && (i == 8||i==9))) {
                            tiecheck = false;
                        }
                    }

                    //Record most common neighbor material for this block
                    if (tiecheck) {
                        newmats[x][y][z] = modematid;
                    }
                }
            }
        }

        //Make the changes
        vUndo h = new vUndo(tb.getWorld().getName());
        double rpow = Math.pow(bsize + 1, 2);
        for (int x = 2 * bsize; x >= 0; x--) {
            double xpow = Math.pow(x - bsize - 1, 2);
            for (int y = 0; y <= 2 * bsize; y++) {
                double ypow = Math.pow(y - bsize - 1, 2);
                for (int z = 2 * bsize; z >= 0; z--) {
                    if (xpow + ypow + Math.pow(z - bsize - 1, 2) <= rpow) {
                        if (!(ablendmode.equalsIgnoreCase("exclude") && newmats[x][y][z] == 0)&& !(wblendmode.equalsIgnoreCase("exclude") && (newmats[x][y][z] == 8||newmats[x][y][z]==9))) {
                            if (getBlockIdAt(bx - bsize + x, by - bsize + y, bz - bsize + z) != newmats[x][y][z]) {
                                h.put(clampY(bx - bsize + x, by - bsize + y, bz - bsize + z));
                            }
                            setBlockIdAt(newmats[x][y][z], bx - bsize + x, by - bsize + y, bz - bsize + z);
                        }
                    }
                }
            }
        }
        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }
}
