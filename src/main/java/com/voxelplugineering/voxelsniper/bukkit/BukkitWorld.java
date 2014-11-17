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

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;

import com.voxelplugineering.voxelsniper.api.Gunsmith;
import com.voxelplugineering.voxelsniper.common.CommonBlock;
import com.voxelplugineering.voxelsniper.common.CommonChunk;
import com.voxelplugineering.voxelsniper.common.CommonLocation;
import com.voxelplugineering.voxelsniper.common.CommonMaterial;
import com.voxelplugineering.voxelsniper.common.CommonWorld;

public class BukkitWorld extends CommonWorld
{

    private WeakReference<World> world;
    private Map<Chunk, CommonChunk> chunks = new WeakHashMap<Chunk, CommonChunk>();

    protected BukkitWorld(World w)
    {
        this.world = new WeakReference<World>(w);
    }

    public World getWorld()
    {
        return this.world.get();
    }

    @Override
    public String getName()
    {
        return this.getWorld().getName();
    }

    @Override
    public CommonChunk getChunkAt(int x, int y, int z)
    {
        Chunk chunk = this.getWorld().getChunkAt(x, z);
        if (chunk == null)
        {
            return null;
        }
        if (!this.chunks.containsKey(chunk))
        {
            this.chunks.put(chunk, new BukkitChunk(chunk));
        }
        return this.chunks.get(chunk);
    }

    @Override
    public CommonBlock getBlockAt(int x, int y, int z)
    {
        return new CommonBlock(new CommonLocation(this, x, y, z), Gunsmith.getMaterialFactory().getMaterial(this.getWorld().getBlockAt(x, y, z).getType().name()));
    }

    @Override
    public void setBlockAt(int x, int y, int z, CommonMaterial<?> material)
    {
        if (!(material instanceof BukkitMaterial))
        {
            return;
        }
        Material mat = ((BukkitMaterial) material).getValue();
        //System.out.println("Setting block at " + x + " " + y + " " + z + " to " + mat.name());
        this.getWorld().getBlockAt(x, y, z).setType(mat);
    }

    protected Material localGetMaterialAt(int x, int y, int z)
    {
        return this.world.get().getBlockAt(x, y, z).getType();
    }

}
