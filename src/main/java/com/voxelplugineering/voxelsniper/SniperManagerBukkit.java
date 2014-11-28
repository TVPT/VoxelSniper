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
package com.voxelplugineering.voxelsniper;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

import com.voxelplugineering.voxelsniper.api.Gunsmith;
import com.voxelplugineering.voxelsniper.api.ISniper;
import com.voxelplugineering.voxelsniper.api.ISniperFactory;
import com.voxelplugineering.voxelsniper.bukkit.BukkitConsoleSniper;
import com.voxelplugineering.voxelsniper.bukkit.BukkitSniper;
import com.voxelplugineering.voxelsniper.common.event.SnipeEvent;
import com.voxelplugineering.voxelsniper.common.event.SniperCreateEvent;

/**
 * The sniper manager for the bukkit specific implementation.
 */
public class SniperManagerBukkit implements ISniperFactory<Player>, Listener
{

    /**
     * A weak Map of bukkit's {@link Player}s to their Gunsmith equivalents.
     */
    private final Map<Player, BukkitSniper> players = new WeakHashMap<Player, BukkitSniper>();
    /**
     * A special {@link ISniper} to represent the console in operations.
     */
    private BukkitConsoleSniper console = new BukkitConsoleSniper(Bukkit.getConsoleSender());
    /**
     * A task to tick all player change queues 5x per second.
     */
    private BukkitTask worldTick;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init()
    {
        this.worldTick = VoxelSniperBukkit.voxelsniper.getServer().getScheduler().runTaskTimer(VoxelSniperBukkit.voxelsniper, new Runnable()
        {

            @Override
            public void run()
            {
                int n = 0;
                for(Player p: players.keySet())
                {
                    BukkitSniper sniper = players.get(p);
                    if(sniper.hasPendingChanges())
                    {
                        n++;
                    }
                }
                if(n == 0)
                {
                    return;
                }
                int remaining = (Integer) Gunsmith.getConfiguration().get("BLOCK_CHANGES_PER_TICK");
                for(Player p: players.keySet())
                {
                    BukkitSniper sniper = players.get(p);
                    if(!sniper.hasPendingChanges())
                    {
                        continue;
                    }
                    int allocation = remaining/(n--);
                    int actual = 0;
                    while(sniper.hasPendingChanges() && actual < allocation)
                    {
                        actual += sniper.getNextPendingChange().perform(allocation);
                        if(sniper.getNextPendingChange().isFinished())
                        {
                            sniper.clearNextPending();
                        }
                    }
                    remaining -= actual;
                    if(remaining <= 0)
                    {
                        break;
                    }
                }
            }

        }, 0, 5);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop()
    {
        this.players.clear();
        this.worldTick.cancel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void restart()
    {
        stop();
        init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISniper getSniper(Player player)
    {
        if (!this.players.containsKey(player))
        {
            this.players.put(player, new BukkitSniper(player));
        }
        return this.players.get(player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Player> getPlayerClass()
    {
        return Player.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISniper getConsoleSniperProxy()
    {
        return this.console;
    }

    /**
     * An event handler for player join events.
     * 
     * @param event the {@link PlayerJoinEvent}
     */
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event)
    {
        ISniper s = getSniper(event.getPlayer());
        SniperCreateEvent sce = new SniperCreateEvent(s);
        Gunsmith.getEventBus().post(sce);
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
        if (p.getItemInHand().getType() == ((Material) Gunsmith.getConfiguration().get("ARROW_MATERIAL"))
                && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR))
        {
            ISniper s = getSniper(p);
            SnipeEvent se = new SnipeEvent(s, p.getLocation().getYaw(), p.getLocation().getPitch());
            Gunsmith.getEventBus().post(se);
        }
    }

}
