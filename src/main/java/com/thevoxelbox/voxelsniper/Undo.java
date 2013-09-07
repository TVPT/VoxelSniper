package com.thevoxelbox.voxelsniper;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;

/**
 * VoxelUndo class holds block data in form of BlockState objects.
 *
 * @author Voxel
 */
public class Undo
{

    private static final Set<Material> FALLING_MATERIALS = new TreeSet<Material>();
    private static final Set<Material> FALLOFF_MATERIALS = new TreeSet<Material>();

    static
    {
        Undo.FALLING_MATERIALS.add(Material.WATER);
        Undo.FALLING_MATERIALS.add(Material.STATIONARY_WATER);
        Undo.FALLING_MATERIALS.add(Material.LAVA);
        Undo.FALLING_MATERIALS.add(Material.STATIONARY_LAVA);

        Undo.FALLOFF_MATERIALS.add(Material.SAPLING);
        Undo.FALLOFF_MATERIALS.add(Material.BED_BLOCK);
        Undo.FALLOFF_MATERIALS.add(Material.POWERED_RAIL);
        Undo.FALLOFF_MATERIALS.add(Material.DETECTOR_RAIL);
        Undo.FALLOFF_MATERIALS.add(Material.LONG_GRASS);
        Undo.FALLOFF_MATERIALS.add(Material.DEAD_BUSH);
        Undo.FALLOFF_MATERIALS.add(Material.PISTON_EXTENSION);
        Undo.FALLOFF_MATERIALS.add(Material.YELLOW_FLOWER);
        Undo.FALLOFF_MATERIALS.add(Material.RED_ROSE);
        Undo.FALLOFF_MATERIALS.add(Material.BROWN_MUSHROOM);
        Undo.FALLOFF_MATERIALS.add(Material.RED_MUSHROOM);
        Undo.FALLOFF_MATERIALS.add(Material.TORCH);
        Undo.FALLOFF_MATERIALS.add(Material.FIRE);
        Undo.FALLOFF_MATERIALS.add(Material.CROPS);
        Undo.FALLOFF_MATERIALS.add(Material.SIGN_POST);
        Undo.FALLOFF_MATERIALS.add(Material.WOODEN_DOOR);
        Undo.FALLOFF_MATERIALS.add(Material.LADDER);
        Undo.FALLOFF_MATERIALS.add(Material.RAILS);
        Undo.FALLOFF_MATERIALS.add(Material.WALL_SIGN);
        Undo.FALLOFF_MATERIALS.add(Material.LEVER);
        Undo.FALLOFF_MATERIALS.add(Material.STONE_PLATE);
        Undo.FALLOFF_MATERIALS.add(Material.IRON_DOOR_BLOCK);
        Undo.FALLOFF_MATERIALS.add(Material.WOOD_PLATE);
        Undo.FALLOFF_MATERIALS.add(Material.REDSTONE_TORCH_OFF);
        Undo.FALLOFF_MATERIALS.add(Material.REDSTONE_TORCH_ON);
        Undo.FALLOFF_MATERIALS.add(Material.STONE_BUTTON);
        Undo.FALLOFF_MATERIALS.add(Material.SNOW);
        Undo.FALLOFF_MATERIALS.add(Material.CACTUS);
        Undo.FALLOFF_MATERIALS.add(Material.SUGAR_CANE_BLOCK);
        Undo.FALLOFF_MATERIALS.add(Material.CAKE_BLOCK);
        Undo.FALLOFF_MATERIALS.add(Material.DIODE_BLOCK_OFF);
        Undo.FALLOFF_MATERIALS.add(Material.DIODE_BLOCK_ON);
        Undo.FALLOFF_MATERIALS.add(Material.TRAP_DOOR);
        Undo.FALLOFF_MATERIALS.add(Material.PUMPKIN_STEM);
        Undo.FALLOFF_MATERIALS.add(Material.MELON_STEM);
        Undo.FALLOFF_MATERIALS.add(Material.VINE);
        Undo.FALLOFF_MATERIALS.add(Material.WATER_LILY);
        Undo.FALLOFF_MATERIALS.add(Material.NETHER_WARTS);
    }
    private final List<BlockState> all;
    private final List<BlockState> falloff;
    private final List<BlockState> dropdown;
    private final String worldName;
    private final World world;

