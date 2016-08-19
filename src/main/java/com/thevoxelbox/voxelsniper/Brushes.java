package com.thevoxelbox.voxelsniper;

import static com.google.common.base.Preconditions.checkNotNull;

import com.thevoxelbox.voxelsniper.brush.IBrush;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Brush registration manager.
 */
public class Brushes {

    private static final Brushes instance = new Brushes();

    public static Brushes get() {
        return instance;
    }

    private Map<String, Class<? extends IBrush>> brushes = Maps.newHashMap();
    private int brush_count = 0;

    private Brushes() {

    }

    /**
     * Register a brush for VoxelSniper to be able to use.
     *
     * @param clazz Brush implementing IBrush interface.
     * @param handles Handles under which the brush can be accessed ingame.
     */
    public void registerSniperBrush(Class<? extends IBrush> clazz, String... handles) {
        checkNotNull(clazz, "Cannot register null as a brush.");
        for (String handle : handles) {
            this.brushes.put(handle.toLowerCase(), clazz);
        }
        this.brush_count++;
    }

    /**
     * Retrieve Brush class via handle Lookup.
     *
     * @param handle Case insensitive brush handle
     * @return Brush class
     */
    public Class<? extends IBrush> getBrushForHandle(String handle) {
        checkNotNull(handle, "Brushhandle can not be null.");
        return this.brushes.get(handle.toLowerCase());
    }

    /**
     * @return Amount of IBrush classes registered with the system under Sniper
     *         visibility.
     */
    public int registeredSniperBrushes() {
        return this.brush_count;
    }

    /**
     * @return Amount of handles registered with the system under Sniper
     *         visibility.
     */
    public int registeredSniperBrushHandles() {
        return this.brushes.size();
    }
}
