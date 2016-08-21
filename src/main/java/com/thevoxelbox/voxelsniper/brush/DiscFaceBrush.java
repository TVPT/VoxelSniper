package com.thevoxelbox.voxelsniper.brush;

import com.flowpowered.math.GenericMath;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A disc aligned to the the target block face.
 */
public class DiscFaceBrush extends PerformBrush {

    public DiscFaceBrush() {
        this.setName("Disc Face");
    }

    private void disc(final SnipeData v, Location<World> targetBlock, Direction axis) {
        double brushSize = v.getBrushSize();
        double brushSizeSquared = brushSize * brushSize;

        int tx = targetBlock.getBlockX();
        int ty = targetBlock.getBlockY();
        int tz = targetBlock.getBlockZ();

        this.undo = new Undo(GenericMath.floor(Math.PI * (brushSize + 1) * (brushSize + 1)));
        int size = GenericMath.floor(brushSize) + 1;
        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = -size; x <= size; x++) {
            for (int z = -size; z <= size; z++) {
                if (x * x + z * z < brushSizeSquared) {
                    if (axis == Direction.UP) {
                        perform(v, x + tx, targetBlock.getBlockY(), z + tz);
                    } else if (axis == Direction.NORTH) {
                        perform(v, x + tx, z + ty, targetBlock.getBlockZ());
                    } else if (axis == Direction.EAST) {
                        perform(v, targetBlock.getBlockX(), x + ty, z + tz);
                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    private void pre(final SnipeData v, Location<World> target) {
        if (this.lastBlock.getBlockY() != this.targetBlock.getBlockY()) {
            disc(v, target, Direction.UP);
        } else if (this.lastBlock.getBlockX() != this.targetBlock.getBlockX()) {
            disc(v, target, Direction.EAST);
        } else if (this.lastBlock.getBlockZ() != this.targetBlock.getBlockZ()) {
            disc(v, target, Direction.NORTH);
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.pre(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.pre(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.discface";
    }
}
