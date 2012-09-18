package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

/**
 * 
 * @author Voxel
 */
public class WarpInStyle extends Brush {
    private static int timesUsed = 0;

    /**
     * 
     */
    public WarpInStyle() {
        this.setName("Warp Like a Boss");
    }

    @Override
    protected final void arrow(final SnipeData v) {
    	Player _player = v.owner().getPlayer();
    	_player.teleport(new Location(_player.getWorld(), this.getLastBlock().getX(), this.getLastBlock().getY(), this.getLastBlock().getZ(), _player.getLocation().getYaw(), _player.getLocation().getPitch()));    
    }

    @Override
    protected final void powder(final SnipeData v) {
    	Player _player = v.owner().getPlayer();
        this.getWorld().strikeLightning(_player.getLocation());
        _player.teleport(new Location(_player.getWorld(), this.getLastBlock().getX(), this.getLastBlock().getY(), this.getLastBlock().getZ(), _player.getLocation().getYaw(), _player.getLocation().getPitch()));
        this.getWorld().strikeLightning(_player.getLocation());
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
    }

    @Override
    public final int getTimesUsed() {
        return WarpInStyle.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
        WarpInStyle.timesUsed = tUsed;
    }

}
