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

    public final void disc(final SnipeData v) {
        final int bsize = v.getBrushSize();

        for (int x = bsize; x >= -bsize; x--) {
            for (int y = bsize; y >= -bsize; y--) {
                this.current.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() + y));
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    public final void discEW(final SnipeData v) {
        final int bsize = v.getBrushSize();

        for (int x = bsize; x >= -bsize; x--) {
            for (int y = bsize; y >= -bsize; y--) {
                this.current.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ()));
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    public final void discNS(final SnipeData v) {
        final int bsize = v.getBrushSize();

        for (int x = bsize; x >= -bsize; x--) {
            for (int y = bsize; y >= -bsize; y--) {
                this.current.perform(this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + x, this.getBlockPositionZ() + y));
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    @Override
    public final int getTimesUsed() {
        return VoxelDiscFace.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        VoxelDiscFace.timesUsed = tUsed;
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
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.pre(v, this.getTargetBlock().getFace(this.getLastBlock()));
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getLastBlock().getX());
        this.setBlockPositionY(this.getLastBlock().getY());
        this.setBlockPositionZ(this.getLastBlock().getZ());
        this.pre(v, this.getTargetBlock().getFace(this.getLastBlock()));
    }
}
