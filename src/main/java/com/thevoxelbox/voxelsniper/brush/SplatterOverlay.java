/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import java.util.Random;
import org.bukkit.ChatColor;

/**
 *
 * @author Gavjenks
 * Splatterized by Giltwist
 */
public class SplatterOverlay extends PerformBrush {

    protected int seedpercent; // Chance block on first pass is made active
    protected int growpercent; // chance block on recursion pass is made active
    protected int splatterrecursions; // How many times you grow the seeds
    protected Random generator = new Random();

    public SplatterOverlay() {
        name = "Splatter Overlay";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        soverlay(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        soverlayTwo(v);
    }

    @Override
    public void info(vMessage vm) {
        if (seedpercent < 1 || seedpercent > 9999) {
            seedpercent = 1000;
        }
        if (growpercent < 1 || growpercent > 9999) {
            growpercent = 1000;
        }
        if (splatterrecursions < 1 || splatterrecursions > 10) {
            splatterrecursions = 3;
        }
        vm.brushName(name);
        vm.size();
        //vm.voxel();
        vm.custom(ChatColor.BLUE + "Seed percent set to: " + seedpercent / 100 + "%");
        vm.custom(ChatColor.BLUE + "Growth percent set to: " + growpercent / 100 + "%");
        vm.custom(ChatColor.BLUE + "Recursions set to: " + splatterrecursions);
    }
    int depth = 3;
    boolean allBlocks = false;

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Splatter Overlay brush parameters:");
            v.sendMessage(ChatColor.AQUA + "d[number] (ex:  d3) How many blocks deep you want to replace from the surface.");
            v.sendMessage(ChatColor.BLUE + "all (ex:  /b over all) Sets the brush to overlay over ALL materials, not just natural surface ones (will no longer ignore trees and buildings).  The parameter /some will set it back to default.");
            v.sendMessage(ChatColor.AQUA + "/b sover s[int] -- set a seed percentage (1-9999). 100 = 1% Default is 1000");
            v.sendMessage(ChatColor.AQUA + "/b sover g[int] -- set a growth percentage (1-9999).  Default is 1000");
            v.sendMessage(ChatColor.AQUA + "/b sover r[int] -- set a recursion (1-10).  Default is 3");
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
            } else if (par[x].startsWith("s")) {
                double temp = Integer.parseInt(par[x].replace("s", ""));
                if (temp >= 1 && temp <= 9999) {
                    v.sendMessage(ChatColor.AQUA + "Seed percent set to: " + temp / 100 + "%");
                    seedpercent = (int) temp;
                } else {
                    v.sendMessage(ChatColor.RED + "Seed percent must be an integer 1-9999!");
                }
                continue;
            } else if (par[x].startsWith("g")) {
                double temp = Integer.parseInt(par[x].replace("g", ""));
                if (temp >= 1 && temp <= 9999) {
                    v.sendMessage(ChatColor.AQUA + "Growth percent set to: " + temp / 100 + "%");
                    growpercent = (int) temp;
                } else {
                    v.sendMessage(ChatColor.RED + "Growth percent must be an integer 1-9999!");
                }
                continue;
            } else if (par[x].startsWith("r")) {
                int temp = Integer.parseInt(par[x].replace("r", ""));
                if (temp >= 1 && temp <= 10) {
                    v.sendMessage(ChatColor.AQUA + "Recursions set to: " + temp);
                    splatterrecursions = temp;
                } else {
                    v.sendMessage(ChatColor.RED + "Recursions must be an integer 1-10!");
                }
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    public void soverlay(vData v) {
        int bsize = v.brushSize;

        //Splatter Time
        int[][] splat = new int[2 * bsize + 1][2 * bsize + 1];
        // Seed the array
        for (int x = 2 * bsize; x >= 0; x--) {
            for (int y = 2 * bsize; y >= 0; y--) {

                if (generator.nextInt(10000) <= seedpercent) {
                    splat[x][y] = 1;

                }
            }
        }
        // Grow the seeds
        int gref = growpercent;
        int growcheck;
        int[][] tempsplat = new int[2 * bsize + 1][2 * bsize + 1];
        for (int r = 0; r < splatterrecursions; r++) {

            growpercent = (int) (gref - ((gref / splatterrecursions) * (r)));
            for (int x = 2 * bsize; x >= 0; x--) {
                for (int y = 2 * bsize; y >= 0; y--) {

                    tempsplat[x][y] = splat[x][y]; //prime tempsplat

                    growcheck = 0;
                    if (splat[x][y] == 0) {
                        if (x != 0 && splat[x - 1][y] == 1) {
                            growcheck++;
                        }
                        if (y != 0 && splat[x][y - 1] == 1) {
                            growcheck++;
                        }
                        if (x != 2 * bsize && splat[x + 1][y] == 1) {
                            growcheck++;
                        }
                        if (y != 2 * bsize && splat[x][y + 1] == 1) {
                            growcheck++;
                        }

                    }

                    if (growcheck >= 1 && generator.nextInt(10000) <= growpercent) {
                        tempsplat[x][y] = 1; //prevent bleed into splat
                    }

                }

            }
            //integrate tempsplat back into splat at end of iteration
            for (int x = 2 * bsize; x >= 0; x--) {
                for (int y = 2 * bsize; y >= 0; y--) {

                    splat[x][y] = tempsplat[x][y];

                }
            }
        }
        growpercent = gref;

        int[][] memory = new int[bsize * 2 + 1][bsize * 2 + 1];
        double bpow = Math.pow(bsize + 0.5, 2);
        for (int z = bsize; z >= -bsize; z--) {
            for (int x = bsize; x >= -bsize; x--) {
                for (int y = by; y > 0; y--) { //start scanning from the height you clicked at
                    if (memory[x + bsize][z + bsize] != 1) { //if haven't already found the surface in this column 
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= bpow && splat[x + bsize][z + bsize] == 1) { //if inside of the column && if to be splattered
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

    public void soverlayTwo(vData v) {
        int bsize = v.brushSize;

        //Splatter Time
        int[][] splat = new int[2 * bsize + 1][2 * bsize + 1];
        // Seed the array
        for (int x = 2 * bsize; x >= 0; x--) {
            for (int y = 2 * bsize; y >= 0; y--) {

                if (generator.nextInt(10000) <= seedpercent) {
                    splat[x][y] = 1;

                }
            }
        }
        // Grow the seeds
        int gref = growpercent;
        int growcheck;
        int[][] tempsplat = new int[2 * bsize + 1][2 * bsize + 1];
        for (int r = 0; r < splatterrecursions; r++) {

            growpercent = (int) (gref - ((gref / splatterrecursions) * (r)));
            for (int x = 2 * bsize; x >= 0; x--) {
                for (int y = 2 * bsize; y >= 0; y--) {

                    tempsplat[x][y] = splat[x][y]; //prime tempsplat

                    growcheck = 0;
                    if (splat[x][y] == 0) {
                        if (x != 0 && splat[x - 1][y] == 1) {
                            growcheck++;
                        }
                        if (y != 0 && splat[x][y - 1] == 1) {
                            growcheck++;
                        }
                        if (x != 2 * bsize && splat[x + 1][y] == 1) {
                            growcheck++;
                        }
                        if (y != 2 * bsize && splat[x][y + 1] == 1) {
                            growcheck++;
                        }

                    }

                    if (growcheck >= 1 && generator.nextInt(10000) <= growpercent) {
                        tempsplat[x][y] = 1; //prevent bleed into splat
                    }

                }

            }
            //integrate tempsplat back into splat at end of iteration
            for (int x = 2 * bsize; x >= 0; x--) {
                for (int y = 2 * bsize; y >= 0; y--) {

                    splat[x][y] = tempsplat[x][y];

                }
            }
        }
        growpercent = gref;

        int[][] memory = new int[bsize * 2 + 1][bsize * 2 + 1];
        double bpow = Math.pow(bsize + 0.5, 2);
        for (int z = bsize; z >= -bsize; z--) {
            for (int x = bsize; x >= -bsize; x--) {
                for (int y = by; y > 0; y--) { //start scanning from the height you clicked at
                    if (memory[x + bsize][z + bsize] != 1) { //if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= bpow && splat[x + bsize][z + bsize] == 1) { //if inside of the column...&& if to be splattered
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
}
