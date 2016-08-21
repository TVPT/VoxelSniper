package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.flowpowered.math.GenericMath;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A brush that creates a solid ball.
 */
public class BallBrush extends PerformBrush {

    public BallBrush() {
        this.setName("Ball");
    }

    private void ball(SnipeData v, Location<World> targetBlock) {
        double brushSize = v.getBrushSize();
        double brushSizeSquared = brushSize * brushSize;

        int tx = targetBlock.getBlockX();
        int ty = targetBlock.getBlockY();
        int tz = targetBlock.getBlockZ();
        
        // Approximate the size of the undo to the volume of a one larger sphere
        this.undo = new Undo(GenericMath.floor(4 * Math.PI * (brushSize + 1) * (brushSize + 1) * (brushSize + 1) / 3));
        int size = GenericMath.floor(brushSize) + 1;
        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = -size; x <= size; x++) {
            for (int y = -size; y <= size; y++) {
                for (int z = -size; z <= size; z++) {
                    if (x * x + y * y + z * z < brushSizeSquared) {
                        perform(v, x + tx, y + ty, z + tz);
                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    protected void arrow(SnipeData v) {
        this.ball(v, this.targetBlock);
    }

    @Override
    protected void powder(SnipeData v) {
        this.ball(v, this.lastBlock);
    }

    @Override
    public void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.ball";
    }
}
