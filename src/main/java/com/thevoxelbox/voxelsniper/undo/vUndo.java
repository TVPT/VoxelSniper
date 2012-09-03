package com.thevoxelbox.voxelsniper.undo;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * VoxelUndo class holds block data in form of vBlock objects
 * 
 * @author Voxel
 */
public class vUndo {

    private static final Set<Material> FALLING_MATERIALS = new TreeSet<Material>();
    private static final Set<Material> FALLOFF_MATERIALS = new TreeSet<Material>();

    static {
        vUndo.FALLING_MATERIALS.add(Material.WATER);
        vUndo.FALLING_MATERIALS.add(Material.STATIONARY_WATER);
        vUndo.FALLING_MATERIALS.add(Material.LAVA);
        vUndo.FALLING_MATERIALS.add(Material.STATIONARY_LAVA);

        vUndo.FALLOFF_MATERIALS.add(Material.SAPLING);
        vUndo.FALLOFF_MATERIALS.add(Material.BED_BLOCK);
        vUndo.FALLOFF_MATERIALS.add(Material.POWERED_RAIL);
        vUndo.FALLOFF_MATERIALS.add(Material.DETECTOR_RAIL);
        vUndo.FALLOFF_MATERIALS.add(Material.LONG_GRASS);
        vUndo.FALLOFF_MATERIALS.add(Material.DEAD_BUSH);
        vUndo.FALLOFF_MATERIALS.add(Material.PISTON_EXTENSION);
        vUndo.FALLOFF_MATERIALS.add(Material.YELLOW_FLOWER);
        vUndo.FALLOFF_MATERIALS.add(Material.RED_ROSE);
        vUndo.FALLOFF_MATERIALS.add(Material.BROWN_MUSHROOM);
        vUndo.FALLOFF_MATERIALS.add(Material.RED_MUSHROOM);
        vUndo.FALLOFF_MATERIALS.add(Material.TORCH);
        vUndo.FALLOFF_MATERIALS.add(Material.FIRE);
        vUndo.FALLOFF_MATERIALS.add(Material.CROPS);
        vUndo.FALLOFF_MATERIALS.add(Material.SIGN_POST);
        vUndo.FALLOFF_MATERIALS.add(Material.WOODEN_DOOR);
        vUndo.FALLOFF_MATERIALS.add(Material.LADDER);
        vUndo.FALLOFF_MATERIALS.add(Material.RAILS);
        vUndo.FALLOFF_MATERIALS.add(Material.WALL_SIGN);
        vUndo.FALLOFF_MATERIALS.add(Material.LEVER);
        vUndo.FALLOFF_MATERIALS.add(Material.STONE_PLATE);
        vUndo.FALLOFF_MATERIALS.add(Material.IRON_DOOR_BLOCK);
        vUndo.FALLOFF_MATERIALS.add(Material.WOOD_PLATE);
        vUndo.FALLOFF_MATERIALS.add(Material.REDSTONE_TORCH_OFF);
        vUndo.FALLOFF_MATERIALS.add(Material.REDSTONE_TORCH_ON);
        vUndo.FALLOFF_MATERIALS.add(Material.STONE_BUTTON);
        vUndo.FALLOFF_MATERIALS.add(Material.SNOW);
        vUndo.FALLOFF_MATERIALS.add(Material.CACTUS);
        vUndo.FALLOFF_MATERIALS.add(Material.SUGAR_CANE_BLOCK);
        vUndo.FALLOFF_MATERIALS.add(Material.CAKE_BLOCK);
        vUndo.FALLOFF_MATERIALS.add(Material.DIODE_BLOCK_OFF);
        vUndo.FALLOFF_MATERIALS.add(Material.DIODE_BLOCK_ON);
        vUndo.FALLOFF_MATERIALS.add(Material.TRAP_DOOR);
        vUndo.FALLOFF_MATERIALS.add(Material.PUMPKIN_STEM);
        vUndo.FALLOFF_MATERIALS.add(Material.MELON_STEM);
        vUndo.FALLOFF_MATERIALS.add(Material.VINE);
        vUndo.FALLOFF_MATERIALS.add(Material.WATER_LILY);
        vUndo.FALLOFF_MATERIALS.add(Material.NETHER_WARTS);
    }

    private final List<uBlock> all;
    private final List<uBlock> falloff;
    private final List<uBlock> dropdown;

    private final String worldName;

    private final World world;

    /**
     * Default constructor of a vUndo container
     * 
     * @param wName
     *            name of the world the blocks reside in
     */
    public vUndo(final String wName) {
        this.worldName = wName;
        this.world = Bukkit.getServer().getWorld(this.worldName);
        this.all = new LinkedList<uBlock>();
        this.falloff = new LinkedList<uBlock>();
        this.dropdown = new LinkedList<uBlock>();
    }

    /**
     * Get the number of blocks in the collection
     * 
     * @return size of the vUndo collection
     */
    public int getSize() {
        return this.all.size();
    }

    /**
     * Adds a Block to the collection
     * 
     * @param b
     *            Block to be added
     */
    public void put(final Block b) { // 63 68
        if (b.getTypeId() == 63 || b.getTypeId() == 68) {
            this.put(new uBlockSign(b));
        } else if (b.getTypeId() == 25) {
            this.put(new uBlockNote(b));
        } else {
            this.put(new uBlock(b));
        }
    }

    /**
     * Adds a vBlock to the collection
     * 
     * @param b
     *            vBlock to be added
     */
    public void put(final uBlock b) {
        this.all.add(b);
        if (vUndo.FALLOFF_MATERIALS.contains(Material.getMaterial(b.id))) {
            this.falloff.add(b);
        } else if (vUndo.FALLING_MATERIALS.contains(Material.getMaterial(b.id))) {
            this.dropdown.add(b);
        }
    }

    /**
     * This method begins the process of replacing the blocks stored in this collection
     */
    public void undo() {

        for (final uBlock _block : this.all) {
            if (this.falloff.contains(_block) || this.dropdown.contains(_block)) {
                continue;
            }
            _block.set(this.world);
        }

        for (final uBlock _block : this.falloff) {
            _block.set(this.world);
        }

        for (final uBlock _block : this.dropdown) {
            _block.set(this.world);
        }
    }
}
