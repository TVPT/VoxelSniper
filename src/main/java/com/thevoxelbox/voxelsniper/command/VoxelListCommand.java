package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.RangeBlockHelper;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class VoxelListCommand extends VoxelCommand
{
    public VoxelListCommand(final VoxelSniper plugin)
    {
        super("VoxelList", plugin);
        setIdentifier("vl");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public boolean onCommand(Player player, String[] args)
    {
        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);

        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
        if (args.length == 0)
        {
            final RangeBlockHelper rangeBlockHelper = new RangeBlockHelper(player, player.getWorld());
            final Block targetBlock = rangeBlockHelper.getTargetBlock();
            snipeData.getVoxelList().add(new int[]{ targetBlock.getTypeId(), targetBlock.getData() });
            snipeData.getVoxelMessage().voxelList();
            return true;
        }
        else
        {
            if (args[0].equalsIgnoreCase("clear"))
            {
                snipeData.getVoxelList().clear();
                snipeData.getVoxelMessage().voxelList();
                return true;
            }
        }

        boolean remove = false;

        for (final String string : args)
        {
            String tmpint;
            Integer xint;
            Integer xdat;

            if (string.startsWith("-"))
            {
                remove = true;
                tmpint = string.replaceAll("-", "");
            }
            else
            {
                tmpint = string;
            }

            try
            {
                if (tmpint.contains(":"))
                {
                    String[] tempintsplit = tmpint.split(":");
                    xint = Integer.parseInt(tempintsplit[0]);
                    xdat = Integer.parseInt(tempintsplit[1]);
                }
                else
                {
                    xint = Integer.parseInt(tmpint);
                    xdat = -1;
                }

                if (Material.getMaterial(xint) != null && Material.getMaterial(xint).isBlock())
                {
                    if (!remove)
                    {
                        snipeData.getVoxelList().add(new int[]{ xint, xdat });
                        snipeData.getVoxelMessage().voxelList();
                    }
                    else
                    {
                        snipeData.getVoxelList().removeValue(new int[]{ xint, xdat });
                        snipeData.getVoxelMessage().voxelList();
                    }
                }

            }
            catch (NumberFormatException ignored)
            {
            }
        }
        return true;
    }
}
