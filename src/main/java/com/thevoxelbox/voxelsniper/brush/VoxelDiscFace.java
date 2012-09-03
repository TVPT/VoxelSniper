package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.block.BlockFace;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Voxel
 */
public class VoxelDiscFace extends PerformBrush {
    private static int timesUsed = 0;

    public VoxelDiscFace() {
        this.setName("Voxel Disc Face");
    }

    private final void disc(final SnipeData v) {
        final int _bSize = v.getBrushSize();

        for (int _x = _bSize; _x >= -_bSize; _x--) {
            for (int _y = _bSize; _y >= -_bSize; _y--) {
                this.current.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _y));
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    private final void discEW(final SnipeData v) {
        final int _bSize = v.getBrushSize();

        for (int _x = _bSize; _x >= -_bSize; _x--) {
            for (int _y = _bSize; _y >= -_bSize; _y--) {
                this.current.perform(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ()));
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    private final void discNS(final SnipeData v) {
        final int _bSize = v.getBrushSize();

        for (int _x = _bSize; _x >= -_bSize; _x--) {
            for (int _y = _bSize; _y >= -_bSize; _y--) {
                this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + _x, this.getBlockPositionZ() + _y));
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    private void pre(final SnipeData v, final BlockFace bf) {
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
    protected final void arrow(final SnipeData v) {
        this.pre(v, this.getTargetBlock().getFace(this.getLastBlock()));
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.setBlockPositionX(this.getLastBlock().getX());
        this.setBlockPositionY(this.getLastBlock().getY());
        this.setBlockPositionZ(this.getLastBlock().getZ());
        this.pre(v, this.getTargetBlock().getFace(this.getLastBlock()));
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public final int getTimesUsed() {
        return VoxelDiscFace.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
        VoxelDiscFace.timesUsed = tUsed;
    }
}
