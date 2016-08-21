package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Chunk;

import java.util.Optional;

/**
 * Creates flat oceans.
 */
public class FlatOceanBrush extends Brush {

    private static final int DEFAULT_WATER_LEVEL = 29;
    private static final int DEFAULT_FLOOR_LEVEL = 8;
    private int waterLevel = DEFAULT_WATER_LEVEL;
    private int floorLevel = DEFAULT_FLOOR_LEVEL;

    public FlatOceanBrush() {
        this.setName("FlatOcean");
    }

    private void flatOcean(final Chunk chunk) {
        int minx = chunk.getBlockMin().getX();
        int miny = chunk.getBlockMin().getY();
        int minz = chunk.getBlockMin().getZ();
        int maxx = chunk.getBlockMax().getX();
        int maxy = chunk.getBlockMax().getY();
        int maxz = chunk.getBlockMax().getZ();

        // @Robustness store undo?
        for (int x = minx; x <= maxx; x++) {
            for (int z = minz; z <= maxz; z++) {
                for (int y = miny; y <= maxy; y++) {
                    if (y <= this.floorLevel) {
                        setBlockType(x, y, z, BlockTypes.DIRT);
                    } else if (y <= this.waterLevel) {
                        setBlockType(x, y, z, BlockTypes.WATER, BlockChangeFlag.NONE);
                    } else {
                        setBlockType(x, y, z, BlockTypes.AIR);
                    }
                }
            }
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        Optional<Chunk> chunk = this.world.getChunk(this.targetBlock.getChunkPosition());
        if (chunk.isPresent()) {
            flatOcean(chunk.get());
        }
    }

    @Override
    protected final void powder(final SnipeData v) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Optional<Chunk> chunk = this.world.getChunk(this.targetBlock.getChunkPosition().add(x, 0, z));
                if (chunk.isPresent()) {
                    flatOcean(chunk.get());
                }
            }
        }
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.custom(TextColors.RED + "THIS BRUSH DOES NOT UNDO");
        vm.custom(TextColors.GREEN + "Water level set to " + this.waterLevel);
        vm.custom(TextColors.GREEN + "Ocean floor level set to " + this.floorLevel);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (int i = 1; i < par.length; i++) {
            final String parameter = par[i];

            if (parameter.equalsIgnoreCase("info")) {
                v.sendMessage(TextColors.GREEN + "yo[number] to set the Level to which the water will rise.");
                v.sendMessage(TextColors.GREEN + "yl[number] to set the Level to which the ocean floor will rise.");
            }
            if (parameter.startsWith("yo")) {
                try {
                    int newWaterLevel = Integer.parseInt(parameter.replace("yo", ""));
                    this.waterLevel = newWaterLevel;
                    if (this.waterLevel <= 0) {
                        v.sendMessage(TextColors.RED, "Water level cannot be negative.");
                        continue;
                    }
                    v.sendMessage(TextColors.GREEN + "Water Level set to " + this.waterLevel);
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid water level value.");
                }
            } else if (parameter.startsWith("yl")) {
                try {
                    int newFloorLevel = Integer.parseInt(parameter.replace("yl", ""));
                    this.floorLevel = newFloorLevel;
                    if (this.waterLevel <= 0) {
                        v.sendMessage(TextColors.RED, "Ocean floor level cannot be negative.");
                        continue;
                    }
                    v.sendMessage(TextColors.GREEN + "Ocean floor Level set to " + this.floorLevel);
                } catch (NumberFormatException e) {
                    v.sendMessage(TextColors.RED, "Invalid ocean floor level value.");
                }
            }
        }
        if (this.floorLevel < 0) {
            this.floorLevel = 0;
        }
        if (this.waterLevel <= this.floorLevel) {
            this.waterLevel = this.floorLevel + 1;
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.flatocean";
    }
}
