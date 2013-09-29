package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;

/**
 *
 */
public class EntityRemovalBrush extends Brush
{
    private static int timesUsed = 0;

    /**
     *
     */
    public EntityRemovalBrush()
    {
        this.setName("Entity Removal");
    }

    private void radialRemoval(SnipeData v)
    {
        final Chunk targetChunk = getTargetBlock().getChunk();
        int entityCount = 0;
        int chunkCount = 0;

        entityCount += removeEntities(targetChunk);

        for (int x = targetChunk.getX() - v.getBrushSize(); x <= targetChunk.getX() + v.getBrushSize(); x++)
        {
            for (int z = targetChunk.getZ() - v.getBrushSize(); z <= targetChunk.getZ() + v.getBrushSize(); z++)
            {
                entityCount += removeEntities(getWorld().getChunkAt(x, z));
                chunkCount++;
            }
        }
        v.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + entityCount + ChatColor.GREEN + " entities out of " + ChatColor.BLUE + chunkCount + ChatColor.GREEN + " chunks.");
    }

    private int removeEntities(Chunk chunk)
    {
        int entityCount = 0;

        for (Entity entity : chunk.getEntities())
        {
            if (entity instanceof Player || entity instanceof Hanging || entity instanceof NPC)
            {
                continue;
            }
            entity.remove();
            entityCount++;
        }

        return entityCount;
    }

    @Override
    protected void arrow(SnipeData v)
    {
        this.radialRemoval(v);
    }

    @Override
    protected void powder(SnipeData v)
    {
        this.radialRemoval(v);
    }

    @Override
    public void info(Message vm)
    {
        vm.brushName(getName());
        vm.size();
    }

    @Override
    public int getTimesUsed()
    {
        return EntityRemovalBrush.timesUsed;
    }

    @Override
    public void setTimesUsed(int tUsed)
    {
        EntityRemovalBrush.timesUsed = tUsed;
    }
}
