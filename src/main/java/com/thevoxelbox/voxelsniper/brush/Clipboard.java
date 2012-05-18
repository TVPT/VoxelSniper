/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;

/**
 *
 * @author Piotr
 */
public class Clipboard extends Brush {

    public Clipboard() {
        name = "Clipboard";
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
}
