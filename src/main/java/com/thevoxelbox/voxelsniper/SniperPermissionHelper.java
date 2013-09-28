package com.thevoxelbox.voxelsniper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.TreeMap;

/**
 * @author MikeMatrix
 */
public class SniperPermissionHelper
{
    private static final String SNIPER_PERMISSION_NODE = "voxelsniper.sniper";
    private static final String LITESNIPER_PERMISSION_NODE = "voxelsniper.litesniper";
    private final TreeMap<String, Sniper> snipers = new TreeMap<String, Sniper>();

    /**
     * @param player
     * @return {@link Sniper} instance or null
     */
    public final Sniper getSniperInstance(final Player player)
    {
        if (this.isSniper(player))
        {
            Sniper instance = this.snipers.get(player.getName());
            if (instance != null && !(instance.getClass() == Sniper.class))
            {
                this.snipers.remove(player.getName());
                instance = null;
            }
            if (instance == null)
            {
                instance = new Sniper();
                instance.setPlayer(player);
                instance.reset();
                instance.loadAllPresets();
                this.snipers.put(player.getName(), instance);
            }
            return instance;
        }
        else if (this.isLiteSniper(player))
        {
            Sniper instance = this.snipers.get(player.getName());
            if (instance != null && !(instance.getClass() == LiteSniper.class))
            {
                this.snipers.remove(player.getName());
                instance = null;
            }
            if (instance == null)
            {
                instance = new LiteSniper();
                instance.setPlayer(player);
                instance.reset();
                instance.loadAllPresets();
                this.snipers.put(player.getName(), instance);
            }
            return instance;
        }
        return null;
    }

    /**
     * @param playerName
     * @return {@link Sniper}
     */
    public final Sniper getSniperInstance(final String playerName)
    {
        return this.getSniperInstance(Bukkit.getPlayer(playerName));
    }

    /**
     * @param player
     * @return boolean
     */
    public final boolean isLiteSniper(final Player player)
    {
        return (player.hasPermission(SniperPermissionHelper.LITESNIPER_PERMISSION_NODE)) && (!this.isSniper(player));
    }

    /**
     * @param playerName
     * @return boolean
     */
    public final boolean isLiteSniper(final String playerName)
    {
        return this.isLiteSniper(Bukkit.getPlayer(playerName));
    }

    /**
     * @param player
     * @return boolean
     */
    public final boolean isSniper(final Player player)
    {
        return player.hasPermission(SniperPermissionHelper.SNIPER_PERMISSION_NODE);
    }

    /**
     * @param playerName
     * @return boolean
     */
    public final boolean isSniper(final String playerName)
    {
        return this.isSniper(Bukkit.getPlayer(playerName));
    }
}
