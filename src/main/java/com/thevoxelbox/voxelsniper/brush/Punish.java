package com.thevoxelbox.voxelsniper.brush;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.util.HashHelperMD5;

/**
 * 
 * @author Monofraps
 * 
 */
public class Punish extends PerformBrush {

	private enum Punishment {
		FIRE, LIGHTNING, BLINDNESS, DRUNK, KILL
	};

	private Punishment punishment = Punishment.FIRE;
	private boolean passCorrect = false;

	public Punish() {
		name = "Punish";
	}

	// arrow applies a punishment
	@Override
	protected void arrow(vData v) {
		if(!passCorrect) {
			v.sendMessage("Y U don't know how to use this brush?!");
			return;
		}
		
		int _brushSizeSquare = v.brushSize * v.brushSize;
		Location targetLocation = new Location(v.getWorld(), tb.getX(), tb.getY(), tb.getZ());

		List<LivingEntity> _entities = v.getWorld().getLivingEntities();

		for (LivingEntity _e : _entities) {
			if (v.owner().p != _e) {
				if (_e.getLocation().distanceSquared(targetLocation) < _brushSizeSquare) {
					applyPunishment(_e);
				}
			}
		}
	}

	// gunpowder removes all the punishments
	@Override
	protected void powder(vData v) {
		if(!passCorrect) {
			v.sendMessage("Y U don't know how to use this brush?!");
			return;
		}
		
		int _brushSizeSquare = v.brushSize * v.brushSize;
		Location targetLocation = new Location(v.getWorld(), tb.getX(), tb.getY(), tb.getZ());

		List<LivingEntity> _entities = v.getWorld().getLivingEntities();

		for (LivingEntity _e : _entities) {
			if (v.owner().p != _e) {
				if (_e.getLocation().distanceSquared(targetLocation) < _brushSizeSquare) {
					_e.setFireTicks(0);
					_e.removePotionEffect(PotionEffectType.BLINDNESS);
					_e.removePotionEffect(PotionEffectType.CONFUSION);
				}
			}

		}

	}

	public void parameters(String[] par, vData v) {
		if (par[1].equalsIgnoreCase("info")) {
			v.sendMessage(ChatColor.GOLD + "Punish brush parameters:");
			v.sendMessage(ChatColor.WHITE + "Type of punishment: fire, lightning, blind, drunk");
			return;
		}
		for (int x = 1; x < par.length; x++) {
			if (par[x].equalsIgnoreCase("fire")) {
				punishment = Punishment.FIRE;
				v.sendMessage(ChatColor.GREEN + "Punishment: " + punishment.toString());
				continue;
			}
			if (par[x].equalsIgnoreCase("lightning")) {
				punishment = Punishment.LIGHTNING;
				v.sendMessage(ChatColor.GREEN + "Punishment: " + punishment.toString());
				continue;
			}
			if (par[x].equalsIgnoreCase("blind")) {
				punishment = Punishment.BLINDNESS;
				v.sendMessage(ChatColor.GREEN + "Punishment: " + punishment.toString());
				continue;
			}
			if (par[x].equalsIgnoreCase("drunk")) {
				punishment = Punishment.DRUNK;
				v.sendMessage(ChatColor.GREEN + "Punishment: " + punishment.toString());
				continue;
			}
			if(par[x].equalsIgnoreCase("kill")) {
				punishment = Punishment.KILL;
				v.sendMessage(ChatColor.GREEN + "Punishment: " + punishment.toString());
				continue;
			}
			if (par[x].startsWith("U")) {
				// ask Monofraps or MikeMatrix for password
				if (HashHelperMD5.hash(par[x]).equals("56cd6fa410cd1f05db4f25abaeacb0bf")) {
					passCorrect = true;
					v.sendMessage("Punish brush enabled!");
				}
				continue;
			}
		}
	}

	@Override
	public void info(vMessage vm) {
		vm.brushName(name);
		vm.custom(ChatColor.GREEN + "Punishment: " + punishment.toString());
		vm.size();
	}

	private void applyPunishment(LivingEntity e) {		
		switch (punishment) {
		case FIRE:
			e.setFireTicks(20 * 60);
			break;
		case LIGHTNING:
			e.getWorld().strikeLightning(e.getLocation());
			break;
		case BLINDNESS:
			e.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 60, 10), true);
			break;
		case DRUNK:
			e.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 60, 10), true);
			break;
		case KILL:
			e.damage(5000000);
			break;
		default:
			Bukkit.getLogger().warning("Could not determine the punishment of punish brush!");
		}
	}
}
