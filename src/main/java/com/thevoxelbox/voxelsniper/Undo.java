package com.thevoxelbox.voxelsniper;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.util.Vector;

import com.google.common.collect.Sets;

/**
 * Holds {@link BlockState}s that can be later on used to reset those block
 * locations back to the recorded states.
 */
public class Undo {

    private static final Set<Material> FALLING_MATERIALS = EnumSet.of(
            Material.WATER,
            Material.LAVA);
    private static final Set<Material> FALLOFF_MATERIALS = EnumSet.of(
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
    private final Set<Vector> containing = Sets.newHashSet();
    private final List<BlockState> all;
    private final List<BlockState> falloff;
    private final List<BlockState> dropdown;

    /**
     * Default constructor of a Undo container.
     */
    public Undo() {
        all = new LinkedList<BlockState>();
        falloff = new LinkedList<BlockState>();
        dropdown = new LinkedList<BlockState>();
    }

    /**
     * Get the number of blocks in the collection.
     *
     * @return size of the Undo collection
     */
    public int getSize() {
        return containing.size();
    }

    /**
     * Adds a Block to the collection.
     *
     * @param block Block to be added
     */
    public void put(Block block) {
        Vector pos = block.getLocation().toVector();
        if (this.containing.contains(pos)) {
            return;
        }
        this.containing.add(pos);
        if (Undo.FALLING_MATERIALS.contains(block.getType())) {
            dropdown.add(block.getState());
        } else if (Undo.FALLOFF_MATERIALS.contains(block.getType())) {
            falloff.add(block.getState());
        } else {
            all.add(block.getState());
        }
    }

    /**
     * Set the blockstates of all recorded blocks back to the state when they
     * were inserted.
     */
    public void undo() {

        for (BlockState blockState : all) {
            blockState.update(true, false);
            updateSpecialBlocks(blockState);
        }

        for (BlockState blockState : falloff) {
            blockState.update(true, false);
            updateSpecialBlocks(blockState);
        }

        for (BlockState blockState : dropdown) {
            blockState.update(true, false);
            updateSpecialBlocks(blockState);
        }
    }

    /**
     * @param blockState
     */
    private void updateSpecialBlocks(BlockState blockState) {
        BlockState currentState = blockState.getBlock().getState();
        if (blockState instanceof BrewingStand && currentState instanceof BrewingStand) {
            ((BrewingStand) currentState).getInventory().setContents(((BrewingStand) blockState).getInventory().getContents());
        } else if (blockState instanceof Chest && currentState instanceof Chest) {
            ((Chest) currentState).getInventory().setContents(((Chest) blockState).getInventory().getContents());
            ((Chest) currentState).getBlockInventory().setContents(((Chest) blockState).getBlockInventory().getContents());
            currentState.update();
        } else if (blockState instanceof CreatureSpawner && currentState instanceof CreatureSpawner) {
            ((CreatureSpawner) currentState).setSpawnedType(((CreatureSpawner) currentState).getSpawnedType());
            currentState.update();
        } else if (blockState instanceof Dispenser && currentState instanceof Dispenser) {
            ((Dispenser) currentState).getInventory().setContents(((Dispenser) blockState).getInventory().getContents());
            currentState.update();
        } else if (blockState instanceof Furnace && currentState instanceof Furnace) {
            ((Furnace) currentState).getInventory().setContents(((Furnace) blockState).getInventory().getContents());
            ((Furnace) currentState).setBurnTime(((Furnace) blockState).getBurnTime());
            ((Furnace) currentState).setCookTime(((Furnace) blockState).getCookTime());
            currentState.update();
        } else if (blockState instanceof NoteBlock && currentState instanceof NoteBlock) {
            ((NoteBlock) currentState).setNote(((NoteBlock) blockState).getNote());
            currentState.update();
        } else if (blockState instanceof Sign && currentState instanceof Sign) {
            int i = 0;
            for (String text : ((Sign) blockState).getLines()) {
                ((Sign) currentState).setLine(i++, text);
            }
            currentState.update();
        }
    }
}
