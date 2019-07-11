package com.thevoxelbox.voxelsniper.util;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Keyed;
import org.bukkit.Tag;

import java.util.Set;

public class SetTag<T extends Keyed> implements Tag<T> {

    private final Set<T> values;

    public SetTag(Set<T> values) {
        this.values = ImmutableSet.copyOf(values);
    }

    @Override
    public boolean isTagged(T item) {
        return values.contains(item);
    }

    @Override
    public Set<T> getValues() {
        return values;
    }
}
