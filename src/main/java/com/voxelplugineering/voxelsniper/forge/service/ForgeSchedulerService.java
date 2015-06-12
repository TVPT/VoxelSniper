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
package com.voxelplugineering.voxelsniper.forge.service;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.voxelplugineering.voxelsniper.api.service.logging.Logger;
import com.voxelplugineering.voxelsniper.api.service.logging.LoggingDistributor;
import com.voxelplugineering.voxelsniper.api.service.scheduler.Scheduler;
import com.voxelplugineering.voxelsniper.api.service.scheduler.Task;
import com.voxelplugineering.voxelsniper.core.service.AbstractService;
import com.voxelplugineering.voxelsniper.core.util.Context;
import com.voxelplugineering.voxelsniper.forge.service.scheduler.ForgeTask;
import com.voxelplugineering.voxelsniper.forge.service.scheduler.ForgeTaskAsync;

/**
 * A scheduler proxy for forge, works off the Tick Event from forge.
 */
public class ForgeSchedulerService extends AbstractService implements Scheduler
{

    private final Logger logger;

    private List<ForgeTask> tasks;
    private List<ForgeTaskAsync> asyncTasks;
    private ExecutorService exec;

    /**
     * Creates a new {@link ForgeSchedulerService}.
     */
    public ForgeSchedulerService(Context context)
    {
        super(context);
        this.logger = context.getRequired(LoggingDistributor.class, this);
        this.exec = java.util.concurrent.Executors.newCachedThreadPool();
    }

    @Override
    public void _init()
    {
        this.tasks = Lists.newArrayList();
        this.asyncTasks = Lists.newArrayList();
    }

    @Override
    protected void _shutdown()
    {
        stopAllTasks();
        this.tasks = null;
        this.asyncTasks = null;
    }

    @Override
    public Optional<ForgeTask> startSynchronousTask(Runnable runnable, int interval)
    {
        ForgeTask task = new ForgeTask(runnable, interval, this);
        this.tasks.add(task);
        return Optional.of(task);
    }

    @Override
    public Optional<ForgeTaskAsync> startAsynchronousTask(Runnable runnable, int interval)
    {
        ForgeTaskAsync task = new ForgeTaskAsync(runnable, interval, this, this.logger);
        this.exec.execute(task);
        this.asyncTasks.add(task);
        return Optional.of(task);
    }

    @Override
    public void stopAllTasks()
    {
        this.tasks.clear();
        for (Iterator<ForgeTaskAsync> it = this.asyncTasks.iterator(); it.hasNext();)
        {
            ForgeTaskAsync task = it.next();
            task.cancel();
        }
    }

    /**
     * Cancels the given {@link ForgeTask} if it exists with this scheduler.
     * 
     * @param forgeTask the task to cancel
     */
    public void cancel(Task forgeTask)
    {
        this.tasks.remove(forgeTask);
        this.asyncTasks.remove(forgeTask);
    }

    /**
     * Ticks the scheduler. Which ticks all tasks within this scheduler by the correct number of
     * intervals according to the delta time since this was last called.
     */
    public void onTick()
    {
        for (ForgeTask task : this.tasks)
        {
            task.tick();
        }
        for (Iterator<ForgeTaskAsync> it = this.asyncTasks.iterator(); it.hasNext();)
        {
            ForgeTaskAsync task = it.next();
            if (!task.isRunning())
            {
                it.remove();
            }
        }
    }

    @Override
    public Iterable<? extends Task> getAllTasks()
    {
        List<Task> tasks = Lists.newArrayList();
        tasks.addAll(this.tasks);
        tasks.addAll(this.asyncTasks);
        return tasks;
    }

}
