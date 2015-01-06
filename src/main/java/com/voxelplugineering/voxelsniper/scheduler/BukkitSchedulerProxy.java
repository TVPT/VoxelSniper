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
package com.voxelplugineering.voxelsniper.scheduler;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.google.common.collect.Lists;
import com.voxelplugineering.voxelsniper.api.service.scheduler.Scheduler;

/**
 * A proxy for Bukkit's {@link BukkitScheduler}.
 */
public class BukkitSchedulerProxy implements Scheduler
{

    private List<WeakReference<BukkitTask>> tasks = Lists.newArrayList();
    private Plugin plugin;

    /**
     * Creates a new {@link BukkitSchedulerProxy}.
     * 
     * @param plugin The plugin
     */
    public BukkitSchedulerProxy(Plugin plugin)
    {
        this.plugin = plugin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task startSynchronousTask(Runnable runnable, int interval)
    {
        BukkitTask newTask = new BukkitTask(runnable, interval, Bukkit.getScheduler().runTaskTimer(this.plugin, runnable, 0, interval / 50));
        this.tasks.add(new WeakReference<BukkitTask>(newTask));
        return newTask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task startAsynchronousTask(Runnable runnable, int interval)
    {
        BukkitTask newTask =
                new BukkitTask(runnable, interval, Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, runnable, 0, interval / 50));
        this.tasks.add(new WeakReference<BukkitTask>(newTask));
        return newTask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopAllTasks()
    {
        for (Iterator<WeakReference<BukkitTask>> iter = this.tasks.iterator(); iter.hasNext();)
        {
            WeakReference<BukkitTask> task = iter.next();
            if (task.get() == null)
            {
                iter.remove();
                continue;
            }
            task.get().cancel();
            iter.remove();
        }
    }
}
