package com.thevoxelbox.voxelsniper;

import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 *
 */
public class SniperManager
{
    private Map<Player, Sniper> sniperInstances = new MapMaker().weakKeys().makeMap();
    private VoxelSniper plugin;

    public SniperManager(VoxelSniper plugin)
    {
        this.plugin = plugin;
    }

    public Sniper getSniperForPlayer(Player player)
    {
        if (sniperInstances.get(player) == null)
        {
            sniperInstances.put(player, new Sniper(plugin, player));
        }
        return sniperInstances.get(player);
    }
}
