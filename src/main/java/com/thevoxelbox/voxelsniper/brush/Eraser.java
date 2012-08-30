/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;

/**
 * THIS BRUSH SHOULD NOT USE PERFORMERS
 *
 * @author Voxel
 */
public class Eraser extends Brush {

    protected String werasemode;

    public Eraser() {
        name = "Eraser";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        werasemode = "nuke";
        doerase(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        werasemode = "keep";
        doerase(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
    }

    public void doerase(vData v) {
        int bsize = v.brushSize;

        vUndo h = new vUndo(tb.getWorld().getName());
        int temp;
        for (int x = 2 * bsize; x >= 0; x--) {
            for (int y = 0; y <= 2 * bsize; y++) {
                for (int z = 2 * bsize; z >= 0; z--) {
                    temp = getBlockIdAt(bx - bsize + x, by - bsize + y, bz - bsize + z);
                    if (temp > 3 && temp != 12 && temp != 13) {
                        if (!(werasemode.equalsIgnoreCase("keep") && (temp == 8 || temp == 9))) {
                            h.put(clampY(bx - bsize + x, by - bsize + y, bz - bsize + z));
                            setBlockIdAt(0, bx - bsize + x, by - bsize + y, bz - bsize + z);
                        }
                    }
                }
            }
        }
        v.storeUndo(h);
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
