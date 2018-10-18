package com.thevoxelbox.voxelsniper;

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

    private static Tag<Material> getBlockTag(String name)
    {
        return Bukkit.getTag(Tag.REGISTRY_BLOCKS, vKey(name), Material.class);
    }

    //TODO: Create tag for this
    private static final Set<Material> NATURAL_MATERIALS = EnumSet.of(Material.STONE,
            Material.GRASS,
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
    public static final Tag<Material> NATURAL = getBlockTag("natural");

    //TODO: Create tag for this
    private static final Set<Material> EXCLUSIVE_MATERIALS = EnumSet.of(
            Material.AIR, Material.STONE, Material.GRASS, Material.DIRT, Material.SAND, Material.GRAVEL, Material.SANDSTONE);
    public static final Tag<Material> ERASER_EXCLUSIVE = getBlockTag("eraser_exclusive");

    //TODO: Create tag for this
    private static final Set<Material> LIQUID_MATERIALS = EnumSet.of(
            Material.WATER,
            Material.LAVA);
    public static final Tag<Material> LIQUID = getBlockTag("liquid");

    //TODO: Create tag for this
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
            Material.POWERED_RAIL,
            Material.DETECTOR_RAIL,
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
            Material.SIGN,
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
            Material.WALL_SIGN,
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
            Material.IRON_DOOR,
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
    public static final Tag<Material> POP_OFF = getBlockTag("pop_off");

    //TODO: Create tag for this
    private static final Set<Material> RESET_DENIED_UPDATE_MATERIALS = EnumSet.of(
            Material.SIGN,
            Material.WALL_SIGN,
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
    public static final Tag<Material> RESET_DENIED_UPDATE = getBlockTag("reset_denied_update");

    //TODO: Create tag for this
    private static final Set<Material> FLAMEABLE_MATERIALS = EnumSet.of(
            Material.WOOD,
            Material.SAPLING,
            Material.LOG,
            Material.LEAVES,
            Material.SPONGE,
            Material.COBWEB,
            Material.LONG_GRASS,
            Material.DEAD_BUSH,
            Material.WOOL,
            Material.YELLOW_FLOWER,
            Material.RED_ROSE,
            Material.TORCH,
            Material.FIRE,
            Material.WOOD_STAIRS,
            Material.CROPS,
            Material.SIGN,
            Material.WOODEN_DOOR,
            Material.LADDER,
            Material.WALL_SIGN,
            Material.WOOD_PLATE,
            Material.SNOW,
            Material.ICE,
            Material.SUGAR_CANE,
            Material.FENCE,
            Material.TRAP_DOOR,
            Material.VINE,
            Material.FENCE_GATE,
            Material.LILY_PAD
    );
    public static final Tag<Material> FLAMABLE = getBlockTag("flameable");

    //TODO: Create tag for this
    private static final Set<Material> FALLING_MATERIALS = EnumSet.of(Material.WATER, Material.LAVA, Material.SAND, Material.GRAVEL);
    public static final Tag<Material> FALLING = getBlockTag("falling");

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
    public static final Tag<Material> OCEAN_EXCLUDED = getBlockTag("ocean_excluded");
}
