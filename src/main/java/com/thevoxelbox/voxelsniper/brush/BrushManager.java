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
package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.thevoxelbox.genesis.logging.Log;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class BrushManager {

    private static final BrushManager instance = new BrushManager();

    public static BrushManager get() {
        return instance;
    }

    private final Map<String, Brush<?>> brushes = Maps.newHashMap();
    private final Set<Brush<?>>         unique  = Sets.newHashSet();

    private BrushManager() {

    }

    public void register(Brush<?> brush) {
        BrushInfo info = brush.getClass().getAnnotation(BrushInfo.class);
        Log.GLOBAL.debug("Registered brush " + brush.getClass().getSimpleName());
        this.unique.add(brush);
        for (String alias : info.alias()) {
            this.brushes.put(alias, brush);
        }
    }

    public Collection<Brush<?>> getLoadedBrushes() {
        return this.unique;
    }

    @SuppressWarnings("unchecked")
    public <B extends Brush<B>> B getBrush(String name) {
        return (B) this.brushes.get(name);
    }

    public static class BrushConsumer implements Consumer<String> {

        @Override
        public void accept(String type) {
            try {
                Class<?> brushClass = Class.forName(type);
                if (!Brush.class.isAssignableFrom(brushClass)) {
                    Log.GLOBAL.error("Brush type " + type + " does not extent Brush");
                    return;
                }
                Brush<?> instance = (Brush<?>) brushClass.newInstance();
                BrushManager.get().register(instance);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

}
