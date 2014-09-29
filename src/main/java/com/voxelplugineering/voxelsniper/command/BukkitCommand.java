package com.voxelplugineering.voxelsniper.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.voxelplugineering.voxelsniper.VoxelSniperBukkit;
import com.voxelplugineering.voxelsniper.common.command.Command;

public class BukkitCommand extends org.bukkit.command.Command
{
    
    Command cmd;

    protected BukkitCommand(String name, Command cmd)
    {
        super(name);
        this.cmd = cmd;
    }

    @Override
    public boolean execute(CommandSender sender, String command, String[] args)
    {
        return cmd.execute(VoxelSniperBukkit.voxelsniper.getSniperHandler().getSniper((Player) sender), args);
    }

}
