package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Voxel
 */
public class SetRedstoneFlipBrush extends Brush {
	private static int timesUsed = 0;
    private Block block = null;
    private Undo undo;
    private boolean northSouth = true;

    public SetRedstoneFlipBrush() {
        this.setName("Set Redstone Flip");
    }

    private boolean set(final Block bl) {
        if (this.block == null) {
            this.block = bl;
            return true;
        } else {
            this.undo = new Undo(this.block.getWorld().getName());
            final int _lowx = (this.block.getX() <= bl.getX()) ? this.block.getX() : bl.getX();
            final int _lowy = (this.block.getY() <= bl.getY()) ? this.block.getY() : bl.getY();
            final int _lowz = (this.block.getZ() <= bl.getZ()) ? this.block.getZ() : bl.getZ();
            final int _highx = (this.block.getX() >= bl.getX()) ? this.block.getX() : bl.getX();
            final int _highy = (this.block.getY() >= bl.getY()) ? this.block.getY() : bl.getY();
            final int _highz = (this.block.getZ() >= bl.getZ()) ? this.block.getZ() : bl.getZ();
            
            for (int _y = _lowy; _y <= _highy; _y++) {
                for (int _x = _lowx; _x <= _highx; _x++) {
                    for (int _z = _lowz; _z <= _highz; _z++) {
                        this.perform(this.clampY(_x, _y, _z));
                    }
                }
            }
            this.block = null;
            return false;
        }
    }
    
    private final void perform(final Block bl) {
    	if (bl.getType() == Material.DIODE_BLOCK_ON || bl.getType() == Material.DIODE_BLOCK_OFF) {
    		if (this.northSouth) {
    			if ((bl.getData() % 4) == 1) {
    				this.undo.put(bl);
    				bl.setData((byte) (bl.getData() + 2));
    			} else if ((bl.getData() % 4) == 3) {
    				this.undo.put(bl);
    				bl.setData((byte) (bl.getData() - 2));
    			}
    		} else {
    			if ((bl.getData() % 4) == 2) {
    				this.undo.put(bl);
    				bl.setData((byte) (bl.getData() - 2));
    			} else if ((bl.getData() % 4) == 0) {
    				this.undo.put(bl);
    				bl.setData((byte) (bl.getData() + 2));
    			}
    		}
    	}
    }

    @Override
    protected final void arrow(final SnipeData v) {
        if (this.set(this.getTargetBlock())) {
            v.sendMessage(ChatColor.GRAY + "Point one");
        } else {
            v.storeUndo(this.undo);
        }
    }

    @Override
    protected final void powder(final SnipeData v) {
        if (this.set(this.getLastBlock())) {
            v.sendMessage(ChatColor.GRAY + "Point one");
        } else {
            v.storeUndo(this.undo);
        }
    }
    
    @Override
    public final void info(final Message vm) {
    	this.block = null;
    	vm.brushName(this.getName());
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	for (int _i = 1; _i < par.length; _i++) {
    		if (par[_i].equalsIgnoreCase("info")) {
    			v.sendMessage(ChatColor.GOLD + "Set Repeater Flip Parameters:");
    			v.sendMessage(ChatColor.AQUA
    					+ "/b setrf <direction> -- valid direction inputs are(n,s,e,world), Set the direction that you wish to flip your repeaters, defaults to north/south.");
    			return;
    		}
    		if (par[_i].startsWith("n") || par[_i].startsWith("s") || par[_i].startsWith("ns")) {
    			this.northSouth = true;
    			v.sendMessage(ChatColor.AQUA + "Flip direction set to north/south");
    			continue;
    		} else if (par[_i].startsWith("e") || par[_i].startsWith("world") || par[_i].startsWith("ew")) {
    			this.northSouth = false;
    			v.sendMessage(ChatColor.AQUA + "Flip direction set to east/west.");
    			continue;
    		} else {
    			v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
    		}
    	}
    }
    
    @Override
    public final int getTimesUsed() {
    	return SetRedstoneFlipBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	SetRedstoneFlipBrush.timesUsed = tUsed;
    }
}
