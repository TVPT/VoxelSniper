package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Voxel
 */
public class CanyonSelectionBrush extends CanyonBrush {
    private boolean first = true;
    private int fx;
    private int fz;

    private static int timesUsed = 0;

    public CanyonSelectionBrush() {
        this.setName("Canyon Selection");
    }

    private void selection(final int lowX, final int lowZ, final int highX, final int highZ, final SnipeData v) {
        this.undo = new Undo(this.getWorld().getChunkAt(this.getTargetBlock()).getWorld().getName());

        for (int _x = lowX; _x <= highX; _x++) {
            for (int _z = lowZ; _z <= highZ; _z++) {
                this.multiCanyon(this.getWorld().getChunkAt(_x, _z), v);
            }
        }

        v.storeUndo(this.undo);
    }

    @Override
    protected final void arrow(final SnipeData v) {
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

    @Override
    protected final void powder(final SnipeData v) {
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
