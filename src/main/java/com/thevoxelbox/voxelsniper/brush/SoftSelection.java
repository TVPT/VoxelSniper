package com.thevoxelbox.voxelsniper.brush;

import java.util.HashSet;

import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

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

    // protected final sPoint p1 = new sPoint(0, 1);
    // protected final sPoint p2 = new sPoint(1, 0);
    protected HashSet<sBlock> surface = new HashSet<sBlock>();
    protected double c1 = 1;
    protected double c2 = 0;

    protected vUndo h;

    private static int timesUsed = 0;

    public SoftSelection() {
        this.setName("SoftSelection");
    }

    @Override
    public int getTimesUsed() {
        return SoftSelection.timesUsed;
    }

    @Override
    public void info(final vMessage vm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTimesUsed(final int tUsed) {
        SoftSelection.timesUsed = tUsed;
    }

    @Override
    protected void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected final double getStr(final double t) {
        final double lt = 1 - t;
        return (lt * lt * lt) + 3 * (lt * lt) * t * this.c1 + 3 * lt * (t * t) * this.c2; // My + (t * ((By + (t * ((c2 + (t * (0 - c2))) - By))) - My));
        // double Ay = 1 + (t * (c1 - 1));
        // double By = c1 + (t * (c2 - c1));
        // double Cy = c2 + (t * (0 - c2));
        // double My = Ay + (t * (By - Ay));
        // double Ny = By + (t * (Cy - By));
    }

    protected final void getSurface(final vData v) {
        final int bsize = v.brushSize;

        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        this.surface.clear();

        final double bpow = Math.pow(bsize + 0.5, 2);
        for (int z = -bsize; z <= bsize; z++) {
            final double zpow = Math.pow(z, 2);
            final int zz = this.getBlockPositionZ() + z;
            for (int x = -bsize; x <= bsize; x++) {
                final double xpow = Math.pow(x, 2);
                final int xx = this.getBlockPositionX() + x;
                for (int y = -bsize; y <= bsize; y++) {
                    final double pow = (xpow + Math.pow(y, 2) + zpow);
                    if (pow <= bpow) {
                        if (this.isSurface(xx, this.getBlockPositionY() + y, zz)) {
                            this.surface.add(new sBlock(this.clampY(xx, this.getBlockPositionY() + y, zz), this.getStr(((pow / bpow)))));
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
    protected void powder(final com.thevoxelbox.voxelsniper.vData v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
