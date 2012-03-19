/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;

/**
 *
 * @author Voxel
 */
public abstract class PerformBrush extends Brush implements Performer {

    protected vPerformer current = new pMaterial();

    public void parse(String[] args, vSniper v) {
        if (PerformerE.has(args[1])) {
            vPerformer p = PerformerE.getPerformer(args[1]);
            if (p != null) {
                current = p;
                info(v.vm);
                current.info(v.vm);
                if (args.length > 2) {
                    String[] t = new String[args.length - 1];
                    t[0] = args[0];
                    for (int x = 2; x < args.length; x++) {
                        t[x - 1] = args[x];
                    }
                    parameters(t, v);
                }
            } else {
                parameters(args, v);
            }
        } else {
            parameters(args, v);
        }
    }

    public void initP(vSniper v) {
        current.init(v);
        current.setUndo(undoScale);
    }

    public void showInfo(vMessage vm) {
        current.info(vm);
    }
}
