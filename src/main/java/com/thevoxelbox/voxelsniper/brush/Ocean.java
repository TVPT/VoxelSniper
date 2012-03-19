/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

/**
 *
 * @author Voxel
 */

//NOTE: Possibly add a parameter to change the sea level when using the brush.
//This would allow for higher or lower oceans. It'w also more useful on maps
//where the sea level isn't at the standard elevation.

public class Ocean extends Brush {

    protected int s1x;
    protected int s1z;
    protected int s2x;
    protected int s2z;
    protected vUndo h;
    
    public Ocean() {
        name = "OCEANATOR 5000(tm)";
    }

    @Override
    public void arrow(vSniper v) {
        h = new vUndo(tb.getWorld().getName());
        oceanator(v);
        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }

    @Override
    public void powder(vSniper v) {
        h = new vUndo(tb.getWorld().getName());
        oceanatorBig(v);
        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
    }

    protected void oceanator(vSniper v) {

        int sx = (int) Math.floor((double) tb.getX() / 16) * 16;
        int sz = (int) Math.floor((double) tb.getZ() / 16) * 16;

        int y;
        int dif;
        if (tb.getX() >= 0 && tb.getZ() >= 0) {
            for (int x = sx; x < sx + 16; x++) {
                for (int z = sz; z < sz + 16; z++) {
                    h.put(clampY(x, 63, z));
                    setBlockIdAt(9, x, 63, z);
                }
            }
            for (int x = sx; x < sx + 16; x++) {
                for (int z = sz; z < sz + 16; z++) {
                    y = getHeight(x, z);
                    if (y > 59) {
                        dif = 59 - (y - 59);
                        for (int t = 127; t > dif; t--) {
                            if (t > 8) {
                                if (t > 63) {
                                    h.put(clampY(x, t, z));
                                    setBlockIdAt(0, x, t, z);
                                } else {
                                    h.put(clampY(x, t, z));
                                    setBlockIdAt(9, x, t, z);
                                }
                            }
                        }
                        for (int r = 63; r > 5; r--) {
                            if (getBlockIdAt(x, r, z) == 0) {
                                h.put(clampY(x, r, z));
                                setBlockIdAt(9, x, r, z);
                            }
                        }
                    }
                }
            }
            tb = clampY(sx + 8, tb.getY(), sz + 8);
        } else if (tb.getX() < 0 && tb.getZ() > 0) {
            sx = (int) Math.floor((tb.getX() - 1) / 16) * 16;
            for (int x = sx - 16; x < sx; x++) {
                for (int z = sz; z < sz + 16; z++) {
                    h.put(clampY(x, 63, z));
                    setBlockIdAt(9, x, 63, z);
                }
            }
            for (int x = sx - 16; x < sx; x++) {
                for (int z = sz; z < sz + 16; z++) {
                    y = getHeight(x, z);
                    if (y > 59) {
                        dif = 59 - (y - 59);
                        for (int t = 127; t > dif; t--) {
                            if (t > 8) {
                                if (t > 63) {
                                    h.put(clampY(x, t, z));
                                    setBlockIdAt(0, x, t, z);
                                } else {
                                    h.put(clampY(x, t, z));
                                    setBlockIdAt(9, x, t, z);
                                }
                            }
                        }
                        for (int r = 63; r > 5; r--) {
                            if (getBlockIdAt(x, r, z) == 0) {
                                h.put(clampY(x, r, z));
                                setBlockIdAt(9, x, r, z);
                            }
                        }
                    }
                }
            }
            tb = clampY(sx - 8, tb.getY(), sz + 8);
        } else if (tb.getX() > 0 && tb.getZ() < 0) {
            sz = (int) Math.floor((tb.getZ() - 1) / 16) * 16;
            for (int x = sx; x < sx + 16; x++) {
                for (int z = sz - 16; z < sz; z++) {
                    h.put(clampY(x, 63, z));
                    setBlockIdAt(9, x, 63, z);
                }
            }
            for (int x = sx; x < sx + 16; x++) {
                for (int z = sz - 16; z < sz; z++) {
                    y = getHeight(x, z);
                    if (y > 59) {
                        dif = 59 - (y - 59);
                        for (int t = 127; t > dif; t--) {
                            if (t > 8) {
                                if (t > 63) {
                                    h.put(clampY(x, t, z));
                                    setBlockIdAt(0, x, t, z);
                                } else {
                                    h.put(clampY(x, t, z));
                                    setBlockIdAt(9, x, t, z);
                                }
                            }
                        }
                        for (int r = 63; r > 5; r--) {
                            if (getBlockIdAt(x, r, z) == 0) {
                                h.put(clampY(x, r, z));
                                setBlockIdAt(9, x, r, z);
                            }
                        }
                    }
                }
            }
            tb = clampY(sx + 8, tb.getY(), sz - 8);
        } else if (tb.getX() < 0 && tb.getZ() < 0) {
            sx = (int) Math.floor((tb.getX() - 1) / 16) * 16;
            sz = (int) Math.floor((tb.getZ() - 1) / 16) * 16;
            for (int x = sx - 16; x < sx; x++) {
                for (int z = sz - 16; z < sz; z++) {
                    h.put(clampY(x, 63, z));
                    setBlockIdAt(9, x, 63, z);
                }
            }
            for (int x = sx - 16; x < sx; x++) {
                for (int z = sz - 16; z < sz; z++) {
                    y = getHeight(x, z);
                    if (y > 59) {
                        dif = 59 - (y - 59);
                        for (int t = 127; t > dif; t--) {
                            if (t > 8) {
                                if (t > 63) {
                                    h.put(clampY(x, t, z));
                                    setBlockIdAt(0, x, t, z);
                                } else {
                                    h.put(clampY(x, t, z));
                                    setBlockIdAt(9, x, t, z);
                                }
                            }
                        }
                        for (int r = 63; r > 5; r--) {
                            if (getBlockIdAt(x, r, z) == 0) {
                                h.put(clampY(x, r, z));
                                setBlockIdAt(9, x, r, z);
                            }
                        }
                    }
                }
            }
            tb = clampY(sx - 8, tb.getY(), sz - 8);
        }
    }

