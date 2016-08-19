package com.thevoxelbox.voxelsniper.util;

import com.google.common.collect.Sets;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;

import java.util.Iterator;
import java.util.Set;

/**
 * Container class for multiple ID/Datavalue pairs.
 */
public class VoxelList {

    private Set<BlockType> wildcardTypes = Sets.newHashSet();
    private Set<BlockState> specificTypes = Sets.newHashSet();

    /**
     * Adds the specified id, data value pair to the VoxelList. A data value of
     * -1 will operate on all data values of that id.
     * 
     * @param i
     */
    public void add(BlockState i) {
        if (this.wildcardTypes.contains(i.getType())) {
            return;
        }
        this.specificTypes.add(i);
    }

    public void add(BlockType t) {
        if (this.wildcardTypes.contains(t)) {
            return;
        }
        for (Iterator<BlockState> it = this.specificTypes.iterator(); it.hasNext();) {
            BlockState state = it.next();
            if (state.getType() == t) {
                it.remove();
            }
        }
        this.wildcardTypes.add(t);
    }

    public boolean remove(BlockState state) {
        return this.specificTypes.remove(state);
    }

    public boolean remove(BlockType t) {
        boolean removed = this.wildcardTypes.remove(t);
        for (Iterator<BlockState> it = this.specificTypes.iterator(); it.hasNext();) {
            BlockState state = it.next();
            if (state.getType() == t) {
                it.remove();
                removed = true;
            }
        }
        return removed;
    }

    public boolean contains(BlockState state) {
        return this.specificTypes.contains(state);
    }

    public boolean contains(BlockType type) {
        return this.wildcardTypes.contains(type);
    }

    public boolean containsAny(BlockType type) {
        boolean contains = this.wildcardTypes.contains(type);
        if (!contains) {
            for (Iterator<BlockState> it = this.specificTypes.iterator(); it.hasNext();) {
                BlockState state = it.next();
                if (state.getType() == type) {
                    return true;
                }
            }
        }
        return contains;
    }

    public void clear() {
        this.specificTypes.clear();
        this.wildcardTypes.clear();
    }

    public boolean isEmpty() {
        return this.specificTypes.isEmpty() && this.wildcardTypes.isEmpty();
    }

    public Set<BlockType> getWildcardTypes() {
        return this.wildcardTypes;
    }

    public Set<BlockState> getSpecificTypes() {
        return this.specificTypes;
    }

}
