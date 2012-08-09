package com.thevoxelbox.voxelsniper;

import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VoxelSniperPermissionHelper {
    private static final String SNIPER_PERMISSION_NODE = "voxelsniper.sniper";
    private static final String LITESNIPER_PERMISSION_NODE = "voxelsniper.litesniper";
    private final TreeMap<String, vSniper> snipers = new TreeMap<String, vSniper>();

    public vSniper getSniperInstance(final Player player) {
        if (this.isSniper(player)) {
            vSniper instance = this.snipers.get(player.getName());
            if (instance != null && !(instance.getClass() == vSniper.class)) {
                this.snipers.remove(player.getName());
                instance = null;
            }
            if (instance == null) {
                instance = new vSniper();
                instance.setPlayer(player);
                instance.reset();
                instance.loadAllPresets();
                this.snipers.put(player.getName(), instance);
            }
            return instance;
        } else if (this.isLiteSniper(player)) {
            vSniper instance = this.snipers.get(player.getName());
            if (instance != null && !(instance.getClass() == liteSniper.class)) {
                this.snipers.remove(player.getName());
                instance = null;
            }
            if (instance == null) {
                instance = new liteSniper();
                instance.setPlayer(player);
                instance.reset();
                instance.loadAllPresets();
                this.snipers.put(player.getName(), instance);
            }
            return instance;
        }
        return null;
    }

    public vSniper getSniperInstance(final String playerName) {
        return this.getSniperInstance(Bukkit.getPlayer(playerName));
    }

    public boolean isLiteSniper(final Player player) {
        return (player.hasPermission(VoxelSniperPermissionHelper.LITESNIPER_PERMISSION_NODE)) && (!this.isSniper(player));
    }

    public boolean isLiteSniper(final String playerName) {
        return this.isLiteSniper(Bukkit.getPlayer(playerName));
    }

    public boolean isSniper(final Player player) {
        return player.hasPermission(VoxelSniperPermissionHelper.SNIPER_PERMISSION_NODE);
    }

    public boolean isSniper(final String playerName) {
        return this.isSniper(Bukkit.getPlayer(playerName));
    }
}
