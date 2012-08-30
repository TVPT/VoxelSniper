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
public class SetRedstoneRotate extends Brush { // Is this used anymore? -psa No worldEdit rotates properly, although it still doesn't flip -Deamon

    protected Block b = null;
    protected vUndo h;

    public SetRedstoneRotate() {
        name = "Set Redstone Rotate";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) { // Derp
        if (set(tb)) {
            v.owner().getPlayer().sendMessage(ChatColor.GRAY + "Point one");
        } else {
            v.storeUndo(h);
        }
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        if (set(lb)) {
            v.owner().getPlayer().sendMessage(ChatColor.GRAY + "Point one");
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
        super.parameters(par, v);
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
            h.put(bl);
            //System.out.println(bl.getData());
            bl.setData((((bl.getData() % 4) + 1 < 5) ? (byte) (bl.getData() + 1) : (byte) (bl.getData() - 4)));
            //System.out.println(bl.getData());
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
