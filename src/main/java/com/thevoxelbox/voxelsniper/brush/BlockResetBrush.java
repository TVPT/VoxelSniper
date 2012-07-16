package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;

/**
 * @author MikeMatrix
 * 
 */
public class BlockResetBrush extends Brush {

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
    }

    @Override
    protected final void arrow(final vData v) {
        w = tb.getWorld();
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();

        for (int _z = -v.brushSize; _z <= v.brushSize; _z++) {
            for (int _x = -v.brushSize; _x <= v.brushSize; _x++) {
                for (int _y = -v.brushSize; _y <= v.brushSize; _y++) {
                    Block _block = w.getBlockAt(bx + _x, by + _y, bz + _z);
                    _block.setType(_block.getType());
                }
            }
        }
    }

    @Override
    protected final void powder(final vData v) {
        this.arrow(v);
    }
}
