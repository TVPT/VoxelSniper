/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.undo;

/**
 *
 * @author Voxel
 */
public class uIterator {

    private uNode current;
    private int cur = 0;

    public uIterator(uNode start) {
        current = start;
    }

    public boolean hasNext() {
        return current.hasMore(cur) || current.hasNext();
    }

    public uBlock getNext() {
        if (current.hasMore(cur)) {
            return current.get(cur++);
        } else if (current.hasNext()) {
            current = current.getNext();
            cur = 0;
            return getNext();
        } else {
            return null;
        }
    }
}
