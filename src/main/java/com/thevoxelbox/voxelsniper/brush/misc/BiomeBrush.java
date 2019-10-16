/*
 * This file is part of VoxelSniper, licensed under the MIT License (MIT).
 *
 * Copyright (c) The VoxelBox <http://thevoxelbox.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.thevoxelbox.voxelsniper.brush.misc;

import com.flowpowered.math.GenericMath;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.Brush;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.util.Optional;

/**
 * Biome painting brush.
 */
@Brush.BrushInfo(
    name = "Biome",
    aliases = {"bio", "biome"},
    permission = "voxelsniper.brush.biome",
    category = Brush.BrushCategory.MISC
)
public class BiomeBrush extends Brush {

    private BiomeType selectedBiome = BiomeTypes.PLAINS;

    public BiomeBrush() {
    }

    private void biome(SnipeData v, Location<World> targetBlock) {
        double brushSize = v.getBrushSize();
        double brushSizeSquared = brushSize * brushSize;

        int tx = targetBlock.getBlockX();
        int tz = targetBlock.getBlockZ();
        int minx = GenericMath.floor(this.targetBlock.getBlockX() - brushSize);
        int maxx = GenericMath.floor(this.targetBlock.getBlockX() + brushSize) + 1;
        int minz = GenericMath.floor(this.targetBlock.getBlockZ() - brushSize);
        int maxz = GenericMath.floor(this.targetBlock.getBlockZ() + brushSize) + 1;

        // @Robustness undo capturing for biome changes

        for (int x = minx; x <= maxx; x++) {
            double xs = (tx - x) * (tx - x);
            for (int z = minz; z <= maxz; z++) {
                double zs = (tz - z) * (tz - z);
                if ((xs + zs) <= brushSizeSquared) {
                    this.world.setBiome(x, 0, z, this.selectedBiome);
                }
            }
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.biome(v, this.targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.biome(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.info.name());
        vm.size();
        vm.custom(TextColors.GOLD, "Currently selected biome type: ", TextColors.DARK_GREEN, this.selectedBiome.getName());
    }

    @Override
    public final void parameters(final String[] args, final SnipeData v) {
        if (args[0].equalsIgnoreCase("info")) {
            v.sendMessage(TextColors.GOLD + "Biome Brush Parameters:");
            String availableBiomes = "";

            for (BiomeType biome : Sponge.getRegistry().getAllOf(BiomeType.class)) {
                availableBiomes += ", " + biome.getId();
            }
            availableBiomes = availableBiomes.substring(2);

            v.sendMessage(TextColors.DARK_BLUE, "Available biomes: ", TextColors.DARK_GREEN, availableBiomes);
        } else {
            String biomeName = args[0];
            Optional<BiomeType> biome = Sponge.getRegistry().getType(BiomeType.class, biomeName);
            if (!biome.isPresent()) {
                v.sendMessage(TextColors.RED, "Unknown biome type: ", TextColors.DARK_AQUA, biomeName);
            } else {
                this.selectedBiome = biome.get();
                v.sendMessage(TextColors.GOLD, "Currently selected biome type: ", TextColors.DARK_GREEN, this.selectedBiome.getName());
            }

        }
    }
}
