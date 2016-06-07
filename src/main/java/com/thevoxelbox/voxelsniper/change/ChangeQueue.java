/*
 * Copyright (c) 2015-2016 VoxelBox <http://engine.thevoxelbox.com>.
 * All Rights Reserved.
 */
package com.thevoxelbox.voxelsniper.change;

import com.thevoxelbox.genesis.logging.Log;

import com.google.common.collect.Queues;

import java.util.Deque;
import java.util.concurrent.TimeUnit;

public class ChangeQueue implements Runnable {

    private static final int    TARGET_TPS   = 20;
    private static final long   TARGET_NANOS = TimeUnit.SECONDS.toNanos(1) / TARGET_TPS;
    private static final long   DELTA_BIAS   = 500000;

    private final Deque<Change> queue        = Queues.newArrayDeque();

    private WorkMode            mode         = WorkMode.NORMAL;
    private long                lasttime     = -1;
    private int                 warnCount    = 0;

    public ChangeQueue() {

    }

    @Override
    public void run() {
        if (this.lasttime == -1) {
            this.lasttime = System.nanoTime();
            return;
        }

        if (!this.queue.isEmpty()) {
            long delta = System.nanoTime() - this.lasttime;
            long remaining = TARGET_NANOS - DELTA_BIAS - delta;
            if (this.mode == WorkMode.NORMAL) {
                if (remaining > DELTA_BIAS) {
                    this.warnCount = 0;
                    long start = System.nanoTime();
                    while (!this.queue.isEmpty()) {
                        Change next = this.queue.peek();
                        if (next.isDone()) {
                            this.queue.removeFirst();
                        } else {
                            next.performSegment();
                            if (System.nanoTime() - start > remaining) {
                                break;
                            }
                        }
                    }
                } else {
                    this.warnCount++;
                    if (this.warnCount == 100) {
                        Log.GLOBAL.warn("Server is overloaded, VoxelSniper has not had the time to tick in over 5 seconds. "
                                + "Switching to minimal work mode.");
                        this.mode = WorkMode.MINIMAL;
                    }
                }
            } else {
                Change next = this.queue.peek();
                next.performSegment();
                if (next.isDone()) {
                    this.queue.removeFirst();
                }
                if (remaining > DELTA_BIAS) {
                    this.warnCount = 0;
                    this.mode = WorkMode.NORMAL;
                }
            }
        }

        this.lasttime = System.nanoTime();
    }

    private static enum WorkMode {
        NORMAL,
        MINIMAL;
    }

}
