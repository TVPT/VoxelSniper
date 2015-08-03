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
package com.voxelplugineering.voxelsniper.forge.world;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.voxelplugineering.voxelsniper.entity.Entity;
import com.voxelplugineering.voxelsniper.forge.entity.ForgeEntity;
import com.voxelplugineering.voxelsniper.forge.world.material.ForgeMaterial;
import com.voxelplugineering.voxelsniper.forge.world.material.ForgeMaterialState;
import com.voxelplugineering.voxelsniper.util.Context;
import com.voxelplugineering.voxelsniper.util.math.Vector3i;
import com.voxelplugineering.voxelsniper.world.AbstractChunk;
import com.voxelplugineering.voxelsniper.world.CommonBlock;
import com.voxelplugineering.voxelsniper.world.CommonLocation;
import com.voxelplugineering.voxelsniper.world.World;
import com.voxelplugineering.voxelsniper.world.material.Material;
import com.voxelplugineering.voxelsniper.world.material.MaterialState;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;

/**
 * A forge chunk wrapper.
 */
public class ForgeChunk extends AbstractChunk<net.minecraft.world.chunk.Chunk>
{

    private final Context context;

    private final Vector3i min;
    private final Vector3i max;
    protected static final Vector3i CHUNK_SIZE = new Vector3i(16, 256, 16);

    /**
     * Creates a new {@link ForgeChunk}
     * 
     * @param chunk the chunk to wrap
     * @param world the world that the chunk belongs to
     */
    public ForgeChunk(net.minecraft.world.chunk.Chunk chunk, World world, Context context)
    {
        super(chunk, world);
        this.context = context;
        this.min = new Vector3i(chunk.xPosition * 16, 0, chunk.zPosition * 16);
        this.max = new Vector3i(chunk.xPosition * 16 + 15, 255, chunk.zPosition * 16 + 15);
    }

    @Override
    public Optional<com.voxelplugineering.voxelsniper.world.Block> getBlock(int x, int y, int z)
    {
        if (x < 0 || x > CHUNK_SIZE.getX() - 1 || z < 0 || z > CHUNK_SIZE.getZ() - 1 || y < 0 || y > CHUNK_SIZE.getY() - 1)
        {
            return Optional.absent();
        }
        IBlockState b = getThis().getBlockState(new BlockPos(x, y, z));
        CommonLocation l = new CommonLocation(this.getWorld(), x + getThis().xPosition * 16, y, z + getThis().zPosition * 16);
        ResourceLocation rs = (ResourceLocation) Block.blockRegistry.getNameForObject(b.getBlock());
        Optional<Material> m = this.getWorld().getMaterialRegistry().getMaterial((!rs.getResourceDomain().equals("minecraft")? rs.getResourceDomain()+":" : "") + rs.getResourcePath());
        if (!m.isPresent())
        {
            return Optional.absent();
        }
        MaterialState ms = ((ForgeMaterial) m.get()).getState(b);
        return Optional.<com.voxelplugineering.voxelsniper.world.Block>of(new CommonBlock(l, ms));
    }

    @Override
    public void setBlock(MaterialState material, int x, int y, int z)
    {
        if (x < 0 || x > CHUNK_SIZE.getX() - 1 || z < 0 || z > CHUNK_SIZE.getZ() - 1 || y < 0 || y > CHUNK_SIZE.getY() - 1)
        {
            return;
        }
        getThis().setBlockState(new net.minecraft.util.BlockPos(x, y, z), ((ForgeMaterialState) material).getState());
    }

    @Override
    public Iterable<Entity> getLoadedEntities()
    {
        List<Entity> entities = Lists.newArrayList();
        net.minecraft.util.ClassInheritanceMultiMap[] entityLists = getThis().getEntityLists();
        for (int i = 0; i < entityLists.length; i++)
        {
            for (Iterator<?> it = entityLists[i].iterator(); it.hasNext();)
            {
                net.minecraft.entity.Entity e = (net.minecraft.entity.Entity) it.next();
                if (((ForgeWorld) this.getWorld()).entitiesCache.containsKey(e))
                {
                    entities.add(((ForgeWorld) this.getWorld()).entitiesCache.get(e));
                } else
                {
                    Entity ent = new ForgeEntity(e, this.context);
                    ((ForgeWorld) this.getWorld()).entitiesCache.put(e, ent);
                    entities.add(ent);
                }
            }
        }
        return entities;
    }

    /**
     * {@inheritDoc}
     * 
     * <p> Note: this refresh method is the bukkit method for refreshing chunks. </p>
     */
    @Override
    public void refreshChunk()
    {
        int px = getThis().xPosition << 4;
        int pz = getThis().zPosition << 4;

        int height = 16;
        for (int idx = 0; idx < 64; idx++)
        {
            ((ForgeWorld) this.getWorld()).getThis().notifyLightSet(new net.minecraft.util.BlockPos(px + idx / height, idx % height * 16, pz));
        }
        ((ForgeWorld) this.getWorld()).getThis().notifyLightSet(new net.minecraft.util.BlockPos(px + 15, height * 16 - 1, pz + 15));
    }

    @Override
    public Vector3i getMinBound()
    {
        return this.min;
    }

    @Override
    public Vector3i getMaxBound()
    {
        return this.max;
    }

    @Override
    public Vector3i getSize()
    {
        return CHUNK_SIZE;
    }

}
