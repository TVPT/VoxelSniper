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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Location;

import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.voxelplugineering.voxelsniper.bukkit.entity.BukkitEntity;
import com.voxelplugineering.voxelsniper.bukkit.world.biome.BukkitBiome;
import com.voxelplugineering.voxelsniper.bukkit.world.material.BukkitMaterial;
import com.voxelplugineering.voxelsniper.bukkit.world.material.BukkitMaterialState;
import com.voxelplugineering.voxelsniper.entity.Entity;
import com.voxelplugineering.voxelsniper.service.registry.BiomeRegistry;
import com.voxelplugineering.voxelsniper.service.registry.MaterialRegistry;
import com.voxelplugineering.voxelsniper.service.registry.WorldRegistry;
import com.voxelplugineering.voxelsniper.util.Context;
import com.voxelplugineering.voxelsniper.util.math.Vector3i;
import com.voxelplugineering.voxelsniper.world.AbstractWorld;
import com.voxelplugineering.voxelsniper.world.Block;
import com.voxelplugineering.voxelsniper.world.Chunk;
import com.voxelplugineering.voxelsniper.world.CommonBlock;
import com.voxelplugineering.voxelsniper.world.CommonLocation;
import com.voxelplugineering.voxelsniper.world.biome.Biome;
import com.voxelplugineering.voxelsniper.world.material.Material;
import com.voxelplugineering.voxelsniper.world.material.MaterialState;

/**
 * A wrapper for bukkit's {@link org.bukkit.World}s.
 */
public class BukkitWorld extends AbstractWorld<org.bukkit.World>
{

    private final MaterialRegistry<org.bukkit.Material> materials;
    private final Map<org.bukkit.Chunk, Chunk> chunks;
    private final Map<org.bukkit.entity.Entity, Entity> entitiesCache;
    private final Thread worldThread;
    private final WorldRegistry<org.bukkit.World> worldReg;
    private final BiomeRegistry<org.bukkit.block.Biome> biomes;

    /**
     * Creates a new {@link BukkitWorld}.
     * 
     * @param world The world
     * @param thread The world Thread
     */
    @SuppressWarnings("unchecked")
    public BukkitWorld(Context context, org.bukkit.World world, Thread thread)
    {
        super(context, world);
        this.materials = context.getRequired(MaterialRegistry.class);
        this.worldReg = context.getRequired(WorldRegistry.class);
        this.biomes = context.getRequired(BiomeRegistry.class);
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
    public Optional<Chunk> getChunk(int x, int y, int z)
    {
        if (!checkAsyncChunkAccess(x, y, z))
        {
            return Optional.empty();
        }
        org.bukkit.Chunk chunk = getThis().getChunkAt(x, z);
        if (chunk == null)
        {
            return Optional.empty();
        }
        if (this.chunks.containsKey(chunk))
        {
            return Optional.of(this.chunks.get(chunk));
        }
        BukkitChunk newChunk = new BukkitChunk(chunk, this, this.worldReg);
        this.chunks.put(chunk, newChunk);
        return Optional.<Chunk>of(newChunk);
    }

    private boolean checkAsyncChunkAccess(int x, int y, int z)
    {
        if (Thread.currentThread() != this.worldThread)
        {
            if (!getThis().isChunkLoaded(x, z))
            {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Optional<Block> getBlock(int x, int y, int z)
    {
        if (!checkAsyncBlockAccess(x, y, z))
        {
            return Optional.empty();
        }
        org.bukkit.block.Block b = getThis().getBlockAt(x, y, z);
        CommonLocation l = new CommonLocation(this, b.getX(), b.getY(), b.getZ());
        Optional<Material> m = this.materials.getMaterial(b.getType().name());
        if (!m.isPresent())
        {
            return Optional.empty();
        }
        MaterialState ms = ((BukkitMaterial) m.get()).getState(b.getData());
        return Optional.<Block>of(new CommonBlock(l, ms));
    }

    private boolean checkAsyncBlockAccess(int x, int y, int z)
    {
        if (Thread.currentThread() != this.worldThread)
        {
            int cx = x < 0 ? (x / getChunkSize().getX() - 1) : x / 16;
            int cz = z < 0 ? (z / getChunkSize().getZ() - 1) : z / 16;
            if (!getThis().isChunkLoaded(cx, cz))
            {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setBlock(MaterialState material, int x, int y, int z, boolean update)
    {
        checkNotNull(material);
        if (y < 0 || y >= 256)
        {
            return;
        }
        if (material instanceof BukkitMaterialState)
        {
            BukkitMaterialState bukkitMaterial = (BukkitMaterialState) material;
            getThis().getBlockAt(x, y, z).setTypeIdAndData(((BukkitMaterial) bukkitMaterial.getType()).getThis().getId(), bukkitMaterial.getState(),
                    update);
        }
    }

    @Override
    public Optional<Biome> getBiome(int x, int y, int z)
    {
        org.bukkit.block.Biome biome = getThis().getBiome(x, z);
        return this.biomes.getBiome(biome.name());
    }

    @Override
    public void setBiome(Biome biome, int x, int y, int z)
    {
        checkNotNull(biome);
        if (biome instanceof BukkitBiome)
        {
            BukkitBiome bukkitBiome = (BukkitBiome) biome;
            getThis().setBiome(x, z, bukkitBiome.getThis());
        }
    }

    @Override
    public MaterialRegistry<?> getMaterialRegistry()
    {
        return this.materials;
    }

    @Override
    public Iterable<Entity> getLoadedEntities()
    {
        List<Entity> entities = Lists.newArrayList();
        for (org.bukkit.entity.Entity e : getThis().getEntities())
        {
            if (this.entitiesCache.containsKey(e))
            {
                entities.add(this.entitiesCache.get(e));
            } else
            {
                Entity ent = new BukkitEntity(e, this.worldReg);
                this.entitiesCache.put(e, ent);
                entities.add(ent);
            }
        }
        return entities;
    }

    @Override
    public Vector3i getChunkSize()
    {
        return BukkitChunk.CHUNK_SIZE;
    }

    /**
     * Gets a live copy of the entities cache.
     * 
     * @return The entities cache
     */
    protected Map<org.bukkit.entity.Entity, Entity> getEntityCache()
    {
        return this.entitiesCache;
    }

    @Override
    public void spawnLightning(Vector3i position)
    {
        getThis().strikeLightning(new Location(getThis(), position.getX(), position.getY(), position.getZ()));
    }

}
