package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Voxel
 */
public class Ocean extends Brush {
    protected int s1x;
    protected int s1z;
    protected int s2x;
    protected int s2z;
    protected Undo undo;

    private static int timesUsed = 0;

    public Ocean() {
        this.setName("OCEANATOR 5000(tm)");
    }

    private final int getHeight(final int maxWorldHeight, final int bx, final int bz) {
        int _i = 0;
        for (int _y = 127; _y > 0; _y--) {
            _i = this.getBlockIdAt(bx, _y, bz);
            if (_i != 0) {
                switch (_i) {
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
                    return _y;
                }
            }
        }
        return 0;
    }

    protected final void oceanator(final SnipeData v) {
        int _sx = (int) Math.floor((double) this.getTargetBlock().getX() / 16) * 16;
        int _sz = (int) Math.floor((double) this.getTargetBlock().getZ() / 16) * 16;

        int _y = 0;
        int _dif = 0;
        if (this.getTargetBlock().getX() >= 0 && this.getTargetBlock().getZ() >= 0) {
            for (int _x = _sx; _x < _sx + 16; _x++) {
                for (int _z = _sz; _z < _sz + 16; _z++) {
                    this.undo.put(this.clampY(_x, 63, _z));
                    this.setBlockIdAt(9, _x, 63, _z);
                }
            }
            for (int _x = _sx; _x < _sx + 16; _x++) {
                for (int _z = _sz; _z < _sz + 16; _z++) {
                    _y = this.getHeight(v.getWorld().getMaxHeight(), _x, _z);
                    if (_y > 59) {
                        _dif = 59 - (_y - 59);
                        for (int _t = 127; _t > _dif; _t--) {
                            if (_t > 8) {
                                if (_t > 63) {
                                    this.undo.put(this.clampY(_x, _t, _z));
                                    this.setBlockIdAt(0, _x, _t, _z);
                                } else {
                                    this.undo.put(this.clampY(_x, _t, _z));
                                    this.setBlockIdAt(9, _x, _t, _z);
                                }
                            }
                        }
                        for (int _r = 63; _r > 5; _r--) {
                            if (this.getBlockIdAt(_x, _r, _z) == 0) {
                                this.undo.put(this.clampY(_x, _r, _z));
                                this.setBlockIdAt(9, _x, _r, _z);
                            }
                        }
                    }
                }
            }
            this.setTargetBlock(this.clampY(_sx + 8, this.getTargetBlock().getY(), _sz + 8));
        } else if (this.getTargetBlock().getX() < 0 && this.getTargetBlock().getZ() > 0) {
            _sx = (int) Math.floor((this.getTargetBlock().getX() - 1) / 16) * 16;
            for (int _x = _sx - 16; _x < _sx; _x++) {
                for (int _z = _sz; _z < _sz + 16; _z++) {
                    this.undo.put(this.clampY(_x, 63, _z));
                    this.setBlockIdAt(9, _x, 63, _z);
                }
            }
            for (int _x = _sx - 16; _x < _sx; _x++) {
                for (int _z = _sz; _z < _sz + 16; _z++) {
                    _y = this.getHeight(v.getWorld().getMaxHeight(), _x, _z);
                    if (_y > 59) {
                        _dif = 59 - (_y - 59);
                        for (int t = 127; t > _dif; t--) {
                            if (t > 8) {
                                if (t > 63) {
                                    this.undo.put(this.clampY(_x, t, _z));
                                    this.setBlockIdAt(0, _x, t, _z);
                                } else {
                                    this.undo.put(this.clampY(_x, t, _z));
                                    this.setBlockIdAt(9, _x, t, _z);
                                }
                            }
                        }
                        for (int _r = 63; _r > 5; _r--) {
                            if (this.getBlockIdAt(_x, _r, _z) == 0) {
                                this.undo.put(this.clampY(_x, _r, _z));
                                this.setBlockIdAt(9, _x, _r, _z);
                            }
                        }
                    }
                }
            }
            this.setTargetBlock(this.clampY(_sx - 8, this.getTargetBlock().getY(), _sz + 8));
        } else if (this.getTargetBlock().getX() > 0 && this.getTargetBlock().getZ() < 0) {
            _sz = (int) Math.floor((this.getTargetBlock().getZ() - 1) / 16) * 16;
            for (int _x = _sx; _x < _sx + 16; _x++) {
                for (int _z = _sz - 16; _z < _sz; _z++) {
                    this.undo.put(this.clampY(_x, 63, _z));
                    this.setBlockIdAt(9, _x, 63, _z);
                }
            }
            for (int _x = _sx; _x < _sx + 16; _x++) {
                for (int _z = _sz - 16; _z < _sz; _z++) {
                    _y = this.getHeight(v.getWorld().getMaxHeight(), _x, _z);
                    if (_y > 59) {
                        _dif = 59 - (_y - 59);
                        for (int _t = 127; _t > _dif; _t--) {
                            if (_t > 8) {
                                if (_t > 63) {
                                    this.undo.put(this.clampY(_x, _t, _z));
                                    this.setBlockIdAt(0, _x, _t, _z);
                                } else {
                                    this.undo.put(this.clampY(_x, _t, _z));
                                    this.setBlockIdAt(9, _x, _t, _z);
                                }
                            }
                        }
                        for (int _r = 63; _r > 5; _r--) {
                            if (this.getBlockIdAt(_x, _r, _z) == 0) {
                                this.undo.put(this.clampY(_x, _r, _z));
                                this.setBlockIdAt(9, _x, _r, _z);
                            }
                        }
                    }
                }
            }
            this.setTargetBlock(this.clampY(_sx + 8, this.getTargetBlock().getY(), _sz - 8));
        } else if (this.getTargetBlock().getX() < 0 && this.getTargetBlock().getZ() < 0) {
            _sx = (int) Math.floor((this.getTargetBlock().getX() - 1) / 16) * 16;
            _sz = (int) Math.floor((this.getTargetBlock().getZ() - 1) / 16) * 16;
            for (int _x = _sx - 16; _x < _sx; _x++) {
                for (int _z = _sz - 16; _z < _sz; _z++) {
                    this.undo.put(this.clampY(_x, 63, _z));
                    this.setBlockIdAt(9, _x, 63, _z);
                }
            }
            for (int _x = _sx - 16; _x < _sx; _x++) {
                for (int _z = _sz - 16; _z < _sz; _z++) {
                    _y = this.getHeight(v.getWorld().getMaxHeight(), _x, _z);
                    if (_y > 59) {
                        _dif = 59 - (_y - 59);
                        for (int _t = 127; _t > _dif; _t--) {
                            if (_t > 8) {
                                if (_t > 63) {
                                    this.undo.put(this.clampY(_x, _t, _z));
                                    this.setBlockIdAt(0, _x, _t, _z);
                                } else {
                                    this.undo.put(this.clampY(_x, _t, _z));
                                    this.setBlockIdAt(9, _x, _t, _z);
                                }
                            }
                        }
                        for (int _r = 63; _r > 5; _r--) {
                            if (this.getBlockIdAt(_x, _r, _z) == 0) {
                                this.undo.put(this.clampY(_x, _r, _z));
                                this.setBlockIdAt(9, _x, _r, _z);
                            }
                        }
                    }
                }
            }
            this.setTargetBlock(this.clampY(_sx - 8, this.getTargetBlock().getY(), _sz - 8));
        }
    }
    
    protected final Block setX(final Block bl, final int bx) {
    	return this.clampY(bx, bl.getY(), bl.getZ());
    }
    
    protected final Block setZ(final Block bl, final int bz) {
    	return this.clampY(bl.getX(), bl.getY(), bz);
    }

    private final void oceanatorBig(final SnipeData v) {
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
    protected void arrow(final SnipeData v) {
        this.undo = new Undo(this.getTargetBlock().getWorld().getName());
        this.oceanator(v);
        v.storeUndo(this.undo);
    }

    @Override
    protected void powder(final SnipeData v) {
        this.undo = new Undo(this.getTargetBlock().getWorld().getName());
        this.oceanatorBig(v);
        v.storeUndo(this.undo);
    }
    
    @Override
    public void info(final Message vm) {
    	vm.brushName(this.getName());
    }
    
    @Override
    public int getTimesUsed() {
    	return Ocean.timesUsed;
    }
    
    @Override
    public void setTimesUsed(final int tUsed) {
    	Ocean.timesUsed = tUsed;
    }
}
