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
package com.voxelplugineering.voxelsniper.bukkit;

import static com.google.common.base.Preconditions.checkNotNull;
import java.lang.ref.WeakReference;

import org.bukkit.Chunk;

import com.voxelplugineering.voxelsniper.api.Gunsmith;
import com.voxelplugineering.voxelsniper.common.CommonBlock;
import com.voxelplugineering.voxelsniper.common.CommonChunk;
import com.voxelplugineering.voxelsniper.common.CommonWorld;

/**
 * A bukkit wrapper for {@link CommonChunk}.
 */
public class BukkitChunk extends CommonChunk
{

    /**
     * A {@link WeakReference} to the chunk underpinning this wrapper.
     */
    private WeakReference<Chunk> chunk;

    /**
     * Creates a new {@link BukkitChunk} wrapping the given bukkit {@link Chunk}.
     * 
     * @param chunk the chunk to wrap, cannot be null
     */
    public BukkitChunk(Chunk chunk)
    {
        checkNotNull(chunk, "Chunk cannot be null");
        this.chunk = new WeakReference<Chunk>(chunk);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommonWorld getCommonWorld()
    {
        return Gunsmith.getWorldFactory().getWorld(this.chunk.get().getWorld().getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommonBlock getRelativeBlockAt(int x, int y, int z)
    {
        return null;
    }

}
