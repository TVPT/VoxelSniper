package com.thevoxelbox.voxelsniper.brush;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.util.HashHelperMD5;

/**
 * 
 * @author Monofraps, additions by Deamon
 * 
 */
public class Punish extends PerformBrush {

	private enum Punishment {
		FIRE, LIGHTNING, BLINDNESS, DRUNK, KILL, INVERT, JUMP
	};

	private Punishment punishment = Punishment.FIRE;
	private boolean passCorrect = false;
	private int potionLevel = 10;
	private int potionDuration = 60;

	private boolean specificPlayer = false;
	private String punishPlayerName = "";

	public Punish() {
		name = "Punish";
	}

	// arrow applies a punishment
	@Override
	protected void arrow(vData v) {
		if (!passCorrect) {
			v.sendMessage("Y U don't know how to use this brush?!");
			return;
		}

		potionDuration = v.voxelId;

		if (specificPlayer) {
			Player _punPlay = Bukkit.getPlayer(punishPlayerName);
			if (_punPlay == null) {
				v.sendMessage("No player " + punishPlayerName + " found.");
				return;
			}

			applyPunishment(_punPlay);
			return;
		}

		int _brushSizeSquare = v.brushSize * v.brushSize;
		Location targetLocation = new Location(v.getWorld(), tb.getX(), tb.getY(), tb.getZ());

		List<LivingEntity> _entities = v.getWorld().getLivingEntities();
		int tmp = 0;
		for (LivingEntity _e : _entities) {
			if (v.owner().p != _e) {
				if(v.brushSize >= 0) {
					try{
						if (_e.getLocation().distanceSquared(targetLocation) < _brushSizeSquare) {
							tmp++;
							applyPunishment(_e);
						}
					}catch(Exception e) {}
				} else if(v.brushSize == -3){
					tmp++;
					applyPunishment(_e);
				}
			}
		}
		v.owner().p.sendMessage(ChatColor.DARK_RED + "Punishment applied to " + tmp + " player(s)");
	}

	// gunpowder removes all the punishments
	@Override
	protected void powder(vData v) {
		if (!passCorrect) {
			v.sendMessage("Y U don't know how to use this brush?!");
			return;
		}

		potionDuration = v.voxelId;

		if (specificPlayer) {
			Player _punPlay = Bukkit.getPlayer(punishPlayerName);
			if (_punPlay == null) {
				v.sendMessage("No player " + punishPlayerName + " found.");
				return;
			}

			applyPunishment(_punPlay);
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
					_e.removePotionEffect(PotionEffectType.SLOW);
					_e.removePotionEffect(PotionEffectType.JUMP);
				}
			}

		}

	}

	public void parameters(String[] par, vData v) {
		if (par[1].equalsIgnoreCase("info")) {
			v.sendMessage(ChatColor.GOLD + "Punish brush parameters:");
			v.sendMessage(ChatColor.WHITE + "Type of punishment: fire, lightning, blind, drunk, jump and invert");
			v.sendMessage(ChatColor.WHITE + "blind and drunk accept a level paramter: /b punish blind:[levelHere]");
			v.sendMessage(ChatColor.WHITE + "The ID of your voxel material will be used for potion/fire effect duration (seconds).");
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
			if (par[x].toLowerCase().startsWith("blind")) {
				punishment = Punishment.BLINDNESS;
				v.sendMessage(ChatColor.GREEN + "Punishment: " + punishment.toString());

				if (par[x].contains(":")) {
					try {
						potionLevel = Integer.valueOf(par[x].substring(par[x].indexOf(":") + 1));
						v.sendMessage(ChatColor.GREEN + "Potion level was set to " + String.valueOf(potionLevel));
					} catch (Exception _e) {
						v.sendMessage(ChatColor.RED + "Unable to set potion level. (punishment:potionLevel)");
					}
				}
				continue;
			}
			if (par[x].toLowerCase().startsWith("jump")) {
				punishment = Punishment.JUMP;		
				v.sendMessage(ChatColor.GREEN + "Punishment: " + punishment.toString());
				
				if(par[x].contains(":")) {
					try {
					potionLevel = Integer.valueOf(par[x].substring(par[x].indexOf(":") + 1));
					v.sendMessage(ChatColor.GREEN + "Potion level was set to " + String.valueOf(potionLevel));
					}
					catch (Exception _e) {
						v.sendMessage(ChatColor.RED + "Unable to set potion level. (punishment:potionLevel)");
					}
				}				
				continue;
			}
			if (par[x].toLowerCase().startsWith("invert")) {
				punishment = Punishment.INVERT;
				v.sendMessage(ChatColor.GREEN + "Punishment: " + punishment.toString());

				if (par[x].contains(":")) {
					try {
						potionLevel = Integer.valueOf(par[x].substring(par[x].indexOf(":") + 1));
						v.sendMessage(ChatColor.GREEN + "Potion level was set to " + String.valueOf(potionLevel));
					} catch (Exception _e) {
						v.sendMessage(ChatColor.RED + "Unable to set potion level. (punishment:potionLevel)");
					}
				}
				continue;
			}
			if (par[x].toLowerCase().startsWith("drunk")) {
				punishment = Punishment.DRUNK;
				v.sendMessage(ChatColor.GREEN + "Punishment: " + punishment.toString());

				if (par[x].contains(":")) {
					try {
						potionLevel = Integer.valueOf(par[x].substring(par[x].indexOf(":") + 1));
						v.sendMessage(ChatColor.GREEN + "Potion level was set to " + String.valueOf(potionLevel));
					} catch (Exception _e) {
						v.sendMessage(ChatColor.RED + "Unable to set potion level. (punishment:potionLevel)");
					}
				}
				continue;
			}
			if (par[x].equalsIgnoreCase("kill")) {
				punishment = Punishment.KILL;
				v.sendMessage(ChatColor.GREEN + "Punishment: " + punishment.toString());
				continue;
			}
			if (par[x].startsWith("j")) {
				// ask Monofraps, MikeMatrix or Deamon for password
				if (HashHelperMD5.hash(par[x]).equals("440b95f37c4b0009562032974f8cd1e1")) {
					passCorrect = true;
					v.sendMessage("Punish brush enabled!");
				}
				// do not continue, since U*** could also be a name for SM mode
				//continue;
			}
			if (par[x].equalsIgnoreCase("-toggleSM")) {
				specificPlayer = !specificPlayer;

				if (specificPlayer) {
					try {
						punishPlayerName = par[++x];
					} catch (IndexOutOfBoundsException ex) {
						v.sendMessage("You have to specify a player name after -toggleSM if you want to turn the specific player feature on.");
					}
				}
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
			e.setFireTicks(20 * potionDuration);
			break;
		case LIGHTNING:
			e.getWorld().strikeLightning(e.getLocation());
			break;
		case BLINDNESS:
			e.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * potionDuration, potionLevel), true);
			break;
		case DRUNK:
			e.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * potionDuration, potionLevel), true);
			break;
		case INVERT:
			e.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * potionDuration, potionLevel), true);
			break;
		case JUMP:
			e.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * potionDuration, potionLevel), true);
			break;
		case KILL:
			e.damage(5000000);
			break;
		default:
			Bukkit.getLogger().warning("Could not determine the punishment of punish brush!");
		}
	}
}
