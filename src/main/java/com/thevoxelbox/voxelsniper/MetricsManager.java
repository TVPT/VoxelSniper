package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.brush.IBrush;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;

import java.io.IOException;
import java.util.HashSet;

/**
 * @author Monofraps
 */
public final class MetricsManager
{
    private static int snipesDone = 0;
    private static long snipeCounterInitTimeStamp = 0;
    private static MetricsManager instance;

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

            final Graph snipersOnlineGraph = metrics.createGraph("Snipers Online");
            snipersOnlineGraph.addPlotter(new Metrics.Plotter("Snipers Online")
            {

                @Override
                public int getValue()
                {
                    int count = 0;
                    for (final Player player : Bukkit.getOnlinePlayers())
                    {
                        if (VoxelSniper.getInstance().getSniperPermissionHelper().isSniper(player))
                        {
                            count++;
                        }
                    }
                    return count;
                }
            });
            snipersOnlineGraph.addPlotter(new Metrics.Plotter("Litesnipers Online")
            {

                @Override
                public int getValue()
                {
                    int count = 0;
                    for (final Player player : Bukkit.getOnlinePlayers())
                    {
                        if (VoxelSniper.getInstance().getSniperPermissionHelper().isLiteSniper(player))
                        {
                            count++;
                        }
                    }
                    return count;
                }
            });

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

            final HashSet<IBrush> brushes = new HashSet<IBrush>(Brushes.getNewSniperBrushInstances().values());

            for (final IBrush brush : brushes)
            {
                brushUsageGraph.addPlotter(new Metrics.Plotter(brush.getName())
                {
                    @Override
                    public int getValue()
                    {
                        return brush.getTimesUsed();
                    }

                    @Override
                    public void reset()
                    {
                        brush.setTimesUsed(0);
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
