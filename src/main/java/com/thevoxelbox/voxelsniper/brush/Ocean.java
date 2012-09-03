package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * 
 * @author Voxel
 */
public class Ocean extends Brush {

    protected int s1x;
    protected int s1z;
    protected int s2x;
    protected int s2z;
    protected vUndo h;

    private static int timesUsed = 0;

    public Ocean() {
        this.setName("OCEANATOR 5000(tm)");
    }

    @Override
    public int getTimesUsed() {
        return Ocean.timesUsed;
    }

    @Override
    public void info(final vMessage vm) {
        vm.brushName(this.getName());
    }

    @Override
    public void setTimesUsed(final int tUsed) {
        Ocean.timesUsed = tUsed;
    }

    @Override
    protected void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.h = new vUndo(this.getTargetBlock().getWorld().getName());
        this.oceanator(v);
        v.storeUndo(this.h);
    }

    protected final int getHeight(final int bx, final int bz) {
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

    protected void ocean(final Chunk c) {
    }

    protected final void oceanator(final vData v) {
        int sx = (int) Math.floor((double) this.getTargetBlock().getX() / 16) * 16;
        int sz = (int) Math.floor((double) this.getTargetBlock().getZ() / 16) * 16;

        int y;
        int dif;
        if (this.getTargetBlock().getX() >= 0 && this.getTargetBlock().getZ() >= 0) {
            for (int x = sx; x < sx + 16; x++) {
                for (int z = sz; z < sz + 16; z++) {
                    this.h.put(this.clampY(x, 63, z));
                    this.setBlockIdAt(9, x, 63, z);
                }
            }
            for (int x = sx; x < sx + 16; x++) {
                for (int z = sz; z < sz + 16; z++) {
                    y = this.getHeight(x, z);
                    if (y > 59) {
                        dif = 59 - (y - 59);
                        for (int t = 127; t > dif; t--) {
                            if (t > 8) {
                                if (t > 63) {
                                    this.h.put(this.clampY(x, t, z));
                                    this.setBlockIdAt(0, x, t, z);
                                } else {
                                    this.h.put(this.clampY(x, t, z));
                                    this.setBlockIdAt(9, x, t, z);
                                }
                            }
                        }
                        for (int r = 63; r > 5; r--) {
                            if (this.getBlockIdAt(x, r, z) == 0) {
                                this.h.put(this.clampY(x, r, z));
                                this.setBlockIdAt(9, x, r, z);
                            }
                        }
                    }
                }
            }
            this.setTargetBlock(this.clampY(sx + 8, this.getTargetBlock().getY(), sz + 8));
        } else if (this.getTargetBlock().getX() < 0 && this.getTargetBlock().getZ() > 0) {
            sx = (int) Math.floor((this.getTargetBlock().getX() - 1) / 16) * 16;
            for (int x = sx - 16; x < sx; x++) {
                for (int z = sz; z < sz + 16; z++) {
                    this.h.put(this.clampY(x, 63, z));
                    this.setBlockIdAt(9, x, 63, z);
                }
            }
            for (int x = sx - 16; x < sx; x++) {
                for (int z = sz; z < sz + 16; z++) {
                    y = this.getHeight(x, z);
                    if (y > 59) {
                        dif = 59 - (y - 59);
                        for (int t = 127; t > dif; t--) {
                            if (t > 8) {
                                if (t > 63) {
                                    this.h.put(this.clampY(x, t, z));
                                    this.setBlockIdAt(0, x, t, z);
                                } else {
                                    this.h.put(this.clampY(x, t, z));
                                    this.setBlockIdAt(9, x, t, z);
                                }
                            }
                        }
                        for (int r = 63; r > 5; r--) {
                            if (this.getBlockIdAt(x, r, z) == 0) {
                                this.h.put(this.clampY(x, r, z));
                                this.setBlockIdAt(9, x, r, z);
                            }
                        }
                    }
                }
            }
            this.setTargetBlock(this.clampY(sx - 8, this.getTargetBlock().getY(), sz + 8));
        } else if (this.getTargetBlock().getX() > 0 && this.getTargetBlock().getZ() < 0) {
            sz = (int) Math.floor((this.getTargetBlock().getZ() - 1) / 16) * 16;
            for (int x = sx; x < sx + 16; x++) {
                for (int z = sz - 16; z < sz; z++) {
                    this.h.put(this.clampY(x, 63, z));
                    this.setBlockIdAt(9, x, 63, z);
                }
            }
            for (int x = sx; x < sx + 16; x++) {
                for (int z = sz - 16; z < sz; z++) {
                    y = this.getHeight(x, z);
                    if (y > 59) {
                        dif = 59 - (y - 59);
                        for (int t = 127; t > dif; t--) {
                            if (t > 8) {
                                if (t > 63) {
                                    this.h.put(this.clampY(x, t, z));
                                    this.setBlockIdAt(0, x, t, z);
                                } else {
                                    this.h.put(this.clampY(x, t, z));
                                    this.setBlockIdAt(9, x, t, z);
                                }
                            }
                        }
                        for (int r = 63; r > 5; r--) {
                            if (this.getBlockIdAt(x, r, z) == 0) {
                                this.h.put(this.clampY(x, r, z));
                                this.setBlockIdAt(9, x, r, z);
                            }
                        }
                    }
                }
            }
            this.setTargetBlock(this.clampY(sx + 8, this.getTargetBlock().getY(), sz - 8));
        } else if (this.getTargetBlock().getX() < 0 && this.getTargetBlock().getZ() < 0) {
            sx = (int) Math.floor((this.getTargetBlock().getX() - 1) / 16) * 16;
            sz = (int) Math.floor((this.getTargetBlock().getZ() - 1) / 16) * 16;
            for (int x = sx - 16; x < sx; x++) {
                for (int z = sz - 16; z < sz; z++) {
                    this.h.put(this.clampY(x, 63, z));
                    this.setBlockIdAt(9, x, 63, z);
                }
            }
            for (int x = sx - 16; x < sx; x++) {
                for (int z = sz - 16; z < sz; z++) {
                    y = this.getHeight(x, z);
                    if (y > 59) {
                        dif = 59 - (y - 59);
                        for (int t = 127; t > dif; t--) {
                            if (t > 8) {
                                if (t > 63) {
                                    this.h.put(this.clampY(x, t, z));
                                    this.setBlockIdAt(0, x, t, z);
                                } else {
                                    this.h.put(this.clampY(x, t, z));
                                    this.setBlockIdAt(9, x, t, z);
                                }
                            }
                        }
                        for (int r = 63; r > 5; r--) {
                            if (this.getBlockIdAt(x, r, z) == 0) {
                                this.h.put(this.clampY(x, r, z));
                                this.setBlockIdAt(9, x, r, z);
                            }
                        }
                    }
                }
            }
            this.setTargetBlock(this.clampY(sx - 8, this.getTargetBlock().getY(), sz - 8));
        }
    }

    protected final void oceanatorBig(final vData v) {
        this.oceanator(v); // center
        this.setTargetBlock(this.setX(this.getTargetBlock(), this.getTargetBlock().getX() + 16));
        this.oceanator(v); // right
        this.setTargetBlock(this.setZ(this.getTargetBlock(), this.getTargetBlock().getZ() + 16));
        this.oceanator(v); // top right
        this.setTargetBlock(this.setX(this.getTargetBlock(), this.getTargetBlock().getX() - 16));
        this.oceanator(v); // top
        this.setTargetBlock(this.setX(this.getTargetBlock(), this.getTargetBlock().getX() - 16));
        this.oceanator(v); // top left
        this.setTargetBlock(this.setZ(this.getTargetBlock(), this.getTargetBlock().getZ() - 16));
        this.oceanator(v); // left
        this.setTargetBlock(this.setZ(this.getTargetBlock(), this.getTargetBlock().getZ() - 16));
        this.oceanator(v); // bottom left
        this.setTargetBlock(this.setX(this.getTargetBlock(), this.getTargetBlock().getX() + 16));
        this.oceanator(v); // bottom
        this.setTargetBlock(this.setX(this.getTargetBlock(), this.getTargetBlock().getX() + 16));
        this.oceanator(v); // bottom right
    }

    @Override
    protected void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.h = new vUndo(this.getTargetBlock().getWorld().getName());
        this.oceanatorBig(v);
        v.storeUndo(this.h);
    }

    protected final Block setX(final Block bl, final int bx) {
        return this.clampY(bx, bl.getY(), bl.getZ());
    }

    protected final Block setZ(final Block bl, final int bz) {
        return this.clampY(bl.getX(), bl.getY(), bz);
    }
}
