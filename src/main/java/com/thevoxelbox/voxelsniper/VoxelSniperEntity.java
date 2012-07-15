package com.thevoxelbox.voxelsniper;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * 
 * @author Piotr
 */
public class VoxelSniperEntity implements Listener {

    private static boolean devServer = false;

    /**
     * @param event
     */
    @EventHandler
    public final void onEntityDamage(final EntityDamageEvent event) {
        if (VoxelSniperEntity.devServer) {
            event.setDamage(0);
            event.setCancelled(true);
            return;
        }

        if (event.getCause().equals(DamageCause.ENTITY_ATTACK)) {
            if (event instanceof EntityDamageByEntityEvent) {
                final EntityDamageByEntityEvent _entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
                if (_entityDamageByEntityEvent.getEntity() instanceof Wolf && _entityDamageByEntityEvent.getDamager() instanceof Player) {
                    if (((Wolf) _entityDamageByEntityEvent.getEntity()).getHealth() - _entityDamageByEntityEvent.getDamage() <= 0) {
                        _entityDamageByEntityEvent.getDamager().getWorld().strikeLightning(_entityDamageByEntityEvent.getDamager().getLocation());
                        ((Player) _entityDamageByEntityEvent.getDamager()).chat(ChatColor.RED + "Look at me! I killed a VoxelFOX!");
                    } else {
                        _entityDamageByEntityEvent.getDamager().getWorld().strikeLightning(_entityDamageByEntityEvent.getDamager().getLocation());
                        final Wolf _wolf = (Wolf) event.getEntity();
                        _wolf.setAngry(false);
                        _wolf.setSitting(true);
                    }
                }
            }
        }
        if (event.getEntity() instanceof Wolf
                && (event.getCause().equals(DamageCause.LIGHTNING) || event.getCause().equals(DamageCause.FIRE) || event.getCause().equals(
                        DamageCause.FIRE_TICK))) {
            event.setCancelled(true);
            final Wolf _wolf = (Wolf) event.getEntity();
            _wolf.setAngry(false);
            _wolf.setSitting(true);
            _wolf.setFireTicks(0);
            _wolf.setLastDamage(0);
        }
    }
}
