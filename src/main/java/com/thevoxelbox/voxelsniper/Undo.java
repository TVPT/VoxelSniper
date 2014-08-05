package com.thevoxelbox.voxelsniper;

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

import com.thevoxelbox.voxelsniper.util.CoreProtectUtils;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Holds {@link BlockState}s that can be later on used to reset those block locations back to the recorded states.
 */
public class Undo
{

    private static final Set<Material> FALLING_MATERIALS = EnumSet.of(
            Material.WATER,
            Material.STATIONARY_WATER,
            Material.LAVA,
            Material.STATIONARY_LAVA
    );
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
            Material.NETHER_WARTS
    );
    private final List<BlockState> all;
    private final List<BlockState> falloff;
    private final List<BlockState> dropdown;

    /**
     * Default constructor of a Undo container.
     */
    public Undo()
    {
        all = new LinkedList<BlockState>();
        falloff = new LinkedList<BlockState>();
        dropdown = new LinkedList<BlockState>();
    }

    /**
     * Get the number of blocks in the collection.
     *
     * @return size of the Undo collection
     */
    public int getSize()
    {
        return all.size();
    }

    /**
     * Adds a Block to the collection.
     *
     * @param block Block to be added
     */
    public void put(Block block)
    {
        for (BlockState blockState : all)
        {
            if (blockState.getLocation().equals(block.getLocation()))
            {
                return;
            }
        }
        all.add(block.getState());

        if (Undo.FALLING_MATERIALS.contains(block.getType()))
        {
            dropdown.add(block.getState());
        }

        if (Undo.FALLOFF_MATERIALS.contains(block.getType()))
        {
            falloff.add(block.getState());
        }
    }

    /**
     * Set the blockstates of all recorded blocks back to the state when they were inserted.
     */
    public void undo(String name)
    {

        for (BlockState blockState : all)
        {
            if (falloff.contains(blockState) || dropdown.contains(blockState))
            {
                continue;
            }
            CoreProtectUtils.logBlockRemove(blockState.getBlock(), name);
            blockState.update(true, false);
            updateSpecialBlocks(blockState);
            CoreProtectUtils.logBlockPlace(blockState, name);
        }

        for (BlockState blockState : falloff)
        {
            CoreProtectUtils.logBlockRemove(blockState.getBlock(), name);
            blockState.update(true, false);
            updateSpecialBlocks(blockState);
            CoreProtectUtils.logBlockPlace(blockState, name);
        }

        for (BlockState blockState : dropdown)
        {
            CoreProtectUtils.logBlockRemove(blockState.getBlock(), name);
            blockState.update(true, false);
            updateSpecialBlocks(blockState);
            CoreProtectUtils.logBlockPlace(blockState, name);
        }
    }

    /**
     * @param blockState
     */
    private void updateSpecialBlocks(BlockState blockState)
    {
        BlockState currentState = blockState.getBlock().getState();
        if (blockState instanceof BrewingStand && currentState instanceof BrewingStand)
        {
            ((BrewingStand) currentState).getInventory().setContents(((BrewingStand) blockState).getInventory().getContents());
        }
        else if (blockState instanceof Chest && currentState instanceof Chest)
        {
            ((Chest) currentState).getInventory().setContents(((Chest) blockState).getInventory().getContents());
            ((Chest) currentState).getBlockInventory().setContents(((Chest) blockState).getBlockInventory().getContents());
            currentState.update();
        }
        else if (blockState instanceof CreatureSpawner && currentState instanceof CreatureSpawner)
        {
            ((CreatureSpawner) currentState).setSpawnedType(((CreatureSpawner) currentState).getSpawnedType());
            currentState.update();
        }
        else if (blockState instanceof Dispenser && currentState instanceof Dispenser)
        {
            ((Dispenser) currentState).getInventory().setContents(((Dispenser) blockState).getInventory().getContents());
            currentState.update();
        }
        else if (blockState instanceof Furnace && currentState instanceof Furnace)
        {
            ((Furnace) currentState).getInventory().setContents(((Furnace) blockState).getInventory().getContents());
            ((Furnace) currentState).setBurnTime(((Furnace) blockState).getBurnTime());
            ((Furnace) currentState).setCookTime(((Furnace) blockState).getCookTime());
            currentState.update();
        }
        else if (blockState instanceof NoteBlock && currentState instanceof NoteBlock)
        {
            ((NoteBlock) currentState).setNote(((NoteBlock) blockState).getNote());
            currentState.update();
        }
        else if (blockState instanceof Sign && currentState instanceof Sign)
        {
            int i = 0;
            for (String text : ((Sign) blockState).getLines())
            {
                ((Sign) currentState).setLine(i++, text);
            }
            currentState.update();
        }
    }
}
