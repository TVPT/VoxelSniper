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
package com.voxelplugineering.voxelsniper.world.biome;

import com.voxelplugineering.voxelsniper.api.world.biome.Biome;
import com.voxelplugineering.voxelsniper.registry.WeakWrapper;

/**
 * A bukkit wrapper for gunsmith's
 * {@link com.voxelplugineering.voxelsniper.api.world.biome.Biome}
 */
public class BukkitBiome extends WeakWrapper<org.bukkit.block.Biome> implements Biome
{

    /**
     * Creates a new {@link BukkitBiome}.
     * 
     * @param value The biome to wrap
     */
    public BukkitBiome(org.bukkit.block.Biome value)
    {
        super(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return getThis().name();
    }

}
