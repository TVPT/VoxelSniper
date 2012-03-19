/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.undo;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 *
 * @author Voxel
 */
public class uBlockSign extends uBlock {

    protected String[] lines;

    public uBlockSign(Block bl) {
        super(bl);
        lines = ((Sign) bl.getState()).getLines();
    }

    public uBlockSign(Block bl, int nx, int ny, int nz) {
        super(bl, nx, ny, nz);
        lines = ((Sign) bl.getState()).getLines();
    }

    @Override
    public void set(World w) {
        Block b = w.getBlockAt(x, y, z);
        b.setTypeIdAndData(id, d, false);
        Sign s = ((Sign) b.getState());
        int i = 0;
        for (String str : lines) {
            s.setLine(i++, str);
        }
        s.update();
    }
}
