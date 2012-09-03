package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

/**
 * 
 * @author GavJenks
 */
public class FlatOcean extends Brush {
    private int waterLevel = 29;
    private int floorLevel = 8;

    private static int timesUsed = 0;

    public FlatOcean() {
        this.setName("FlatOcean");
    }

    private void flatOcean(final Chunk chunk) {
        for (int _x = 0; _x < 16; _x++) {
            for (int _z = 0; _z < 16; _z++) {
                for (int _y = 0; _y < 256; _y++) {
                    if (_y <= this.floorLevel) {
                        chunk.getBlock(_x, _y, _z).setTypeId(3, true);
                    } else if (_y <= this.waterLevel) {
                        chunk.getBlock(_x, _y, _z).setTypeId(9, false);
                    } else {
                        chunk.getBlock(_x, _y, _z).setTypeId(0, false);
                    }
                }
            }
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.flatOcean(this.getWorld().getChunkAt(this.getTargetBlock()));
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.flatOcean(this.getWorld().getChunkAt(this.getTargetBlock()));
        this.flatOcean(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() + 16, 63, this.getBlockPositionZ())));
        this.flatOcean(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() + 16, 63, this.getBlockPositionZ() + 16)));
        this.flatOcean(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX(), 63, this.getBlockPositionZ() + 16)));
        this.flatOcean(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() - 16, 63, this.getBlockPositionZ() + 16)));
        this.flatOcean(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() - 16, 63, this.getBlockPositionZ())));
        this.flatOcean(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() - 16, 63, this.getBlockPositionZ() - 16)));
        this.flatOcean(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX(), 63, this.getBlockPositionZ() - 16)));
        this.flatOcean(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() + 16, 63, this.getBlockPositionZ() - 16)));
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.custom(ChatColor.RED + "THIS BRUSH DOES NOT UNDO");
    	vm.custom(ChatColor.GREEN + "Water level set to " + this.waterLevel);
    	vm.custom(ChatColor.GREEN + "Ocean floor level set to " + this.floorLevel);
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	
    	if (par[1].equalsIgnoreCase("info")) {
    		v.sendMessage(ChatColor.GREEN + "yo[number] to set the Level to which the water will rise.");
    		v.sendMessage(ChatColor.GREEN + "yl[number] to set the Level to which the ocean floor will rise.");
    	}
    	for (int x = 1; x < par.length; x++) {
    		
    		if (par[x].startsWith("yo")) {
    			int _newWaterLevel = Integer.parseInt(par[x].replace("yo", ""));
    			if (_newWaterLevel < this.floorLevel) {
    				_newWaterLevel = this.floorLevel + 1;
    			}
    			this.waterLevel = _newWaterLevel;
    			v.sendMessage(ChatColor.GREEN + "Water Level set to " + this.waterLevel);
    			continue;
    		} else if (par[x].startsWith("yl")) {
    			int _newFloorLevel = Integer.parseInt(par[x].replace("yl", ""));
    			if (_newFloorLevel > this.waterLevel) {
    				_newFloorLevel = this.waterLevel - 1;
    				if (_newFloorLevel == 0) {
    					_newFloorLevel = 1;
    					this.waterLevel = 2;
    				}
    			}
    			this.floorLevel = _newFloorLevel;
    			v.sendMessage(ChatColor.GREEN + "Ocean floor Level set to " + this.floorLevel);
    			continue;
    		}
    	}
    }
    
    @Override
    public final int getTimesUsed() {
    	return FlatOcean.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	FlatOcean.timesUsed = tUsed;
    }
}
