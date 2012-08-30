/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;

/**
 *
 * @author Gavjenks
 */
public class Overlay extends PerformBrush {

    public Overlay() {
        name = "Overlay (Topsoil Filling)";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        overlay(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        overlayTwo(v);
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
            v.sendMessage(ChatColor.GOLD + "Overlay brush parameters:");
            v.sendMessage(ChatColor.AQUA + "d[number] (ex:  d3) How many blocks deep you want to replace from the surface.");
            v.sendMessage(ChatColor.BLUE + "all (ex:  /b over all) Sets the brush to overlay over ALL materials, not just natural surface ones (will no longer ignore trees and buildings).  The parameter /some will set it back to default.");

            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("d")) {
                depth = Integer.parseInt(par[x].replace("d", ""));
                v.sendMessage(ChatColor.AQUA + "Depth set to " + depth);
                if (depth < 1) {
                    depth = 1;
                }
                continue;
            } else if (par[x].startsWith("all")) {
                allBlocks = true;
                v.sendMessage(ChatColor.BLUE + "Will overlay over any block." + depth);
                continue;
            } else if (par[x].startsWith("some")) {
                allBlocks = false;
                v.sendMessage(ChatColor.BLUE + "Will overlay only natural block types." + depth);
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    public void overlay(vData v) {
        int bsize = v.brushSize;

        int[][] memory = new int[bsize * 2 + 1][bsize * 2 + 1];
        double bpow = Math.pow(bsize + 0.5, 2);
        for (int z = bsize; z >= -bsize; z--) {
            for (int x = bsize; x >= -bsize; x--) {
                for (int y = by; y > 0; y--) { //start scanning from the height you clicked at
                    if (memory[x + bsize][z + bsize] != 1) { //if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= bpow) { //if inside of the column...
                            int check = getBlockIdAt(bx + x, y + 1, bz + z);
                            if (check == 0 || check == 8 || check == 9) { //must start at surface... this prevents it filling stuff in if you click in a wall and it starts out below surface.
                                if (!allBlocks) { //if the override parameter has not been activated, go to the switch that filters out manmade stuff.
                                    switch (getBlockIdAt(bx + x, y, bz + z)) {
                                        case 1:
                                        case 2:
                                        case 3:
                                        case 12:
                                        case 13:
                                        //case 14: //commented out the ores, since voxelbox uses these for structural materials.
                                        //case 15:
                                        //case 16:
                                        case 24://These cases filter out any manufactured or refined blocks, any trees and leas, etc. that you don't want to mess with.
                                        case 48:
                                        case 82:
                                        case 49:
                                        case 78:
                                            for (int d = 0; (d < depth); d++) {
                                                if (clampY(bx + x, y - d, bz + z).getTypeId() != 0) {
                                                    current.perform(clampY(bx + x, y - d, bz + z)); //fills down as many layers as you specify in parameters
                                                    memory[x + bsize][z + bsize] = 1; //stop it from checking any other blocks in this vertical 1x1 column.
                                                }
                                            }
                                            break;

                                        default:
                                            break;
                                    }
                                } else {
                                    for (int d = 0; (d < depth); d++) {
                                        if (clampY(bx + x, y - d, bz + z).getTypeId() != 0) {
                                            current.perform(clampY(bx + x, y - d, bz + z)); //fills down as many layers as you specify in parameters
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

        v.storeUndo(current.getUndo());
    }

    public void overlayTwo(vData v) {
        int bsize = v.brushSize;

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
                                                for (int d = 1; (d < depth + 1); d++) {
                                                    current.perform(clampY(bx + x, y + d, bz + z)); //fills down as many layers as you specify in parameters
                                                    memory[x + bsize][z + bsize] = 1; //stop it from checking any other blocks in this vertical 1x1 column.
                                                }
                                                break;

                                            default:
                                                break;
                                        }
                                    } else {
                                        for (int d = 1; (d < depth + 1); d++) {
                                            current.perform(clampY(bx + x, y + d, bz + z)); //fills down as many layers as you specify in parameters
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

        v.storeUndo(current.getUndo());
    }
    
    private static int timesUsed = 0;
	
    @Override
	public int getTimesUsed() {
		return timesUsed;
	}

	@Override
	public void setTimesUsed(int tUsed) {
		timesUsed = tUsed; 
	}
}
