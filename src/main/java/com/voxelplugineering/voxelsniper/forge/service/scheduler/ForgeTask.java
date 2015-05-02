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
package com.voxelplugineering.voxelsniper.forge.service.scheduler;

import com.voxelplugineering.voxelsniper.api.service.scheduler.Task;
import com.voxelplugineering.voxelsniper.forge.service.ForgeSchedulerService;

/**
 * A task for the {@link ForgeSchedulerService}.
 */
public class ForgeTask extends Task
{

    private static final int MILLISECONDS_PER_TICK = 50;

    private ForgeSchedulerService scheduler;
    private int ticks = 0;

    /**
     * Creates a new {@link ForgeTask}.
     * 
     * @param runnable the runnable for the task
     * @param interval the interval to execute the task on
     * @param scheduler the scheduler controlling this task
     */
    public ForgeTask(Runnable runnable, int interval, ForgeSchedulerService scheduler)
    {
        super(runnable, interval);
        this.scheduler = scheduler;
    }

    @Override
    public void cancel()
    {
        this.scheduler.cancel(this);
    }

    /**
     * Returns whether this task should be run.
     * 
     * @return should run
     */
    public boolean shouldTick()
    {
        return this.ticks * MILLISECONDS_PER_TICK > this.getInterval();
    }

    /**
     * Executes this task if the number of elapsed ticks is greater than the interval number of
     * times. Please note that this will not attempt to stack multiple ticks in the event of server
     * lag causing a larger effective delta time as this would only compound any lag experienced.
     */
    public void tick()
    {
        this.ticks++;
        if (shouldTick())
        {
            this.getRunnable().run();
            this.ticks = 0;
        }
    }

}
