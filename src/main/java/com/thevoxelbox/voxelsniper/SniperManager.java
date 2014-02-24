package com.thevoxelbox.voxelsniper;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 *
 */
public class SniperManager
{
    private Map<Player, Sniper> sniperInstances = Maps.newHashMap();
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
