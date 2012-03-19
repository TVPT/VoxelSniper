package com.thevoxelbox.voxelsniper;

import org.bukkit.block.Block;

public class vBlock {

    public int id;
    public int x;
    public int y;
    public int z;
    public byte d;

    public vBlock(Block b) {
        this.id = b.getTypeId();
        this.x = b.getX();
        this.y = b.getY();
        this.z = b.getZ();
        this.d = b.getData();
    }

    public vBlock(Block b, int i) {
        this.id = i;
        this.x = b.getX();
        this.y = b.getY();
        this.z = b.getZ();
        this.d = b.getData();
    }
}
