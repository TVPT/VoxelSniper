package com.thevoxelbox.voxelsniper;

import com.google.common.collect.Sets;
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

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Holds {@link BlockState}s that can be later on used to reset those block
 * locations back to the recorded states.
 */
public class Undo {

    private static final Set<Material> FALLING_MATERIALS = EnumSet.of(
            Material.WATER,
            Material.STATIONARY_WATER,
            Material.LAVA,
            Material.STATIONARY_LAVA);
    private static final Set<Material> FALLOFF_MATERIALS = EnumSet.of(
            Material.SAPLING,
            Material.BED_BLOCK,
            Material.POWERED_RAIL,
            Material.DETECTOR_RAIL,
            Material.LONG_GRASS,
            Material.DEAD_BUSH,
            Material.PISTON_EXTENSION,
            Material.YELLOW_FLOWER,
            Material.RED_ROSE,
            Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM,
            Material.TORCH,
            Material.FIRE,
            Material.CROPS,
            Material.SIGN_POST,
            Material.WOODEN_DOOR,
            Material.LADDER,
            Material.RAILS,
            Material.WALL_SIGN,
            Material.LEVER,
            Material.STONE_PLATE,
            Material.IRON_DOOR_BLOCK,
            Material.WOOD_PLATE,
            Material.REDSTONE_TORCH_OFF,
            Material.REDSTONE_TORCH_ON,
            Material.REDSTONE_WIRE,
            Material.STONE_BUTTON,
            Material.SNOW,
            Material.CACTUS,
            Material.SUGAR_CANE_BLOCK,
            Material.CAKE_BLOCK,
            Material.DIODE_BLOCK_OFF,
            Material.DIODE_BLOCK_ON,
            Material.TRAP_DOOR,
            Material.PUMPKIN_STEM,
            Material.MELON_STEM,
            Material.VINE,
            Material.WATER_LILY,
            Material.NETHER_WARTS);
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
