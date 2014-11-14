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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.voxelplugineering.voxelsniper.api.Gunsmith;
import com.voxelplugineering.voxelsniper.api.ISniper;
import com.voxelplugineering.voxelsniper.api.ISniperManager;
import com.voxelplugineering.voxelsniper.bukkit.BukkitConsoleSniper;
import com.voxelplugineering.voxelsniper.bukkit.BukkitSniper;
import com.voxelplugineering.voxelsniper.common.CommonVector;
import com.voxelplugineering.voxelsniper.common.event.SnipeEvent;
import com.voxelplugineering.voxelsniper.common.event.SniperCreateEvent;

public class SniperManagerBukkit implements ISniperManager<Player>
{
    
    private Map<Player, BukkitSniper> players = new WeakHashMap<Player, BukkitSniper>();
    private BukkitConsoleSniper console = new BukkitConsoleSniper(Bukkit.getConsoleSender());

    @Override
    public void init()
    {
        
    }

    @Override
    public void stop()
    {
        this.players.clear();
    }

    @Override
    public void restart()
    {
        stop();
        init();
    }

    @Override
    public ISniper getSniper(Player player)
    {
        if (!this.players.containsKey(player))
        {
            this.players.put(player, new BukkitSniper(player));
        }
        return this.players.get(player);
    }

    @Override
    public Class<Player> getPlayerClass()
    {
        return Player.class;
    }

    public ISniper getConsoleSniperProxy()
    {
        return this.console;
    }
    
    @EventHandler
    public boolean onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event)
    {
    	ISniper s = getSniper(event.getPlayer());
    	SniperCreateEvent sce = new SniperCreateEvent(s);
    	Gunsmith.getEventBus().post(sce);
		return true;
    }
    
    @EventHandler
    public boolean onPlayerInteractEvent(org.bukkit.event.player.PlayerInteractEvent event)
    {
    	Player p = event.getPlayer();
    	ISniper s = getSniper(p);
    	CommonVector dir = new CommonVector(p.getLocation().getYaw(), p.getLocation().getPitch(), 0);
    	SnipeEvent se = new SnipeEvent(s, dir);
    	Gunsmith.getEventBus().post(se);
		return true;
    }

}
