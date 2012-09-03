package com.thevoxelbox.voxelsniper.brush;

import java.util.HashSet;

import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Voxel
 */
public class SoftSelection extends Brush {
    protected class sBlock {

        public int id;
        public byte d;
        public double str;
        public int x;
        public int y;
        public int z;

        public sBlock(final Block b, final double st) {
            this.id = b.getTypeId();
            this.d = b.getData();
            this.x = b.getX();
            this.y = b.getY();
            this.z = b.getZ();
            this.str = st;
        }
    }

    protected HashSet<sBlock> surface = new HashSet<sBlock>();
    protected double c1 = 1;
    protected double c2 = 0;

    protected Undo undo;

    private static int timesUsed = 0;

    public SoftSelection() {
        this.setName("SoftSelection");
    }

    protected final double getStr(final double t) {
        final double lt = 1 - t;
        return (lt * lt * lt) + 3 * (lt * lt) * t * this.c1 + 3 * lt * (t * t) * this.c2;
    }

    protected final void getSurface(final SnipeData v) {
        final int _bSize = v.getBrushSize();

        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        this.surface.clear();

        final double _bPow = Math.pow(_bSize + 0.5, 2);
        for (int _z = -_bSize; _z <= _bSize; _z++) {
            final double _zPow = Math.pow(_z, 2);
            final int _zz = this.getBlockPositionZ() + _z;
            for (int _x = -_bSize; _x <= _bSize; _x++) {
                final double _xPow = Math.pow(_x, 2);
                final int _xx = this.getBlockPositionX() + _x;
                for (int _y = -_bSize; _y <= _bSize; _y++) {
                    final double _pow = (_xPow + Math.pow(_y, 2) + _zPow);
                    if (_pow <= _bPow) {
                        if (this.isSurface(_xx, this.getBlockPositionY() + _y, _zz)) {
                            this.surface.add(new sBlock(this.clampY(_xx, this.getBlockPositionY() + _y, _zz), this.getStr(((_pow / _bPow)))));
                        }
                    }
                }
            }
        }
    }

    protected final boolean isSurface(final int x, final int y, final int z) {
        if (this.getBlockIdAt(x, y, z) == 0) {
            return false;
        }
        if (this.getBlockIdAt(x, y - 1, z) == 0) {
            return true;
        } else if (this.getBlockIdAt(x, y + 1, z) == 0) {
            return true;
        } else if (this.getBlockIdAt(x + 1, y, z) == 0) {
            return true;
        } else if (this.getBlockIdAt(x - 1, y, z) == 0) {
            return true;
        } else if (this.getBlockIdAt(x, y, z + 1) == 0) {
            return true;
        } else if (this.getBlockIdAt(x, y, z - 1) == 0) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    protected void arrow(final SnipeData v) {
    	throw new UnsupportedOperationException("Not supported yet.");
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
    	return SoftSelection.timesUsed;
    }
    
    @Override
    public void setTimesUsed(final int tUsed) {
    	SoftSelection.timesUsed = tUsed;
    }
}
