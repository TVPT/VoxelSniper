package com.thevoxelbox.voxelsniper.brush;

import com.flowpowered.math.vector.Vector3i;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Chunk;

import java.util.Optional;

/**
 * Shifts terrain vertically chunk by chunk.
 */
public class CanyonBrush extends Brush {

    private static final int SHIFT_LEVEL_MIN = -255;
    private static final int SHIFT_LEVEL_MAX = 255;
    protected int yLevel = -10;

    public CanyonBrush() {
        this.setName("Canyon");
    }

    protected final void canyon(SnipeData v, Chunk chunk) {
        int minx = chunk.getBlockMin().getX();
        int minz = chunk.getBlockMin().getZ();
        BlockState fillBlock = v.getVoxelIdState();
        if (fillBlock.getType() == BlockTypes.AIR) {
            fillBlock = BlockTypes.STONE.getDefaultState();
        }
        int sy = this.yLevel < 0 ? 0 : WORLD_HEIGHT;
        int ey = this.yLevel < 0 ? WORLD_HEIGHT : 0;
        int dir = this.yLevel < 0 ? 1 : -1;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = sy; y != ey; y += dir) {
                    int srcy = y - this.yLevel;
                    if (srcy < 0) {
                        setBlockState(minx + x, y, minz + z, fillBlock);
                    } else if (srcy > WORLD_HEIGHT) {
                        setBlockType(minx + x, y, minz + z, BlockTypes.AIR);
                    } else {
                        setBlockState(minx + x, y, minz + z, this.world.getBlock(minx + x, srcy, minz + z));
                    }
                }
            }
        }
    }

    @Override
    protected void arrow(final SnipeData v) {
        if (this.yLevel == 0) {
            return;
        }
        Optional<Chunk> chunk = this.world.getChunk(this.targetBlock.getChunkPosition());
        if (chunk.isPresent()) {
            this.undo = new Undo(4 * 16 * 256);
            canyon(v, chunk.get());
            v.owner().storeUndo(this.undo);
            this.undo = null;
        }
    }

    @Override
    protected void powder(final SnipeData v) {
        if (this.yLevel == 0) {
            return;
        }
        this.undo = new Undo(2 * 16 * 16 * 256);

        Vector3i chunkPos = this.targetBlock.getChunkPosition();
        for (int x = chunkPos.getX() - 1; x <= chunkPos.getX() + 1; x++) {
            for (int z = chunkPos.getZ() - 1; z <= chunkPos.getZ() + 1; z++) {
                Optional<Chunk> chunk = this.world.getChunk(x, 0, z);
                if (chunk.isPresent()) {
                    canyon(v, chunk.get());
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    public void info(final Message vm) {
        vm.brushName(this.getName());
        vm.custom(TextColors.GREEN + "Shift Level set to " + this.yLevel);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par.length == 0 || par[0].equalsIgnoreCase("info")) {
            v.sendMessage(TextColors.GREEN + "y[number] to set the Level to which the land will be shifted down");
        }
        if (par[0].startsWith("y")) {
            int _i = Integer.parseInt(par[1].replace("y", ""));
            if (_i < SHIFT_LEVEL_MIN) {
                _i = SHIFT_LEVEL_MIN;
            } else if (_i > SHIFT_LEVEL_MAX) {
                _i = SHIFT_LEVEL_MAX;
            }
            this.yLevel = _i;
            v.sendMessage(TextColors.GREEN, "Shift Level set to " + this.yLevel);
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.canyon";
    }
}
