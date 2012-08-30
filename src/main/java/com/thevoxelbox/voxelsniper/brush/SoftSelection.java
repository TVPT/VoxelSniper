/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import java.util.HashSet;
import org.bukkit.block.Block;

/**
 *
 * @author Voxel
 */
public class SoftSelection extends Brush {

//    protected final sPoint p1 = new sPoint(0, 1);
//    protected final sPoint p2 = new sPoint(1, 0);
    protected HashSet<sBlock> surface = new HashSet<sBlock>();
    protected double c1 = 1;
    protected double c2 = 0;
    protected vUndo h;

    public SoftSelection() {
        name = "SoftSelection";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void info(vMessage vm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void getSurface(vData v) {
        int bsize = v.brushSize;

        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();

        surface.clear();

        double bpow = Math.pow(bsize + 0.5, 2);
        for (int z = -bsize; z <= bsize; z++) {
            double zpow = Math.pow(z, 2);
            int zz = bz + z;
            for (int x = -bsize; x <= bsize; x++) {
                double xpow = Math.pow(x, 2);
                int xx = bx + x;
                for (int y = -bsize; y <= bsize; y++) {
                    double pow = (xpow + Math.pow(y, 2) + zpow);
                    if (pow <= bpow) {
                        if (isSurface(xx, by + y, zz)) {
                            surface.add(new sBlock(clampY(xx, by + y, zz), getStr(((pow / bpow)))));
                        }
                    }
                }
            }
        }
    }

    protected boolean isSurface(int x, int y, int z) {
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

    protected double getStr(double t) {
        double lt = 1 - t;
        return (lt * lt * lt) + 3 * (lt * lt) * t * c1 + 3 * lt * (t * t) * c2; //My + (t * ((By + (t * ((c2 + (t * (0 - c2))) - By))) - My)); 
        // double Ay = 1 + (t * (c1 - 1));
        // double By = c1 + (t * (c2 - c1));
        // double Cy = c2 + (t * (0 - c2));
        // double My = Ay + (t * (By - Ay));
        //double Ny = By + (t * (Cy - By));
    }

    protected class sBlock {

        public int id;
        public byte d;
        public double str;
        public int x;
        public int y;
        public int z;

        public sBlock(Block b, double st) {
            id = b.getTypeId();
            d = b.getData();
            x = b.getX();
            y = b.getY();
            z = b.getZ();
            str = st;
        }
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
