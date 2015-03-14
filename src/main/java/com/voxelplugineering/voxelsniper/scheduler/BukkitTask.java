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

import com.voxelplugineering.voxelsniper.api.service.scheduler.Scheduler;

/**
 * A wrapper for bukkit task's for Gunsmith's {@link Scheduler}.
 */
public class BukkitTask extends Task
{

    private WeakReference<org.bukkit.scheduler.BukkitTask> task;

    /**
     * Creates a new {@link BukkitTask}.
     * 
     * @param runnable the runnable
     * @param interval the interval, in milliseconds
     * @param task the underlying {@link org.bukkit.scheduler.BukkitTask}
     */
    public BukkitTask(Runnable runnable, int interval, org.bukkit.scheduler.BukkitTask task)
    {
        super(runnable, interval);
        this.task = new WeakReference<org.bukkit.scheduler.BukkitTask>(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel()
    {
        org.bukkit.scheduler.BukkitTask bukkitTask = this.task.get();
        if (bukkitTask != null)
        {
            bukkitTask.cancel();
        }
    }

    /**
     * Gets whether the referenced task is still valid.
     * 
     * @return True if the task has not been garbage collected yet
     */
    public boolean check()
    {
        return this.task.get() != null;
    }

}
