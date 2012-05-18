/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;

/**
 *
 * @author Piotr
 */
public class Voxel extends PerformBrush {

    public Voxel() {
        name = "Voxel";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        voxel(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        arrow(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
    }

    public void voxel(vData v) {
        int bsize = v.brushSize;

        for (int z = bsize; z >= -bsize; z--) {
            for (int x = bsize; x >= -bsize; x--) {
                for (int y = bsize; y >= -bsize; y--) {
                    current.perform(clampY(bx + x, by + z, bz + y));
                }
            }
        }
        v.storeUndo(current.getUndo());
    }
}
