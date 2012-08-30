/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author Voxel
 */
public class SetRedstoneFlip extends Brush {

    protected Block b = null;
    protected vUndo h;
    private boolean northSouth = true;

    public SetRedstoneFlip() {
        name = "Set Redstone Flip";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) { // Derp
        if (set(tb)) {
            v.sendMessage(ChatColor.GRAY + "Point one");
        } else {
            v.storeUndo(h);
        }
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        if (set(lb)) {
            v.sendMessage(ChatColor.GRAY + "Point one");
        } else {
            v.storeUndo(h);
        }
    }

    @Override
    public void info(vMessage vm) {
        b = null;
        vm.brushName(name);
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Set Repeater Flip Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b setrf <direction> -- valid direction inputs are(n,s,e,w), Set the direction that you wish to flip your repeaters, defaults to north/south.");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("n") || par[x].startsWith("s") || par[x].startsWith("ns")) {
                northSouth = true;
                v.sendMessage(ChatColor.AQUA + "Flip direction set to north/south");
                continue;
            } else if (par[x].startsWith("e") || par[x].startsWith("w") || par[x].startsWith("ew")) {
                northSouth = false;
                v.sendMessage(ChatColor.AQUA + "Flip direction set to east/west.");
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    private boolean set(Block bl) {
        if (b == null) {
            b = bl;
            return true;
        } else {
            h = new vUndo(b.getWorld().getName());
            int lowx = (b.getX() <= bl.getX()) ? b.getX() : bl.getX();
            int lowy = (b.getY() <= bl.getY()) ? b.getY() : bl.getY();
            int lowz = (b.getZ() <= bl.getZ()) ? b.getZ() : bl.getZ();
            int highx = (b.getX() >= bl.getX()) ? b.getX() : bl.getX();
            int highy = (b.getY() >= bl.getY()) ? b.getY() : bl.getY();
            int highz = (b.getZ() >= bl.getZ()) ? b.getZ() : bl.getZ();
            for (int y = lowy; y <= highy; y++) {
                for (int x = lowx; x <= highx; x++) {
                    for (int z = lowz; z <= highz; z++) {
                        perform(clampY(x, y, z));
                    }
                }
            }
            b = null;
            return false;
        }
    }

    protected void perform(Block bl) {
        if (bl.getType() == Material.DIODE_BLOCK_ON || bl.getType() == Material.DIODE_BLOCK_OFF) {
            if (northSouth) {
                if ((bl.getData() % 4) == 1) {
                    h.put(bl);
                    bl.setData((byte) (bl.getData() + 2));
                } else if ((bl.getData() % 4) == 3) {
                    h.put(bl);
                    bl.setData((byte) (bl.getData() - 2));
                }
            } else {
                if ((bl.getData() % 4) == 2) {
                    h.put(bl);
                    bl.setData((byte) (bl.getData() - 2));
                } else if ((bl.getData() % 4) == 0) {
                    h.put(bl);
                    bl.setData((byte) (bl.getData() + 2));
                }
            }
        }
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