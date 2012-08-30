/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import net.minecraft.server.EntityCreature;
import net.minecraft.server.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 *
 * @author Voxel
 */
public class EntityRemoval extends Brush {

    private int entcount = 0;
    private int chunkcount = 0;

    public EntityRemoval() {
        name = "Entity Removal";
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        radialRemoval(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        radialRemoval(v);
    }

    protected void radialRemoval(vData v) {
        entcount = 0;
        chunkcount = 0;
        Chunk ch = tb.getChunk();
        removeEntities(ch);
        for (int x = ch.getX() - v.brushSize; x <= ch.getX() + v.brushSize; x++) {
            for (int z = ch.getZ() - v.brushSize; z <= ch.getZ() + v.brushSize; z++) {
                removeEntities(w.getChunkAt(x, z));
                chunkcount++;
            }
        }
        v.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + entcount + ChatColor.GREEN + " entities out of " + ChatColor.BLUE + chunkcount + ChatColor.GREEN + " chunks.");
    }

    protected void removeEntities(Chunk c) {
        for (Entity e : c.getEntities()) {
            if (e instanceof Player) {
                continue;
            } else if (e instanceof org.bukkit.entity.Painting) {
                continue;
            } else {
                if (((CraftEntity) e).getHandle() instanceof NPC) {
                    if (!(((CraftEntity) e).getHandle() instanceof EntityCreature)) {
                        continue;
                    }
                }
                e.remove();
                entcount++;
            }
        }
    }
    
    private static int timesUsed = 0;
	
    @Override
	public int getTimesUsed() {
		return timesUsed;
	}

	@Override
	public void setTimesUsed(int tUsed) {
		timesUsed = tUsed; 
	}
}
