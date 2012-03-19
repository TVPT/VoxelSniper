/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;

/**
 *
 * @author Voxel
 */
public class noUndoVoxel extends Brush {

    public noUndoVoxel() {
        name = "noUndo Voxel";
    }

    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        voxel(v);
    }

    @Override
    public void powder(vSniper v) {
        arrow(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
        vm.voxel();
    }

    public void voxel(vSniper v) {
        int bsize = v.brushSize;
        int bId = v.voxelId;

        for (int y = by - bsize; y < by + bsize; y++) {
            for (int x = bx - bsize; x < bx + bsize; x++) {
                for (int z = bz - bsize; z < bz + bsize; z++) {
                    clampY(x, y, z).setTypeId(bId);
                }
            }
        }
    }
}
