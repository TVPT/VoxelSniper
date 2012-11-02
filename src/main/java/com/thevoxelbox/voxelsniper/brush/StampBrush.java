package com.thevoxelbox.voxelsniper.brush;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Voxel
 * 
 *         Would it be possible to make this a performer brush, so people can use the Inclusion and Exclusion performers? -psa
 */
public class StampBrush extends Brush {
    /**
     * 
     * @author Voxel
     *
     */
	protected class BlockWrapper {
        public int id;
        public int x;
        public int y;
        public int z;
        public byte d;

        /**
         * 
         * @param b
         * @param blx
         * @param bly
         * @param blz
         */
        public BlockWrapper(final Block b, final int blx, final int bly, final int blz) {
            this.id = b.getTypeId();
            this.d = b.getData();
            this.x = blx;
            this.y = bly;
            this.z = blz;
        }
    }
    
	/**
	 * 
	 * @author Monofraps
	 *
	 */
    protected enum StampType {
		NO_AIR, FILL, DEFAULT
	}

    private static int timesUsed = 0;

    protected HashSet<BlockWrapper> clone = new HashSet<BlockWrapper>();
    protected HashSet<BlockWrapper> fall = new HashSet<BlockWrapper>();
    protected HashSet<BlockWrapper> drop = new HashSet<BlockWrapper>();
    protected HashSet<BlockWrapper> solid = new HashSet<BlockWrapper>();
    protected Undo undo;
    protected boolean sorted = false;

    protected StampType stamp = StampType.DEFAULT;

    /**
     * 
     */
    public StampBrush() {
        this.setName("Stamp");
    }

    /**
     * 
     */
    public final void reSort() {
        this.sorted = false;
    }

    /**
     * 
     * @param id
     * @return
     */
    protected final boolean falling(final int id) {
        return (id > 7 && id < 14);
    }

    /**
     * 
     * @param id
     * @return
     */
    protected final boolean fallsOff(final int id) {
        switch (id) {
        // 6, 37, 38, 39, 40, 50, 51, 55, 59, 63, 64, 65, 66, 69, 70, 71, 72, 75, 76, 77, 83
        case (6):
        case (37):
        case (38):
        case (39):
        case (40):
        case (50):
        case (51):
        case (55):
        case (59):
        case (63):
        case (64):
        case (65):
        case (66):
        case (68):
        case (69):
        case (70):
        case (71):
        case (72):
        case (75):
        case (76):
        case (77):
        case (78):
        case (83):
        case (93):
        case (94):
        default:
            return false;
        }
    }

    /**
     * 
     */
    protected final void setBlock(final BlockWrapper cb) {
        final Block _b = this.clampY(this.getBlockPositionX() + cb.x, this.getBlockPositionY() + cb.y, this.getBlockPositionZ() + cb.z);
        this.undo.put(_b);
        _b.setTypeId(cb.id);
        _b.setData(cb.d);
    }

    /**
     * 
     * @param cb
     */
    protected final void setBlockFill(final BlockWrapper cb) {
        final Block _b = this.clampY(this.getBlockPositionX() + cb.x, this.getBlockPositionY() + cb.y, this.getBlockPositionZ() + cb.z);
        if (_b.getTypeId() == 0) {
            this.undo.put(_b);
            _b.setTypeId(cb.id);
            _b.setData(cb.d);
        }
    }

    /**
     * 
     * @param type
     */
    protected final void setStamp(final StampType type) {
        this.stamp = type;
    }

    /**
     * 
     * @param v
     */
    protected final void stamp(final SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY() + v.getcCen());
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        this.undo = new Undo(this.getTargetBlock().getWorld().getName());

