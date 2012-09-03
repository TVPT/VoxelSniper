package com.thevoxelbox.voxelsniper;

import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author MikeMatrix
 */
public class SniperPermissionHelper {
    private static final String SNIPER_PERMISSION_NODE = "voxelsniper.sniper";
    private static final String LITESNIPER_PERMISSION_NODE = "voxelsniper.litesniper";
    private final TreeMap<String, Sniper> snipers = new TreeMap<String, Sniper>();

    /**
     * @param player
     * @return {@link Sniper}
     */
    public final Sniper getSniperInstance(final Player player) {
        if (this.isSniper(player)) {
            Sniper _instance = this.snipers.get(player.getName());
            if (_instance != null && !(_instance.getClass() == Sniper.class)) {
                this.snipers.remove(player.getName());
                _instance = null;
            }
            if (_instance == null) {
                _instance = new Sniper();
                _instance.setPlayer(player);
                _instance.reset();
                _instance.loadAllPresets();
                this.snipers.put(player.getName(), _instance);
            }
            return _instance;
        } else if (this.isLiteSniper(player)) {
            Sniper _instance = this.snipers.get(player.getName());
            if (_instance != null && !(_instance.getClass() == LiteSniper.class)) {
                this.snipers.remove(player.getName());
                _instance = null;
            }
            if (_instance == null) {
                _instance = new LiteSniper();
                _instance.setPlayer(player);
                _instance.reset();
                _instance.loadAllPresets();
                this.snipers.put(player.getName(), _instance);
            }
            return _instance;
        }
        return null;
    }

    /**
     * @param playerName
     * @return {@link Sniper}
     */
    public final Sniper getSniperInstance(final String playerName) {
        return this.getSniperInstance(Bukkit.getPlayer(playerName));
    }

    /**
     * @param player
     * @return boolean
     */
    public final boolean isLiteSniper(final Player player) {
        return (player.hasPermission(SniperPermissionHelper.LITESNIPER_PERMISSION_NODE)) && (!this.isSniper(player));
    }

    /**
     * @param playerName
     * @return boolean
     */
    public final boolean isLiteSniper(final String playerName) {
        return this.isLiteSniper(Bukkit.getPlayer(playerName));
    }

    /**
     * @param player
     * @return boolean
     */
    public final boolean isSniper(final Player player) {
        return player.hasPermission(SniperPermissionHelper.SNIPER_PERMISSION_NODE);
    }

    /**
     * @param playerName
     * @return boolean
     */
    public final boolean isSniper(final String playerName) {
        return this.isSniper(Bukkit.getPlayer(playerName));
    }
}
