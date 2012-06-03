package com.thevoxelbox.voxelsniper.brush;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Monofraps
 * 
 */
public class Punish extends PerformBrush {

	private enum Punishment {
		FIRE, LIGHTNING, BLINDNESS, DRUNK
	};

	private Punishment punishment = Punishment.FIRE;

	public Punish() {
		name = "Punish";
	}

	// arrow applies a punishment
	@Override
	protected void arrow(vData v) {
		int _brushSizeSquare = v.brushSize * v.brushSize;
		Location targetLocation = new Location(v.getWorld(), tb.getX(), tb.getY(), tb.getZ());

		List<Player> _players = v.getWorld().getPlayers();

		for (Player _p : _players) {
			if (v.owner().p != _p) {
				if (_p.getLocation().distanceSquared(targetLocation) < _brushSizeSquare) {
					applyPunishment(_p);
				}
			}
		}
	}

	// gunpowder removes all the punishments
	@Override
	protected void powder(vData v) {
		int _brushSizeSquare = v.brushSize * v.brushSize;
		Location targetLocation = new Location(v.getWorld(), tb.getX(), tb.getY(), tb.getZ());

		List<Player> _players = v.getWorld().getPlayers();

		for (Player _p : _players) {
			if (v.owner().p != _p) {
				if (_p.getLocation().distanceSquared(targetLocation) < _brushSizeSquare) {
					_p.setFireTicks(0);
					_p.removePotionEffect(PotionEffectType.BLINDNESS);
					_p.removePotionEffect(PotionEffectType.CONFUSION);
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
			}
			if (par[x].equalsIgnoreCase("lightning")) {
				punishment = Punishment.LIGHTNING;
				v.sendMessage(ChatColor.GREEN + "Punishment: " + punishment.toString());
			}
			if (par[x].equalsIgnoreCase("blind")) {
				punishment = Punishment.BLINDNESS;
				v.sendMessage(ChatColor.GREEN + "Punishment: " + punishment.toString());
			}
			if (par[x].equalsIgnoreCase("drunk")) {
				punishment = Punishment.DRUNK;
				v.sendMessage(ChatColor.GREEN + "Punishment: " + punishment.toString());
			}
		}
	}

	@Override
	public void info(vMessage vm) {
		vm.brushName(name);
		vm.custom(ChatColor.GREEN + "Punishment: " + punishment.toString());
		vm.size();
	}

	private void applyPunishment(Player p) {
		switch (punishment) {
		case FIRE:
			p.setFireTicks(20 * 60);
			break;
		case LIGHTNING:
			p.getWorld().strikeLightning(p.getLocation());
			break;
		case BLINDNESS:
			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 60, 10), true);
			break;
		case DRUNK:
			p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 60, 10), true);
			break;
		default:
			Bukkit.getLogger().warning("Could not determine the punishment of punish brush!");
		}
	}
}
