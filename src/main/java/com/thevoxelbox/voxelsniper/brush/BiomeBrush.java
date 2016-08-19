package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

import com.flowpowered.math.GenericMath;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.util.Optional;

/**
 * Biome painting brush.
 */
public class BiomeBrush extends Brush {

    private BiomeType selectedBiome = BiomeTypes.PLAINS;

    public BiomeBrush() {
        this.setName("Biome (/b biome [Biome Name])");
    }

    private void biome(final SnipeData v) {
        double brushSize = v.getBrushSize();
        double brushSizeSquared = brushSize * brushSize;

        int minx = GenericMath.floor(this.targetBlock.getBlockX() - brushSize);
        int maxx = GenericMath.floor(this.targetBlock.getBlockX() + brushSize) + 1;
        int minz = GenericMath.floor(this.targetBlock.getBlockZ() - brushSize);
        int maxz = GenericMath.floor(this.targetBlock.getBlockZ() + brushSize) + 1;

        // @Robustness undo capturing for biome changes

        for (int x = minx; x <= maxx; x++) {
            double xs = (minx - x) * (minx - x);
            for (int z = minz; z <= maxz; z++) {
                double zs = (minz - z) * (minz - z);
                if ((xs + zs) <= brushSizeSquared) {
                    this.world.setBiome(x, z, this.selectedBiome);
                }
            }
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.biome(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.biome(v);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.custom(TextColors.GOLD, "Currently selected biome type: ", TextColors.DARK_GREEN, this.selectedBiome.getName());
    }

    @Override
    public final void parameters(final String[] args, final SnipeData v) {
        if (args[1].equalsIgnoreCase("info")) {
            v.sendMessage(TextColors.GOLD + "Biome Brush Parameters:");
            String availableBiomes = "";

            for (BiomeType biome : Sponge.getRegistry().getAllOf(BiomeType.class)) {
                availableBiomes += ", " + biome.getId();
            }
            availableBiomes = availableBiomes.substring(2);

            v.sendMessage(TextColors.DARK_BLUE, "Available biomes: ", TextColors.DARK_GREEN, availableBiomes);
        } else {
            String biomeName = args[1];

            Optional<BiomeType> biome = Sponge.getRegistry().getType(BiomeType.class, biomeName);
            if (!biome.isPresent()) {
                v.sendMessage(TextColors.RED, "Unknown biome type: ", TextColors.DARK_AQUA, biomeName);
            } else {
                this.selectedBiome = biome.get();
                v.sendMessage(TextColors.GOLD, "Currently selected biome type: ", TextColors.DARK_GREEN, this.selectedBiome.getName());
            }

        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.biome";
    }
}
