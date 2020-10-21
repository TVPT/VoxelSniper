package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;
import org.bukkit.entity.Player;

public class VoxelInkReplaceCommand extends VoxelCommand
{
    public VoxelInkReplaceCommand(final VoxelSniper plugin)
    {
        super("VoxelInkReplace", plugin);
        setIdentifier("vir");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public boolean onCommand(Player player, String[] args)
    {
        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);

        String ink;

        if (args.length == 0)
        {
            ink = "";
        }
        else
        {
            String arg = args[0];
            // We do some basic validation, but hard to do much here. Maybe we could do more in the future.
            if (arg.startsWith("[") && arg.endsWith("]")) {
                ink = arg;
            }
            else {
                player.sendMessage("Input is not a valid trait list.");
                return true;
            }
        }

        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
        snipeData.setReplaceInk(ink);
        snipeData.getVoxelMessage().replaceInk();
        return true;
    }
}
