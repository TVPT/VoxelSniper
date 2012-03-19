/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.undo;

import org.bukkit.World;

/**
 *
 * @author Voxel
 */
public class uCollection {

    private uNode start;
    private uNode end;
    private int scale;

    public uCollection() {
        scale = 1000;
        start = new uNode(scale);
        end = start;
    }

    public uCollection(int size) {
        scale = size;
        start = new uNode(scale);
        end = start;
    }

    public void add(uBlock b) {
        if (!end.add(b)) {
            end = end.setNext(new uNode(scale));
            add(b);
        }
    }

    public void setAll(World w) {
        start.setAll(w);
    }
    
    public uIterator getIterator() {
        return new uIterator(start);
    }

    public int getSize() {
        return start.getSize();
    }
}
