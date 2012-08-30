/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * THIS BRUSH SHOULD NOT USE PERFORMERS
 *
 * @author Voxel
 */
public class Jockey extends Brush {

    public Jockey() {
        name = "Jockey";
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();

        sitOn(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        v.owner().getPlayer().eject();
        v.owner().getPlayer().sendMessage(ChatColor.GOLD + "You have been ejected!");
    }

    private void sitOn(vData v) {
        Location l = v.owner().getPlayer().getLocation();
        double px = l.getX();
        double py = l.getY();
        double pz = l.getZ();

        Entity closest = null;
        double range = 99999999;

        Chunk c = w.getChunkAt(tb.getLocation());
        int chunkx = c.getX();
        int chunkz = c.getZ();

        for (int x = chunkx - 1; x <= chunkx + 1; x++) {
            for (int y = chunkz - 1; y <= chunkz + 1; y++) {
                c = w.getChunkAt(x, y);
                Entity[] toCheck = c.getEntities();
                for (Entity e : toCheck) {
                    if (e.getEntityId() == v.owner().getPlayer().getEntityId()) {
                        continue;
                    }
                    Location el = e.getLocation();

                    double erange = Math.pow(bx - el.getX(), 2) + Math.pow(by - el.getY(), 2) + Math.pow(bz - el.getZ(), 2);

                    if (erange < range) {
                        range = erange;
                        closest = e;
                    }
                }
            }
        }

        if (closest != null) {
            boolean teleport = false;
            PlayerTeleportEvent teleEvent = null;

            Player player = v.owner().getPlayer();
            teleport = player.isOnline();

            if (teleport) {
                teleEvent = new PlayerTeleportEvent(player, player.getLocation(), closest.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                Bukkit.getPluginManager().callEvent(teleEvent);
                teleport = !teleEvent.isCancelled();
            }

            if (teleport) {
                ((CraftEntity) v.owner().getPlayer()).getHandle().setPassengerOf(((CraftEntity) closest).getHandle());
                v.sendMessage(ChatColor.GREEN + "You are now saddles on entity: " + closest.getEntityId());
            }
        } else {
            v.sendMessage(ChatColor.RED + "Could not find any entities");
        }
    }
    
    private static int timesUsed = 0;
	
    @Override
	public int getTimesUsed() {
		return timesUsed;
	}

	@Override
	public void setTimesUsed(int tUsed) {
		timesUsed = tUsed; 
	}
}
