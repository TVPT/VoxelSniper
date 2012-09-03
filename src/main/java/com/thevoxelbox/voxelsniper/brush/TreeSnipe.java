package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;

/**
 * 
 * @author Mick
 */
public class TreeSnipe extends Brush {

    private TreeType treeType = TreeType.TREE;

    private static int timesUsed = 0;

    public TreeSnipe() {
        this.setName("Tree Snipe");
    }

    @Override
    public final int getTimesUsed() {
        return TreeSnipe.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        this.printTreeType(vm);
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Tree snipe brush:");
            v.sendMessage(ChatColor.AQUA + "/b t treetype");
            this.printTreeType(v.getVoxelMessage());
            return;
        }
        for (int x = 1; x < par.length; x++) {
            try {
                this.treeType = TreeType.valueOf(par[x].toUpperCase());
                this.printTreeType(v.getVoxelMessage());
            } catch (final IllegalArgumentException _ex) {
                v.getVoxelMessage().brushMessage("No such tree type.");
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        TreeSnipe.timesUsed = tUsed;
    }

    public final void single(final SnipeData v) {
        try {
            this.getWorld().generateTree(new Location(this.getWorld(), this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()), this.treeType);
        } catch (final Exception e) {
            v.sendMessage("Tree placement unexpectedly failed.");
        }
    }

    private int getLocation(final SnipeData v) {
        for (int i = 1; i < (255 - this.getBlockPositionY()); i++) {
            if (this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + i, this.getBlockPositionZ()).getType() == Material.AIR) {
                return this.getBlockPositionY() + i;
            }
        }
        return this.getBlockPositionY();
    }

    private void printTreeType(final Message vm) {
        String _printout = "";

        boolean _delimiterHelper = true;
        for (final TreeType _treeType : TreeType.values()) {
            if (_delimiterHelper) {
                _delimiterHelper = false;
            } else {
                _printout += ", ";
            }
            _printout += ((_treeType.equals(this.treeType)) ? ChatColor.GRAY + _treeType.name().toLowerCase() : ChatColor.DARK_GRAY
                    + _treeType.name().toLowerCase())
                    + ChatColor.WHITE;
        }

        vm.custom(_printout);
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.setBlockPositionY(this.getLocation(v));
        this.single(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.single(v);
    }
}
