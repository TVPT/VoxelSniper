package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VoxelDefaultCommand extends VoxelCommand
{
    public VoxelDefaultCommand()
    {
        super("VoxelDefault");
        setIdentifier("d");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public boolean onCommand(Player player, String[] args)
    {
        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);

        sniper.reset(sniper.getCurrentToolId());
        player.sendMessage(ChatColor.AQUA + "Brush settings reset to their default values.");
        return true;
    }
}
