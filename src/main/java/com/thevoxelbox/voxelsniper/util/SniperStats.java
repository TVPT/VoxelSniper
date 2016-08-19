/*
 * Copyright (c) 2015-2016 VoxelBox <http://engine.thevoxelbox.com>.
 * All Rights Reserved.
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

    private File configFile;

    /**
     * Creates a new instance of SniperStats for Metrics.
     * 
     * @param pluginVersion The plugin version string marked by the platform
     * @throws IOException In the event Metrics is unable to start
     */
    public SniperStats(String pluginVersion, File configFile) throws IOException {
        super("VoxelSniper", pluginVersion);
        this.configFile = configFile;
    }

    @Override
    public String getServerVersion() {
        return "Sponge " + Sponge.getPlatform().getMinecraftVersion().getName();
    }

    @Override
    public int getPlayersOnline() {
        return Sponge.getServer().getOnlinePlayers().size();
    }

    @Override
    public File getConfigFile() {
        return this.configFile;
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
