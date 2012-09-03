package com.thevoxelbox.voxelsniper.brush;

import net.minecraft.server.EntityCreature;
import net.minecraft.server.NPC;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;

/**
 * 
 * @author Voxel
 */
public class EntityRemoval extends Brush {

    private int entcount = 0;
    private int chunkcount = 0;

    private static int timesUsed = 0;

    public EntityRemoval() {
        this.name = "Entity Removal";
    }

    @Override
    public final int getTimesUsed() {
        return EntityRemoval.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        vm.size();
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        EntityRemoval.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.radialRemoval(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.radialRemoval(v);
    }

    protected final void radialRemoval(final vData v) {
        this.entcount = 0;
        this.chunkcount = 0;
        final Chunk ch = this.tb.getChunk();
        this.removeEntities(ch);
        for (int x = ch.getX() - v.brushSize; x <= ch.getX() + v.brushSize; x++) {
            for (int z = ch.getZ() - v.brushSize; z <= ch.getZ() + v.brushSize; z++) {
                this.removeEntities(this.w.getChunkAt(x, z));
                this.chunkcount++;
            }
        }
        v.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + this.entcount + ChatColor.GREEN + " entities out of " + ChatColor.BLUE + this.chunkcount
                + ChatColor.GREEN + " chunks.");
    }

    protected final void removeEntities(final Chunk c) {
        for (final Entity e : c.getEntities()) {
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
                this.entcount++;
            }
        }
    }
}
