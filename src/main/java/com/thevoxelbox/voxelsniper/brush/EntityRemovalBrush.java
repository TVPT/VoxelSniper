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

        int radius = Math.round(v.getBrushSize() / 16);

        for (int x = targetChunk.getX() - radius; x <= targetChunk.getX() + radius; x++)
        {
            for (int z = targetChunk.getZ() - radius; z <= targetChunk.getZ() + radius; z++)
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
    public String getPermissionNode()
    {
        return "voxelsniper.brush.entityremoval";
    }
}
