package com.voxelplugineering.voxelsniper.bukkit;

import org.bukkit.entity.Player;

import com.voxelplugineering.voxelsniper.Sniper;

public class BukkitSniper extends Sniper<Player>
{

    public BukkitSniper(Player player)
    {
        super(player);
    }

    @Override
    public void sendMessage(String msg)
    {
        getPlayer().sendMessage(msg);
    }

}
