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
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.voxelplugineering.voxelsniper.api.entity.Entity;
import com.voxelplugineering.voxelsniper.api.service.registry.BiomeRegistry;
import com.voxelplugineering.voxelsniper.api.service.registry.MaterialRegistry;
import com.voxelplugineering.voxelsniper.api.shape.MaterialShape;
import com.voxelplugineering.voxelsniper.api.shape.Shape;
import com.voxelplugineering.voxelsniper.api.world.Block;
import com.voxelplugineering.voxelsniper.api.world.Chunk;
import com.voxelplugineering.voxelsniper.api.world.Location;
import com.voxelplugineering.voxelsniper.api.world.biome.Biome;
import com.voxelplugineering.voxelsniper.api.world.material.Material;
import com.voxelplugineering.voxelsniper.core.shape.ComplexMaterialShape;
import com.voxelplugineering.voxelsniper.core.util.Context;
import com.voxelplugineering.voxelsniper.core.util.math.Vector3i;
import com.voxelplugineering.voxelsniper.core.world.AbstractWorld;
import com.voxelplugineering.voxelsniper.core.world.CommonBlock;
import com.voxelplugineering.voxelsniper.core.world.CommonLocation;
import com.voxelplugineering.voxelsniper.sponge.entity.SpongeEntity;
import com.voxelplugineering.voxelsniper.sponge.world.biome.SpongeBiome;
import com.voxelplugineering.voxelsniper.sponge.world.material.SpongeMaterial;

/**
 * A wrapper for Sponge's World.
 */
public class SpongeWorld extends AbstractWorld<org.spongepowered.api.world.World>
{

    private final Context context;
    private final MaterialRegistry<org.spongepowered.api.block.BlockType> materials;
    private final BiomeRegistry<org.spongepowered.api.world.biome.BiomeType> biomes;
    private final Thread worldThread;
    private final Map<org.spongepowered.api.world.Chunk, Chunk> chunks;
    protected final Map<org.spongepowered.api.entity.Entity, Entity> entitiesCache;

    /**
     * Creates a new {@link SpongeWorld}.
     * 
     * @param world The world to wrap
     * @param materials The material registry for this world
     * @param thread The main thread of this world
     */
    @SuppressWarnings("unchecked")
    public SpongeWorld(Context context, org.spongepowered.api.world.World world, Thread thread)
    {
        super(context, world);
        this.context = context;
        this.biomes = context.getRequired(BiomeRegistry.class);
        this.materials = context.getRequired(MaterialRegistry.class);
        this.chunks = new MapMaker().weakKeys().makeMap();
        this.entitiesCache = new MapMaker().weakKeys().makeMap();
        this.worldThread = thread;
    }

    @Override
    public String getName()
    {
        return getThis().getName();
    }

    @Override
    public Optional<com.voxelplugineering.voxelsniper.api.world.Block> getBlock(int x, int y, int z)
    {
        if (!checkAsyncBlockAccess(x, y, z))
        {
            return Optional.absent();
        }
        org.spongepowered.api.world.Location b = getThis().getFullBlock(x, y, z);
        CommonLocation l = new CommonLocation(this, b.getX(), b.getY(), b.getZ());
        Optional<com.voxelplugineering.voxelsniper.api.world.material.Material> m = this.materials.getMaterial(b.getType());
        if (!m.isPresent())
        {
            return Optional.absent();
        }
        return Optional.<com.voxelplugineering.voxelsniper.api.world.Block>of(new CommonBlock(l, m.get()));
    }

    private boolean checkAsyncBlockAccess(int x, int y, int z)
    {
        if (Thread.currentThread() != this.worldThread)
        {
            int cx = x < 0 ? (x / 16 - 1) : x / 16;
            int cz = z < 0 ? (z / 16 - 1) : z / 16;
            if (!getThis().getChunk(new com.flowpowered.math.vector.Vector3i(cx, 0, cz)).isPresent())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setBlock(Material material, int x, int y, int z)
    {
        if (y < 0 || y >= 256)
        {
            return;
        }
        if (material instanceof SpongeMaterial)
        {
            SpongeMaterial spongeMaterial = (SpongeMaterial) material;
            getThis().getFullBlock(x, y, z).replaceWith(spongeMaterial.getThis());
        }
    }

    @Override
    public MaterialRegistry<org.spongepowered.api.block.BlockType> getMaterialRegistry()
    {
        return this.materials;
    }

    @Override
    public Iterable<Entity> getLoadedEntities()
    {
        List<Entity> entities = Lists.newArrayList();
        for (org.spongepowered.api.entity.Entity e : getThis().getEntities())
        {
            if (this.entitiesCache.containsKey(e))
            {
                entities.add(this.entitiesCache.get(e));
            } else
            {
                Entity ent = new SpongeEntity(this.context, e);
                this.entitiesCache.put(e, ent);
                entities.add(ent);
            }
        }
        return entities;
    }

    @Override
    public Optional<Chunk> getChunk(int x, int y, int z)
    {
        if (!checkAsyncChunkAccess(x, y, z))
        {
            return Optional.absent();
        }
        org.spongepowered.api.world.Chunk chunk = getThis().getChunk(new com.flowpowered.math.vector.Vector3i(x, y, z)).get();
        if (this.chunks.containsKey(chunk))
        {
            return Optional.of(this.chunks.get(chunk));
        }
        SpongeChunk newChunk = new SpongeChunk(this.context, chunk, this);
        this.chunks.put(chunk, newChunk);
        return Optional.<Chunk>of(newChunk);
    }

    private boolean checkAsyncChunkAccess(int x, int y, int z)
    {
        if (Thread.currentThread() != this.worldThread)
        {
            if (!getThis().getChunk(new com.flowpowered.math.vector.Vector3i(x, y, z)).isPresent())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public Optional<Biome> getBiome(int x, int y, int z)
    {
        org.spongepowered.api.world.biome.BiomeType biome = getThis().getBiome(x, z);
        return this.biomes.getBiome(biome);
    }

    @Override
    public void setBiome(Biome biome, int x, int y, int z)
    {
        if (biome instanceof SpongeBiome)
        {
            SpongeBiome spongeBiome = (SpongeBiome) biome;
            getThis().setBiome(x, z, spongeBiome.getThis());
        }
    }

    @Override
    public MaterialShape getShapeFromWorld(Location origin, Shape shape)
    {
        MaterialShape mat = new ComplexMaterialShape(shape, this.materials.getAirMaterial());
        for (int x = 0; x < shape.getWidth(); x++)
        {
            int ox = x + origin.getFlooredX() - shape.getOrigin().getX();
            for (int y = 0; y < shape.getHeight(); y++)
            {
                int oy = y + origin.getFlooredY() - shape.getOrigin().getY();
                for (int z = 0; z < shape.getLength(); z++)
                {
                    int oz = z + origin.getFlooredZ() - shape.getOrigin().getZ();
                    if (shape.get(x, y, z, false))
                    {
                        Optional<Block> block = getBlock(ox, oy, oz);
                        if (!block.isPresent())
                        {
                            shape.unset(x, y, z, false);
                        } else
                        {
                            mat.setMaterial(x, y, z, false, block.get().getMaterial());
                        }
                    }
                }
            }
        }
        return mat;
    }

    @Override
    public Vector3i getChunkSize()
    {
        return SpongeChunk.CHUNK_SIZE;
    }
}
