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
package com.voxelplugineering.voxelsniper.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;
import com.google.common.collect.MapMaker;
import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.api.service.scheduler.Scheduler;
import com.voxelplugineering.voxelsniper.api.service.scheduler.Task;
import com.voxelplugineering.voxelsniper.service.AbstractService;
import com.voxelplugineering.voxelsniper.service.scheduler.SpongeTask;

/**
 * A proxy for sponge's scheduler.
 */
public class SpongeSchedulerService extends AbstractService implements Scheduler
{

    private static final int MILLISECONDS_PER_TICK = 50;

    private Map<org.spongepowered.api.service.scheduler.Task, SpongeTask> tasks;

    /**
     * Creates a new {@link SpongeSchedulerService}.
     */
    public SpongeSchedulerService()
    {
        super(11);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return "scheduler";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
        this.tasks = new MapMaker().weakKeys().makeMap();
        Gunsmith.getLogger().info("Initialized SpongeSchedulerProxy service");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void destroy()
    {
        stopAllTasks();
        this.tasks = null;
        Gunsmith.getLogger().info("Stopped SpongeSchedulerProxy service");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SpongeTask> startSynchronousTask(Runnable runnable, int interval)
    {
        //TODO cleanup this line...
        Optional<org.spongepowered.api.service.scheduler.Task> task =
                ((SpongePlatformProxyService) Gunsmith.getPlatformProxy())
                        .getGame()
                        .getSyncScheduler()
                        .runRepeatingTask(
                                ((SpongePlatformProxyService) Gunsmith.getPlatformProxy()).getGame().getPluginManager().getPlugin("voxelsniper-sponge")
                                        .get(), runnable, interval / MILLISECONDS_PER_TICK);
        if (task.isPresent())
        {
            SpongeTask stask = new SpongeTask(task.get(), runnable, interval);
            this.tasks.put(task.get(), stask);
            return Optional.of(stask);
        }
        return Optional.absent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SpongeTask> startAsynchronousTask(Runnable runnable, int interval)
    {
        Optional<org.spongepowered.api.service.scheduler.Task> task =
                ((SpongePlatformProxyService) Gunsmith.getPlatformProxy())
                        .getGame()
                        .getAsyncScheduler()
                        .runRepeatingTask(
                                ((SpongePlatformProxyService) Gunsmith.getPlatformProxy()).getGame().getPluginManager().getPlugin("VoxelSniper-Sponge")
                                        .get(), runnable, TimeUnit.MILLISECONDS, interval / MILLISECONDS_PER_TICK);
        if (task.isPresent())
        {
            SpongeTask stask = new SpongeTask(task.get(), runnable, interval);
            this.tasks.put(task.get(), stask);
            return Optional.of(stask);
        }
        return Optional.absent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopAllTasks()
    {
        for (org.spongepowered.api.service.scheduler.Task task : this.tasks.keySet())
        {
            task.cancel();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<? extends Task> getAllTasks()
    {
        return this.tasks.values();
    }
}
