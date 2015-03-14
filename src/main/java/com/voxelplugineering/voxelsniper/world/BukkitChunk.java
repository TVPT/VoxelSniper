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
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.voxelplugineering.voxelsniper.api.entity.Entity;
import com.voxelplugineering.voxelsniper.api.world.Block;
import com.voxelplugineering.voxelsniper.api.world.World;
import com.voxelplugineering.voxelsniper.api.world.material.Material;
import com.voxelplugineering.voxelsniper.entity.BukkitEntity;
import com.voxelplugineering.voxelsniper.util.math.Vector3i;
import com.voxelplugineering.voxelsniper.world.material.BukkitMaterial;

/**
 * A bukkit wrapper for {@link org.bukkit.Chunk}.
 */
public class BukkitChunk extends AbstractChunk<org.bukkit.Chunk>
{

    protected static final Vector3i CHUNK_SIZE = new Vector3i(16, 256, 16);

    private final Vector3i min;
    private final Vector3i max;
    private final BukkitWorld world;

    /**
     * Creates a new {@link BukkitChunk} wrapping the given
     * {@link org.bukkit.Chunk} .
     * 
     * @param chunk the chunk to wrap, cannot be null
     * @param world The world to wrap
     */
    public BukkitChunk(org.bukkit.Chunk chunk, World world)
    {
        super(chunk, world);
        if (world instanceof BukkitWorld)
        {
            this.world = (BukkitWorld) world;
        } else
        {
            throw new RuntimeException("Cannot create a BukkitChunk with a non-Bukkit world");
        }
        this.min = new Vector3i(chunk.getX() * 16, 0, chunk.getZ() * 16);
        this.max = new Vector3i(chunk.getX() * 16 + 15, 255, chunk.getZ() * 16 + 15);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Block> getBlock(int x, int y, int z)
    {
        if (!checkBounds(x, y, z))
        {
            return Optional.absent();
        }
        org.bukkit.block.Block b = getThis().getBlock(x, y, z);
        CommonLocation l = new CommonLocation(this.getWorld(), b.getX(), b.getY(), b.getZ());
        Optional<Material> m = this.getWorld().getMaterialRegistry().getMaterial(b.getType().name());
        if (!m.isPresent())
        {
            return Optional.absent();
        }
        return Optional.<Block>of(new CommonBlock(l, m.get()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBlock(Material material, int x, int y, int z)
    {
        if (!checkBounds(x, y, z))
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
        Map<org.bukkit.entity.Entity, Entity> cache = this.world.getEntityCache();
        for (org.bukkit.entity.Entity e : getThis().getEntities())
        {
            if (cache.containsKey(e))
            {
                entities.add(cache.get(e));
            } else
            {
                Entity ent = new BukkitEntity(e);
                cache.put(e, ent);
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
        this.world.getThis().refreshChunk(getThis().getX(), getThis().getZ());
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

    private boolean checkBounds(int x, int y, int z)
    {
        if (x < 0 || x >= CHUNK_SIZE.getX() || z < 0 || z >= CHUNK_SIZE.getZ() || y < 0 || y >= CHUNK_SIZE.getY())
        {
            return false;
        }
        return true;
    }

}