    protected void oceanatorBig(vSniper v) {
        oceanator(v); // center
        tb = setX(tb, tb.getX() + 16);
        oceanator(v); // right
        tb = setZ(tb, tb.getZ() + 16);
        oceanator(v); // top right
        tb = setX(tb, tb.getX() - 16);
        oceanator(v); // top
        tb = setX(tb, tb.getX() - 16);
        oceanator(v); // top left
        tb = setZ(tb, tb.getZ() - 16);
        oceanator(v); // left
        tb = setZ(tb, tb.getZ() - 16);
        oceanator(v); // bottom left
        tb = setX(tb, tb.getX() + 16);
        oceanator(v); // bottom
        tb = setX(tb, tb.getX() + 16);
        oceanator(v); // bottom right
    }

    protected Block setX(Block bl, int bx) {
        return clampY(bx, bl.getY(), bl.getZ());
    }

    protected Block setZ(Block bl, int bz) {
        return clampY(bl.getX(), bl.getY(), bz);
    }

    protected void ocean(Chunk c) {



    }

    protected int getHeight(int bx, int bz) {
        int i;
        for (int y = 127; y > 0; y--) {
            i = this.getBlockIdAt(bx, y, bz);
            if (i != 0) {
                switch (i) {
                    case 0:
                        break;

                    case 6:
                        break;

                    case 8:
                        break;

                    case 9:
                        break;

                    case 10:
                        break;

                    case 11:
                        break;

                    case 17:
                        break;

                    case 18:
                        break;

                    case 37:
                        break;

                    case 38:
                        break;

                    case 39:
                        break;

                    case 40:
                        break;

                    case 78:
                        break;

                    case 79:
                        break;

                    case 80:
                        break;

                    case 81:
                        break;

                    case 83:
                        break;

                    case 86:
                        break;

                    default:
                        return y;
                }
            }
        }
        return 0;
    }
}
