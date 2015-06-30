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
package com.voxelplugineering.voxelsniper.sponge.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.PluginContainer;

import com.google.common.base.Optional;
import com.google.common.collect.MapMaker;
import com.voxelplugineering.voxelsniper.service.AbstractService;
import com.voxelplugineering.voxelsniper.service.scheduler.Scheduler;
import com.voxelplugineering.voxelsniper.service.scheduler.Task;
import com.voxelplugineering.voxelsniper.sponge.service.scheduler.SpongeTask;
import com.voxelplugineering.voxelsniper.util.Context;

/**
 * A proxy for sponge's scheduler.
 */
public class SpongeSchedulerService extends AbstractService implements Scheduler
{

    private static final int MILLISECONDS_PER_TICK = 50;

    private Map<org.spongepowered.api.service.scheduler.Task, SpongeTask> tasks;
    private final PluginContainer plugin;
    private final Game game;

    /**
     * Creates a new {@link SpongeSchedulerService}.
     * 
     * @param plugin The plugin container
     * @param game The game
     */
    public SpongeSchedulerService(Context context, PluginContainer plugin, Game game)
    {
        super(context);
        this.plugin = plugin;
        this.game = game;
    }

    @Override
    protected void _init()
    {
        this.tasks = new MapMaker().weakKeys().makeMap();
    }

    @Override
    protected void _shutdown()
    {
        stopAllTasks();
        this.tasks = null;
    }

    @Override
    public Optional<SpongeTask> startSynchronousTask(Runnable runnable, int interval)
    {
        org.spongepowered.api.service.scheduler.Task task =
                this.game.getScheduler().getTaskBuilder().interval(interval, TimeUnit.MILLISECONDS).execute(runnable).submit(this.plugin);
        SpongeTask stask = new SpongeTask(task, runnable, interval);
        this.tasks.put(task, stask);
        return Optional.of(stask);
    }

    @Override
    public Optional<SpongeTask> startAsynchronousTask(Runnable runnable, int interval)
    {
        org.spongepowered.api.service.scheduler.Task task =
                this.game.getScheduler().getTaskBuilder().async().interval(interval, TimeUnit.MILLISECONDS).execute(runnable).submit(this.plugin);
        SpongeTask stask = new SpongeTask(task, runnable, interval);
        this.tasks.put(task, stask);
        return Optional.of(stask);
    }

    @Override
    public void stopAllTasks()
    {
        for (org.spongepowered.api.service.scheduler.Task task : this.tasks.keySet())
        {
            task.cancel();
        }
    }

    @Override
    public Iterable<? extends Task> getAllTasks()
    {
        return this.tasks.values();
    }
}
