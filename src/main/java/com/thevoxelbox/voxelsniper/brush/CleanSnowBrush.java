package com.thevoxelbox.voxelsniper.brush;

import com.flowpowered.math.GenericMath;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Removes floating snow tiles.
 */
public class CleanSnowBrush extends Brush {

    public CleanSnowBrush() {
        this.setName("Clean Snow");
    }

    private void cleanSnow(SnipeData v, Location<World> targetBlock) {
        double brushSize = v.getBrushSize();
        double brushSizeSquared = brushSize * brushSize;

        int minx = GenericMath.floor(targetBlock.getBlockX() - brushSize);
        int maxx = GenericMath.floor(targetBlock.getBlockX() + brushSize) + 1;
        int miny = Math.max(GenericMath.floor(targetBlock.getBlockY() - brushSize), 0);
        int maxy = Math.min(GenericMath.floor(targetBlock.getBlockY() + brushSize) + 1, WORLD_HEIGHT);
        int minz = GenericMath.floor(targetBlock.getBlockZ() - brushSize);
        int maxz = GenericMath.floor(targetBlock.getBlockZ() + brushSize) + 1;
        if (miny <= 0) {
            miny = 1;
        }
        // Approximate the size of the undo
        this.undo = new Undo(GenericMath.floor(Math.PI * (brushSize + 1) * (brushSize + 1) * (brushSize + 1) / 3));

        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = minx; x <= maxx; x++) {
            double xs = (minx - x) * (minx - x);
            for (int z = minz; z <= maxz; z++) {
                double zs = (minz - z) * (minz - z);
                for (int y = maxy; y >= miny; y--) {
                    double ys = (miny - y) * (miny - y);
                    if (xs + ys + zs < brushSizeSquared) {
                        BlockType below = this.world.getBlockType(x, y, z);
                        if (below == BlockTypes.SNOW_LAYER || below == BlockTypes.AIR) {
                            setBlockType(x, y, z, BlockTypes.AIR);
                        }
                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.cleanSnow(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.cleanSnow(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.cleansnow";
    }
}
