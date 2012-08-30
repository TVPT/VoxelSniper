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
 * @author giltwist
 */
public class SpiralStaircase extends Brush {

    protected String stairtype = "block"; //"block" 1x1 blocks (default), "step" alternating step double step, "stair" staircase with blocks on corners 
    protected String sdirect = "c"; // "c" clockwise (default), "cc" counter-clockwise
    protected String sopen = "n"; // "n" north (default), "e" east, "w" south, "w" west

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        digstairwell(v); //make stairwell below target
    }

    public SpiralStaircase() {
        name = "Spiral Staircase";
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = lb.getX();
        by = lb.getY();
        bz = lb.getZ();
        buildstairwell(v);  //make stairwell above target
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName("Spiral Staircase");
        vm.size();
        vm.voxel();
        vm.height();
        vm.data();
        vm.custom(ChatColor.BLUE + "Staircase type: " + stairtype);
        vm.custom(ChatColor.BLUE + "Staircase turns: " + sdirect);
        vm.custom(ChatColor.BLUE + "Staircase opens: " + sopen);
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Spiral Staircase Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b sstair 'block' (default) | 'step' | 'woodstair' | 'cobblestair' -- set the type of staircase");
            v.sendMessage(ChatColor.AQUA + "/b sstair 'c' (default) | 'cc' -- set the turning direction of staircase");
            v.sendMessage(ChatColor.AQUA + "/b sstair 'n' (default) | 'e' | 's' | 'w' -- set the opening direction of staircase");
            return;
        }

        for (int x = 1; x < par.length; x++) {
            if (par[x].equalsIgnoreCase("block") || par[x].equalsIgnoreCase("step") || par[x].equalsIgnoreCase("woodstair") || par[x].equalsIgnoreCase("cobblestair")) {
                stairtype = par[x];
                v.sendMessage(ChatColor.BLUE + "Staircase type: " + stairtype);
                continue;
            } else if (par[x].equalsIgnoreCase("c") || par[x].equalsIgnoreCase("cc")) {
                sdirect = par[x];
                v.sendMessage(ChatColor.BLUE + "Staircase turns: " + sdirect);
                continue;
            } else if (par[x].equalsIgnoreCase("n") || par[x].equalsIgnoreCase("e") || par[x].equalsIgnoreCase("s") || par[x].equalsIgnoreCase("w")) {
                sopen = par[x];
                v.sendMessage(ChatColor.BLUE + "Staircase opens: " + sopen);
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    public void buildstairwell(vData v) {
        int bsize = v.brushSize;
        int bId = v.voxelId;

        if (v.voxelHeight < 1) {
            v.voxelHeight = 1;
            v.sendMessage(ChatColor.RED + "VoxelHeight must be a natural number! Set to 1.");
        }
        int height = v.voxelHeight;

        //initialize array
        int[][][] spiral = new int[2 * bsize + 1][height][2 * bsize + 1];

        //locate first block in staircase
        //Note to self, fix these   
        int startx = 0;
        int startz = 0;
        int y = 0;
        int xoffset = 0;
        int zoffset = 0;
        int toggle = 0;

        if (sdirect.equalsIgnoreCase("cc")) {
            if (sopen.equalsIgnoreCase("n")) {
                startx = 0;
                startz = 2 * bsize;
            } else if (sopen.equalsIgnoreCase("e")) {
                startx = 0;
                startz = 0;
            } else if (sopen.equalsIgnoreCase("s")) {
                startx = 2 * bsize;
                startz = 0;
            } else {
                startx = 2 * bsize;
                startz = 2 * bsize;
            }
        } else {
            if (sopen.equalsIgnoreCase("n")) {
                startx = 0;
                startz = 0;
            } else if (sopen.equalsIgnoreCase("e")) {
                startx = 2 * bsize;
                startz = 0;
            } else if (sopen.equalsIgnoreCase("s")) {
                startx = 2 * bsize;
                startz = 2 * bsize;
            } else {
                startx = 0;
                startz = 2 * bsize;
            }
        }

        while (y < height) {
            if (stairtype.equalsIgnoreCase("block")) {
                // 1x1x1 voxel material steps
                spiral[startx + xoffset][y][startz + zoffset] = 1;
                y++;
            } else if (stairtype.equalsIgnoreCase("step")) {
                // alternating step-doublestep, uses data value to determine type
                switch (toggle) {
                    case 0:
                        toggle = 2;
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                        break;
                    case 1:
                        toggle = 2;
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                        break;
                    case 2:
                        toggle = 1;
                        spiral[startx + xoffset][y][startz + zoffset] = 2;
                        y++;
                        break;
                }

            }

            // Adjust horizontal position and do stair-option array stuff
            //v.sendMessage(ChatColor.DARK_RED + "" + (startx + xoffset) + " " + (startz + zoffset));
            if (startx + xoffset == 0) { //All North
                if (startz + zoffset == 0) { //NORTHEAST
                    if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                    }
                    if (sdirect.equalsIgnoreCase("c")) {
                        xoffset++;
                    } else {
                        zoffset++;
                    }
                } else if (startz + zoffset == 2 * bsize) { //NORTHWEST
                    if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                    }
                    if (sdirect.equalsIgnoreCase("c")) {
                        zoffset--;
                    } else {
                        xoffset++;
                    }
                    {
                    }
                } else { //JUST PLAIN NORTH
                    if (sdirect.equalsIgnoreCase("c")) {
                        if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                            spiral[startx + xoffset][y][startz + zoffset] = 5;
                            y++;
                        }
                        zoffset--;
                    } else {
                        if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                            spiral[startx + xoffset][y][startz + zoffset] = 4;
                            y++;
                        }
                        zoffset++;
                    }
                }
            } else if (startx + xoffset == 2 * bsize) { //ALL SOUTH
                if (startz + zoffset == 0) { //SOUTHEAST
                    if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                    }
                    if (sdirect.equalsIgnoreCase("c")) {
                        zoffset++;
                    } else {
                        xoffset--;
                    }
                    {
                    }
                } else if (startz + zoffset == 2 * bsize) { //SOUTHWEST
                    if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                    }
                    if (sdirect.equalsIgnoreCase("c")) {
                        xoffset--;
                    } else {
                        zoffset--;
                    }
                    {
                    }
                } else { //JUST PLAIN SOUTH
                    if (sdirect.equalsIgnoreCase("c")) {
                        if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                            spiral[startx + xoffset][y][startz + zoffset] = 4;
                            y++;
                        }
                        zoffset++;
                    } else {
                        if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                            spiral[startx + xoffset][y][startz + zoffset] = 5;
                            y++;
                        }
                        zoffset--;
                    }
                }
            } else if (startz + zoffset == 0) { //JUST PLAIN EAST
                if (sdirect.equalsIgnoreCase("c")) {
                    if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 2;
                        y++;
                    }
                    xoffset++;
                } else {
                    if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 3;
                        y++;
                    }
                    xoffset--;
                }
            } else { //JUST PLAIN WEST
                if (sdirect.equalsIgnoreCase("c")) {
                    if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 3;
                        y++;
                    }
                    xoffset--;
                } else {
                    if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 2;
                        y++;
                    }
                    xoffset++;
                }
            }
        }
        vUndo h = new vUndo(tb.getWorld().getName());
        // Make the changes

        for (int x = 2 * bsize; x >= 0; x--) {
            for (int i = height - 1; i >= 0; i--) {
                for (int z = 2 * bsize; z >= 0; z--) {
                    switch (spiral[x][i][z]) {
                        case 0:
                            if (i != height - 1) {
                                if (!((stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) && spiral[x][i + 1][z] == 1)) {
                                    if (getBlockIdAt(bx - bsize + x, by + i, bz - bsize + z) != 0) {
                                        h.put(clampY(bx - bsize + x, by + i, bz - bsize + z));
                                    }
                                    setBlockIdAt(0, bx - bsize + x, by + i, bz - bsize + z);
                                }

                            } else {
                                if (getBlockIdAt(bx - bsize + x, by + i, bz - bsize + z) != 0) {
                                    h.put(clampY(bx - bsize + x, by + i, bz - bsize + z));
                                }
                                setBlockIdAt(0, bx - bsize + x, by + i, bz - bsize + z);
                            }

                            break;
                        case 1:
                            if (stairtype.equalsIgnoreCase("block")) {
                                if (getBlockIdAt(bx - bsize + x, by + i, bz - bsize + z) != bId) {
                                    h.put(clampY(bx - bsize + x, by + i, bz - bsize + z));
                                }
                                setBlockIdAt(bId, bx - bsize + x, by + i, bz - bsize + z);
                            } else if (stairtype.equalsIgnoreCase("step")) {
                                if (getBlockIdAt(bx - bsize + x, by + i, bz - bsize + z) != 44) {
                                    h.put(clampY(bx - bsize + x, by + i, bz - bsize + z));
                                }
                                setBlockIdAt(44, bx - bsize + x, by + i, bz - bsize + z);
                                clampY(bx - bsize + x, by + i, bz - bsize + z).setData(v.data);
                            } else if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                                if (getBlockIdAt(bx - bsize + x, by + i - 1, bz - bsize + z) != bId) {
                                    h.put(clampY(bx - bsize + x, by + i - 1, bz - bsize + z));
                                }
                                setBlockIdAt(bId, bx - bsize + x, by + i - 1, bz - bsize + z);

                            }
                            break;
                        case 2:
                            if (stairtype.equalsIgnoreCase("step")) {
                                if (getBlockIdAt(bx - bsize + x, by + i, bz - bsize + z) != 43) {
                                    h.put(clampY(bx - bsize + x, by + i, bz - bsize + z));
                                }
                                setBlockIdAt(43, bx - bsize + x, by + i, bz - bsize + z);
                                clampY(bx - bsize + x, by + i, bz - bsize + z).setData(v.data);
                            } else if (stairtype.equalsIgnoreCase("woodstair")) {
                                if (getBlockIdAt(bx - bsize + x, by + i, bz - bsize + z) != 53) {
                                    h.put(clampY(bx - bsize + x, by + i, bz - bsize + z));
                                }
                                setBlockIdAt(53, bx - bsize + x, by + i, bz - bsize + z);
                                clampY(bx - bsize + x, by + i, bz - bsize + z).setData((byte) 0);
                            } else if (stairtype.equalsIgnoreCase("cobblestair")) {
                                if (getBlockIdAt(bx - bsize + x, by + i, bz - bsize + z) != 67) {
                                    h.put(clampY(bx - bsize + x, by + i, bz - bsize + z));
                                }
                                setBlockIdAt(67, bx - bsize + x, by + i, bz - bsize + z);
                                clampY(bx - bsize + x, by + i, bz - bsize + z).setData((byte) 0);
                            }
                            break;
                        default:
                            if (stairtype.equalsIgnoreCase("woodstair")) {
                                if (getBlockIdAt(bx - bsize + x, by + i, bz - bsize + z) != 53) {
                                    h.put(clampY(bx - bsize + x, by + i, bz - bsize + z));
                                }
                                setBlockIdAt(53, bx - bsize + x, by + i, bz - bsize + z);
                                clampY(bx - bsize + x, by + i, bz - bsize + z).setData((byte) (spiral[x][i][z] - 2));
                            } else if (stairtype.equalsIgnoreCase("cobblestair")) {
                                if (getBlockIdAt(bx - bsize + x, by + i, bz - bsize + z) != 67) {
                                    h.put(clampY(bx - bsize + x, by + i, bz - bsize + z));
                                }
                                setBlockIdAt(67, bx - bsize + x, by + i, bz - bsize + z);
                                clampY(bx - bsize + x, by + i, bz - bsize + z).setData((byte) (spiral[x][i][z] - 2));
                            }
                            break;
                    }
                }
            }
        }
        v.storeUndo(h);
    }

    public void digstairwell(vData v) {
        int bsize = v.brushSize;
        int bId = v.voxelId;

        if (v.voxelHeight < 1) {
            v.voxelHeight = 1;
            v.sendMessage(ChatColor.RED + "VoxelHeight must be a natural number! Set to 1.");
        }
        int height = v.voxelHeight;

        //initialize array
        int[][][] spiral = new int[2 * bsize + 1][height][2 * bsize + 1];

        //locate first block in staircase
        //Note to self, fix these   
        int startx = 0;
        int startz = 0;
        int y = 0;
        int xoffset = 0;
        int zoffset = 0;
        int toggle = 0;

        if (sdirect.equalsIgnoreCase("cc")) {
            if (sopen.equalsIgnoreCase("n")) {
                startx = 0;
                startz = 2 * bsize;
            } else if (sopen.equalsIgnoreCase("e")) {
                startx = 0;
                startz = 0;
            } else if (sopen.equalsIgnoreCase("s")) {
                startx = 2 * bsize;
                startz = 0;
            } else {
                startx = 2 * bsize;
                startz = 2 * bsize;
            }
        } else {
            if (sopen.equalsIgnoreCase("n")) {
                startx = 0;
                startz = 0;
            } else if (sopen.equalsIgnoreCase("e")) {
                startx = 2 * bsize;
                startz = 0;
            } else if (sopen.equalsIgnoreCase("s")) {
                startx = 2 * bsize;
                startz = 2 * bsize;
            } else {
                startx = 0;
                startz = 2 * bsize;
            }
        }




        while (y < height) {
            if (stairtype.equalsIgnoreCase("block")) {
                // 1x1x1 voxel material steps
                spiral[startx + xoffset][y][startz + zoffset] = 1;
                y++;
            } else if (stairtype.equalsIgnoreCase("step")) {
                // alternating step-doublestep, uses data value to determine type
                switch (toggle) {
                    case 0:
                        toggle = 2;
                        spiral[startx + xoffset][y][startz + zoffset] = 2;
                        break;
                    case 1:
                        toggle = 2;
                        spiral[startx + xoffset][y][startz + zoffset] = 2;
                        break;
                    case 2:
                        toggle = 1;
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                        y++;
                        break;
                }

            }

            // Adjust horizontal position and do stair-option array stuff
            //v.sendMessage(ChatColor.DARK_RED + "" + (startx + xoffset) + " " + (startz + zoffset));
            if (startx + xoffset == 0) { //All North
                if (startz + zoffset == 0) { //NORTHEAST
                    if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                    }
                    if (sdirect.equalsIgnoreCase("c")) {
                        xoffset++;
                    } else {
                        zoffset++;
                    }
                } else if (startz + zoffset == 2 * bsize) { //NORTHWEST
                    if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                    }
                    if (sdirect.equalsIgnoreCase("c")) {
                        zoffset--;
                    } else {
                        xoffset++;
                    }
                    {
                    }
                } else { //JUST PLAIN NORTH
                    if (sdirect.equalsIgnoreCase("c")) {
                        if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                            spiral[startx + xoffset][y][startz + zoffset] = 4;
                            y++;
                        }
                        zoffset--;
                    } else {
                        if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                            spiral[startx + xoffset][y][startz + zoffset] = 5;
                            y++;
                        }
                        zoffset++;
                    }
                }


            } else if (startx + xoffset == 2 * bsize) { //ALL SOUTH
                if (startz + zoffset == 0) { //SOUTHEAST
                    if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                    }
                    if (sdirect.equalsIgnoreCase("c")) {
                        zoffset++;
                    } else {
                        xoffset--;
                    }
                    {
                    }
                } else if (startz + zoffset == 2 * bsize) { //SOUTHWEST
                    if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                    }
                    if (sdirect.equalsIgnoreCase("c")) {
                        xoffset--;
                    } else {
                        zoffset--;
                    }
                    {
                    }
                } else { //JUST PLAIN SOUTH
                    if (sdirect.equalsIgnoreCase("c")) {
                        if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                            spiral[startx + xoffset][y][startz + zoffset] = 5;
                            y++;
                        }
                        zoffset++;
                    } else {
                        if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                            spiral[startx + xoffset][y][startz + zoffset] = 4;
                            y++;
                        }
                        zoffset--;
                    }
                }


            } else if (startz + zoffset == 0) { //JUST PLAIN EAST
                if (sdirect.equalsIgnoreCase("c")) {
                    if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 3;
                        y++;
                    }
                    xoffset++;
                } else {
                    if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 2;
                        y++;
                    }
                    xoffset--;
                }
            } else { //JUST PLAIN WEST
                if (sdirect.equalsIgnoreCase("c")) {
                    if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 2;
                        y++;
                    }
                    xoffset--;
                } else {
                    if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 3;
                        y++;
                    }
                    xoffset++;
                }
            }


        }



        vUndo h = new vUndo(tb.getWorld().getName());
        // Make the changes

        for (int x = 2 * bsize; x >= 0; x--) {

            for (int i = height - 1; i >= 0; i--) {

                for (int z = 2 * bsize; z >= 0; z--) {

                    switch (spiral[x][i][z]) {
                        case 0:
                            if (getBlockIdAt(bx - bsize + x, by - i, bz - bsize + z) != 0) {
                                h.put(clampY(bx - bsize + x, by - i, bz - bsize + z));
                            }
                            setBlockIdAt(0, bx - bsize + x, by - i, bz - bsize + z);
                            break;
                        case 1:
                            if (stairtype.equalsIgnoreCase("block")) {
                                if (getBlockIdAt(bx - bsize + x, by - i, bz - bsize + z) != bId) {
                                    h.put(clampY(bx - bsize + x, by - i, bz - bsize + z));
                                }
                                setBlockIdAt(bId, bx - bsize + x, by - i, bz - bsize + z);
                            } else if (stairtype.equalsIgnoreCase("step")) {
                                if (getBlockIdAt(bx - bsize + x, by - i, bz - bsize + z) != 44) {
                                    h.put(clampY(bx - bsize + x, by - i, bz - bsize + z));
                                }
                                setBlockIdAt(44, bx - bsize + x, by - i, bz - bsize + z);
                                clampY(bx - bsize + x, by - i, bz - bsize + z).setData(v.data);
                            } else if (stairtype.equalsIgnoreCase("woodstair") || stairtype.equalsIgnoreCase("cobblestair")) {
                                if (getBlockIdAt(bx - bsize + x, by - i, bz - bsize + z) != bId) {
                                    h.put(clampY(bx - bsize + x, by - i, bz - bsize + z));
                                }
                                setBlockIdAt(bId, bx - bsize + x, by - i, bz - bsize + z);

                            }
                            break;
                        case 2:
                            if (stairtype.equalsIgnoreCase("step")) {
                                if (getBlockIdAt(bx - bsize + x, by - i, bz - bsize + z) != 43) {
                                    h.put(clampY(bx - bsize + x, by - i, bz - bsize + z));
                                }
                                setBlockIdAt(43, bx - bsize + x, by - i, bz - bsize + z);
                                clampY(bx - bsize + x, by - i, bz - bsize + z).setData(v.data);
                            } else if (stairtype.equalsIgnoreCase("woodstair")) {
                                if (getBlockIdAt(bx - bsize + x, by - i, bz - bsize + z) != 53) {
                                    h.put(clampY(bx - bsize - x, by + i, bz - bsize + z));
                                }
                                setBlockIdAt(53, bx - bsize + x, by - i, bz - bsize + z);
                                clampY(bx - bsize + x, by - i, bz - bsize + z).setData((byte) 0);
                            } else if (stairtype.equalsIgnoreCase("cobblestair")) {
                                if (getBlockIdAt(bx - bsize + x, by - i, bz - bsize + z) != 67) {
                                    h.put(clampY(bx - bsize + x, by - i, bz - bsize + z));
                                }
                                setBlockIdAt(67, bx - bsize + x, by - i, bz - bsize + z);
                                clampY(bx - bsize + x, by - i, bz - bsize + z).setData((byte) 0);
                            }
                            break;
                        default:
                            if (stairtype.equalsIgnoreCase("woodstair")) {
                                if (getBlockIdAt(bx - bsize + x, by - i, bz - bsize + z) != 53) {
                                    h.put(clampY(bx - bsize + x, by - i, bz - bsize + z));
                                }
                                setBlockIdAt(53, bx - bsize + x, by - i, bz - bsize + z);
                                clampY(bx - bsize + x, by - i, bz - bsize + z).setData((byte) (spiral[x][i][z] - 2));
                            } else if (stairtype.equalsIgnoreCase("cobblestair")) {
                                if (getBlockIdAt(bx - bsize + x, by - i, bz - bsize + z) != 67) {
                                    h.put(clampY(bx - bsize + x, by - i, bz - bsize + z));
                                }
                                setBlockIdAt(67, bx - bsize + x, by - i, bz - bsize + z);
                                clampY(bx - bsize + x, by - i, bz - bsize + z).setData((byte) (spiral[x][i][z] - 2));
                            }
                            break;

                    }

                }
            }
        }
        v.storeUndo(h);

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
