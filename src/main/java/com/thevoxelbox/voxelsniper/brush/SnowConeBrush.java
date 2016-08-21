package com.thevoxelbox.voxelsniper.brush;

import com.flowpowered.math.GenericMath;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * Creates mounds of snow tiles.
 */
public class SnowConeBrush extends Brush {

    private void addSnow(final SnipeData v, Location<World> targetBlock) {
        double brushSize = v.getBrushSize();
        double brushSizeSquared = brushSize * brushSize;

        int minx = GenericMath.floor(targetBlock.getBlockX() - brushSize);
        int maxx = GenericMath.floor(targetBlock.getBlockX() + brushSize) + 1;
        int miny = Math.max(GenericMath.floor(targetBlock.getBlockY() - brushSize), 0);
        int maxy = Math.min(GenericMath.floor(targetBlock.getBlockY() + brushSize) + 1, WORLD_HEIGHT - 1);
        int minz = GenericMath.floor(targetBlock.getBlockZ() - brushSize);
        int maxz = GenericMath.floor(targetBlock.getBlockZ() + brushSize) + 1;

        this.undo = new Undo(GenericMath.floor((brushSize + 1) * (brushSize + 1)));

        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = minx; x <= maxx; x++) {
            double xs = (minx - x) * (minx - x);
            for (int z = minz; z <= maxz; z++) {
                double zs = (minz - z) * (minz - z);
                if (xs + zs < brushSizeSquared) {
                    int y = maxy;
                    boolean topFound = false;
                    for (; y >= miny; y--) {
                        if (this.world.getBlockType(x, y, z) != BlockTypes.AIR) {
                            topFound = true;
                            break;
                        }
                    }
                    if (topFound) {
                        if (y == maxy) {
                            BlockType above = this.world.getBlock(x, y + 1, z).getType();
                            if (above != BlockTypes.AIR) {
                                continue;
                            }
                        }
                        BlockState block = this.world.getBlock(x, y, z);
                        if (block.getType() != BlockTypes.SNOW_LAYER) {
                            setBlockType(x, y + 1, z, BlockTypes.SNOW_LAYER);
                        } else {
                            Optional<Integer> height = block.get(Keys.LAYER);
                            if (!height.isPresent()) {
                                BlockState newSnow = BlockTypes.SNOW_LAYER.getDefaultState().with(Keys.LAYER, 2).get();
                                setBlockState(x, y, z, newSnow);
                            } else {
                                int sheight = height.get();
                                if (sheight == block.getValue(Keys.LAYER).get().getMaxValue()) {
                                    setBlockType(x, y, z, BlockTypes.SNOW);
                                    setBlockType(x, y + 1, z, BlockTypes.SNOW_LAYER);
                                } else {
                                    BlockState newSnow = BlockTypes.SNOW_LAYER.getDefaultState().with(Keys.LAYER, sheight + 1).get();
                                    setBlockState(x, y, z, newSnow);
                                }
                            }
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
        addSnow(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        addSnow(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName("Snow Cone");
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.snowcone";
    }
}
