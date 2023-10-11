package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.util.SetTag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

import java.util.EnumSet;
import java.util.Set;

public class VTags {

    private static NamespacedKey vKey(String name)
    {
        return new NamespacedKey(VoxelSniper.getInstance(), name);
    }

    private static Tag<Material> getBlockTag(String name, Set<Material> defaults)
    {
        Tag<Material> tag = Bukkit.getTag(Tag.REGISTRY_BLOCKS, vKey(name), Material.class);
        return tag == null ? new SetTag<Material>(defaults, vKey(name)) : tag;
    }

    private static final Set<Material> NATURAL_MATERIALS = EnumSet.of(Material.STONE,
            Material.GRASS_BLOCK,
            Material.DIRT,
            Material.SAND,
            Material.GRAVEL,
            Material.GOLD_ORE,
            Material.IRON_ORE,
            Material.COAL_ORE,
            Material.SANDSTONE,
            Material.MOSSY_COBBLESTONE,
            Material.CLAY,
            Material.OBSIDIAN,
            Material.SNOW);
    public static final Tag<Material> NATURAL = getBlockTag("natural", NATURAL_MATERIALS);

    private static final Set<Material> EXCLUSIVE_MATERIALS = EnumSet.of(
            Material.AIR, Material.STONE, Material.GRASS, Material.DIRT, Material.SAND, Material.GRAVEL, Material.SANDSTONE);
    public static final Tag<Material> ERASER_EXCLUSIVE = getBlockTag("eraser_exclusive", EXCLUSIVE_MATERIALS);

    private static final Set<Material> LIQUID_MATERIALS = EnumSet.of(
            Material.WATER,
            Material.LAVA);
    public static final Tag<Material> LIQUID = getBlockTag("liquid", LIQUID_MATERIALS);

    private static final Set<Material> POP_OFF_MATERIALS = EnumSet.of(
            Material.OAK_SAPLING,
            Material.JUNGLE_SAPLING,
            Material.DARK_OAK_SAPLING,
            Material.BIRCH_SAPLING,
            Material.ACACIA_SAPLING,
            Material.SPRUCE_SAPLING,
            Material.BLACK_BED,
            Material.BLUE_BED,
            Material.BROWN_BED,
            Material.CYAN_BED,
            Material.GRAY_BED,
            Material.GREEN_BED,
            Material.LIGHT_BLUE_BED,
            Material.LIGHT_GRAY_BED,
            Material.LIME_BED,
            Material.MAGENTA_BED,
            Material.ORANGE_BED,
            Material.PINK_BED,
            Material.PURPLE_BED,
            Material.RED_BED,
            Material.WHITE_BED,
            Material.YELLOW_BED,
            Material.TALL_GRASS,
            Material.DEAD_BUSH,
            Material.PISTON_HEAD,
            Material.DANDELION,
            Material.POPPY,
            Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM,
            Material.TORCH,
            Material.FIRE,
            Material.WHEAT,
            Material.OAK_SIGN,
            Material.SPRUCE_SIGN,
            Material.BIRCH_SIGN,
            Material.JUNGLE_SIGN,
            Material.ACACIA_SIGN,
            Material.DARK_OAK_SIGN,
            Material.CRIMSON_SIGN,
            Material.WARPED_SIGN,
            Material.DARK_OAK_DOOR,
            Material.ACACIA_DOOR,
            Material.BIRCH_DOOR,
            Material.IRON_DOOR,
            Material.JUNGLE_DOOR,
            Material.OAK_DOOR,
            Material.SPRUCE_DOOR,
            Material.LADDER,
            Material.RAIL,
            Material.ACTIVATOR_RAIL,
            Material.DETECTOR_RAIL,
            Material.POWERED_RAIL,
            Material.OAK_WALL_SIGN,
            Material.SPRUCE_WALL_SIGN,
            Material.BIRCH_WALL_SIGN,
            Material.JUNGLE_WALL_SIGN,
            Material.ACACIA_WALL_SIGN,
            Material.DARK_OAK_WALL_SIGN,
            Material.CRIMSON_WALL_SIGN,
            Material.WARPED_WALL_SIGN,
            Material.LEVER,
            Material.ACACIA_PRESSURE_PLATE,
            Material.BIRCH_PRESSURE_PLATE,
            Material.JUNGLE_PRESSURE_PLATE,
            Material.OAK_PRESSURE_PLATE,
            Material.SPRUCE_PRESSURE_PLATE,
            Material.STONE_PRESSURE_PLATE,
            Material.DARK_OAK_PRESSURE_PLATE,
            Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
            Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
            Material.REDSTONE_TORCH,
            Material.REDSTONE_WIRE,
            Material.STONE_BUTTON,
            Material.SNOW,
            Material.CACTUS,
            Material.SUGAR_CANE,
            Material.CAKE,
            Material.REPEATER,
            Material.ACACIA_TRAPDOOR,
            Material.BIRCH_TRAPDOOR,
            Material.DARK_OAK_TRAPDOOR,
            Material.IRON_TRAPDOOR,
            Material.JUNGLE_TRAPDOOR,
            Material.OAK_TRAPDOOR,
            Material.SPRUCE_TRAPDOOR,
            Material.PUMPKIN_STEM,
            Material.MELON_STEM,
            Material.VINE,
            Material.LILY_PAD,
            Material.NETHER_WART);
    public static final Tag<Material> POP_OFF = getBlockTag("pop_off", POP_OFF_MATERIALS);

