package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.RangeBlockHelper;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class VoxelReplaceCommand extends VoxelCommand
{
    public VoxelReplaceCommand()
    {
        super("VoxelReplace");
        setIdentifier("vr");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public boolean onCommand(Player player, String[] args)
    {
        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);
        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());

        if (args.length == 0)
        {
            Block targetBlock = new RangeBlockHelper(player, player.getWorld()).getTargetBlock();
            if (targetBlock != null)
            {
                snipeData.setReplaceId(targetBlock.getTypeId());
                snipeData.getVoxelMessage().replace();
            }
            return true;
        }

        Material material = Material.matchMaterial(args[0]);
        if (material != null)
        {
            if (material.isBlock())
            {
                snipeData.setReplaceId(material.getId());
                snipeData.getVoxelMessage().replace();
                return true;
            }
            else
            {
                player.sendMessage(ChatColor.RED + "You have entered an invalid Item ID.");
                return true;
            }
        }
        return false;
    }
}
