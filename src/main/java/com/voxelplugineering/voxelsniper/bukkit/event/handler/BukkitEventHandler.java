/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 The Voxel Plugineering Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.voxelplugineering.voxelsniper.bukkit.event.handler;

import com.voxelplugineering.voxelsniper.GunsmithLogger;
import com.voxelplugineering.voxelsniper.brush.BrushAction;
import com.voxelplugineering.voxelsniper.bukkit.config.BukkitConfiguration;
import com.voxelplugineering.voxelsniper.entity.Player;
import com.voxelplugineering.voxelsniper.event.SnipeEvent;
import com.voxelplugineering.voxelsniper.event.SniperEvent;
import com.voxelplugineering.voxelsniper.event.SniperEvent.SniperDestroyEvent;
import com.voxelplugineering.voxelsniper.service.eventbus.EventBus;
import com.voxelplugineering.voxelsniper.service.registry.PlayerRegistry;
import com.voxelplugineering.voxelsniper.util.Context;
import org.bukkit.Material;

import java.util.Optional;

/**
 * An event handler for bukkit's events to post the events to Gunsmith from.
 */
public class BukkitEventHandler implements org.bukkit.event.Listener
{

    private final PlayerRegistry<org.bukkit.entity.Player> pr;
    private final EventBus bus;

    /**
     * Creates a new {@link BukkitEventHandler}.
     */
    @SuppressWarnings({ "unchecked" })
    public BukkitEventHandler(Context context)
    {
        this.pr = context.getRequired(PlayerRegistry.class);
        this.bus = context.getRequired(EventBus.class);
    }

    /**
     * An event handler for player join events.
     * 
     * @param event The {@link org.bukkit.event.player.PlayerJoinEvent}
     */
    @org.bukkit.event.EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event)
    {
        if (!event.getPlayer().hasPermission("voxelsniper.sniper"))
        {
            return;
        }
        Optional<Player> s = this.pr.getPlayer(event.getPlayer().getName());
        if (s.isPresent())
        {
            SniperEvent.SniperCreateEvent sce = new SniperEvent.SniperCreateEvent(s.get());
            this.bus.post(sce);
        }
    }

    /**
     * An event handler for quit events, proxies to Gunsmith's {@link SniperDestroyEvent}.
     * 
     * @param event The event
     */
    @org.bukkit.event.EventHandler
    public void onPlayerLeave(org.bukkit.event.player.PlayerQuitEvent event)
    {
        if (!event.getPlayer().hasPermission("voxelsniper.sniper"))
        {
            return;
        }
        Optional<Player> s = this.pr.getPlayer(event.getPlayer());
        if (s.isPresent())
        {
            SniperEvent.SniperDestroyEvent sde = new SniperEvent.SniperDestroyEvent(s.get());
            this.bus.post(sde);
        }
    }

    /**
     * An event handler for player interact events.
     * 
     * @param event The {@link org.bukkit.event.player.PlayerInteractEvent}
     */
    @org.bukkit.event.EventHandler
    public void onPlayerInteractEvent(org.bukkit.event.player.PlayerInteractEvent event)
    {
        if (!event.getPlayer().hasPermission("voxelsniper.sniper"))
        {
            return;
        }
        org.bukkit.entity.Player p = event.getPlayer();
        if (event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_AIR
                || event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK)
        {
            BrushAction action = null;
            if (p.getItemInHand().getType().equals(Material.valueOf(BukkitConfiguration.primaryMaterial)))
            {
                action = BrushAction.PRIMARY;
            } else if (p.getItemInHand().getType().equals(Material.valueOf(BukkitConfiguration.altMaterial)))
            {
                action = BrushAction.ALTERNATE;
            } else
            {
                return;
            }

            Optional<Player> s = this.pr.getPlayer(event.getPlayer());
            if (s.isPresent())
            {
                SnipeEvent se = new SnipeEvent(s.get(), p.getLocation().getYaw(), p.getLocation().getPitch(), action);
                this.bus.post(se);
            } else
            {
                GunsmithLogger.getLogger().warn("error getting player");
            }
        }
    }
}
