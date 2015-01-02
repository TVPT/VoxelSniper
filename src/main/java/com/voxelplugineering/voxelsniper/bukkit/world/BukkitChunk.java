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
package com.voxelplugineering.voxelsniper.bukkit.world;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

import com.google.common.base.Optional;
import com.voxelplugineering.voxelsniper.api.entity.Entity;
import com.voxelplugineering.voxelsniper.api.world.Location;
import com.voxelplugineering.voxelsniper.api.world.World;
import com.voxelplugineering.voxelsniper.api.world.material.Material;
import com.voxelplugineering.voxelsniper.bukkit.world.material.BukkitMaterial;
import com.voxelplugineering.voxelsniper.registry.WeakWrapper;
import com.voxelplugineering.voxelsniper.util.math.Vector3i;
import com.voxelplugineering.voxelsniper.world.CommonBlock;
import com.voxelplugineering.voxelsniper.world.CommonLocation;

/**
 * A bukkit wrapper for {@link Chunk}.
 */
public class BukkitChunk extends WeakWrapper<Chunk> implements com.voxelplugineering.voxelsniper.api.world.Chunk
{

    private World world;

    /**
     * Creates a new {@link BukkitChunk} wrapping the given bukkit {@link Chunk}.
     * 
     * @param chunk the chunk to wrap, cannot be null
     * @param world The world to wrap
     */
    public BukkitChunk(Chunk chunk, World world)
    {
        super(chunk);
        this.world = world;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<com.voxelplugineering.voxelsniper.api.world.Block> getBlock(int x, int y, int z)
    {
        Block b = getThis().getBlock(x, y, z);
        CommonLocation l = new CommonLocation(this.getWorld(), b.getX(), b.getY(), b.getZ());
        Optional<Material> m = this.getWorld().getMaterialRegistry().getMaterial(b.getType().name());
        if (!m.isPresent())
        {
            return Optional.absent();
        }
        return Optional.<com.voxelplugineering.voxelsniper.api.world.Block>of(new CommonBlock(l, m.get()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<com.voxelplugineering.voxelsniper.api.world.Block> getBlock(Location location)
    {
        if (location.getWorld() != this.world)
        {
            return Optional.absent();
        }
        return getBlock(location.getFlooredX(), location.getFlooredY(), location.getFlooredZ());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<com.voxelplugineering.voxelsniper.api.world.Block> getBlock(Vector3i vector)
    {
        return getBlock(vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBlock(Material material, int x, int y, int z)
    {
        //TODO range checks
        if (material instanceof BukkitMaterial)
        {
            BukkitMaterial bukkitMaterial = (BukkitMaterial) material;
            getThis().getBlock(x, y, z).setType(bukkitMaterial.getThis());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBlock(Material material, Location location)
    {
        setBlock(material, location.getFlooredX(), location.getFlooredY(), location.getFlooredZ());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBlock(Material material, Vector3i vector)
    {
        setBlock(material, vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public World getWorld()
    {
        return this.world;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Entity> getLoadedEntities()
    {
        return null; //TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshChunk()
    {
        ((BukkitWorld) this.world).getThis().refreshChunk(getThis().getX(), getThis().getZ());
    }

}
