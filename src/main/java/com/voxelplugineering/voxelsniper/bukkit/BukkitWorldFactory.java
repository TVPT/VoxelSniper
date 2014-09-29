/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 The Voxel Plugin Team
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

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.Server;
import org.bukkit.World;

import com.voxelplugineering.voxelsniper.common.CommonWorld;
import com.voxelplugineering.voxelsniper.common.factory.CommonWorldFactory;

public class BukkitWorldFactory extends CommonWorldFactory
{
    
    Server server;
    
    Map<World, CommonWorld> worlds = new WeakHashMap<World, CommonWorld>();
    
    public BukkitWorldFactory(Server s)
    {
        this.server = s;
    }

    @Override
    protected CommonWorld getWorldRaw(String name)
    {
        World world = this.server.getWorld(name);
        if(world == null) return null;
        if(!worlds.containsKey(world))
        {
            worlds.put(world, new BukkitWorld(world));
        }
        return worlds.get(world);
    }

}
