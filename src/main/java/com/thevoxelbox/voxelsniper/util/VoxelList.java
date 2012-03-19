/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.util;

/**
 *
 * @author Voxel
 */
public class VoxelList {

    private int[] col = new int[100];
    private int vir = 0;

    public void add(int i) {
        if(!contains(i)) {
            col[vir++] = i;
        }
    }

    public boolean removeValue(int i) {
        if(isEmpty()) {
            return false;
        } else {
            return removeFrom(getIndexOf(i));
        }
    }

    public boolean removeFrom(int i) {
        if(i >= 0 && i < vir) {
            for(int x = i; x < vir; x++) {
                col[x] = col[x+1];
            }
            vir--;
            return true;
        } else {
            return false;
        }
    }

    public boolean contains(int i) {
        if (isEmpty()) {
            return false;
        } else {
            for (int x = 0; x < vir; x++) {
                if(col[x] == i) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean isEmpty() {
        return vir == 0;
    }

    public void clear() {
        vir = 0;
    }

    public int getIndexOf(int i) {
        if (isEmpty()) {
            return -1;
        } else {
            for (int x = 0; x < vir; x++) {
                if(col[x] == i) {
                    return x;
                }
            }
            return -1;
        }
    }

    public int getFrom(int i) {
        if(i >= 0 && i < vir) {
            return col[i];
        } else {
            return -1;
        }
    }
    
    public VoxIterator getIterator() {
        return new VoxIterator(col, vir);
    }

    public class VoxIterator {

        private int[] col;
        private int vir;
        private int cur = 0;

        public VoxIterator(int[] collection, int virtual) {
            col = collection;
            vir = virtual;
        }

        public boolean hasNext() {
            return cur < vir;
        }

        public int next() {
            return col[cur++];
        }
    }
}
