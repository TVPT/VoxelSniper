package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;

/**
 * 
 * @author Mick
 */
public class TreeSnipe extends Brush {

    private TreeType treeType = TreeType.TREE;

    private static int timesUsed = 0;

    public TreeSnipe() {
        this.name = "Tree Snipe";
    }

    @Override
    public final int getTimesUsed() {
        return TreeSnipe.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        this.printTreeType(vm);
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Tree snipe brush:");
            v.sendMessage(ChatColor.AQUA + "/b t treetype");
            this.printTreeType(v.vm);
            return;
        }
        for (int x = 1; x < par.length; x++) {
            try {
                this.treeType = TreeType.valueOf(par[x].toUpperCase());
                this.printTreeType(v.vm);
            } catch (final IllegalArgumentException _ex) {
                v.vm.brushMessage("No such tree type.");
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        TreeSnipe.timesUsed = tUsed;
    }

    public final void single(final vData v) {
        try {
            this.w.generateTree(new Location(this.w, this.bx, this.by, this.bz), this.treeType);
        } catch (final Exception e) {
            v.sendMessage("Tree placement unexpectedly failed.");
        }
    }

    private int getLocation(final vData v) {
        for (int i = 1; i < (255 - this.by); i++) {
            if (this.clampY(this.bx, this.by + i, this.bz).getType() == Material.AIR) {
                return this.by + i;
            }
        }
        return this.by;
    }

    private void printTreeType(final vMessage vm) {
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
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.by = this.getLocation(v);
        this.single(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.single(v);
    }
}
