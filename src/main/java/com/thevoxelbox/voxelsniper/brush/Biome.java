package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

/**
 * 
 * @author Piotr <przerwap@gmail.com>
 */
public class Biome extends Brush {
	private static int timesUsed = 0;
    private org.bukkit.block.Biome selectedBiome = org.bukkit.block.Biome.PLAINS;

    /**
     * 
     */
    public Biome() {
        this.setName("Biome");
    }   
    
    private final void biome(final SnipeData v) {
        final int _bSize = v.getBrushSize();
        final double _bPow = Math.pow(_bSize, 2);
        double _xPow = 0;
        
        for (int _x = -_bSize; _x <= _bSize; _x++) {
            _xPow = Math.pow(_x, 2);
            for (int _z = -_bSize; _z <= _bSize; _z++) {
                if ((_xPow + Math.pow(_z, 2)) <= _bPow) {
                    this.getWorld().setBiome(this.getBlockPositionX() + _x, this.getBlockPositionZ() + _z, this.selectedBiome);
                }
            }
        }
        

        final Block _b1 = this.getWorld().getBlockAt(this.getBlockPositionX() - _bSize, 0, this.getBlockPositionZ() - _bSize);
        final Block _b2 = this.getWorld().getBlockAt(this.getBlockPositionX() + _bSize, 0, this.getBlockPositionZ() + _bSize);

        final int _lowX = (_b1.getX() <= _b2.getX()) ? _b1.getChunk().getX() : _b2.getChunk().getX();
        final int _lowZ = (_b1.getZ() <= _b2.getZ()) ? _b1.getChunk().getZ() : _b2.getChunk().getZ();
        final int _highX = (_b1.getX() >= _b2.getX()) ? _b1.getChunk().getX() : _b2.getChunk().getX();
        final int _highZ = (_b1.getZ() >= _b2.getZ()) ? _b1.getChunk().getZ() : _b2.getChunk().getZ();
        
        for (int x = _lowX; x <= _highX; x++) {
            for (int z = _lowZ; z <= _highZ; z++) {
                this.getWorld().refreshChunk(x, z);
            }
        }
    }
    
    @Override
    protected final void arrow(final SnipeData v) {
        this.biome(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.biome(v);
    }    
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.size();
    	vm.custom(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN + this.selectedBiome.name());
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	if (par[1].equalsIgnoreCase("info")) {
    		v.sendMessage(ChatColor.GOLD + "Biome Brush Parameters:");
    		String _availableBiomes = "";

    		for (final org.bukkit.block.Biome _biome : org.bukkit.block.Biome.values()) {
    			if(_availableBiomes.isEmpty()) {
    				_availableBiomes = ChatColor.DARK_GREEN + _biome.name();
    				continue;
    			}

    			_availableBiomes += ChatColor.RED + ", " + ChatColor.DARK_GREEN + _biome.name();
    			
    		}
    		v.sendMessage(ChatColor.DARK_BLUE + "Available biomes: " + _availableBiomes);
    	} else {
    		for (final org.bukkit.block.Biome bio : org.bukkit.block.Biome.values()) {
    			if (bio.name().equals(par[1])) {
    				this.selectedBiome = bio;
    				break;
    			}
    		}
    		v.sendMessage(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN + this.selectedBiome.name());
    	}
    }
    
    @Override
    public final int getTimesUsed() {
        return Biome.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
        Biome.timesUsed = tUsed;
    }
}
