package com.thevoxelbox.voxelsniper.brush;

import net.minecraft.server.EntityCreature;
import net.minecraft.server.NPC;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;

/**
 * 
 * @author Voxel
 */
public class EntityRemovalBrush extends Brush {
    private static int timesUsed = 0;

    public EntityRemovalBrush() {
        this.setName("Entity Removal");
    }

    private final void radialRemoval(final SnipeData v) {
    	final Chunk _targetChunk = this.getTargetBlock().getChunk();
    	int entityCount = 0;
        int chunkCount = 0;
        
        entityCount += this.removeEntities(_targetChunk);
        
        for (int _x = _targetChunk.getX() - v.getBrushSize(); _x <= _targetChunk.getX() + v.getBrushSize(); _x++) {
            for (int _z = _targetChunk.getZ() - v.getBrushSize(); _z <= _targetChunk.getZ() + v.getBrushSize(); _z++) {
            	entityCount += removeEntities(this.getWorld().getChunkAt(_x, _z));
                chunkCount++;
            }
        }
        v.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + entityCount + ChatColor.GREEN + " entities out of " + ChatColor.BLUE + chunkCount
                + ChatColor.GREEN + " chunks.");
    }

    private final int removeEntities(final Chunk chunk) {
    	int entityCount = 0;
    	
        for (final Entity _e : chunk.getEntities()) {
            if ((_e instanceof Player) || (_e instanceof Painting)) {
                continue;
            } else {
                if (((CraftEntity) _e).getHandle() instanceof NPC) {
                    if (!(((CraftEntity) _e).getHandle() instanceof EntityCreature)) {
                        continue;
                    }
                }
                _e.remove();
                entityCount++;
            }
        }
        
        return entityCount;
    }
    
    @Override
    protected final void arrow(final SnipeData v) {
    	this.radialRemoval(v);
    }
    
    @Override
    protected final void powder(final SnipeData v) {
    	this.radialRemoval(v);
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.size();
    }
    
    @Override
    public final int getTimesUsed() {
    	return EntityRemovalBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	EntityRemovalBrush.timesUsed = tUsed;
    }
}
