package com.thevoxelbox.voxelsniper.brush;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Monofraps
 * @author Deamon
 * @author MikeMatrix
 * 
 */
public class PunishBrush extends PerformBrush {

    /**
     * @author Monofraps
     * 
     */
    private enum Punishment {
        // Monofraps
        FIRE, LIGHTNING, BLINDNESS, DRUNK, KILL, RANDOMTP, ALL_POTION,
        // Deamon
        INVERT, JUMP,
        // MikeMatrix
        FORCE, HYPNO
    }

    private static final int MAXIMAL_RANDOM_TELEPORTATION_RANGE = 400;

    private static final int TICKS_PER_SECOND = 20;
    private static final int INFINIPUNISH_SIZE = -3;
    private static final int DEFAULT_PUNISH_LEVEL = 10;
    private static final int DEFAULT_PUSNIH_DURATION = 60;

    private static int timesUsed = 0;

    private Punishment punishment = Punishment.FIRE;
    private int punishLevel = DEFAULT_PUNISH_LEVEL;
    private int punishDuration = DEFAULT_PUSNIH_DURATION;

    private boolean specificPlayer = false;
    private String punishPlayerName = "";

    private boolean hypnoAffectLandscape = false;

    /**
     * Default Constructor.
     */
    public PunishBrush() {
        this.setName("Punish");
    }

