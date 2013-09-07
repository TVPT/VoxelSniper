package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.Chunk;

/**
 * Regenerates the target chunk.
 *
 * @author Mick
 */
public class RegenerateChunkBrush extends Brush
{
    private static int timesUsed = 0;

    /**
     *
     */
    public RegenerateChunkBrush()
    {
        this.setName("Chunk Generator 40k");
    }

    private void generateChunk(final SnipeData v)
    {
        final Chunk chunk = this.getTargetBlock().getChunk();
        final Undo undo = new Undo(this.getTargetBlock().getWorld().getName());

        for (int z = CHUNK_SIZE; z >= 0; z--)
        {
            for (int x = CHUNK_SIZE; x >= 0; x--)
            {
                for (int y = this.getWorld().getMaxHeight(); y >= 0; y--)
                {
                    undo.put(chunk.getBlock(x, y, z));
                }
            }
        }
        v.storeUndo(undo);

        v.sendMessage("Generate that chunk! " + chunk.getX() + " " + chunk.getZ());
        this.getWorld().regenerateChunk(chunk.getX(), chunk.getZ());
        this.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.generateChunk(v);
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.generateChunk(v);
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.brushMessage("Tread lightly.");
        vm.brushMessage("This brush will melt your spleen and sell your kidneys.");
    }

    @Override
    public final int getTimesUsed()
    {
        return RegenerateChunkBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        RegenerateChunkBrush.timesUsed = tUsed;
    }
}
