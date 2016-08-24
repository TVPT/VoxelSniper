/*
 * This file is part of VoxelSniper, licensed under the MIT License (MIT).
 *
 * Copyright (c) The VoxelBox <http://thevoxelbox.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.thevoxelbox.voxelsniper;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Maps;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import com.thevoxelbox.voxelsniper.event.RegisterBrushEvent;
import org.spongepowered.api.Sponge;

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
        RegisterBrushEvent event = new RegisterBrushEvent(clazz, handles, VoxelSniper.plugin_cause);
        Sponge.getEventManager().post(event);
        for (String handle : event.getAliases()) {
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

    public String getAllBrushes() {
        StringBuilder brushes = new StringBuilder();
        for (String brush : this.brushes.keySet()) {
            brushes.append(", ").append(brush);
        }
        return brushes.toString().substring(2);
    }
}
