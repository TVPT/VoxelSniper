/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.block.BlockFace;

/**
 *
 * @author Voxel
 */
public class VoxelDiscFace extends PerformBrush {

    public VoxelDiscFace() {
        name = "Voxel Disc Face";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        pre(v, tb.getFace(lb));
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = lb.getX();
        by = lb.getY();
        bz = lb.getZ();
        pre(v, tb.getFace(lb));
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
    }

    private void pre(vData v, BlockFace bf) {
        if (bf == null) {
            return;
        }
        switch (bf) {
            case NORTH:
            case SOUTH:
                discNS(v);
                break;

            case EAST:
            case WEST:
                discEW(v);
                break;

            case UP:
            case DOWN:
                disc(v);
                break;

            default:
                break;
        }
    }

    public void disc(vData v) {
        int bsize = v.brushSize;

        for (int x = bsize; x >= -bsize; x--) {
            for (int y = bsize; y >= -bsize; y--) {
                current.perform(clampY(bx + x, by, bz + y));
            }
        }

        v.storeUndo(current.getUndo());
    }

    public void discEW(vData v) {
        int bsize = v.brushSize;

        for (int x = bsize; x >= -bsize; x--) {
            for (int y = bsize; y >= -bsize; y--) {
                current.perform(clampY(bx + x, by + y, bz));
            }
        }

        v.storeUndo(current.getUndo());
    }

    public void discNS(vData v) {
        int bsize = v.brushSize;

        for (int x = bsize; x >= -bsize; x--) {
            for (int y = bsize; y >= -bsize; y--) {
                current.perform(clampY(bx, by + x, bz + y));
            }
        }

        v.storeUndo(current.getUndo());
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