        if (this.sorted) {
            for (final BlockWrapper _cb : this.solid) {
                this.setBlock(_cb);
            }
            for (final BlockWrapper _cb : this.drop) {
                this.setBlock(_cb);
            }
            for (final BlockWrapper _cb : this.fall) {
                this.setBlock(_cb);
            }
        } else {
            this.fall.clear();
            this.drop.clear();
            this.solid.clear();
            for (final BlockWrapper _cb : this.clone) {
                if (this.fallsOff(_cb.id)) {
                    this.fall.add(_cb);
                } else if (this.falling(_cb.id)) {
                    this.drop.add(_cb);
                } else {
                    this.solid.add(_cb);
                    this.setBlock(_cb);
                }
            }
            for (final BlockWrapper _cb : this.drop) {
                this.setBlock(_cb);
            }
            for (final BlockWrapper _cb : this.fall) {
                this.setBlock(_cb);
            }
            this.sorted = true;
        }

        v.storeUndo(this.undo);
    }

    /**
     * 
     * @param v
     */
    protected final void stampFill(final SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY() + v.getcCen());
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        this.undo = new Undo(this.getTargetBlock().getWorld().getName());

        if (this.sorted) {
            for (final BlockWrapper _cb : this.solid) {
                this.setBlockFill(_cb);
            }
            for (final BlockWrapper _cb : this.drop) {
                this.setBlockFill(_cb);
            }
            for (final BlockWrapper _cb : this.fall) {
                this.setBlockFill(_cb);
            }
        } else {
            this.fall.clear();
            this.drop.clear();
            this.solid.clear();
            for (final BlockWrapper _cb : this.clone) {
                if (this.fallsOff(_cb.id)) {
                    this.fall.add(_cb);
                } else if (this.falling(_cb.id)) {
                    this.drop.add(_cb);
                } else if (_cb.id != 0) {
                    this.solid.add(_cb);
                    this.setBlockFill(_cb);
                }
            }
            for (final BlockWrapper _cb : this.drop) {
                this.setBlockFill(_cb);
            }
            for (final BlockWrapper _cb : this.fall) {
                this.setBlockFill(_cb);
            }
            this.sorted = true;
        }

        v.storeUndo(this.undo);
    }

    /**
     * 
     * @param v
     */
    protected final void stampNoAir(final SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY() + v.getcCen());
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        this.undo = new Undo(this.getTargetBlock().getWorld().getName());

        if (this.sorted) {
            for (final BlockWrapper _cb : this.solid) {
                this.setBlock(_cb);
            }
            for (final BlockWrapper _cb : this.drop) {
                this.setBlock(_cb);
            }
            for (final BlockWrapper _cb : this.fall) {
                this.setBlock(_cb);
            }
        } else {
            this.fall.clear();
            this.drop.clear();
            this.solid.clear();
            for (final BlockWrapper _cb : this.clone) {
                if (this.fallsOff(_cb.id)) {
                    this.fall.add(_cb);
                } else if (this.falling(_cb.id)) {
                    this.drop.add(_cb);
                } else if (_cb.id != 0) {
                    this.solid.add(_cb);
                    this.setBlock(_cb);
                }
            }
            for (final BlockWrapper _cb : this.drop) {
                this.setBlock(_cb);
            }
            for (final BlockWrapper _cb : this.fall) {
                this.setBlock(_cb);
            }
            this.sorted = true;
        }

        v.storeUndo(this.undo);
    }    

    @Override
    protected final void arrow(final SnipeData v) {
        switch (this.stamp) {
        case DEFAULT:
            this.stamp(v);
            break;

        case NO_AIR:
            this.stampNoAir(v);
            break;

        case FILL:
            this.stampFill(v);
            break;

        default:
            v.sendMessage(ChatColor.DARK_RED + "Error while stamping! Report");
            break;
        }
    }
    
    @Override
    protected void powder(final SnipeData v) {
    	throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void info(final Message vm) {
    	throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public int getTimesUsed() {
    	return StampBrush.timesUsed;
    }
    
    @Override
    public void setTimesUsed(final int tUsed) {
    	StampBrush.timesUsed = tUsed;
    }
}
