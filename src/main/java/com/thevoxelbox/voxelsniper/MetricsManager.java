package com.thevoxelbox.voxelsniper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;

import com.thevoxelbox.voxelsniper.brush.Brush;

/**
 * @author Monofraps
 */
public final class MetricsManager {
    private static int snipesDone = 0;
    private static long snipeCounterInitTimeStamp = 0;
    private static MetricsManager instance;

    /**
     * @return {@link MetricsManager}
     */
    public static MetricsManager getInstance() {
        if (MetricsManager.instance == null) {
            MetricsManager.instance = new MetricsManager();
        }

        return MetricsManager.instance;
    }

    /**
     * Increase the Snipes Counter.
     */
    public static void increaseSnipeCounter() {
        MetricsManager.snipesDone++;
    }

    /**
     * Set Initialization time for reference when calculating average Snipes per Minute.
     * 
     * @param currentTimeMillis
     */
    public static void setSnipeCounterInitTimeStamp(final long currentTimeMillis) {
        MetricsManager.snipeCounterInitTimeStamp = currentTimeMillis;
    }

    private MetricsManager() {
    }

    /**
     * Start sending Metrics.
     */
    public void start() {
        try {
            final Metrics _metrics = new Metrics(VoxelSniper.getInstance());

            final Graph _graph = _metrics.createGraph("Snipers Online");
            _graph.addPlotter(new Metrics.Plotter("Snipers Online") {

                @Override
                public int getValue() {
                    int _count = 0;
                    for (final Player _player : Bukkit.getOnlinePlayers()) {
                        if (VoxelSniper.getSniperPermissionHelper().isSniper(_player)) {
                            _count++;
                        }
                    }
                    return _count;
                }
            });
            _graph.addPlotter(new Metrics.Plotter("Litesnipers Online") {

                @Override
                public int getValue() {
                    int _count = 0;
                    for (final Player _player : Bukkit.getOnlinePlayers()) {
                        if (VoxelSniper.getSniperPermissionHelper().isLiteSniper(_player)) {
                            _count++;
                        }
                    }
                    return _count;
                }
            });

            _metrics.addCustomData(new Metrics.Plotter("Average Snipes per Minute") {

                @Override
                public int getValue() {
                    final int _currentSnipes = MetricsManager.snipesDone;
                    final long _initializationTimeStamp = MetricsManager.snipeCounterInitTimeStamp;
                    final double _timeRunning = (System.currentTimeMillis() - _initializationTimeStamp) / 60000;
                    final double _avg = _currentSnipes / _timeRunning;

                    return NumberConversions.floor(_avg);
                }
            });

            final Graph _graphBrushUsage = _metrics.createGraph("Brush Usage");

            final HashMap<String, Brush> _temp = SniperBrushes.getSniperBrushes();
            for (final Entry<String, Brush> _entry : _temp.entrySet()) {
                _graphBrushUsage.addPlotter(new Metrics.Plotter(SniperBrushes.getName(_entry.getValue())) {
                    @Override
                    public int getValue() {
                        return _entry.getValue().getTimesUsed();
                    }

                    @Override
                    public void reset() {
                        _entry.getValue().setTimesUsed(0);
                    }
                });
            }

            _metrics.start();
        } catch (final IOException _e) {
            VoxelSniper.getInstance().getLogger().finest("Failed to submit Metrics Data.");
        }
    }
}
