package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.block.BlockFace;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Voxel
 */
public class VoxelDiscFace extends PerformBrush {

    private static int timesUsed = 0;

    public VoxelDiscFace() {
        this.name = "Voxel Disc Face";
    }

    public final void disc(final vData v) {
        final int bsize = v.brushSize;

        for (int x = bsize; x >= -bsize; x--) {
            for (int y = bsize; y >= -bsize; y--) {
                this.current.perform(this.clampY(this.bx + x, this.by, this.bz + y));
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    public final void discEW(final vData v) {
        final int bsize = v.brushSize;

        for (int x = bsize; x >= -bsize; x--) {
            for (int y = bsize; y >= -bsize; y--) {
                this.current.perform(this.clampY(this.bx + x, this.by + y, this.bz));
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    public final void discNS(final vData v) {
        final int bsize = v.brushSize;

        for (int x = bsize; x >= -bsize; x--) {
            for (int y = bsize; y >= -bsize; y--) {
                this.current.perform(this.clampY(this.bx, this.by + x, this.bz + y));
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    @Override
    public final int getTimesUsed() {
        return VoxelDiscFace.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        vm.size();
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        VoxelDiscFace.timesUsed = tUsed;
    }

    private void pre(final vData v, final BlockFace bf) {
        if (bf == null) {
            return;
        }
        switch (bf) {
        case NORTH:
        case SOUTH:
            this.discNS(v);
            break;

        case EAST:
        case WEST:
            this.discEW(v);
            break;

        case UP:
        case DOWN:
            this.disc(v);
            break;

        default:
            break;
        }
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.pre(v, this.tb.getFace(this.lb));
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.lb.getX();
        this.by = this.lb.getY();
        this.bz = this.lb.getZ();
        this.pre(v, this.tb.getFace(this.lb));
    }
}
