/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vMessage;

/**
 *
 * @author Voxel
 */
public class Snipe extends PerformBrush {

    public Snipe() {
        name = "Snipe";
        undoScale = 1;
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        current.perform(tb);
        v.storeUndo(current.getUndo());
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        current.perform(lb);
        v.storeUndo(current.getUndo());
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
    }
}
