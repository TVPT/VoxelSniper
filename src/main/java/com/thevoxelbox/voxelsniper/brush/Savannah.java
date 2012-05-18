/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.undo.uBlock;
import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;

/**
 *
 * @author Gavjenks
 */
public class Savannah extends PerformBrush {

    public Savannah() {
        name = "Overlay (Topsoil Filling)";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        savannah(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        savannahTwo(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
        //vm.voxel();
    }
    int depth = 3;
    boolean allBlocks = false;

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Savannah brush has no parameters.");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            v.sendMessage(ChatColor.RED + "This brush takes no parameters.");
        }
    }

    public void savannah(vData v) {
        vUndo h = new vUndo(tb.getWorld().getName());
        int bsize = v.brushSize;

        int[][] memory = new int[bsize * 2 + 1][bsize * 2 + 1];
        double bpow = Math.pow(bsize + 0.5, 2);
        for (int z = bsize; z >= -bsize; z--) {
            for (int x = bsize; x >= -bsize; x--) {
                for (int y = by; y > 0; y--) { //start scanning from the height you clicked at
                    if (memory[x + bsize][z + bsize] != 1) { //if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= bpow) { //if inside of the column...
                            int check = getBlockIdAt(bx + x, y + 1, bz + z);
                            if (check == 8 || check == 9) { //must be underwater
                                for (int d = 0; (d < 1); d++) {
                                    if (clampY(bx + x, y, bz + z).getTypeId() == 12) {
                                        h.put(new uBlock(clampY(bx + x, y, bz + z)));
                                        clampY(bx + x, y, bz + z).setTypeId(3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        v.storeUndo(h);
    }

    public void savannahTwo(vData v) {
        int bsize = v.brushSize;
        vUndo h = new vUndo(tb.getWorld().getName());

        int[][] memory = new int[bsize * 2 + 1][bsize * 2 + 1];
        double bpow = Math.pow(bsize + 0.5, 2);
        for (int z = bsize; z >= -bsize; z--) {
            for (int x = bsize; x >= -bsize; x--) {
                for (int y = by; y > 0; y--) { //start scanning from the height you clicked at
                    if (memory[x + bsize][z + bsize] != 1) { //if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= bpow) { //if inside of the column...
                            if (getBlockIdAt(bx + x, y - 1, bz + z) != 0) { //if not a floating block (like one of Notch'w pools)
                                if (getBlockIdAt(bx + x, y + 1, bz + z) == 0) { //must start at surface... this prevents it filling stuff in if you click in a wall and it starts out below surface.
                                    if (!allBlocks) { //if the override parameter has not been activated, go to the switch that filters out manmade stuff.

                                        switch (getBlockIdAt(bx + x, y, bz + z)) {
                                            case 1:
                                            case 2:
                                            case 3:
                                            case 12:
                                            case 13:
                                            case 14: //These cases filter out any manufactured or refined blocks, any trees and leas, etc. that you don't want to mess with.
                                            case 15:
                                            case 16:
                                            case 24:
                                            case 48:
                                            case 82:
                                            case 49:
                                            case 78:
                                                for (int d = 0; (d < 1); d++) {
                                                    if (clampY(bx + x, y + 1, bz + z).getTypeId() == 0) {
                                                        if (Math.random() > 0.5) {
                                                            h.put(new uBlock(clampY(bx + x, y + 1, bz + z)));
                                                            clampY(bx + x, y + 1, bz + z).setTypeId(101);
                                                            if (Math.random() > 0.5) {
                                                                h.put(new uBlock(clampY(bx + x, y + 2, bz + z)));
                                                                clampY(bx + x, y + 2, bz + z).setTypeId(101);
                                                            }
                                                        }
                                                        memory[x + bsize][z + bsize] = 1; //stop it from checking any other blocks in this vertical 1x1 column.
                                                    }
                                                }
                                                break;

                                            default:
                                                break;
                                        }
                                    } else {
                                        for (int d = 0; (d < 1); d++) {
                                            if (clampY(bx + x, y + 1, bz + z).getTypeId() == 0) {
                                                //s.getBlockAt(bx + x, y - d, bz + z).setTypeId(60);
                                                h.put(new uBlock(clampY(bx + x, y + 1, bz + z)));
                                                clampY(bx + x, y + 1, bz + z).setTypeId(101);
                                                if (Math.random() > 0.5) {
                                                    h.put(new uBlock(clampY(bx + x, y + 2, bz + z)));
                                                    clampY(bx + x, y + 2, bz + z).setTypeId(101);
                                                }
                                                memory[x + bsize][z + bsize] = 1; //stop it from checking any other blocks in this vertical 1x1 column.
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        v.storeUndo(h);
    }
}
