package com.thevoxelbox.voxelsniper.util;

import org.bukkit.block.Block;

public class BlockWrapper {

    public int id;
    public int x;
    public int y;
    public int z;
    public byte d;

    public BlockWrapper(Block b) {
        this.id = b.getTypeId();
        this.x = b.getX();
        this.y = b.getY();
        this.z = b.getZ();
        this.d = b.getData();
    }

    public BlockWrapper(Block b, int i) {
        this.id = i;
        this.x = b.getX();
        this.y = b.getY();
        this.z = b.getZ();
        this.d = b.getData();
    }
}
