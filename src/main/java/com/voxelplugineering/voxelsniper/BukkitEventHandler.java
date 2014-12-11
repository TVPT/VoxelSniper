package com.voxelplugineering.voxelsniper;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.voxelplugineering.voxelsniper.api.ISniper;
import com.voxelplugineering.voxelsniper.common.event.SnipeEvent;
import com.voxelplugineering.voxelsniper.common.event.SniperCreateEvent;

/**
 * An event handler for bukkit's events to post the events to Gunsmith from.
 */
public class BukkitEventHandler implements Listener
{

    /**
     * Creates a new {@link BukkitEventHandler}.
     * 
     * @param eventBus Gunsmith's {@link EventBus}
     * @param sniperRegistry the sniper registry to get players from
     * @param tool the tool
     */
    public BukkitEventHandler()
    {

    }

    /**
     * An event handler for player join events.
     * 
     * @param event the {@link PlayerJoinEvent}
     */
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event)
    {
        Optional<?> s = Gunsmith.getVoxelSniper().getPlayerRegistry().get(event.getPlayer().getName());
        if (s.isPresent())
        {
            SniperCreateEvent sce = new SniperCreateEvent((ISniper) s.get());
            Gunsmith.getEventBus().post(sce);
        }
    }

    /**
     * An event handler for player interact events.
     * 
     * @param event the {@link PlayerInteractEvent}
     */
    @EventHandler
    public void onPlayerInteractEvent(org.bukkit.event.player.PlayerInteractEvent event)
    {
        Player p = event.getPlayer();
        Gunsmith.getLogger().debug("PlayerInteractEvent for " + p.getName());
        if (p.getItemInHand().getType() == (Material) Gunsmith.getConfiguration().get("ARROW_MATERIAL").get()
                && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR))
        {
            Optional<?> s = Gunsmith.getVoxelSniper().getPlayerRegistry().get(event.getPlayer().getName());
            if (s.isPresent())
            {
                SnipeEvent se = new SnipeEvent((ISniper) s.get(), p.getLocation().getYaw(), p.getLocation().getPitch());
                Gunsmith.getEventBus().post(se);
            }
        }
    }
}
