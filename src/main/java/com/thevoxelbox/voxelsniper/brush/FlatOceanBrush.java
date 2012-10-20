package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

/**
 * 
 * @author GavJenks
 */
public class FlatOceanBrush extends Brush {
	private static int timesUsed = 0;	
    private int waterLevel = 29;
    private int floorLevel = 8;

    /**
     * 
     */
    public FlatOceanBrush() {
        this.setName("FlatOcean");
    }

    private void flatOcean(final Chunk chunk) {
        for (int _x = 0; _x < CHUNK_SIZE; _x++) {
            for (int _z = 0; _z < CHUNK_SIZE; _z++) {
                for (int _y = 0; _y < chunk.getWorld().getMaxHeight(); _y++) {
                    if (_y <= this.floorLevel) {
                        chunk.getBlock(_x, _y, _z).setType(Material.DIRT);
                    } else if (_y <= this.waterLevel) {
                        chunk.getBlock(_x, _y, _z).setTypeId(Material.STATIONARY_WATER.getId(), false);
                    } else {
                        chunk.getBlock(_x, _y, _z).setTypeId(Material.AIR.getId(), false);
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
        this.flatOcean(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() + CHUNK_SIZE, 1, this.getBlockPositionZ())));
        this.flatOcean(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() + CHUNK_SIZE, 1, this.getBlockPositionZ() + CHUNK_SIZE)));
        this.flatOcean(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX(), 1, this.getBlockPositionZ() + CHUNK_SIZE)));
        this.flatOcean(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() - CHUNK_SIZE, 1, this.getBlockPositionZ() + CHUNK_SIZE)));
        this.flatOcean(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() - CHUNK_SIZE, 1, this.getBlockPositionZ())));
        this.flatOcean(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() - CHUNK_SIZE, 1, this.getBlockPositionZ() - CHUNK_SIZE)));
        this.flatOcean(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX(), 1, this.getBlockPositionZ() - CHUNK_SIZE)));
        this.flatOcean(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() + CHUNK_SIZE, 1, this.getBlockPositionZ() - CHUNK_SIZE)));
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
    	for (int _i = 1; _i < par.length; _i++) {
    		final String _param = par[_i];
    		
    		if (_param.equalsIgnoreCase("info")) {
    			v.sendMessage(ChatColor.GREEN + "yo[number] to set the Level to which the water will rise.");
    			v.sendMessage(ChatColor.GREEN + "yl[number] to set the Level to which the ocean floor will rise.");
    		}    		
    		if (_param.startsWith("yo")) {
    			int _newWaterLevel = Integer.parseInt(_param.replace("yo", ""));
    			if (_newWaterLevel < this.floorLevel) {
    				_newWaterLevel = this.floorLevel + 1;
    			}
    			this.waterLevel = _newWaterLevel;
    			v.sendMessage(ChatColor.GREEN + "Water Level set to " + this.waterLevel);
    			continue;
    		} else if (_param.startsWith("yl")) {
    			int _newFloorLevel = Integer.parseInt(_param.replace("yl", ""));
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
    	return FlatOceanBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	FlatOceanBrush.timesUsed = tUsed;
    }
}
