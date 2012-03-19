
package com.thevoxelbox.voxelsniper;
import com.thevoxelbox.voxelsniper.brush.Brush;

/**
 *
 * @author Gavin
 */

public class ThrottlingTask implements Runnable {

    Brush b;
    int[] pn;
    vSniper v;

    public ThrottlingTask(vSniper vs, Brush br, int[] pieceNumbers) {
        b = br;
        v = vs;
        pn = pieceNumbers;
    }

    public void run() {
        b.ThrottledRun(v, pn);
    }
}
