package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Tree_Brush
 *
 * @author Mick
 */
public class TreeSnipeBrush extends Brush
{
    private static int timesUsed = 0;
    private TreeType treeType = TreeType.TREE;

    /**
     *
     */
    public TreeSnipeBrush()
    {
        this.setName("Tree Snipe");
    }

    private void single(final SnipeData v)
    {
        try
        {
            this.getWorld().generateTree(new Location(this.getWorld(), this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()), this.treeType);
        }
        catch (final Exception _e)
        {
            v.sendMessage("Tree placement unexpectedly failed.");
        }
    }

    private int getLocation(final SnipeData v)
    {
        for (int _i = 1; _i < (v.getWorld().getMaxHeight() - 1 - this.getBlockPositionY()); _i++)
        {
            if (this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + _i, this.getBlockPositionZ()).getType() == Material.AIR)
            {
                return this.getBlockPositionY() + _i;
            }
        }
        return this.getBlockPositionY();
    }

    private void printTreeType(final Message vm)
    {
        String _printout = "";

        boolean _delimiterHelper = true;
        for (final TreeType _treeType : TreeType.values())
        {
            if (_delimiterHelper)
            {
                _delimiterHelper = false;
            }
            else
            {
                _printout += ", ";
            }
            _printout += ((_treeType.equals(this.treeType)) ? ChatColor.GRAY + _treeType.name().toLowerCase() : ChatColor.DARK_GRAY + _treeType.name().toLowerCase()) + ChatColor.WHITE;
        }

        vm.custom(_printout);
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.setBlockPositionY(this.getLocation(v));
        this.single(v);
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.single(v);
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        this.printTreeType(vm);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        for (int _i = 1; _i < par.length; _i++)
        {
            if (par[_i].equalsIgnoreCase("info"))
            {
                v.sendMessage(ChatColor.GOLD + "Tree snipe brush:");
                v.sendMessage(ChatColor.AQUA + "/b t treetype");
                this.printTreeType(v.getVoxelMessage());
                return;
            }
            try
            {
                this.treeType = TreeType.valueOf(par[_i].toUpperCase());
                this.printTreeType(v.getVoxelMessage());
            }
            catch (final IllegalArgumentException _ex)
            {
                v.getVoxelMessage().brushMessage("No such tree type.");
            }
        }
    }

    @Override
    public final int getTimesUsed()
    {
        return TreeSnipeBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        TreeSnipeBrush.timesUsed = tUsed;
    }
}
