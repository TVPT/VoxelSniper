package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_OCEANATOR_5000
 * @author Voxel
 */
public class OceanBrush extends Brush {
    protected int s1x;
    protected int s1z;
    protected int s2x;
    protected int s2z;
    protected Undo undo;

    private static int timesUsed = 0;

    /**
     * 
     */
    public OceanBrush() {
        this.setName("OCEANATOR 5000(tm)");
    }

    private final int getHeight(final int bx, final int bz) {
        for (int _y = this.getWorld().getMaxHeight(); _y > 0; _y--) {
        	final Material _mat = this.clampY(bx, _y, bz).getType();
            if (_mat != Material.AIR) {
                switch (_mat) {               
                case SAPLING:                	
                    break;

                case WATER:
                    break;

                case STATIONARY_WATER:
                    break;

                case LAVA:
                    break;

                case STATIONARY_LAVA:
                    break;

                case LOG:
                    break;

                case LEAVES:
                    break;

                case YELLOW_FLOWER:
                    break;

                case RED_ROSE:
                    break;

                case BROWN_MUSHROOM:
                    break;

                case RED_MUSHROOM:
                    break;

                case SNOW:
                    break;

                case ICE:
                    break;

                case SNOW_BLOCK:
                    break;

                case CACTUS:
                    break;

                case SUGAR_CANE_BLOCK:
                    break;

                case PUMPKIN:
                    break;

                default:
                    return _y;
                }
            }
        }
        return 0;
    }

