package com.thevoxelbox.voxelsniper;

import com.google.common.collect.Maps;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Monofraps
 */
public final class MetricsManager
{
    private static int snipesDone = 0;
    private static long snipeCounterInitTimeStamp = 0;
    private static MetricsManager instance;
    private static Map<String, Integer> brushUsageCounter = Maps.newHashMap();

    private MetricsManager()
    {
    }

    /**
     * @return {@link MetricsManager}
     */
    public static MetricsManager getInstance()
    {
        if (MetricsManager.instance == null)
        {
            MetricsManager.instance = new MetricsManager();
        }

        return MetricsManager.instance;
    }

    /**
     * Increase the Snipes Counter.
     */
    public static void increaseSnipeCounter()
    {
        MetricsManager.snipesDone++;
    }

    /**
     * Increase usage for a specific brush.
     *
     * @param brushName Name of the Brush
     */
    public static void increaseBrushUsage(String brushName)
    {
        if (brushUsageCounter.get(brushName) == null)
        {
            brushUsageCounter.put(brushName, 0);
        }
        brushUsageCounter.put(brushName, brushUsageCounter.get(brushName));
    }

    /**
     * Set Initialization time for reference when calculating average Snipes per Minute.
     *
     * @param currentTimeMillis
     */
    public static void setSnipeCounterInitTimeStamp(final long currentTimeMillis)
    {
        MetricsManager.snipeCounterInitTimeStamp = currentTimeMillis;
    }

    /**
     * Start sending Metrics.
     */
    public void start()
    {
        try
        {
            final Metrics metrics = new Metrics(VoxelSniper.getInstance());

            final Graph defaultGraph = metrics.createGraph("Default");
            defaultGraph.addPlotter(new Metrics.Plotter("Average Snipes per Minute")
            {

                @Override
                public int getValue()
                {
                    final int currentSnipes = MetricsManager.snipesDone;
                    final long initializationTimeStamp = MetricsManager.snipeCounterInitTimeStamp;
                    final double deltaTime = System.currentTimeMillis() - initializationTimeStamp;

                    double average = 0;
                    if (deltaTime < 60000)
                    {
                        average = currentSnipes;
                    }
                    else
                    {
                        final double timeRunning = deltaTime / 60000;
                        average = currentSnipes / timeRunning;
                    }

                    // quite unlikely ...
                    if (average > 10000)
                    {
                        average = 0;
                    }

                    return NumberConversions.floor(average);
                }
            });


            final Graph brushUsageGraph = metrics.createGraph("Brush Usage");

            for (final Map.Entry<String, Integer> entry : brushUsageCounter.entrySet())
            {
                brushUsageGraph.addPlotter(new Metrics.Plotter(entry.getKey())
                {
                    @Override
                    public int getValue()
                    {
                        return entry.getValue();
                    }

                    @Override
                    public void reset()
                    {
                        brushUsageCounter.remove(entry.getKey());
                    }
                });
            }

            metrics.start();
        }
        catch (final IOException exception)
        {
            VoxelSniper.getInstance().getLogger().finest("Failed to submit Metrics Data.");
        }
    }
}
