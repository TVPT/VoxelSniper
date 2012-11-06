package com.thevoxelbox.voxelsniper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import com.thevoxelbox.voxelgunsmith.Brush;
import com.thevoxelbox.voxelgunsmith.BrushManager;

/**
 * Implementation of VoxelGunsmith BrushManager.
 * 
 * @author MikeMatrix
 * 
 */
public class SniperBrushManager implements BrushManager {

    private final Set<Class<Brush>> registeredBrushes = new HashSet<Class<Brush>>();
    private final Set<Brush> registeredBrushesSampleInstances = new HashSet<Brush>();

    @Override
    public final void clear() {
        this.registeredBrushes.clear();
    }

    @Override
    public final Class<Brush> getBrush(final String name) {
        for (final Brush _brush : this.registeredBrushesSampleInstances) {
            if (_brush.getName().equalsIgnoreCase(name)) {
                return (Class<Brush>) _brush.getClass();
            }
        }
        return null;
    }

    @Override
    public final Set<Class<Brush>> getBrushes(final String regex) {
        final Pattern _regexPattern = Pattern.compile(regex);
        final Set<Class<Brush>> _returnSet = new HashSet<Class<Brush>>();
        for (final Brush _brush : this.registeredBrushesSampleInstances) {
            if (_regexPattern.matcher(_brush.getName()).matches()) {
                final Class<Brush> _class =(Class<Brush>) _brush.getClass();
                _returnSet.add(_class);
            }
        }
        return _returnSet;
    }

    @Override
    public final Brush getBrushInstance(final String name) {
        for (final Brush _brush : this.registeredBrushesSampleInstances) {
            if (_brush.getName().equalsIgnoreCase(name)) {
                try {
                    return _brush.getClass().newInstance();
                } catch (final InstantiationException _e) {
                } catch (final IllegalAccessException _e) {
                }
            }
        }
        return null;
    }

    @Override
    public final Set<Class<Brush>> getRegisteredBrushes() {
        return Collections.unmodifiableSet(this.registeredBrushes);
    }

    @Override
    public final boolean isBrushRegistered(final Class<Brush> brush) {
        return this.registeredBrushes.contains(brush);
    }

    @Override
    public final boolean isBrushRegistered(final String name) {
        for (final Brush _brush : this.registeredBrushesSampleInstances) {
            if (_brush.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final void registerBrush(final Class<Brush> brush) {
        this.registeredBrushes.add(brush);
        try {
            this.registeredBrushesSampleInstances.add(brush.newInstance());
        } catch (final InstantiationException _e) {
        } catch (final IllegalAccessException _e) {
        }
    }

    @Override
    public final void unregisterBrush(final Class<Brush> brush) {
        for (final Iterator<Brush> _it = this.registeredBrushesSampleInstances.iterator(); _it.hasNext();) {
            final Brush _brush = _it.next();
            if (_brush.getClass().equals(brush)) {
                _it.remove();
            }
        }
        this.registeredBrushes.remove(brush);
    }

}
