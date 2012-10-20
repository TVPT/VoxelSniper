package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_CANYONATOR
 * @author Voxel
 *
 */
public class CanyonBrush extends Brush {
	private static final int SHIFT_LEVEL_MIN = 10;
	private static final int SHIFT_LEVEL_MAX = 60;
	
	private static int timesUsed = 0;	
    protected int yLevel = 10;

    /**
     * 
     */
    public CanyonBrush() {
        this.setName("Canyon");
    }

    /**
     * 
     * @param chunk
     * @param undo
     */
    protected final void canyon(final Chunk chunk, final Undo undo) {
        for (int _x = 0; _x < CHUNK_SIZE; _x++) {
            for (int _z = 0; _z < CHUNK_SIZE; _z++) {
                int _yy = this.yLevel;
                
                for (int _y = 63; _y < this.getWorld().getMaxHeight(); _y++) {
                    final Block _b = chunk.getBlock(_x, _y, _z);
                    final Block _bb = chunk.getBlock(_x, _yy, _z);
                    
                    undo.put(_b);
                    undo.put(_bb);
                    
                    _bb.setTypeId(_b.getTypeId(), false);
                    _b.setType(Material.AIR);
                    
                    _yy++;
                }
                
                final Block _b = chunk.getBlock(_x, 0, _z);
                undo.put(_b);                
                _b.setTypeId(Material.BEDROCK.getId());
                
                for (int _y = 1; _y < 10; _y++) {
                    final Block _bb = chunk.getBlock(_x, _y, _z);
                    undo.put(_bb);
                    _bb.setType(Material.STONE);
                }
            }
        }
    }
    
    @Override
    protected void arrow(final SnipeData v) {    	
    	final Undo _undo = new Undo(this.getWorld().getName());
    	this.canyon(this.getWorld().getChunkAt(this.getTargetBlock()), _undo);
    	v.storeUndo(_undo);
    }

    @Override
    protected void powder(final SnipeData v) {
    	final Undo _undo = new Undo(this.getWorld().getName());

        this.canyon(this.getWorld().getChunkAt(this.getTargetBlock()), _undo);
        this.canyon(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() + CHUNK_SIZE, 63, this.getBlockPositionZ())), _undo);
        this.canyon(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() + CHUNK_SIZE, 63, this.getBlockPositionZ() + CHUNK_SIZE)), _undo);
        this.canyon(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX(), 63, this.getBlockPositionZ() + CHUNK_SIZE)), _undo);
        this.canyon(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() - CHUNK_SIZE, 63, this.getBlockPositionZ() + CHUNK_SIZE)), _undo);
        this.canyon(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() - CHUNK_SIZE, 63, this.getBlockPositionZ())), _undo);
        this.canyon(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() - CHUNK_SIZE, 63, this.getBlockPositionZ() - CHUNK_SIZE)), _undo);
        this.canyon(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX(), 63, this.getBlockPositionZ() - CHUNK_SIZE)), _undo);
        this.canyon(this.getWorld().getChunkAt(this.clampY(this.getBlockPositionX() + CHUNK_SIZE, 63, this.getBlockPositionZ() - CHUNK_SIZE)), _undo);

        v.storeUndo(_undo);
    }
    
    @Override
    public void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.custom(ChatColor.GREEN + "Shift Level set to " + this.yLevel);
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	if (par[1].equalsIgnoreCase("info")) {
    		v.sendMessage(ChatColor.GREEN + "y[number] to set the Level to which the land will be shifted down");
    	}
    	if (par[1].startsWith("y")) {
    		int _i = Integer.parseInt(par[1].replace("y", ""));
    		if (_i < SHIFT_LEVEL_MIN) {
    			_i = SHIFT_LEVEL_MIN;
    		} else if (_i > SHIFT_LEVEL_MAX) {
    			_i = SHIFT_LEVEL_MAX;
    		}
    		this.yLevel = _i;
    		v.sendMessage(ChatColor.GREEN + "Shift Level set to " + this.yLevel);
    	}
    }
    
    @Override
    public int getTimesUsed() {
        return CanyonBrush.timesUsed;
    }
    
    @Override
    public void setTimesUsed(final int tUsed) {
        CanyonBrush.timesUsed = tUsed;
    }
}