    private void applyPunishment(final LivingEntity entity, final SnipeData v) {
        switch (this.punishment) {
        case FIRE:
            entity.setFireTicks(PunishBrush.TICKS_PER_SECOND * this.punishDuration);
            break;
        case LIGHTNING:
            entity.getWorld().strikeLightning(entity.getLocation());
            break;
        case BLINDNESS:
            entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, PunishBrush.TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
            break;
        case DRUNK:
            entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, PunishBrush.TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
            break;
        case INVERT:
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, PunishBrush.TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
            break;
        case JUMP:
            entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, PunishBrush.TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
            break;
        case KILL:
            entity.setHealth(0);
            break;
        case RANDOMTP:
            final Random _rand = new Random();
            final Location _targetLocation = entity.getLocation();
            _targetLocation.setX(_targetLocation.getX() + (_rand.nextInt(MAXIMAL_RANDOM_TELEPORTATION_RANGE) - (MAXIMAL_RANDOM_TELEPORTATION_RANGE / 2)));
            _targetLocation.setZ(_targetLocation.getZ()
                    + (_rand.nextInt(PunishBrush.MAXIMAL_RANDOM_TELEPORTATION_RANGE) - PunishBrush.MAXIMAL_RANDOM_TELEPORTATION_RANGE / 2));
            entity.teleport(_targetLocation);
            break;
        case ALL_POTION:
            entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, PunishBrush.TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
            entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, PunishBrush.TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, PunishBrush.TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
            entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, PunishBrush.TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
            break;
        case FORCE:
            final Vector _playerVector = this.getTargetBlock().getLocation().toVector();
            final Vector _direction = entity.getLocation().toVector().clone();
            _direction.subtract(_playerVector);
            final double _length = _direction.length();
            final double _stregth = (1 - (_length / v.getBrushSize())) * this.punishLevel;
            _direction.normalize();
            _direction.multiply(_stregth);
            entity.setVelocity(_direction);
            break;
        case HYPNO:
            if (entity instanceof Player) {
                final Location _loc = entity.getLocation();
                Location _target = _loc.clone();
                for (int _z = this.punishLevel; _z >= -this.punishLevel; _z--) {
                    for (int _x = this.punishLevel; _x >= -this.punishLevel; _x--) {
                        for (int _y = this.punishLevel; _y >= -this.punishLevel; _y--) {
                            _target.setX(_loc.getX() + _x);
                            _target.setY(_loc.getY() + _y);
                            _target.setZ(_loc.getZ() + _z);
                            if (this.hypnoAffectLandscape && _target.getBlock().getType() == Material.AIR) {
                                continue;
                            }
                            _target = _loc.clone();
                            _target.add(_x, _y, _z);
                            ((Player) entity).sendBlockChange(_target, v.getVoxelId(), v.getData());
                        }
                    }
                }
            }
            break;
        default:
            Bukkit.getLogger().warning("Could not determine the punishment of punish brush!");
            break;
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.punishDuration = v.getVoxelHeight();
        this.punishLevel = v.getcCen();

        if (this.specificPlayer) {
            final Player _punPlay = Bukkit.getPlayer(this.punishPlayerName);
            if (_punPlay == null) {
                v.sendMessage("No player " + this.punishPlayerName + " found.");
                return;
            }

            this.applyPunishment(_punPlay, v);
            return;
        }

        final int _brushSizeSquare = v.getBrushSize() * v.getBrushSize();
        final Location _targetLocation = new Location(v.getWorld(), this.getTargetBlock().getX(), this.getTargetBlock().getY(), this.getTargetBlock().getZ());

        final List<LivingEntity> _entities = v.getWorld().getLivingEntities();
        int _numPunishApps = 0;
        for (final LivingEntity _entity : _entities) {
            if (v.owner().getPlayer() != _entity) {
                if (v.getBrushSize() >= 0) {
                    try {
                        if (_entity.getLocation().distanceSquared(_targetLocation) <= _brushSizeSquare) {
                            _numPunishApps++;
                            this.applyPunishment(_entity, v);
                        }
                    } catch (final Exception _e) {
                    	_e.printStackTrace();
                    	v.sendMessage("An error occured.");
                    	return;
                    }
                } else if (v.getBrushSize() == PunishBrush.INFINIPUNISH_SIZE) {
                    _numPunishApps++;
                    this.applyPunishment(_entity, v);
                }
            }
        }
        v.sendMessage(ChatColor.DARK_RED + "Punishment applied to " + _numPunishApps + " living entities.");
    }

    @Override
    protected final void powder(final SnipeData v) {
        final int _brushSizeSquare = v.getBrushSize() * v.getBrushSize();
        final Location _targetLocation = new Location(v.getWorld(), this.getTargetBlock().getX(), this.getTargetBlock().getY(), this.getTargetBlock().getZ());

        final List<LivingEntity> _entities = v.getWorld().getLivingEntities();

        for (final LivingEntity _e : _entities) {
            if (_e.getLocation().distanceSquared(_targetLocation) < _brushSizeSquare) {
                _e.setFireTicks(0);
                _e.removePotionEffect(PotionEffectType.BLINDNESS);
                _e.removePotionEffect(PotionEffectType.CONFUSION);
                _e.removePotionEffect(PotionEffectType.SLOW);
                _e.removePotionEffect(PotionEffectType.JUMP);
            }
        }

    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.custom(ChatColor.GREEN + "Punishment: " + this.punishment.toString());
    	vm.size();
    	vm.center();
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	for (int _i = 1; _i < par.length; _i++) {
    		final String _string = par[_i].toLowerCase();
    		
    		if (_string.equalsIgnoreCase("info")) {
    			v.sendMessage(ChatColor.GOLD + "Punish Brush Options:");
    			v.sendMessage(ChatColor.AQUA + "Punishments can be set via /b p [punishment]");
    			v.sendMessage(ChatColor.AQUA + "Punishment level can be set with /vc [level]");
    			v.sendMessage(ChatColor.AQUA + "Punishment duration in seconds can be set with /vh [duration]");
    			v.sendMessage(ChatColor.AQUA + "Parameter -toggleHypnoLandscape will make Hypno punishment only affect landscape.");
    			v.sendMessage(ChatColor.AQUA + "Parameter -toggleSM [playername] will make punishbrush only affect that player.");
    			v.sendMessage(ChatColor.AQUA + "Available Punishment Options:");
    			final StringBuilder _punishmentOptions = new StringBuilder();
    			for (final Punishment _punishment : Punishment.values()) {
    				if (_punishmentOptions.length() != 0) {
    					_punishmentOptions.append(" | ");
    				}
    				_punishmentOptions.append(_punishment.name());
    			}
				v.sendMessage(ChatColor.GOLD + _punishmentOptions.toString());
				return;
			} else if (_string.equalsIgnoreCase("-toggleSM")) {
    			this.specificPlayer = !this.specificPlayer;
    			if (this.specificPlayer) {
    				try {
    					this.punishPlayerName = par[++_i];
    					continue;
    				} catch (final IndexOutOfBoundsException _e) {
    					v.sendMessage(ChatColor.AQUA + "You have to specify a player name after -toggleSM if you want to turn the specific player feature on.");
    				}
    			}
    		} else if (_string.equalsIgnoreCase("-toggleHypnoLandscape")) {
    			this.hypnoAffectLandscape = !this.hypnoAffectLandscape;
    		} else {
    			try {
    				this.punishment = Punishment.valueOf(_string.toUpperCase());
    				v.sendMessage(ChatColor.AQUA + this.punishment.name().toLowerCase() + " punishment selected.");
    				continue;
    			} catch (final IllegalArgumentException _e) {
    				v.sendMessage(ChatColor.AQUA + "No such Punishment.");
    			}
    		}
    	}
    	
    }
    
    @Override
    public final int getTimesUsed() {
    	return PunishBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	PunishBrush.timesUsed = tUsed;
    }
}
