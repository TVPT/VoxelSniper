
package com.thevoxelbox.voxelsniper;
import com.thevoxelbox.voxelsniper.brush.Brush;

/**
 *
 * @author Gavin
 */

public class ThrottlingTask implements Runnable {

    Brush b;
    int[] pn;
    vData v;

    public ThrottlingTask(vData vs, Brush br, int[] pieceNumbers) {
        b = br;
        v = vs;
        pn = pieceNumbers;
    }

    @Override
    public void run() {
        b.ThrottledRun(v, pn);
    }
}
