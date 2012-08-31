package com.thevoxelbox.voxelsniper.brush;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Monofraps
 * @author Deamon
 * @author MikeMatrix
 * 
 */
public class Punish extends PerformBrush {

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
        FORCE, BLOCKS
    }

    private static final int MAXIMAL_RANDOM_TELEPORTATION_RANGE = 400;

    private static final int TICKS_PER_SECOND = 20;
    private static final int INFINIPUNISH_SIZE = -3;
    
    private static int timesUsed = 0;

    private Punishment punishment = Punishment.FIRE;
    private int punishLevel = 10;
    private int punishDuration = 60;

    private boolean specificPlayer = false;
    private String punishPlayerName = "";

    /**
     * Default Constructor.
     */
    public Punish() {
        this.name = "Punish";
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        vm.custom(ChatColor.GREEN + "Punishment: " + this.punishment.toString());
        vm.size();
        vm.center();
    }

    @Override
    public final void parameters(final String[] par, final vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Punish Brush Options:");
            v.sendMessage(ChatColor.AQUA + "Punishments can be set via /b p [punishment]");
            v.sendMessage(ChatColor.AQUA + "Punishment level can be set with /vc [level]");
            v.sendMessage(ChatColor.AQUA + "Punishment duration in seconds can be set with /vh [duration]");
            v.sendMessage(ChatColor.AQUA + "Available Punishment Options:");
            final StringBuilder _punishmentOptions = new StringBuilder();
            for (final Punishment _punishment : Punishment.values()) {
                if (_punishmentOptions.length() != 0) {
                    _punishmentOptions.append(" | ");
                }
                _punishmentOptions.append(_punishment.name());
            }
            v.sendMessage(ChatColor.AQUA + _punishmentOptions.toString());
            return;
        }
        for (int _x = 1; _x < par.length; _x++) {
            final String _string = par[_x].toLowerCase();

            if (_string.equalsIgnoreCase("-toggleSM")) {
                this.specificPlayer = !this.specificPlayer;
                if (this.specificPlayer) {
                    try {
                        this.punishPlayerName = par[++_x];
                        continue;
                    } catch (final IndexOutOfBoundsException _e) {
                        v.sendMessage(ChatColor.AQUA + "You have to specify a player name after -toggleSM if you want to turn the specific player feature on.");
                    }
                }
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

    private void applyPunishment(final LivingEntity entity, final vData v) {
        switch (this.punishment) {
        case FIRE:
            entity.setFireTicks(Punish.TICKS_PER_SECOND * this.punishDuration);
            break;
        case LIGHTNING:
            entity.getWorld().strikeLightning(entity.getLocation());
            break;
        case BLINDNESS:
            entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Punish.TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
            break;
        case DRUNK:
            entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Punish.TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
            break;
        case INVERT:
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Punish.TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
            break;
        case JUMP:
            entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Punish.TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
            break;
        case KILL:
            entity.damage(Integer.MAX_VALUE);
            break;
        case RANDOMTP:
            final Random _rand = new Random();
            final Location _targetLocation = entity.getLocation();
            _targetLocation.setX(_targetLocation.getX() + (_rand.nextInt(400) - 200));
            _targetLocation.setZ(_targetLocation.getZ()
                    + (_rand.nextInt(Punish.MAXIMAL_RANDOM_TELEPORTATION_RANGE) - Punish.MAXIMAL_RANDOM_TELEPORTATION_RANGE / 2));
            entity.teleport(_targetLocation);
            break;
        case ALL_POTION:
            entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Punish.TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
            entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Punish.TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Punish.TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
            entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Punish.TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
            break;
        case FORCE:
            final Vector _playerVector = this.tb.getLocation().toVector();
            final Vector _direction = entity.getLocation().toVector().clone();
            _direction.subtract(_playerVector);
            final double _length = _direction.length();
            final double _stregth = (1 - (_length / v.brushSize)) * this.punishLevel;
            _direction.normalize();
            _direction.multiply(_stregth);
            entity.setVelocity(_direction);
            break;
        case BLOCKS:
            if (entity instanceof Player) {
                Location _loc = entity.getLocation();
                Location _target = null;
                for (int z = this.punishLevel; z >= -this.punishLevel; z--) {
                    for (int x = this.punishLevel; x >= -this.punishLevel; x--) {
                        for (int y = this.punishLevel; y >= -this.punishLevel; y--) {
                            _target = _loc.clone();
                            _target.add(x, y, z);
                            ((Player) entity).sendBlockChange(_target, v.voxelId, v.data);
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
    protected final void arrow(final vData v) {
        this.punishDuration = v.voxelHeight;
        this.punishLevel = v.cCen;

        if (this.specificPlayer) {
            final Player _punPlay = Bukkit.getPlayer(this.punishPlayerName);
            if (_punPlay == null) {
                v.sendMessage("No player " + this.punishPlayerName + " found.");
                return;
            }

            this.applyPunishment(_punPlay, v);
            return;
        }

        final int _brushSizeSquare = v.brushSize * v.brushSize;
        final Location _targetLocation = new Location(v.getWorld(), this.tb.getX(), this.tb.getY(), this.tb.getZ());

        final List<LivingEntity> _entities = v.getWorld().getLivingEntities();
        int _numPunishApps = 0;
        for (final LivingEntity _entity : _entities) {
            if (v.owner().getPlayer() != _entity) {
                if (v.brushSize >= 0) {
                    try {
                        if (_entity.getLocation().distanceSquared(_targetLocation) <= _brushSizeSquare) {
                            _numPunishApps++;
                            this.applyPunishment(_entity, v);
                        }
                    } catch (final Exception _e) {
                    }
                } else if (v.brushSize == Punish.INFINIPUNISH_SIZE) {
                    _numPunishApps++;
                    this.applyPunishment(_entity, v);
                }
            }
        }
        v.sendMessage(ChatColor.DARK_RED + "Punishment applied to " + _numPunishApps + " Living Entities.");
    }

    @Override
    protected final void powder(final vData v) {
        if (!v.owner().getPlayer().hasPermission("voxelsniper.punish")) {
            v.sendMessage("Y U don't have permission to use this brush?!");
            return;
        }

        final int _brushSizeSquare = v.brushSize * v.brushSize;
        final Location _targetLocation = new Location(v.getWorld(), this.tb.getX(), this.tb.getY(), this.tb.getZ());

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
	public int getTimesUsed() {
		return timesUsed;
	}

	@Override
	public void setTimesUsed(int tUsed) {
		timesUsed = tUsed; 
	}
}
