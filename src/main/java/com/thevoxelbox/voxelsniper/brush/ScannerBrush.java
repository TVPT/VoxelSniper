package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;

/**
 * 
 * @author DivineRage
 */
public class ScannerBrush extends Brush {
	private static final int DEPTH_MIN = 1;
	private static final int DEPTH_DEFAULT = 24;
	private static final int DEPTH_MAX = 64;

	private static int timesUsed = 0;

	private int depth = DEPTH_DEFAULT;
	private Material checkFor = Material.AIR;

	/**
	 * 
	 */
    public ScannerBrush() {
        this.setName("Scanner");
    }

    private int clamp(final int value, final int min, final int max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        } else {
            return value;
        }
    }

    private void scan(final SnipeData v, final BlockFace bf) {
        if (bf == null) {
            return;
        }
        
        switch (bf) {
        case NORTH:
            // Scan south
            for (int _i = 1; _i < this.depth + 1; _i++) {
                if (this.clampY(this.getBlockPositionX() + _i, this.getBlockPositionY(), this.getBlockPositionZ()).getType() == this.checkFor) {
                    v.sendMessage(ChatColor.GREEN + "" + this.checkFor + " found after " + _i + " blocks.");
                    return;
                }
            }
            v.sendMessage(ChatColor.GRAY + "Nope.");
            break;

        case SOUTH:
            // Scan north
            for (int _i = 1; _i < this.depth + 1; _i++) {
                if (this.clampY(this.getBlockPositionX() - _i, this.getBlockPositionY(), this.getBlockPositionZ()).getType() == this.checkFor) {
                    v.sendMessage(ChatColor.GREEN + "" + this.checkFor + " found after " + _i + " blocks.");
                    return;
                }
            }
            v.sendMessage(ChatColor.GRAY + "Nope.");
            break;

        case EAST:
            // Scan west
            for (int _i = 1; _i < this.depth + 1; _i++) {
                if (this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ() + _i).getType() == this.checkFor) {
                    v.sendMessage(ChatColor.GREEN + "" + this.checkFor + " found after " + _i + " blocks.");
                    return;
                }
            }
            v.sendMessage(ChatColor.GRAY + "Nope.");
            break;

        case WEST:
            // Scan east
            for (int _i = 1; _i < this.depth + 1; _i++) {
                if (this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ() - _i).getType() == this.checkFor) {
                    v.sendMessage(ChatColor.GREEN + "" + this.checkFor + " found after " + _i + " blocks.");
                    return;
                }
            }
            v.sendMessage(ChatColor.GRAY + "Nope.");
            break;

        case UP:
            // Scan down
            for (int _i = 1; _i < this.depth + 1; _i++) {
                if ((this.getBlockPositionY() - _i) <= 0) {
                    break;
                }
                if (this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - _i, this.getBlockPositionZ()).getType() == this.checkFor) {
                    v.sendMessage(ChatColor.GREEN + "" + this.checkFor + " found after " + _i + " blocks.");
                    return;
                }
            }
            v.sendMessage(ChatColor.GRAY + "Nope.");
            break;

        case DOWN:
            // Scan up
            for (int _i = 1; _i < this.depth + 1; _i++) {
                if ((this.getBlockPositionY() + _i) >= v.getWorld().getMaxHeight()) {
                    break;
                }
                if (this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + _i, this.getBlockPositionZ()).getType() == this.checkFor) {
                    v.sendMessage(ChatColor.GREEN + "" + this.checkFor + " found after " + _i + " blocks.");
                    return;
                }
            }
            v.sendMessage(ChatColor.GRAY + "Nope.");
            break;

        default:
            break;
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.checkFor = Material.getMaterial(v.getVoxelId());
        this.scan(v, this.getTargetBlock().getFace(this.getLastBlock()));
    }

    @Override
    protected final void powder(final SnipeData v) {
    	this.checkFor = Material.getMaterial(v.getVoxelId());
        this.scan(v, this.getTargetBlock().getFace(this.getLastBlock()));
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.custom(ChatColor.GREEN + "Scanner depth set to " + this.depth);
    	vm.custom(ChatColor.GREEN + "Scanner scans for " + this.checkFor + " (change with /v #)");
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	for (int _i = 1; _i < par.length; _i++) {
    		if (par[_i].equalsIgnoreCase("info")) {
    			v.sendMessage(ChatColor.GOLD + "Scanner brush Parameters:");
    			v.sendMessage(ChatColor.AQUA + "/b sc d# -- will set the search depth to #. Clamps to 1 - 64.");
    			return;
    		}
    		if (par[_i].startsWith("d")) {
    			this.depth = this.clamp(Integer.parseInt(par[_i].substring(1)), DEPTH_MIN, DEPTH_MAX);
    			v.sendMessage(ChatColor.AQUA + "Scanner depth set to " + this.depth);
    			continue;
    		} else {
    			v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
    		}
    	}
    	
    }
    
    @Override
    public final int getTimesUsed() {
    	return ScannerBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	ScannerBrush.timesUsed = tUsed;
    }
}
