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

import com.voxelplugineering.voxelsniper.api.service.logging.Logger;
import com.voxelplugineering.voxelsniper.api.service.scheduler.Task;
import com.voxelplugineering.voxelsniper.forge.service.ForgeSchedulerService;

/**
 * An asynchronous task.
 */
public class ForgeTaskAsync extends Task implements Runnable
{

    private final Logger logger;

    private boolean running;
    private ForgeSchedulerService scheduler;

    /**
     * Creates a new {@link ForgeTaskAsync}.
     * 
     * @param runnable The task runnable
     * @param interval The interval
     * @param scheduler The scheduler
     */
    public ForgeTaskAsync(Runnable runnable, int interval, ForgeSchedulerService scheduler, Logger logger)
    {
        super(runnable, interval);
        this.running = true;
        this.logger = logger;
    }

    @Override
    public void cancel()
    {
        this.running = false;
        this.scheduler.cancel(this);
    }

    /**
     * Starts the task executing on the current thread, recommend call from a spawned thread.
     */
    @Override
    public void run()
    {
        long start = 0;
        long delta = 0;
        while (this.running)
        {
            start = System.currentTimeMillis();
            this.getRunnable().run();
            delta = System.currentTimeMillis() - start;
            try
            {
                Thread.sleep(this.getInterval() - delta);
            } catch (InterruptedException e)
            {
                this.logger.warn("Asynchronous task interrupted, stopping.");
                this.running = false;
            }
        }
    }

    /**
     * Gets whether this task is still running.
     * 
     * @return Is running
     */
    public boolean isRunning()
    {
        return this.running;
    }

}
