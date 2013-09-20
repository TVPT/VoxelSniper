package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import net.minecraft.server.v1_6_R3.EntityCreature;
import net.minecraft.server.v1_6_R3.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;

/**
 * @author Voxel
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

    private void radialRemoval(final SnipeData v)
    {
        final Chunk targetChunk = this.getTargetBlock().getChunk();
        int entityCount = 0;
        int chunkCount = 0;

        entityCount += this.removeEntities(targetChunk);

        for (int x = targetChunk.getX() - v.getBrushSize(); x <= targetChunk.getX() + v.getBrushSize(); x++)
        {
            for (int z = targetChunk.getZ() - v.getBrushSize(); z <= targetChunk.getZ() + v.getBrushSize(); z++)
            {
                entityCount += removeEntities(this.getWorld().getChunkAt(x, z));
                chunkCount++;
            }
        }
        v.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + entityCount + ChatColor.GREEN + " entities out of " + ChatColor.BLUE + chunkCount + ChatColor.GREEN + " chunks.");
    }

    private int removeEntities(final Chunk chunk)
    {
        int entityCount = 0;

        for (final Entity entity : chunk.getEntities())
        {
            if ((!(entity instanceof Player)) && (!(entity instanceof Painting)) && (!(entity instanceof ItemFrame)))
            {
                if (((CraftEntity) entity).getHandle() instanceof NPC)
                {
                    if (!(((CraftEntity) entity).getHandle() instanceof EntityCreature))
                    {
                        continue;
                    }
                }
                entity.remove();
                entityCount++;
            }
        }

        return entityCount;
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.radialRemoval(v);
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.radialRemoval(v);
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public final int getTimesUsed()
    {
        return EntityRemovalBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        EntityRemovalBrush.timesUsed = tUsed;
    }
}
