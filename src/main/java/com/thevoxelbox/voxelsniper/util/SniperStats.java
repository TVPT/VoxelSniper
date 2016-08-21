/*
 * This file is part of VoxelSniper, licensed under the MIT License (MIT).
 *
 * Copyright (c) The VoxelBox <http://thevoxelbox.com>
 * Copyright (c) contributors
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
package com.thevoxelbox.voxelsniper.util;

import com.google.common.collect.Maps;
import org.spongepowered.api.Sponge;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class SniperStats extends Metrics {

    static int snipesDone;
    static long snipeCounterInitTimeStamp;
    static Map<String, Integer> brushUsageCounter = Maps.newHashMap();

    /**
     * Increase the Snipes Counter.
     */
    public static void increaseSnipeCounter() {
        SniperStats.snipesDone++;
    }

    /**
     * Increase usage for a specific brush.
     * 
     * @param brushName Name of the Brush
     */
    public static void increaseBrushUsage(String brushName) {
        Integer current = brushUsageCounter.get(brushName);
        if (current == null) {
            brushUsageCounter.put(brushName, 1);
        } else {
            brushUsageCounter.put(brushName, current + 1);
        }
    }

    /**
     * Set Initialization time for reference when calculating average Snipes per
     * Minute.
     * 
     * @param currentTimeMillis Current time
     */
    public static void setSnipeCounterInitTimeStamp(final long currentTimeMillis) {
        SniperStats.snipeCounterInitTimeStamp = currentTimeMillis;
    }

    /**
     * Creates a new instance of SniperStats for Metrics.
     * 
     * @param pluginVersion The plugin version string marked by the platform
     * @throws IOException In the event Metrics is unable to start
     */
    public SniperStats(String pluginVersion, File configFile) throws IOException {
        super("VoxelSniper", pluginVersion, configFile);
    }

    @Override
    public String getServerVersion() {
        return Sponge.getPlatform().getImplementation().getName() + " " + Sponge.getPlatform().getMinecraftVersion().getName();
    }

    @Override
    public int getPlayersOnline() {
        return Sponge.getServer().getOnlinePlayers().size();
    }

    /**
     * Initializes the stats manager.
     */
    public void init() {
        try {
            final Graph defaultGraph = createGraph("Default");
            defaultGraph.addPlotter(new Metrics.Plotter("Average Snipes per Minute") {

                @Override
                public int getValue() {
                    final int currentSnipes = snipesDone;
                    final long initializationTimeStamp = snipeCounterInitTimeStamp;
                    final double deltaTime = System.currentTimeMillis() - initializationTimeStamp;

                    double average;
                    if (deltaTime < 60000) {
                        average = currentSnipes;
                    } else {
                        final double timeRunning = deltaTime / 60000;
                        average = currentSnipes / timeRunning;
                    }

                    if (average > 10000) {
                        average = 0;
                    }

                    return (int) Math.floor(average);
                }
            });

            final Graph brushUsageGraph = createGraph("Brush Usage");

            for (final Map.Entry<String, Integer> entry : brushUsageCounter.entrySet()) {
                brushUsageGraph.addPlotter(new Metrics.Plotter(entry.getKey()) {

                    @Override
                    public int getValue() {
                        return entry.getValue();
                    }

                    @Override
                    public void reset() {
                        brushUsageCounter.remove(entry.getKey());
                    }
                });
            }

            start();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Disables the stats manager.
     */
    public void stop() {
        try {
            disable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restarts the stats manager.
     */
    public void restart() {
        stop();
        start();
    }
}
