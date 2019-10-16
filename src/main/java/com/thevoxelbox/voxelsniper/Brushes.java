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
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.event.RegisterBrushEvent;
import org.spongepowered.api.Sponge;

import java.util.Map;

/**
 * Brush registration manager.
 */
public final class Brushes {

    private static Map<String, Class<? extends Brush>> brushes = Maps.newHashMap();

    /**
     * Register a brush for VoxelSniper to be able to use.
     *
     * @param clazz Brush class.
     */
    public static void registerSniperBrush(Class<? extends Brush> clazz) {
        checkNotNull(clazz, "Cannot register null as a brush.");
        Brush.BrushInfo info = clazz.getAnnotation(Brush.BrushInfo.class);
        if (info == null) {
            VoxelSniper.getLogger().warn("Brush class " + clazz.getName() + " has no BrushInfo annotation.");
            return;
        }
        RegisterBrushEvent event = new RegisterBrushEvent(Sponge.getCauseStackManager().getCurrentCause(), clazz, info.aliases());
        Sponge.getEventManager().post(event);
        for (String handle : event.getAliases()) {
            brushes.put(handle.toLowerCase(), clazz);
        }
    }

    /**
     * Retrieve Brush class via handle Lookup.
     *
     * @param handle Case insensitive brush handle
     * @return Brush class
     */
    public static Class<? extends Brush> getBrushForHandle(String handle) {
        checkNotNull(handle, "Brushhandle can not be null.");
        return brushes.get(handle.toLowerCase());
    }

    /**
     * @return Amount of handles registered with the system under Sniper
     *         visibility.
     */
    public static int getBrushCount() {
        return brushes.size();
    }

    public static String getAllBrushes() {
        return String.join(", ", brushes.keySet());
    }

    private Brushes() {
    }
}
