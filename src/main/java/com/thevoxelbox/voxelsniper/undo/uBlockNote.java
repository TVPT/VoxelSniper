/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.undo;

import org.bukkit.Note;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.NoteBlock;

/**
 *
 * @author Voxel
 */
public class uBlockNote extends uBlock {

    protected Note n;

    public uBlockNote(Block bl) {
        super(bl);
        n = ((NoteBlock) bl.getState()).getNote();
    }

    public uBlockNote(Block bl, int nx, int ny, int nz) {
        super(bl, nx, ny, nz);
        n = ((NoteBlock) bl.getState()).getNote();
    }

    @Override
    public void set(World w) {
        Block b = w.getBlockAt(x, y, z);
        b.setTypeIdAndData(id, d, false);
        NoteBlock nb = ((NoteBlock) b.getState());
        nb.setNote(n);
        nb.update();
    }
}
