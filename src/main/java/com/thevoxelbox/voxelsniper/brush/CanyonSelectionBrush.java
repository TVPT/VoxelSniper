package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Canyon_Selection_Brush
 * @author Voxel
 */
public class CanyonSelectionBrush extends CanyonBrush {
	private static int timesUsed = 0;
   
	private boolean first = true;
    private int fx;
    private int fz;

    /**
     * 
     */
    public CanyonSelectionBrush() {
        this.setName("Canyon Selection");
    }

    private void execute(final SnipeData v) {
    	final Chunk _c = this.getWorld().getChunkAt(this.getTargetBlock());
    	
    	if (this.first) {
    		this.fx = _c.getX();
    		this.fz = _c.getZ();
    		
    		v.sendMessage(ChatColor.YELLOW + "First point selected!");
    		this.first = !this.first;
    	} else {            
    		this.setBlockPositionX(_c.getX());
    		this.setBlockPositionZ(_c.getZ());
    		
    		v.sendMessage(ChatColor.YELLOW + "Second point selected!");
    		this.selection(this.fx < this.getBlockPositionX() ? this.fx : this.getBlockPositionX(), this.fz < this.getBlockPositionZ() ? this.fz : this.getBlockPositionZ(), this.fx > this.getBlockPositionX() ? this.fx : this.getBlockPositionX(),
    				this.fz > this.getBlockPositionZ() ? this.fz : this.getBlockPositionZ(), v);
    		
    		this.first = !this.first;
    	}    	
    }
    
    private void selection(final int lowX, final int lowZ, final int highX, final int highZ, final SnipeData v) {
        final Undo _undo = new Undo(this.getWorld().getChunkAt(this.getTargetBlock()).getWorld().getName());

        for (int _x = lowX; _x <= highX; _x++) {
            for (int _z = lowZ; _z <= highZ; _z++) {
                this.canyon(this.getWorld().getChunkAt(_x, _z), _undo);
            }
        }

        v.storeUndo(_undo);
    }

    @Override
    protected final void arrow(final SnipeData v) {
    	execute(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
    	execute(v);
    }

    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.custom(ChatColor.GREEN + "Shift Level set to " + this.yLevel);
    }
    
    @Override
    public final int getTimesUsed() {
    	return CanyonSelectionBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	CanyonSelectionBrush.timesUsed = tUsed;
    }
}
