package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;

/**
 * 
 * @author Mick
 */
public class TreeSnipe extends Brush {

    private TreeType treeType = TreeType.TREE;

    public TreeSnipe() {
        name = "Tree Snipe";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        by = getLocation(v);
        single(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        single(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        printTreeType(vm);
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Tree snipe brush:");
            v.sendMessage(ChatColor.AQUA + "/b t treetype");
            printTreeType(v.vm);
            return;
        }
        for (int x = 1; x < par.length; x++) {
            try {
                this.treeType = TreeType.valueOf(par[x].toUpperCase());
                printTreeType(v.vm);
            } catch (IllegalArgumentException _ex) {
                v.vm.brushMessage("No such tree type.");
            }
        }
    }

    public void single(vData v) {
        try {
            w.generateTree(new Location(w, (double) bx, (double) by, (double) bz), treeType);
        } catch (Exception e) {
            v.sendMessage("Tree placement unexpectedly failed.");
        }
    }

    private int getLocation(vData v) {
        for (int i = 1; i < (255 - by); i++) {
            if (clampY(bx, by + i, bz).getType() == Material.AIR) {
                return by + i;
            }
        }
        return by;
    }

    private void printTreeType(vMessage vm) {
        String _printout = "";

        boolean _delimiterHelper = true;
        for (TreeType _treeType : TreeType.values()) {
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
    
    private static int timesUsed = 0;
	
    @Override
	public int getTimesUsed() {
		return timesUsed;
	}

	@Override
	public void setTimesUsed(int tUsed) {
		timesUsed = tUsed; 
	}
}
