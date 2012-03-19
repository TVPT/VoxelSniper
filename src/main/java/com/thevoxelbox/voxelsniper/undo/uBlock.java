/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.undo;

import org.bukkit.World;
import org.bukkit.block.Block;

/**
 *
 * @author Voxel
 */
public class uBlock {

    protected int x;
    protected int y;
    protected int z;
    protected int id;
    protected byte d;

    public uBlock(Block bl) {
        x = bl.getX();
        y = bl.getY();
        z = bl.getZ();
        id = bl.getTypeId();
        d = bl.getData();
    }
    
    public uBlock(Block bl, int ni) {
        x = bl.getX();
        y = bl.getY();
        z = bl.getZ();
        id = ni;
    }

    public uBlock(Block bl, int nx, int ny, int nz) {
        x = nx;
        y = ny;
        z = nz;
        id = bl.getTypeId();
        d = bl.getData();
    }

    public void set(World w) {
        w.getBlockAt(x, y, z).setTypeIdAndData(id, d, false);
    }
}
