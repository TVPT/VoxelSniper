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
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

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
    private OutOfThreadBlockPlaceTask task;

    protected BukkitWorld(World w)
    {
        this.world = new WeakReference<World>(w);
        this.task = new OutOfThreadBlockPlaceTask(this.world);
        this.task.setTaskHolder(Bukkit.getScheduler().runTaskTimer((Plugin) Gunsmith.getVoxelSniper(), this.task, 0, 5));
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
        if (Thread.currentThread() == Gunsmith.getVoxelSniper().getMainThread())
        {
            this.getWorld().getBlockAt(x, y, z).setType(mat);
        }
        else
        {
            this.task.addChange(x, y, z, mat);
        }
    }

    protected Material localGetMaterialAt(int x, int y, int z)
    {
        return this.world.get().getBlockAt(x, y, z).getType();
    }

}

class OutOfThreadBlockPlaceTask implements Runnable
{
    
    private BukkitTask task = null;
    private WeakReference<World> world;
    Queue<BlockChange> pending;
    
    public OutOfThreadBlockPlaceTask(WeakReference<World> w)
    {
        this.world = w;
        this.pending = new LinkedList<BlockChange>();
    }
    
    public void setTaskHolder(BukkitTask task)
    {
        this.task = task;
    }
    
    public void addChange(int x, int y, int z, Material m)
    {
        this.pending.add(new BlockChange(x, y, z, m));
    }

    @Override
    public void run()
    {
        int count = ((Integer) Gunsmith.getConfiguration().get("BLOCK_CHANGES_PER_SECOND"))/4;
        World w = this.world.get();
        if(w == null)
        {
            Gunsmith.getLogger().warn("Founding pending changes on world which no longer is referenced!");
            this.pending.clear();
            if(this.task != null) this.task.cancel();
            return;
        }
        while(count > 0 && !this.pending.isEmpty())
        {
            BlockChange next = this.pending.poll();
            w.getBlockAt(next.getX(), next.getY(), next.getZ()).setType(next.getMaterial());
            count--;
        }
    }
}

class BlockChange
{
    int x;
    int y;
    int z;
    Material material;
    
    public BlockChange(int x, int y, int z, Material m)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.material = m;
    }
    
    public int getX()
    {
        return this.x;
    }
    
    public int getY()
    {
        return this.y;
    }
    
    public int getZ()
    {
        return this.z;
    }
    
    public Material getMaterial()
    {
        return this.material;
    }
    
}
