package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * A single block brush.
 */
public class SnipeBrush extends PerformBrush {

    public SnipeBrush() {
        this.setName("Snipe");
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.undo = new Undo(1);
        perform(v, this.targetBlock);
        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.undo = new Undo(1);
        perform(v, this.lastBlock);
        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.snipe";
    }
}
