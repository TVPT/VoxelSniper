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
import java.util.ArrayList;
import java.util.List;
import javax.swing.plaf.basic.BasicScrollPaneUI.VSBChangeListener;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Jockey_Brush
 * 
 * @author Voxel
 * @author Monofraps
 */
public class JockeyBrush extends Brush {
	private static int timesUsed = 0;

        private final int ENTITY_STACK_LIMIT = 50;
        
	private JockeyType jockeyType = JockeyType.NORMAL_ALL_ENTITIES;
	private Entity jockeyedEntity = null;

	private enum JockeyType {
		NORMAL_ALL_ENTITIES, NORMAL_PLAYER_ONLY, INVERSE_ALL_ENTITIES, INVERSE_PLAYER_ONLY, STACK_ALL_ENTITIES, STACK_PLAYER_ONLY
	}

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

					if (jockeyType == JockeyType.NORMAL_PLAYER_ONLY || jockeyType == JockeyType.INVERSE_PLAYER_ONLY) {
						if (!(_e instanceof Player)) {
							continue;
						}
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
			final PlayerTeleportEvent _teleEvent = new PlayerTeleportEvent(_player, _player.getLocation(), _closest.getLocation(),
					PlayerTeleportEvent.TeleportCause.PLUGIN);

			Bukkit.getPluginManager().callEvent(_teleEvent);

			if (!_teleEvent.isCancelled()) {
				if (jockeyType == JockeyType.INVERSE_PLAYER_ONLY || jockeyType == JockeyType.INVERSE_ALL_ENTITIES) {
					_player.setPassenger(_closest);
				} else {
					_closest.setPassenger(_player);
					jockeyedEntity = _closest;
				}
				v.sendMessage(ChatColor.GREEN + "You are now saddles on entity: " + _closest.getEntityId());
			}
		} else {
			v.sendMessage(ChatColor.RED + "Could not find any entities");
		}
	}
        
        private void stack(final SnipeData v){
            List<Entity> nearbyEntities = v.owner().getPlayer().getNearbyEntities(v.getBrushSize() * 2, v.getBrushSize() * 2, v.getBrushSize() * 2);
            Entity lastEntity = v.owner().getPlayer(); 
            int stackHeight = 0;
            
            for(Entity e : nearbyEntities){
                if(!(stackHeight >= ENTITY_STACK_LIMIT)){
                    if(jockeyType == JockeyType.STACK_ALL_ENTITIES){
                        lastEntity.setPassenger(e);
                        lastEntity = e;
                        stackHeight++;
                    }
                    else if(jockeyType == JockeyType.STACK_PLAYER_ONLY){
                        if(e instanceof Player){
                            lastEntity.setPassenger(e);
                            lastEntity = e;
                            stackHeight++;
                        }
                    }
                    else{
                        v.owner().getPlayer().sendMessage("You broke stack! :O");
                    }
                } 
                else{
                    return;
                }
            }
            
        }

	@Override
	protected final void arrow(final SnipeData v) {
            if(jockeyType == JockeyType.STACK_ALL_ENTITIES || jockeyType == JockeyType.STACK_PLAYER_ONLY){
                stack(v);
            }
            else{
                this.sitOn(v);
            }
		
	}

	@Override
	protected final void powder(final SnipeData v) {
		if (jockeyType == JockeyType.INVERSE_PLAYER_ONLY || jockeyType == JockeyType.INVERSE_ALL_ENTITIES) {
			v.owner().getPlayer().eject();
			v.owner().getPlayer().sendMessage(ChatColor.GOLD + "The guy on top of you has been ejected!");
		} else {
			if (jockeyedEntity != null) {
				jockeyedEntity.eject();
				jockeyedEntity = null;
				v.owner().getPlayer().sendMessage(ChatColor.GOLD + "You have been ejected!");
			}
		}

	}

	@Override
	public final void info(final Message vm) {
		vm.brushName(this.getName());
		vm.custom("Current jockey mode: " + ChatColor.GREEN + jockeyType.toString());
                vm.custom(ChatColor.GREEN + "Help: " + ChatColor.AQUA + "http://www.voxelwiki.com/minecraft/Voxelsniper#The_Jockey_Brush" );
	}

	@Override
	public void parameters(final String[] par, final SnipeData v) {
		boolean _inverse = false;
		boolean _playerOnly = false;
                boolean _stack = false;

		try {
			for (String _arg : par) {
				if (_arg.startsWith("-i:")) {
					_inverse = _arg.endsWith("y") ? true : false;
				}

				if (_arg.startsWith("-po:")) {
					_playerOnly = _arg.endsWith("y") ? true : false;
				}
				if (_arg.startsWith("-s:")) {
					_stack = _arg.endsWith("y") ? true : false;
				}                               

			}

			if (_inverse) {
				if (_playerOnly) {
					jockeyType = JockeyType.INVERSE_PLAYER_ONLY;
				} else {
					jockeyType = JockeyType.INVERSE_ALL_ENTITIES;
				}
                        } else if(_stack){
                                if(_playerOnly){
                                    jockeyType = JockeyType.STACK_PLAYER_ONLY;
                                } else {
                                    jockeyType = JockeyType.STACK_ALL_ENTITIES;
                                }
			} else {
				if (_playerOnly) {
					jockeyType = JockeyType.NORMAL_PLAYER_ONLY;
				} else {
					jockeyType = JockeyType.NORMAL_ALL_ENTITIES;
				}
			}                        

			v.sendMessage("Current jockey mode: " + ChatColor.GREEN + jockeyType.toString());
		} catch (Exception _e) {
			v.sendMessage("Error while parsing your arguments.");
			_e.printStackTrace();
		}
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
