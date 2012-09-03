package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * THIS BRUSH SHOULD NOT USE PERFORMERS
 * 
 * @author giltwist
 */
public class SpiralStaircase extends Brush {

    protected String stairtype = "block"; // "block" 1x1 blocks (default), "step" alternating step double step, "stair" staircase with blocks on corners
    protected String sdirect = "c"; // "c" clockwise (default), "cc" counter-clockwise
    protected String sopen = "n"; // "n" north (default), "e" east, "world" south, "world" west

    private static int timesUsed = 0;

    public SpiralStaircase() {
        this.setName("Spiral Staircase");
    }

    public final void buildstairwell(final vData v) {
        final int bsize = v.brushSize;
        final int bId = v.voxelId;

        if (v.voxelHeight < 1) {
            v.voxelHeight = 1;
            v.sendMessage(ChatColor.RED + "VoxelHeight must be a natural number! Set to 1.");
        }
        final int height = v.voxelHeight;

        // initialize array
        final int[][][] spiral = new int[2 * bsize + 1][height][2 * bsize + 1];

        // locate first block in staircase
        // Note to self, fix these
        int startx = 0;
        int startz = 0;
        int y = 0;
        int xoffset = 0;
        int zoffset = 0;
        int toggle = 0;

        if (this.sdirect.equalsIgnoreCase("cc")) {
            if (this.sopen.equalsIgnoreCase("n")) {
                startx = 0;
                startz = 2 * bsize;
            } else if (this.sopen.equalsIgnoreCase("e")) {
                startx = 0;
                startz = 0;
            } else if (this.sopen.equalsIgnoreCase("s")) {
                startx = 2 * bsize;
                startz = 0;
            } else {
                startx = 2 * bsize;
                startz = 2 * bsize;
            }
        } else {
            if (this.sopen.equalsIgnoreCase("n")) {
                startx = 0;
                startz = 0;
            } else if (this.sopen.equalsIgnoreCase("e")) {
                startx = 2 * bsize;
                startz = 0;
            } else if (this.sopen.equalsIgnoreCase("s")) {
                startx = 2 * bsize;
                startz = 2 * bsize;
            } else {
                startx = 0;
                startz = 2 * bsize;
            }
        }

        while (y < height) {
            if (this.stairtype.equalsIgnoreCase("block")) {
                // 1x1x1 voxel material steps
                spiral[startx + xoffset][y][startz + zoffset] = 1;
                y++;
            } else if (this.stairtype.equalsIgnoreCase("step")) {
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
                default:
                    break;
                }

            }

            // Adjust horizontal position and do stair-option array stuff
            // v.sendMessage(ChatColor.DARK_RED + "" + (startx + xoffset) + " " + (startz + zoffset));
            if (startx + xoffset == 0) { // All North
                if (startz + zoffset == 0) { // NORTHEAST
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        xoffset++;
                    } else {
                        zoffset++;
                    }
                } else if (startz + zoffset == 2 * bsize) { // NORTHWEST
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        zoffset--;
                    } else {
                        xoffset++;
                    }
                } else { // JUST PLAIN NORTH
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            spiral[startx + xoffset][y][startz + zoffset] = 5;
                            y++;
                        }
                        zoffset--;
                    } else {
                        if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            spiral[startx + xoffset][y][startz + zoffset] = 4;
                            y++;
                        }
                        zoffset++;
                    }
                }
            } else if (startx + xoffset == 2 * bsize) { // ALL SOUTH
                if (startz + zoffset == 0) { // SOUTHEAST
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        zoffset++;
                    } else {
                        xoffset--;
                    }
                } else if (startz + zoffset == 2 * bsize) { // SOUTHWEST
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        xoffset--;
                    } else {
                        zoffset--;
                    }
                } else { // JUST PLAIN SOUTH
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            spiral[startx + xoffset][y][startz + zoffset] = 4;
                            y++;
                        }
                        zoffset++;
                    } else {
                        if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            spiral[startx + xoffset][y][startz + zoffset] = 5;
                            y++;
                        }
                        zoffset--;
                    }
                }
            } else if (startz + zoffset == 0) { // JUST PLAIN EAST
                if (this.sdirect.equalsIgnoreCase("c")) {
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 2;
                        y++;
                    }
                    xoffset++;
                } else {
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 3;
                        y++;
                    }
                    xoffset--;
                }
            } else { // JUST PLAIN WEST
                if (this.sdirect.equalsIgnoreCase("c")) {
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 3;
                        y++;
                    }
                    xoffset--;
                } else {
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 2;
                        y++;
                    }
                    xoffset++;
                }
            }
        }
        final vUndo h = new vUndo(this.getTargetBlock().getWorld().getName());
        // Make the changes

        for (int x = 2 * bsize; x >= 0; x--) {
            for (int i = height - 1; i >= 0; i--) {
                for (int z = 2 * bsize; z >= 0; z--) {
                    switch (spiral[x][i][z]) {
                    case 0:
                        if (i != height - 1) {
                            if (!((this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) && spiral[x][i + 1][z] == 1)) {
                                if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z) != 0) {
                                    h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z));
                                }
                                this.setBlockIdAt(0, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z);
                            }

                        } else {
                            if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z) != 0) {
                                h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z));
                            }
                            this.setBlockIdAt(0, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z);
                        }

                        break;
                    case 1:
                        if (this.stairtype.equalsIgnoreCase("block")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z) != bId) {
                                h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z));
                            }
                            this.setBlockIdAt(bId, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z);
                        } else if (this.stairtype.equalsIgnoreCase("step")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z) != 44) {
                                h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z));
                            }
                            this.setBlockIdAt(44, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z);
                            this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z).setData(v.data);
                        } else if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i - 1, this.getBlockPositionZ() - bsize + z) != bId) {
                                h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i - 1, this.getBlockPositionZ() - bsize + z));
                            }
                            this.setBlockIdAt(bId, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i - 1, this.getBlockPositionZ() - bsize + z);

                        }
                        break;
                    case 2:
                        if (this.stairtype.equalsIgnoreCase("step")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z) != 43) {
                                h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z));
                            }
                            this.setBlockIdAt(43, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z);
                            this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z).setData(v.data);
                        } else if (this.stairtype.equalsIgnoreCase("woodstair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z) != 53) {
                                h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z));
                            }
                            this.setBlockIdAt(53, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z);
                            this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z).setData((byte) 0);
                        } else if (this.stairtype.equalsIgnoreCase("cobblestair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z) != 67) {
                                h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z));
                            }
                            this.setBlockIdAt(67, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z);
                            this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z).setData((byte) 0);
                        }
                        break;
                    default:
                        if (this.stairtype.equalsIgnoreCase("woodstair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z) != 53) {
                                h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z));
                            }
                            this.setBlockIdAt(53, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z);
                            this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z).setData((byte) (spiral[x][i][z] - 2));
                        } else if (this.stairtype.equalsIgnoreCase("cobblestair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z) != 67) {
                                h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z));
                            }
                            this.setBlockIdAt(67, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z);
                            this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z).setData((byte) (spiral[x][i][z] - 2));
                        }
                        break;
                    }
                }
            }
        }
        v.storeUndo(h);
    }

    public final void digstairwell(final vData v) {
        final int bsize = v.brushSize;
        final int bId = v.voxelId;

        if (v.voxelHeight < 1) {
            v.voxelHeight = 1;
            v.sendMessage(ChatColor.RED + "VoxelHeight must be a natural number! Set to 1.");
        }
        final int height = v.voxelHeight;

        // initialize array
        final int[][][] spiral = new int[2 * bsize + 1][height][2 * bsize + 1];

        // locate first block in staircase
        // Note to self, fix these
        int startx = 0;
        int startz = 0;
        int y = 0;
        int xoffset = 0;
        int zoffset = 0;
        int toggle = 0;

        if (this.sdirect.equalsIgnoreCase("cc")) {
            if (this.sopen.equalsIgnoreCase("n")) {
                startx = 0;
                startz = 2 * bsize;
            } else if (this.sopen.equalsIgnoreCase("e")) {
                startx = 0;
                startz = 0;
            } else if (this.sopen.equalsIgnoreCase("s")) {
                startx = 2 * bsize;
                startz = 0;
            } else {
                startx = 2 * bsize;
                startz = 2 * bsize;
            }
        } else {
            if (this.sopen.equalsIgnoreCase("n")) {
                startx = 0;
                startz = 0;
            } else if (this.sopen.equalsIgnoreCase("e")) {
                startx = 2 * bsize;
                startz = 0;
            } else if (this.sopen.equalsIgnoreCase("s")) {
                startx = 2 * bsize;
                startz = 2 * bsize;
            } else {
                startx = 0;
                startz = 2 * bsize;
            }
        }

        while (y < height) {
            if (this.stairtype.equalsIgnoreCase("block")) {
                // 1x1x1 voxel material steps
                spiral[startx + xoffset][y][startz + zoffset] = 1;
                y++;
            } else if (this.stairtype.equalsIgnoreCase("step")) {
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
                default:
                    break;
                }

            }

            // Adjust horizontal position and do stair-option array stuff
            // v.sendMessage(ChatColor.DARK_RED + "" + (startx + xoffset) + " " + (startz + zoffset));
            if (startx + xoffset == 0) { // All North
                if (startz + zoffset == 0) { // NORTHEAST
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        xoffset++;
                    } else {
                        zoffset++;
                    }
                } else if (startz + zoffset == 2 * bsize) { // NORTHWEST
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        zoffset--;
                    } else {
                        xoffset++;
                    }
                } else { // JUST PLAIN NORTH
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            spiral[startx + xoffset][y][startz + zoffset] = 4;
                            y++;
                        }
                        zoffset--;
                    } else {
                        if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            spiral[startx + xoffset][y][startz + zoffset] = 5;
                            y++;
                        }
                        zoffset++;
                    }
                }

            } else if (startx + xoffset == 2 * bsize) { // ALL SOUTH
                if (startz + zoffset == 0) { // SOUTHEAST
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        zoffset++;
                    } else {
                        xoffset--;
                    }
                } else if (startz + zoffset == 2 * bsize) { // SOUTHWEST
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        xoffset--;
                    } else {
                        zoffset--;
                    }
                } else { // JUST PLAIN SOUTH
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            spiral[startx + xoffset][y][startz + zoffset] = 5;
                            y++;
                        }
                        zoffset++;
                    } else {
                        if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            spiral[startx + xoffset][y][startz + zoffset] = 4;
                            y++;
                        }
                        zoffset--;
                    }
                }

            } else if (startz + zoffset == 0) { // JUST PLAIN EAST
                if (this.sdirect.equalsIgnoreCase("c")) {
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 3;
                        y++;
                    }
                    xoffset++;
                } else {
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 2;
                        y++;
                    }
                    xoffset--;
                }
            } else { // JUST PLAIN WEST
                if (this.sdirect.equalsIgnoreCase("c")) {
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 2;
                        y++;
                    }
                    xoffset--;
                } else {
                    if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                        spiral[startx + xoffset][y][startz + zoffset] = 3;
                        y++;
                    }
                    xoffset++;
                }
            }

        }

        final vUndo h = new vUndo(this.getTargetBlock().getWorld().getName());
        // Make the changes

        for (int x = 2 * bsize; x >= 0; x--) {

            for (int i = height - 1; i >= 0; i--) {

                for (int z = 2 * bsize; z >= 0; z--) {

                    switch (spiral[x][i][z]) {
                    case 0:
                        if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z) != 0) {
                            h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z));
                        }
                        this.setBlockIdAt(0, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z);
                        break;
                    case 1:
                        if (this.stairtype.equalsIgnoreCase("block")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z) != bId) {
                                h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z));
                            }
                            this.setBlockIdAt(bId, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z);
                        } else if (this.stairtype.equalsIgnoreCase("step")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z) != 44) {
                                h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z));
                            }
                            this.setBlockIdAt(44, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z);
                            this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z).setData(v.data);
                        } else if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z) != bId) {
                                h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z));
                            }
                            this.setBlockIdAt(bId, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z);

                        }
                        break;
                    case 2:
                        if (this.stairtype.equalsIgnoreCase("step")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z) != 43) {
                                h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z));
                            }
                            this.setBlockIdAt(43, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z);
                            this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z).setData(v.data);
                        } else if (this.stairtype.equalsIgnoreCase("woodstair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z) != 53) {
                                h.put(this.clampY(this.getBlockPositionX() - bsize - x, this.getBlockPositionY() + i, this.getBlockPositionZ() - bsize + z));
                            }
                            this.setBlockIdAt(53, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z);
                            this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z).setData((byte) 0);
                        } else if (this.stairtype.equalsIgnoreCase("cobblestair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z) != 67) {
                                h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z));
                            }
                            this.setBlockIdAt(67, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z);
                            this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z).setData((byte) 0);
                        }
                        break;
                    default:
                        if (this.stairtype.equalsIgnoreCase("woodstair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z) != 53) {
                                h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z));
                            }
                            this.setBlockIdAt(53, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z);
                            this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z).setData((byte) (spiral[x][i][z] - 2));
                        } else if (this.stairtype.equalsIgnoreCase("cobblestair")) {
                            if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z) != 67) {
                                h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z));
                            }
                            this.setBlockIdAt(67, this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z);
                            this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z).setData((byte) (spiral[x][i][z] - 2));
                        }
                        break;

                    }

                }
            }
        }
        v.storeUndo(h);

    }

    @Override
    public final int getTimesUsed() {
        return SpiralStaircase.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName("Spiral Staircase");
        vm.size();
        vm.voxel();
        vm.height();
        vm.data();
        vm.custom(ChatColor.BLUE + "Staircase type: " + this.stairtype);
        vm.custom(ChatColor.BLUE + "Staircase turns: " + this.sdirect);
        vm.custom(ChatColor.BLUE + "Staircase opens: " + this.sopen);
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Spiral Staircase Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b sstair 'block' (default) | 'step' | 'woodstair' | 'cobblestair' -- set the type of staircase");
            v.sendMessage(ChatColor.AQUA + "/b sstair 'c' (default) | 'cc' -- set the turning direction of staircase");
            v.sendMessage(ChatColor.AQUA + "/b sstair 'n' (default) | 'e' | 's' | 'world' -- set the opening direction of staircase");
            return;
        }

        for (int x = 1; x < par.length; x++) {
            if (par[x].equalsIgnoreCase("block") || par[x].equalsIgnoreCase("step") || par[x].equalsIgnoreCase("woodstair")
                    || par[x].equalsIgnoreCase("cobblestair")) {
                this.stairtype = par[x];
                v.sendMessage(ChatColor.BLUE + "Staircase type: " + this.stairtype);
                continue;
            } else if (par[x].equalsIgnoreCase("c") || par[x].equalsIgnoreCase("cc")) {
                this.sdirect = par[x];
                v.sendMessage(ChatColor.BLUE + "Staircase turns: " + this.sdirect);
                continue;
            } else if (par[x].equalsIgnoreCase("n") || par[x].equalsIgnoreCase("e") || par[x].equalsIgnoreCase("s") || par[x].equalsIgnoreCase("world")) {
                this.sopen = par[x];
                v.sendMessage(ChatColor.BLUE + "Staircase opens: " + this.sopen);
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        SpiralStaircase.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.digstairwell(v); // make stairwell below target
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getLastBlock().getX());
        this.setBlockPositionY(this.getLastBlock().getY());
        this.setBlockPositionZ(this.getLastBlock().getZ());
        this.buildstairwell(v); // make stairwell above target
    }
}
