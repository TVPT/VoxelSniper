package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.brush.Brush;

/**
 * 
 * @author Gavin
 */
public class ThrottlingTask implements Runnable {

    private Brush brush;
    private int[] pieceNumbers;
    private vData v;

    /**
     * @param vs
     * @param br
     * @param pieceNumbers
     */
    public ThrottlingTask(final vData vs, final Brush br, final int[] pieceNumbers) {
        this.brush = br;
        this.v = vs;
        this.pieceNumbers = pieceNumbers;
    }

    @Override
    public final void run() {
        this.brush.ThrottledRun(this.v, this.pieceNumbers);
    }
}
