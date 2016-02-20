/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 The Voxel Plugineering Team
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
package com.voxelplugineering.voxelsniper.world.material;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * A cache for {@link MaterialState} instances.
 * 
 * @param <T> The underlying data type
 * @param <M> The MaterialState instance type
 */
public class MaterialStateCache<T, M extends MaterialState>
{

    private final Map<T, M> cache = Maps.newHashMap();
    private final Function<T, M> builder;

    /**
     * Creates a new {@link MaterialStateCache} with the given builder function.
     * 
     * @param builder The builder function
     */
    public MaterialStateCache(Function<T, M> builder)
    {
        this.builder = builder;
    }

    /**
     * Gets a {@link MaterialState} from the cache for the given data value.
     * 
     * @param state The data value
     * @return A MaterialState
     */
    public M get(T state)
    {
        if (this.cache.containsKey(state))
        {
            return this.cache.get(state);
        }
        synchronized (this.cache)
        {
            M mat = this.builder.apply(state);
            this.cache.put(state, mat);
            return mat;
        }
    }

}
