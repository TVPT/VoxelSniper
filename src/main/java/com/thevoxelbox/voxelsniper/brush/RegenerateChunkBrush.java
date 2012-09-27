package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.Chunk;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * Regenerates the target chunk.
 * @author Mick
 */
public class RegenerateChunkBrush extends Brush {
    private static int timesUsed = 0;

    /**
     * 
     */
    public RegenerateChunkBrush() {
        this.setName("Chunk Generator 40k");
    }

    private final void generateChunk(final SnipeData v) {
    	final Chunk _chunk = this.getTargetBlock().getChunk();
        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());

        for (int _z = CHUNK_SIZE; _z >= 0; _z--) {
            for (int _x = CHUNK_SIZE; _x >= 0; _x--) {
                for (int _y = this.getWorld().getMaxHeight(); _y >= 0; _y--) {
                    _undo.put(_chunk.getBlock(_x, _y, _z));
                }
            }
        }
        v.storeUndo(_undo);

        v.sendMessage("Generate that chunk! " + _chunk.getX() + " " + _chunk.getZ());
        this.getWorld().regenerateChunk(_chunk.getX(), _chunk.getZ());
        this.getWorld().refreshChunk(_chunk.getX(), _chunk.getZ());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.generateChunk(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
    	this.generateChunk(v);
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.brushMessage("Tread lightly.");
    	vm.brushMessage("This brush will melt your spleen and sell your kidneys.");
    }
    
    @Override
    public final int getTimesUsed() {
    	return RegenerateChunkBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	RegenerateChunkBrush.timesUsed = tUsed;
    }
}
