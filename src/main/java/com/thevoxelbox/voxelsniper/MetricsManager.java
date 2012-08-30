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

public class MetricsManager {
	private static int snipesDone = 0;
	private static long snipeCounterInitTimeStamp = 0;
	private static MetricsManager instance;

	private MetricsManager() {

	}

	public void start() {
		try {
			Metrics _metrics = new Metrics(VoxelSniper.getInstance());

			
			Graph _graph = _metrics.createGraph("Snipers Online");
			_graph.addPlotter(new Metrics.Plotter("Snipers Online") {

				@Override
				public int getValue() {
					int _count = 0;
					for (Player _player : Bukkit.getOnlinePlayers()) { 
						if (VoxelSniperListener.getSniperPermissionHelper().isSniper(_player)) {
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
					for (Player _player : Bukkit.getOnlinePlayers()) { 
						if (VoxelSniperListener.getSniperPermissionHelper().isLiteSniper(_player)) {
							_count++;
						}
					}
					return _count;
				}
			});

			_metrics.addCustomData(new Metrics.Plotter("Average Snipes per Minute") {

				@Override
				public int getValue() {
					int _currentSnipes = snipesDone;
					long _initializationTimeStamp = snipeCounterInitTimeStamp;
					double _timeRunning = (System.currentTimeMillis() - _initializationTimeStamp) / 60000;
					double _avg = _currentSnipes / _timeRunning;

					return NumberConversions.floor(_avg);
				}
			});

			Graph _graphBrushUsage = _metrics.createGraph("Brush Usage");
			
			final HashMap<String, Brush> _temp = vBrushes.getSniperBrushes();
			for(final Entry<String, Brush> _entry : _temp.entrySet()) {
				_graphBrushUsage.addPlotter(new Metrics.Plotter(vBrushes.getName(_entry.getValue())) {
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
		} catch (IOException _e) {
			// Failed to submit the stats :-(
		}
	}

	public static void increaseSnipeCounter() {
		snipesDone++;
	}

	public static void setSnipeCounterInitTimeStamp(long currentTimeMillis) {
		snipeCounterInitTimeStamp = currentTimeMillis;
	}

	public static MetricsManager getInstance() {
		if (instance == null) {
			instance = new MetricsManager();
		}

		return instance;
	}
}
