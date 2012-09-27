package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

/**
 * 
 * 
 * @author Voxel
 */
public class JockeyBrush extends Brush {
    private static int timesUsed = 0;

    /**
     * 
     */
    public JockeyBrush() {
        this.setName("Jockey");
    }

    private void sitOn(final SnipeData v) {
    	final Chunk _targetChunk = this.getWorld().getChunkAt(this.getTargetBlock().getLocation());
    	final int _targetChunkX = _targetChunk.getX();
    	final int _targetChunkZ = _targetChunk.getZ();

        double _range = Double.MAX_VALUE;
        Entity _closest = null;

        for (int _x = _targetChunkX - 1; _x <= _targetChunkX + 1; _x++) {
            for (int _y = _targetChunkZ - 1; _y <= _targetChunkZ + 1; _y++) {
                for (final Entity _e : this.getWorld().getChunkAt(_x, _y).getEntities()) {
                    if (_e.getEntityId() == v.owner().getPlayer().getEntityId()) {
                        continue;
                    }
                    
                    final Location _entityLocation = _e.getLocation();
                    final double _entityDistance = _entityLocation.distance(v.owner().getPlayer().getLocation());
                    
                    if (_entityDistance < _range) {
                        _range = _entityDistance;
                        _closest = _e;
                    }
                }
            }
        }

        if (_closest != null) {
			final Player _player = v.owner().getPlayer();
			final PlayerTeleportEvent _teleEvent =  new PlayerTeleportEvent(_player, _player.getLocation(), _closest.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
			
			Bukkit.getPluginManager().callEvent(_teleEvent);

			if (!_teleEvent.isCancelled()) {
				_player.setPassenger(_closest);
				v.sendMessage(ChatColor.GREEN + "You are now saddles on entity: " + _closest.getEntityId());
			}
		} else {
			v.sendMessage(ChatColor.RED + "Could not find any entities");
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.sitOn(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        v.owner().getPlayer().eject();
        v.owner().getPlayer().sendMessage(ChatColor.GOLD + "You have been ejected!");
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    }
    
    @Override
    public final int getTimesUsed() {
    	return JockeyBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	JockeyBrush.timesUsed = tUsed;
    }
}
