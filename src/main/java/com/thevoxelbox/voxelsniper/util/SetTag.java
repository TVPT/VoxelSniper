package com.thevoxelbox.voxelsniper.util;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class SetTag<T extends Keyed> implements Tag<T> {

    private final Set<T> values;
    private final NamespacedKey key;

    public SetTag(Set<T> values, NamespacedKey key) {
        this.values = ImmutableSet.copyOf(values);
        this.key = key;
    }

    @Override
    public boolean isTagged(T item) {
        return values.contains(item);
    }

    @Override
    public Set<T> getValues() {
        return values;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }
}
