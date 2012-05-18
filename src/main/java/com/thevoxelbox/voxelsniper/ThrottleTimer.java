
package com.thevoxelbox.voxelsniper;
import com.thevoxelbox.voxelsniper.brush.Brush;
import org.bukkit.Bukkit;

/**
 *
 * @author Gavin
 */

public class ThrottleTimer implements Runnable {

    Brush b;
    int[] pieceNumbers;
    vData v;

    public ThrottleTimer(vData vs, Brush br, int numPieces) {
        b = br;
        v = vs;
    }

    public void run() {
        VoxelSniper vsPlugin = (VoxelSniper)Bukkit.getPluginManager().getPlugin("VoxelSniper");
        if (b.throttleQueue.peek() == null) {
            Bukkit.getScheduler().cancelTask(b.currentTimerID); //this timer, kill it.
        } else {
            try {
                if (Bukkit.getScheduler().isQueued(b.currentOneOffID) || Bukkit.getScheduler().isCurrentlyRunning(b.currentOneOffID)) {
                    //nothing
                } else {
                    pieceNumbers = b.throttleQueue.poll();
                    b.currentTimerID = Bukkit.getScheduler().scheduleSyncDelayedTask(vsPlugin, new ThrottlingTask(v, b, pieceNumbers));
                }
            } catch (Exception e) {
                pieceNumbers = b.throttleQueue.poll();
                b.currentTimerID = Bukkit.getScheduler().scheduleSyncDelayedTask(vsPlugin, new ThrottlingTask(v, b, pieceNumbers));
            }
        }
    }
}