    private static final Set<Material> RESET_DENIED_UPDATE_MATERIALS = EnumSet.of(
            Material.OAK_SIGN,
            Material.SPRUCE_SIGN,
            Material.BIRCH_SIGN,
            Material.JUNGLE_SIGN,
            Material.ACACIA_SIGN,
            Material.DARK_OAK_SIGN,
            Material.CRIMSON_SIGN,
            Material.WARPED_SIGN,
            Material.OAK_WALL_SIGN,
            Material.SPRUCE_WALL_SIGN,
            Material.BIRCH_WALL_SIGN,
            Material.JUNGLE_WALL_SIGN,
            Material.ACACIA_WALL_SIGN,
            Material.DARK_OAK_WALL_SIGN,
            Material.CRIMSON_WALL_SIGN,
            Material.WARPED_WALL_SIGN,
            Material.CHEST,
            Material.FURNACE,
            Material.REDSTONE_TORCH,
            Material.REDSTONE_WIRE,
            Material.REPEATER,
            Material.SPRUCE_DOOR,
            Material.OAK_DOOR,
            Material.JUNGLE_DOOR,
            Material.BIRCH_DOOR,
            Material.ACACIA_DOOR,
            Material.DARK_OAK_DOOR,
            Material.IRON_DOOR,
            Material.ACACIA_FENCE_GATE,
            Material.BIRCH_FENCE_GATE,
            Material.JUNGLE_FENCE_GATE,
            Material.OAK_FENCE_GATE,
            Material.SPRUCE_FENCE_GATE,
            Material.DARK_OAK_FENCE_GATE,
            Material.AIR);
    public static final Tag<Material> RESET_DENIED_UPDATE = getBlockTag("reset_denied_update", RESET_DENIED_UPDATE_MATERIALS);

    private static Set<Material> fallingMaterials()
    {
        Set<Material> accum = EnumSet.noneOf(Material.class);

        for (Material material : Material.values())
        {
            if(material.hasGravity())
            {
                accum.add(material);
            }
        }

        //Fluids aren't considered by the method
        accum.add(Material.WATER);
        accum.add(Material.LAVA);

        return accum;
    }

    private static final Set<Material> FALLING_MATERIALS = fallingMaterials();
    public static final Tag<Material> FALLING = getBlockTag("falling", FALLING_MATERIALS);

    private static final Set<Material> OCEAN_EXLUCDED_MATERIALS = EnumSet.of(
            Material.AIR,
            Material.OAK_SAPLING,
            Material.JUNGLE_SAPLING,
            Material.DARK_OAK_SAPLING,
            Material.BIRCH_SAPLING,
            Material.ACACIA_SAPLING,
            Material.SPRUCE_SAPLING,
            Material.WATER,
            Material.LAVA,
            Material.ACACIA_LOG,
            Material.BIRCH_LOG,
            Material.DARK_OAK_LOG,
            Material.JUNGLE_LOG,
            Material.OAK_LOG,
            Material.SPRUCE_LOG,
            Material.ACACIA_LEAVES,
            Material.BIRCH_LEAVES,
            Material.DARK_OAK_LEAVES,
            Material.JUNGLE_LEAVES,
            Material.OAK_LEAVES,
            Material.SPRUCE_LEAVES,
            Material.DANDELION,
            Material.POPPY,
            Material.RED_MUSHROOM,
            Material.BROWN_MUSHROOM,
            Material.MELON,
            Material.MELON_STEM,
            Material.PUMPKIN,
            Material.PUMPKIN_STEM,
            Material.COCOA,
            Material.SNOW,
            Material.SNOW_BLOCK,
            Material.ICE,
            Material.SUGAR_CANE,
            Material.TALL_GRASS,
            Material.SNOW);
    public static final Tag<Material> OCEAN_EXCLUDED = getBlockTag("ocean_excluded", OCEAN_EXLUCDED_MATERIALS);
}
