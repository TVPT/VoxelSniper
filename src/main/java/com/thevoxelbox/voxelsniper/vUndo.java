package com.thevoxelbox.voxelsniper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * VoxelUndo class holds block data in form of vBlock objects.
 * 
 * @author Voxel
 */
public class vUndo {

    private static Set<Integer> FALL_OFF_LIST;

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
        HashSet<Integer> _temporarySet = new HashSet<Integer>();
        _temporarySet.add(6);
        _temporarySet.add(37);
        _temporarySet.add(37);
        _temporarySet.add(39);
        _temporarySet.add(40);
        _temporarySet.add(50);
        _temporarySet.add(51);
        _temporarySet.add(55);
        _temporarySet.add(59);
        _temporarySet.add(63);
        _temporarySet.add(64);
        _temporarySet.add(69);
        _temporarySet.add(70);
        _temporarySet.add(71);
        _temporarySet.add(72);
        _temporarySet.add(75);
        _temporarySet.add(76);
        _temporarySet.add(77);
        _temporarySet.add(83);
        vUndo.FALL_OFF_LIST = Collections.unmodifiableSet(_temporarySet);
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
