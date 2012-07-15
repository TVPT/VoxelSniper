package com.thevoxelbox.voxelsniper;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * VoxelUndo class holds block data in form of vBlock objects.
 * 
 * @author Voxel
 */
public class vUndo {

    private static final HashSet<Integer> FALL_OFF_LIST = new HashSet<Integer>();

    /**
     * Checks whether a block falls. Water, lava, sand, gravel etc.
     * 
     * @param id
     *            The id to be checked.
     * @return true if the block falls. Otherwise false
     */
    public static boolean falling(final int id) {
        if (id > 7 && id < 14) {
            return true;
        }
        return false;

    }

    private final HashSet<vBlock> hm = new HashSet<vBlock>();
    private final HashSet<vBlock> fall = new HashSet<vBlock>();
    private final HashSet<vBlock> drop = new HashSet<vBlock>();
    private final String worldName;

    private World w;

    static {
        vUndo.FALL_OFF_LIST.add(6);
        vUndo.FALL_OFF_LIST.add(37);
        vUndo.FALL_OFF_LIST.add(37);
        vUndo.FALL_OFF_LIST.add(39);
        vUndo.FALL_OFF_LIST.add(40);
        vUndo.FALL_OFF_LIST.add(50);
        vUndo.FALL_OFF_LIST.add(51);
        vUndo.FALL_OFF_LIST.add(55);
        vUndo.FALL_OFF_LIST.add(59);
        vUndo.FALL_OFF_LIST.add(63);
        vUndo.FALL_OFF_LIST.add(64);
        vUndo.FALL_OFF_LIST.add(69);
        vUndo.FALL_OFF_LIST.add(70);
        vUndo.FALL_OFF_LIST.add(71);
        vUndo.FALL_OFF_LIST.add(72);
        vUndo.FALL_OFF_LIST.add(75);
        vUndo.FALL_OFF_LIST.add(76);
        vUndo.FALL_OFF_LIST.add(77);
        vUndo.FALL_OFF_LIST.add(83);
    }

    /**
     * Checks whether a block falls off. Doesn't stay in mid air.
     * 
     * @param id
     *            The id to be checked.
     * @return true if the block falls off. Otherwise false
     */
    public static boolean fallsOff(final int id) {
        return vUndo.FALL_OFF_LIST.contains(id);
    }

    /**
     * Default constructor of a vUndo container.
     * 
     * @param wName
     *            name of the world the blocks reside in
     */
    public vUndo(final String wName) {
        this.worldName = wName;
    }

    /**
     * Get the number of blocks in the collection.
     * 
     * @return size of the vUndo collection
     */
    public final int getSize() {
        return this.hm.size();
    }

    /**
     * Adds a Block to the collection.
     * 
     * @param b
     *            Block to be added
     */
    public final void put(final Block b) {
        this.hm.add(new vBlock(b));
    }

    /**
     * Adds a vBlock to the collection.
     * 
     * @param b
     *            vBlock to be added
     */
    public final void put(final vBlock b) {
        this.hm.add(b);
    }

    /**
     * This method begins the process of replacing the blocks stored in this collection.
     */
    public final void undo() {
        this.w = Bukkit.getServer().getWorld(this.worldName);
        for (final vBlock _vb : this.hm) {
            if (vUndo.fallsOff(_vb.id)) {
                this.fall.add(_vb);
            } else if (vUndo.falling(_vb.id)) {
                this.drop.add(_vb);
            } else {
                this.setBlock(_vb);
            }
        }
        for (final vBlock _vb : this.drop) {
            this.setBlock(_vb);
        }
        for (final vBlock _vb : this.fall) {
            this.setBlock(_vb);
        }
    }

    private void setBlock(final vBlock vb) {
        final Block _block = this.w.getBlockAt(vb.x, vb.y, vb.z);
        _block.setTypeIdAndData(vb.id, vb.d, false);
    }
}
