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
package com.voxelplugineering.voxelsniper.sponge.world;

import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.voxelplugineering.voxelsniper.api.entity.Entity;
import com.voxelplugineering.voxelsniper.api.world.Chunk;
import com.voxelplugineering.voxelsniper.api.world.World;
import com.voxelplugineering.voxelsniper.api.world.material.Material;
import com.voxelplugineering.voxelsniper.sponge.entity.SpongeEntity;
import com.voxelplugineering.voxelsniper.sponge.world.material.SpongeMaterial;
import com.voxelplugineering.voxelsniper.util.math.Vector3i;
import com.voxelplugineering.voxelsniper.world.AbstractChunk;
import com.voxelplugineering.voxelsniper.world.CommonBlock;
import com.voxelplugineering.voxelsniper.world.CommonLocation;

/**
 * Wraps a {@link Chunk}.
 */
public class SpongeChunk extends AbstractChunk<org.spongepowered.api.world.Chunk>
{

    private final Vector3i min;
    private final Vector3i max;
    protected static final Vector3i CHUNK_SIZE = new Vector3i(16, 256, 16);

    /**
     * Creates a new {@link SpongeChunk}.
     * 
     * @param chunk The chunk to wrap
     * @param world The parent world
     */
    public SpongeChunk(org.spongepowered.api.world.Chunk chunk, World world)
    {
        super(chunk, world);
        com.flowpowered.math.vector.Vector3i pos = chunk.getPosition();
        this.min = new Vector3i(pos.getX() * 16, 0, pos.getZ() * 16);
        this.max = new Vector3i(pos.getX() * 16 + 15, 255, pos.getZ() * 16 + 15);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<com.voxelplugineering.voxelsniper.api.world.Block> getBlock(int x, int y, int z)
    {
        if (x < 0 || x > CHUNK_SIZE.getX() - 1 || z < 0 || z > CHUNK_SIZE.getZ() - 1 || y < 0 || y > CHUNK_SIZE.getY() - 1)
        {
            return Optional.absent();
        }
        org.spongepowered.api.block.BlockLoc b = getThis().getFullBlock(x, y, z);
        CommonLocation l = new CommonLocation(this.getWorld(), b.getX(), b.getY(), b.getZ());
        Optional<Material> m = ((SpongeWorld) this.getWorld()).getMaterialRegistry().getMaterial(b.getType());
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
    public void setBlock(Material material, int x, int y, int z)
    {
        if (x < 0 || x > CHUNK_SIZE.getX() - 1 || z < 0 || z > CHUNK_SIZE.getZ() - 1 || y < 0 || y > CHUNK_SIZE.getY() - 1)
        {
            return;
        }
        if (material instanceof SpongeMaterial)
        {
            SpongeMaterial spongeMaterial = (SpongeMaterial) material;
            getThis().getFullBlock(x, y, z).replaceWith(spongeMaterial.getThis());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<com.voxelplugineering.voxelsniper.api.entity.Entity> getLoadedEntities()
    {
        List<Entity> entities = Lists.newArrayList();
        for (org.spongepowered.api.entity.Entity e : getThis().getEntities())
        {
            if (((SpongeWorld) this.getWorld()).entitiesCache.containsKey(e))
            {
                entities.add(((SpongeWorld) this.getWorld()).entitiesCache.get(e));
            } else
            {
                Entity ent = new SpongeEntity(e);
                ((SpongeWorld) this.getWorld()).entitiesCache.put(e, ent);
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
        //TODO Sponge refreshChunk
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
