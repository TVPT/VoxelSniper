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
 * THIS BRUSH SHOULD NOT USE PERFORMERS
 * 
 * @author Voxel
 */
public class Jockey extends Brush {

    private static int timesUsed = 0;

    public Jockey() {
        this.setName("Jockey");
    }

    @Override
    public final int getTimesUsed() {
        return Jockey.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Jockey.timesUsed = tUsed;
    }

    private void sitOn(final SnipeData v) {
        final Location l = v.owner().getPlayer().getLocation();
        l.getX();
        l.getY();
        l.getZ();

        Entity closest = null;
        double range = 99999999;

        Chunk c = this.getWorld().getChunkAt(this.getTargetBlock().getLocation());
        final int chunkx = c.getX();
        final int chunkz = c.getZ();

        for (int x = chunkx - 1; x <= chunkx + 1; x++) {
            for (int y = chunkz - 1; y <= chunkz + 1; y++) {
                c = this.getWorld().getChunkAt(x, y);
                final Entity[] toCheck = c.getEntities();
                for (final Entity e : toCheck) {
                    if (e.getEntityId() == v.owner().getPlayer().getEntityId()) {
                        continue;
                    }
                    final Location el = e.getLocation();

                    final double erange = Math.pow(this.getBlockPositionX() - el.getX(), 2) + Math.pow(this.getBlockPositionY() - el.getY(), 2) + Math.pow(this.getBlockPositionZ() - el.getZ(), 2);

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
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        this.sitOn(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        v.owner().getPlayer().eject();
        v.owner().getPlayer().sendMessage(ChatColor.GOLD + "You have been ejected!");
    }
}
