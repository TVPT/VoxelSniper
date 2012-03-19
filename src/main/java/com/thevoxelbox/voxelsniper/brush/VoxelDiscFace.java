/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.block.BlockFace;

/**
 *
 * @author Voxel
 */
public class VoxelDiscFace extends PerformBrush {

    public VoxelDiscFace() {
        name = "Voxel Disc Face";
    }

    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        pre(v, tb.getFace(lb));
    }

    @Override
    public void powder(vSniper v) {
        bx = lb.getX();
        by = lb.getY();
        bz = lb.getZ();
        pre(v, tb.getFace(lb));
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
    }

    private void pre(vSniper v, BlockFace bf) {
        if (bf == null) {
            return;
        }
        switch (bf) {
            case NORTH:
            case SOUTH:
                discNS(v);
                break;

            case EAST:
            case WEST:
                discEW(v);
                break;

            case UP:
            case DOWN:
                disc(v);
                break;

            default:
                break;
        }
    }

    public void disc(vSniper v) {
        int bsize = v.brushSize;

        for (int x = bsize; x >= -bsize; x--) {
            for (int y = bsize; y >= -bsize; y--) {
                current.perform(clampY(bx + x, by, bz + y));
            }
        }

        if (current.getUndo().getSize() > 0) {
            v.hashUndo.put(v.hashEn, current.getUndo());
            v.hashEn++;
        }
    }

    public void discEW(vSniper v) {
        int bsize = v.brushSize;

        for (int x = bsize; x >= -bsize; x--) {
            for (int y = bsize; y >= -bsize; y--) {
                current.perform(clampY(bx + x, by + y, bz));
            }
        }

        if (current.getUndo().getSize() > 0) {
            v.hashUndo.put(v.hashEn, current.getUndo());
            v.hashEn++;
        }
    }

    public void discNS(vSniper v) {
        int bsize = v.brushSize;

        for (int x = bsize; x >= -bsize; x--) {
            for (int y = bsize; y >= -bsize; y--) {
                current.perform(clampY(bx, by + x, bz + y));
            }
        }

        if (current.getUndo().getSize() > 0) {
            v.hashUndo.put(v.hashEn, current.getUndo());
            v.hashEn++;
        }
    }
}
