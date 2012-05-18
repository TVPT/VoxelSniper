/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vPainting;

/**
 * Painting scrolling Brush
 * THIS BRUSH SHOULD NOT USE PERFORMERS
 *
 * @author Voxel
 */
public class Painting extends Brush {

    public Painting() {
        name = "Painting";
    }

    /**
     * Scroll painting forward
     *
     * @param v vSniper caller
     */
    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        vPainting.paint(v.owner().p, true, false, 0);
    }

    /**
     * Scroll painting backwards
     *
     * @param v vSniper caller
     */
    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        vPainting.paint(v.owner().p, true, true, 0);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
    }
}