    /**
     * 
     * @param v
     */
    protected final void oceanator(final SnipeData v) {
        int _sx = (int) Math.floor((double) this.getTargetBlock().getX() / CHUNK_SIZE) * CHUNK_SIZE;
        int _sz = (int) Math.floor((double) this.getTargetBlock().getZ() / CHUNK_SIZE) * CHUNK_SIZE;

        int _y = 0;
        int _dif = 0;
        
        if (this.getTargetBlock().getX() >= 0 && this.getTargetBlock().getZ() >= 0) {
            for (int _x = _sx; _x < _sx + CHUNK_SIZE; _x++) {
                for (int _z = _sz; _z < _sz + CHUNK_SIZE; _z++) {
                    this.undo.put(this.clampY(_x, 63, _z));
                    this.setBlockIdAt(Material.STATIONARY_WATER.getId(), _x, 63, _z);
                }
            }
            for (int _x = _sx; _x < _sx + CHUNK_SIZE; _x++) {
                for (int _z = _sz; _z < _sz + CHUNK_SIZE; _z++) {
                    _y = this.getHeight(_x, _z);
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
                                this.setBlockIdAt(Material.STATIONARY_WATER.getId(), _x, _r, _z);
                            }
                        }
                    }
                }
            }
            this.setTargetBlock(this.clampY(_sx + 8, this.getTargetBlock().getY(), _sz + 8));
        } else if (this.getTargetBlock().getX() < 0 && this.getTargetBlock().getZ() > 0) {
            _sx = (int) Math.floor((this.getTargetBlock().getX() - 1) / CHUNK_SIZE) * CHUNK_SIZE;
            for (int _x = _sx - CHUNK_SIZE; _x < _sx; _x++) {
                for (int _z = _sz; _z < _sz + CHUNK_SIZE; _z++) {
                    this.undo.put(this.clampY(_x, 63, _z));
                    this.setBlockIdAt(Material.STATIONARY_WATER.getId(), _x, 63, _z);
                }
            }
            for (int _x = _sx - CHUNK_SIZE; _x < _sx; _x++) {
                for (int _z = _sz; _z < _sz + CHUNK_SIZE; _z++) {
                    _y = this.getHeight(_x, _z);
                    if (_y > 59) {
                        _dif = 59 - (_y - 59);
                        for (int _t = 127; _t > _dif; _t--) {
                            if (_t > 8) {
                                if (_t > 63) {
                                    this.undo.put(this.clampY(_x, _t, _z));
                                    this.setBlockIdAt(0, _x, _t, _z);
                                } else {
                                    this.undo.put(this.clampY(_x, _t, _z));
                                    this.setBlockIdAt(Material.STATIONARY_WATER.getId(), _x, _t, _z);
                                }
                            }
                        }
                        for (int _r = 63; _r > 5; _r--) {
                            if (this.getBlockIdAt(_x, _r, _z) == 0) {
                                this.undo.put(this.clampY(_x, _r, _z));
                                this.setBlockIdAt(Material.STATIONARY_WATER.getId(), _x, _r, _z);
                            }
                        }
                    }
                }
            }
            this.setTargetBlock(this.clampY(_sx - 8, this.getTargetBlock().getY(), _sz + 8));
        } else if (this.getTargetBlock().getX() > 0 && this.getTargetBlock().getZ() < 0) {
            _sz = (int) Math.floor((this.getTargetBlock().getZ() - 1) / CHUNK_SIZE) * CHUNK_SIZE;
            for (int _x = _sx; _x < _sx + CHUNK_SIZE; _x++) {
                for (int _z = _sz - CHUNK_SIZE; _z < _sz; _z++) {
                    this.undo.put(this.clampY(_x, 63, _z));
                    this.setBlockIdAt(Material.STATIONARY_WATER.getId(), _x, 63, _z);
                }
            }
            for (int _x = _sx; _x < _sx + CHUNK_SIZE; _x++) {
                for (int _z = _sz - CHUNK_SIZE; _z < _sz; _z++) {
                    _y = this.getHeight(_x, _z);
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
                                this.setBlockIdAt(Material.STATIONARY_WATER.getId(), _x, _r, _z);
                            }
                        }
                    }
                }
            }
            this.setTargetBlock(this.clampY(_sx + 8, this.getTargetBlock().getY(), _sz - 8));
        } else if (this.getTargetBlock().getX() < 0 && this.getTargetBlock().getZ() < 0) {
            _sx = (int) Math.floor((this.getTargetBlock().getX() - 1) / CHUNK_SIZE) * CHUNK_SIZE;
            _sz = (int) Math.floor((this.getTargetBlock().getZ() - 1) / CHUNK_SIZE) * CHUNK_SIZE;
            for (int _x = _sx - CHUNK_SIZE; _x < _sx; _x++) {
                for (int _z = _sz - CHUNK_SIZE; _z < _sz; _z++) {
                    this.undo.put(this.clampY(_x, 63, _z));
                    this.setBlockIdAt(Material.STATIONARY_WATER.getId(), _x, 63, _z);
                }
            }
            for (int _x = _sx - CHUNK_SIZE; _x < _sx; _x++) {
                for (int _z = _sz - CHUNK_SIZE; _z < _sz; _z++) {
                    _y = this.getHeight(_x, _z);
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
    
    /**
     * 
     * @param bl
     * @param bx
     * @return
     */
    protected final Block setX(final Block bl, final int bx) {
    	return this.clampY(bx, bl.getY(), bl.getZ());
    }
    
    /**
     * 
     * @param bl
     * @param bz
     * @return
     */
    protected final Block setZ(final Block bl, final int bz) {
    	return this.clampY(bl.getX(), bl.getY(), bz);
    }

    private final void oceanatorBig(final SnipeData v) {
        this.oceanator(v); // center
        this.setTargetBlock(this.setX(this.getTargetBlock(), this.getTargetBlock().getX() + CHUNK_SIZE));
        this.oceanator(v); // right
        this.setTargetBlock(this.setZ(this.getTargetBlock(), this.getTargetBlock().getZ() + CHUNK_SIZE));
        this.oceanator(v); // top right
        this.setTargetBlock(this.setX(this.getTargetBlock(), this.getTargetBlock().getX() - CHUNK_SIZE));
        this.oceanator(v); // top
        this.setTargetBlock(this.setX(this.getTargetBlock(), this.getTargetBlock().getX() - CHUNK_SIZE));
        this.oceanator(v); // top left
        this.setTargetBlock(this.setZ(this.getTargetBlock(), this.getTargetBlock().getZ() - CHUNK_SIZE));
        this.oceanator(v); // left
        this.setTargetBlock(this.setZ(this.getTargetBlock(), this.getTargetBlock().getZ() - CHUNK_SIZE));
        this.oceanator(v); // bottom left
        this.setTargetBlock(this.setX(this.getTargetBlock(), this.getTargetBlock().getX() + CHUNK_SIZE));
        this.oceanator(v); // bottom
        this.setTargetBlock(this.setX(this.getTargetBlock(), this.getTargetBlock().getX() + CHUNK_SIZE));
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
    	return OceanBrush.timesUsed;
    }
    
    @Override
    public void setTimesUsed(final int tUsed) {
    	OceanBrush.timesUsed = tUsed;
    }
}
