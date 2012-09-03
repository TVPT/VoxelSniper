package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;

/**
 * THIS BRUSH SHOULD NOT USE PERFORMERS
 * 
 * @author Voxel
 */
public class Jockey extends Brush {

    private static int timesUsed = 0;

    public Jockey() {
        this.name = "Jockey";
    }

    @Override
    public final int getTimesUsed() {
        return Jockey.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Jockey.timesUsed = tUsed;
    }

    private void sitOn(final vData v) {
        final Location l = v.owner().getPlayer().getLocation();
        l.getX();
        l.getY();
        l.getZ();

        Entity closest = null;
        double range = 99999999;

        Chunk c = this.w.getChunkAt(this.tb.getLocation());
        final int chunkx = c.getX();
        final int chunkz = c.getZ();

        for (int x = chunkx - 1; x <= chunkx + 1; x++) {
            for (int y = chunkz - 1; y <= chunkz + 1; y++) {
                c = this.w.getChunkAt(x, y);
                final Entity[] toCheck = c.getEntities();
                for (final Entity e : toCheck) {
                    if (e.getEntityId() == v.owner().getPlayer().getEntityId()) {
                        continue;
                    }
                    final Location el = e.getLocation();

                    final double erange = Math.pow(this.bx - el.getX(), 2) + Math.pow(this.by - el.getY(), 2) + Math.pow(this.bz - el.getZ(), 2);

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

            final Player player = v.owner().getPlayer();
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

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();

        this.sitOn(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        v.owner().getPlayer().eject();
        v.owner().getPlayer().sendMessage(ChatColor.GOLD + "You have been ejected!");
    }
}
