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
package com.voxelplugineering.voxelsniper.bukkit.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.voxelplugineering.voxelsniper.api.service.scheduler.Scheduler;
import com.voxelplugineering.voxelsniper.bukkit.service.scheduler.BukkitTask;
import com.voxelplugineering.voxelsniper.core.Gunsmith;
import com.voxelplugineering.voxelsniper.core.service.AbstractService;

/**
 * A proxy for Bukkit's {@link org.bukkit.scheduler.BukkitScheduler}.
 */
public class BukkitSchedulerService extends AbstractService implements Scheduler
{

    private List<BukkitTask> tasks;
    private final org.bukkit.plugin.Plugin plugin;
    private final org.bukkit.scheduler.BukkitScheduler scheduler;

    /**
     * Creates a new {@link BukkitSchedulerService}.
     * 
     * @param plugin The plugin
     */
    public BukkitSchedulerService(org.bukkit.plugin.Plugin plugin)
    {
        super(Scheduler.class, 11);
        this.plugin = checkNotNull(plugin);
        this.scheduler = org.bukkit.Bukkit.getScheduler();
    }

    @Override
    public String getName()
    {
        return "scheduler";
    }

    @Override
    public void init()
    {
        this.tasks = Lists.newArrayList();
        Gunsmith.getLogger().info("Initialized BukkitSchedulerProxy service");
    }

    @Override
    protected void destroy()
    {
        stopAllTasks();
        this.tasks = null;
        Gunsmith.getLogger().info("Stopped BukkitSchedulerProxy service");
    }

    @Override
    public Optional<BukkitTask> startSynchronousTask(Runnable runnable, int interval)
    {
        org.bukkit.scheduler.BukkitTask task = this.scheduler.runTaskTimer(this.plugin, runnable, 0, interval / 50);
        BukkitTask newTask = new BukkitTask(runnable, interval, task);
        this.tasks.add(newTask);
        return Optional.of(newTask);
    }

    @Override
    public Optional<BukkitTask> startAsynchronousTask(Runnable runnable, int interval)
    {
        org.bukkit.scheduler.BukkitTask task = this.scheduler.runTaskTimerAsynchronously(this.plugin, runnable, 0, interval / 50);
        BukkitTask newTask = new BukkitTask(runnable, interval, task);
        this.tasks.add(newTask);
        return Optional.of(newTask);
    }

    @Override
    public void stopAllTasks()
    {
        for (Iterator<BukkitTask> iter = this.tasks.iterator(); iter.hasNext();)
        {
            BukkitTask task = iter.next();
            task.cancel();
            iter.remove();
        }
    }

    @Override
    public List<BukkitTask> getAllTasks()
    {
        return this.tasks;
    }
}
