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

import org.bukkit.Material;
import org.bukkit.World;

import com.google.common.base.Optional;
import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.api.IMaterialRegistry;
import com.voxelplugineering.voxelsniper.common.CommonBiome;
import com.voxelplugineering.voxelsniper.common.CommonBlock;
import com.voxelplugineering.voxelsniper.common.CommonChunk;
import com.voxelplugineering.voxelsniper.common.CommonLocation;
import com.voxelplugineering.voxelsniper.common.CommonMaterial;
import com.voxelplugineering.voxelsniper.common.CommonWorld;

/**
 * A wrapper for bukkit's {@link World}s.
 */
public class BukkitWorld extends CommonWorld<World>
{

    /**
     * Creates a new {@link BukkitWorld}.
     * 
     * @param world the world
     * @param materialRegistry the registry
     */
    public BukkitWorld(World world, IMaterialRegistry<Material> materialRegistry)
    {
        super(world, materialRegistry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return this.getThis().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<CommonChunk<?>> getChunkAt(int x, int y, int z)
    {

        return Optional.absent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<CommonBlock> getBlockAt(int x, int y, int z)
    {
        Optional<?> m = this.getMaterialRegistry().get(this.getThis().getBlockAt(x, y, z).getType().name());
        if (!m.isPresent())
        {
            return Optional.absent();
        }
        return Optional.of(new CommonBlock(new CommonLocation(this, x, y, z), (CommonMaterial<?>) m.get()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBlockAt(int x, int y, int z, CommonMaterial<?> material)
    {
        if (!(material instanceof BukkitMaterial))
        {
            return;
        }
        Material mat = ((BukkitMaterial) material).getThis();
        if (Thread.currentThread() == Gunsmith.getVoxelSniper().getMainThread())
        {
            if (y >= 0 && y < 256)
            {
                this.getThis().getBlockAt(x, y, z).setType(mat);
            }
        }
    }

    /**
     * Returns the bukkit specific {@link Material} at the given location.
     * 
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @return the material
     */
    protected Material localGetMaterialAt(int x, int y, int z)
    {
        return this.getThis().getBlockAt(x, y, z).getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBiomeAt(int x, int z, CommonBiome<?> biomeName)
    {
        // TODO setBiomeAt
    }

}
