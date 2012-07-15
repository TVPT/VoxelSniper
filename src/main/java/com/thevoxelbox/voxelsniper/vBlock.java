package com.thevoxelbox.voxelsniper;

import org.bukkit.block.Block;

/**
 * @author Voxel
 * 
 */
public class vBlock {

    public int id;
    public int x;
    public int y;
    public int z;
    public byte d;

    /**
     * @param b
     */
    public vBlock(final Block b) {
        this.id = b.getTypeId();
        this.x = b.getX();
        this.y = b.getY();
        this.z = b.getZ();
        this.d = b.getData();
    }

    /**
     * @param b
     * @param i
     */
    public vBlock(final Block b, final int i) {
        this.id = i;
        this.x = b.getX();
        this.y = b.getY();
        this.z = b.getZ();
        this.d = b.getData();
    }
}
