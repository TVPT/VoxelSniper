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
package com.voxelplugineering.voxelsniper.world;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.voxelplugineering.voxelsniper.api.entity.Entity;
import com.voxelplugineering.voxelsniper.api.world.World;
import com.voxelplugineering.voxelsniper.api.world.material.Material;
import com.voxelplugineering.voxelsniper.entity.BukkitEntity;
import com.voxelplugineering.voxelsniper.util.math.Vector3i;
import com.voxelplugineering.voxelsniper.world.material.BukkitMaterial;

/**
 * A bukkit wrapper for {@link Chunk}.
 */
public class BukkitChunk extends AbstractChunk<Chunk>
{

    private final Vector3i min;
    private final Vector3i max;
    protected static final Vector3i CHUNK_SIZE = new Vector3i(16, 256, 16);

    /**
     * Creates a new {@link BukkitChunk} wrapping the given bukkit {@link Chunk}
     * .
     * 
     * @param chunk the chunk to wrap, cannot be null
     * @param world The world to wrap
     */
    public BukkitChunk(Chunk chunk, World world)
    {
        super(chunk, world);
        this.min = new Vector3i(chunk.getX() * 16, 0, chunk.getZ() * 16);
        this.max = new Vector3i(chunk.getX() * 16 + 15, 255, chunk.getZ() * 16 + 15);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<com.voxelplugineering.voxelsniper.api.world.Block> getBlock(int x, int y, int z)
    {
        if (x < 0 || x > 15 || z < 0 || z > 15 || y < 0 || y > 255)
        {
            return Optional.absent();
        }
        Block b = getThis().getBlock(x, y, z);
        CommonLocation l = new CommonLocation(this.getWorld(), b.getX(), b.getY(), b.getZ());
        Optional<Material> m = this.getWorld().getMaterialRegistry().getMaterial(b.getType().name());
        if (!m.isPresent())
        {
            return Optional.absent();
        }
        return Optional.<com.voxelplugineering.voxelsniper.api.world.Block> of(new CommonBlock(l, m.get()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBlock(Material material, int x, int y, int z)
    {
        if (x < 0 || x > 15 || z < 0 || z > 15 || y < 0 || y > 255)
        {
            return;
        }
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
    public Iterable<Entity> getLoadedEntities()
    {
        List<Entity> entities = Lists.newArrayList();
        for (org.bukkit.entity.Entity e : getThis().getEntities())
        {
            if (((BukkitWorld) getWorld()).entitiesCache.containsKey(e))
            {
                entities.add(((BukkitWorld) getWorld()).entitiesCache.get(e));
            } else
            {
                Entity ent = new BukkitEntity(e);
                ((BukkitWorld) getWorld()).entitiesCache.put(e, ent);
                entities.add(ent);
            }
        }
        return entities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshChunk()
    {
        ((BukkitWorld) this.getWorld()).getThis().refreshChunk(getThis().getX(), getThis().getZ());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector3i getMinBound()
    {
        return this.min;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector3i getMaxBound()
    {
        return this.max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector3i getSize()
    {
        return CHUNK_SIZE;
    }

}
