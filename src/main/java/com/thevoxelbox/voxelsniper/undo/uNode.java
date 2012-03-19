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
public class uNode {

    private uNode next;
    private uBlock[] col;
    private int num = 0;

    public uNode(int scale) {
        col = new uBlock[scale];
    }

    public boolean add(uBlock b) {
        if (num < col.length) {
            col[num++] = b;
            return true;
        } else {
            return false;
        }
    }

    public uNode setNext(uNode n) {
        next = n;
        return next;
    }

    public uNode getNext() {
        return next;
    }

    public boolean hasNext() {
        return next != null;
    }

    public boolean hasMore(int cur) {
        return cur < num;
    }

    public uBlock get(int cur) {
        return col[cur];
    }

    public void setAll(World w) {
        if (num == col.length) {
            for (uBlock b : col) {
                b.set(w);
            }
            if (next != null) {
                next.setAll(w);
                return;
            } else {
                return;
            }
        } else {
            for (int x = 0; x < num; x++) {
                col[x].set(w);
            }
        }
    }

    public int getSize() {
        if (num == col.length) {
            if (next == null) {
                return num;
            } else {
                return num + next.getSize();
            }
        } else {
            return num;
        }
    }
}
