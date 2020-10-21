package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.RangeBlockHelper;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class VoxelListCommand extends VoxelCommand
{
    public VoxelListCommand(final VoxelSniper plugin)
    {
        super("VoxelList", plugin);
        setIdentifier("vl");
        setPermission("voxelsniper.sniper");
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(Player player, String[] args)
    {
        Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);

        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
        if (args.length == 0)
        {
            final RangeBlockHelper rangeBlockHelper = new RangeBlockHelper(player, player.getWorld());
            final Block targetBlock = rangeBlockHelper.getTargetBlock();
            snipeData.getVoxelList().addBlock(targetBlock.getBlockData());
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
            String tmpint = string;
            boolean isTag = false;

            if (string.startsWith("-"))
            {
                remove = true;
                tmpint = string.replaceAll("-", "");
            }
            else if (string.startsWith("#"))
            {
                isTag = true;
                tmpint = string.replaceAll("#", "");
            }

            try
            {
                if (isTag)
                {
                    String[] parts = tmpint.split(":", 2);

                    NamespacedKey key;
                    if (parts.length == 1)
                    {
                        key = NamespacedKey.minecraft(parts[0]);
                    }
                    else
                    {
                        key = new NamespacedKey(parts[0], parts[1]);
                    }

                    Tag<Material> tag = Bukkit.getTag(Tag.REGISTRY_BLOCKS, key, Material.class);

                    if (tag != null)
                    {
                        if (remove)
                        {
                            snipeData.getVoxelList().addTag(tag);
                        }
                        else
                        {
                            snipeData.getVoxelList().removeTag(tag);
                        }
                    }
                }
                else
                {
                    BlockData blockData = Bukkit.createBlockData(tmpint);

                    if(remove)
                    {
                        snipeData.getVoxelList().removeBlock(blockData);
                    }
                    else
                    {
                        snipeData.getVoxelList().addBlock(blockData);
                    }
                }
            }
            catch (IllegalArgumentException ignored)
            {
            }
        }
        snipeData.getVoxelMessage().voxelList();

        return true;
    }
}
