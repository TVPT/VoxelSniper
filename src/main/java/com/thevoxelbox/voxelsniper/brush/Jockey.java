package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;

/**
 * 
 * 
 * @author Voxel
 */
public class Jockey extends Brush {
    private static int timesUsed = 0;

    public Jockey() {
        this.setName("Jockey");
    }

    private void sitOn(final SnipeData v) {
        final Location _loc = v.owner().getPlayer().getLocation();
        _loc.getX();
        _loc.getY();
        _loc.getZ();

        Entity _closest = null;
        double _range = 99999999;

        Chunk _chunk = this.getWorld().getChunkAt(this.getTargetBlock().getLocation());
        final int chunkx = _chunk.getX();
        final int chunkz = _chunk.getZ();

        for (int _x = chunkx - 1; _x <= chunkx + 1; _x++) {
            for (int _y = chunkz - 1; _y <= chunkz + 1; _y++) {
                _chunk = this.getWorld().getChunkAt(_x, _y);
                final Entity[] _toCheck = _chunk.getEntities();
                for (final Entity _e : _toCheck) {
                    if (_e.getEntityId() == v.owner().getPlayer().getEntityId()) {
                        continue;
                    }
                    final Location _entityLocation = _e.getLocation();

                    final double _entityRange = Math.pow(this.getBlockPositionX() - _entityLocation.getX(), 2) + Math.pow(this.getBlockPositionY() - _entityLocation.getY(), 2) + Math.pow(this.getBlockPositionZ() - _entityLocation.getZ(), 2);

                    if (_entityRange < _range) {
                        _range = _entityRange;
                        _closest = _e;
                    }
                }
            }
        }

        if (_closest != null) {
            boolean _teleport = false;
            PlayerTeleportEvent _teleEvent = null;

            final Player _player = v.owner().getPlayer();
            _teleport = _player.isOnline();

            if (_teleport) {
                _teleEvent = new PlayerTeleportEvent(_player, _player.getLocation(), _closest.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                Bukkit.getPluginManager().callEvent(_teleEvent);
                _teleport = !_teleEvent.isCancelled();
            }

            if (_teleport) {
                ((CraftEntity) v.owner().getPlayer()).getHandle().setPassengerOf(((CraftEntity) _closest).getHandle());
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
    	return Jockey.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	Jockey.timesUsed = tUsed;
    }
}
