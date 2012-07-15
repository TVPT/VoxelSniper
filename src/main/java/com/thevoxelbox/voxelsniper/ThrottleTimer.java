package com.thevoxelbox.voxelsniper;

import org.bukkit.Bukkit;

import com.thevoxelbox.voxelsniper.brush.Brush;

/**
 * 
 * @author Gavin
 */

public class ThrottleTimer implements Runnable {

    private final Brush brush;
    private int[] pieceNumbers;
    private final vData v;

    /**
     * @param vs
     * @param br
     * @param numPieces
     */
    public ThrottleTimer(final vData vs, final Brush br, final int numPieces) {
        this.brush = br;
        this.v = vs;
    }

    @Override
    public final void run() {
        final VoxelSniper _vsPlugin = (VoxelSniper) Bukkit.getPluginManager().getPlugin("VoxelSniper");
        if (this.brush.throttleQueue.peek() == null) {
            // this timer, kill it.
            Bukkit.getScheduler().cancelTask(this.brush.currentTimerID);
        } else {
            try {
                if (!(Bukkit.getScheduler().isQueued(this.brush.currentOneOffID) || Bukkit.getScheduler().isCurrentlyRunning(this.brush.currentOneOffID))) {
                    this.pieceNumbers = this.brush.throttleQueue.poll();
                    this.brush.currentTimerID = Bukkit.getScheduler().scheduleSyncDelayedTask(_vsPlugin,
                            new ThrottlingTask(this.v, this.brush, this.pieceNumbers));
                }
            } catch (final Exception _e) {
                this.pieceNumbers = this.brush.throttleQueue.poll();
                this.brush.currentTimerID = Bukkit.getScheduler().scheduleSyncDelayedTask(_vsPlugin, new ThrottlingTask(this.v, this.brush, this.pieceNumbers));
            }
        }
    }
}