    /**
     * Default constructor of a Undo container.
     *
     * @param wName name of the world the blocks reside in
     */
    public Undo(final String wName)
    {
        this.worldName = wName;
        this.world = Bukkit.getServer().getWorld(this.worldName);
        this.all = new LinkedList<BlockState>();
        this.falloff = new LinkedList<BlockState>();
        this.dropdown = new LinkedList<BlockState>();
    }

    /**
     * Get the number of blocks in the collection.
     *
     * @return size of the Undo collection
     */
    public final int getSize()
    {
        return this.all.size();
    }

    /**
     * Adds a Block to the collection.
     *
     * @param b Block to be added
     */
    public final void put(final Block b)
    {
        ListIterator<BlockState> iterator = this.all.listIterator();
        while(iterator.hasNext()) {
            if(iterator.next().getLocation().equals(b.getLocation())) {
                return;
            }
        }
        this.all.add(b.getState());
        if (Undo.FALLING_MATERIALS.contains(b.getType()))
        {
            this.dropdown.add(b.getState());
        }

        if (Undo.FALLOFF_MATERIALS.contains(b.getType()))
        {
            this.falloff.add(b.getState());
        }
    }

    /**
     * This method begins the process of replacing the blocks stored in this
     * collection.
     */
    public final void undo()
    {

        for (final BlockState _blockState : this.all)
        {
            if (this.falloff.contains(_blockState) || this.dropdown.contains(_blockState))
            {
                continue;
            }
            _blockState.getBlock().setTypeIdAndData(_blockState.getTypeId(), _blockState.getRawData(), false);
            updateSpecialBlocks(_blockState);
        }

        for (final BlockState _blockState : this.falloff)
        {
            _blockState.getBlock().setTypeIdAndData(_blockState.getTypeId(), _blockState.getRawData(), false);
            updateSpecialBlocks(_blockState);
        }

        for (final BlockState _blockState : this.dropdown)
        {
            _blockState.getBlock().setTypeIdAndData(_blockState.getTypeId(), _blockState.getRawData(), false);
            updateSpecialBlocks(_blockState);
        }
    }

    /**
     * @param blockState
     */
    private void updateSpecialBlocks(final BlockState blockState)
    {
        BlockState _currentState = blockState.getWorld().getBlockAt(blockState.getLocation()).getState();
        if (blockState instanceof BrewingStand)
        {
            if (_currentState instanceof BrewingStand)
            {
                ((BrewingStand) _currentState).getInventory().setContents(((BrewingStand) blockState).getInventory().getContents());
            }
        } else if (blockState instanceof Chest)
        {
            if (_currentState instanceof Chest)
            {
                ((Chest) _currentState).getInventory().setContents(((Chest) blockState).getInventory().getContents());
                ((Chest) _currentState).getBlockInventory().setContents(((Chest) blockState).getBlockInventory().getContents());
                _currentState.update();
            }
        } else if (blockState instanceof CreatureSpawner)
        {
            if (_currentState instanceof CreatureSpawner)
            {
                ((CreatureSpawner) _currentState).setSpawnedType(((CreatureSpawner) _currentState).getSpawnedType());
                _currentState.update();
            }
        } else if (blockState instanceof Dispenser)
        {
            if (_currentState instanceof Dispenser)
            {
                ((Dispenser) _currentState).getInventory().setContents(((Dispenser) blockState).getInventory().getContents());
                _currentState.update();
            }
        } else if (blockState instanceof Furnace)
        {
            if (_currentState instanceof Furnace)
            {
                ((Furnace) _currentState).getInventory().setContents(((Furnace) blockState).getInventory().getContents());
                ((Furnace) _currentState).setBurnTime(((Furnace) blockState).getBurnTime());
                ((Furnace) _currentState).setCookTime(((Furnace) blockState).getCookTime());
                _currentState.update();
            }
        } else if (blockState instanceof NoteBlock)
        {
            if (_currentState instanceof NoteBlock)
            {
                ((NoteBlock) _currentState).setNote(((NoteBlock) blockState).getNote());
                _currentState.update();
            }
        } else if (blockState instanceof Sign)
        {
            if (_currentState instanceof Sign)
            {
                int _i = 0;
                for (String _text : ((Sign) blockState).getLines())
                {
                    ((Sign) _currentState).setLine(_i++, _text);
                }
                _currentState.update();
            }
        }
    }
}
