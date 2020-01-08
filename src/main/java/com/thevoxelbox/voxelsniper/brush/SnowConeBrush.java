package com.thevoxelbox.voxelsniper.brush;


import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Snow;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Snow_cone_brush
 *
 * @author Voxel
 */
public class SnowConeBrush extends Brush
{
    private void addSnow(final SnipeData v, Block targetBlock)
    {
        int brushSize;
        int blockPositionX = targetBlock.getX();
        int blockPositionY = targetBlock.getY();
        int blockPositionZ = targetBlock.getZ();
        if (this.getBlockTypeAt(blockPositionX, blockPositionY, blockPositionZ) == Material.AIR)
        {
            brushSize = 0;
        }
        else
        {
            brushSize = this.clampY(blockPositionX, blockPositionY, blockPositionZ).getData() + 1;
        }

        final int brushSizeDoubled = 2 * brushSize;
        final Material[][] snowcone = new Material[brushSizeDoubled + 1][brushSizeDoubled + 1]; // Will hold block IDs
        final int[][] snowconeData = new int[brushSizeDoubled + 1][brushSizeDoubled + 1]; // Will hold data values for snowcone
        final int[][] yOffset = new int[brushSizeDoubled + 1][brushSizeDoubled + 1];
        // prime the arrays

        for (int x = 0; x <= brushSizeDoubled; x++)
        {
            for (int z = 0; z <= brushSizeDoubled; z++)
            {
                boolean flag = true;

                int brushX = blockPositionX - brushSize + x;
                int brushZ = blockPositionZ - brushSize + z;
                for (int i = 0; i < 10; i++)
                { // overlay
                    if (flag)
                    {
                        int iy = blockPositionY - i;
                        if ((this.getBlockTypeAt(brushX, iy, brushZ) == Material.AIR || this.getBlockTypeAt(brushX, iy, brushZ) == Material.SNOW) && this.getBlockTypeAt(brushX, iy - 1, brushZ) != Material.AIR && this.getBlockTypeAt(brushX, iy - 1, brushZ) != Material.SNOW)
                        {
                            flag = false;
                            yOffset[x][z] = i;
                        }
                    }
                }
                snowcone[x][z] = this.getBlockTypeAt(brushX, blockPositionY - yOffset[x][z], brushZ);
                Block dataBlock = this.clampY(brushX, blockPositionY - yOffset[x][z], brushZ);
                snowconeData[x][z] = dataBlock.getType() == Material.SNOW ? ((Snow)dataBlock.getBlockData()).getLayers() : 0;
            }
        }

        // figure out new snowheights
        for (int x = 0; x <= brushSizeDoubled; x++)
        {
            final double xSquared = Math.pow(x - brushSize, 2);

            for (int z = 0; z <= 2 * brushSize; z++)
            {
                final double zSquared = Math.pow(z - brushSize, 2);
                final double dist = Math.pow(xSquared + zSquared, .5); // distance from center of array
                final int snowData = brushSize - (int) Math.ceil(dist);

                if (snowData >= 0)
                { // no funny business
                    switch (snowData)
                    {
                        case 0:
                            if (snowcone[x][z] == Material.AIR)
                            {
                                snowcone[x][z] = Material.SNOW;
                                snowconeData[x][z] = 0;
                            }
                            break;
                        case 7: // Turn largest snowtile into snowblock
                            if (snowcone[x][z] == Material.SNOW)
                            {
                                snowcone[x][z] = Material.SNOW_BLOCK;
                                snowconeData[x][z] = 0;
                            }
                            break;
                        default: // Increase snowtile size, if smaller than target

                            if (snowData > snowconeData[x][z])
                            {
                                switch (snowcone[x][z])
                                {
                                    case AIR:
                                        snowconeData[x][z] = snowData;
                                        snowcone[x][z] = Material.SNOW;
                                    case SNOW:
                                        snowconeData[x][z] = snowData;
                                        break;
                                    default:
                                        break;

                                }
                            }
                            else if (yOffset[x][z] > 0 && snowcone[x][z] == Material.SNOW)
                            {
                                snowconeData[x][z]++;
                                if (snowconeData[x][z] == 7)
                                {
                                    snowconeData[x][z] = 0;
                                    snowcone[x][z] = Material.SNOW_BLOCK;
                                }
                            }
                            break;
                    }
                }
            }
        }

        final Undo undo = new Undo();

        for (int x = 0; x <= brushSizeDoubled; x++)
        {
            for (int z = 0; z <= brushSizeDoubled; z++)
            {

                Block block = this.clampY(blockPositionX - brushSize + x, blockPositionY - yOffset[x][z], blockPositionZ - brushSize + z);
                Snow oldSnow = block.getType() == Material.SNOW ? (Snow)block.getBlockData() : null;
                if (block.getType() != snowcone[x][z] || (oldSnow != null && oldSnow.getLayers() != snowconeData[x][z]))
                {
                    undo.put(block);
                }
                this.setBlockTypeAt(blockPositionZ - brushSize + z, blockPositionX - brushSize + x, blockPositionY - yOffset[x][z], snowcone[x][z]);
                if(block.getType() == Material.SNOW) {
                    ((Snow)block.getBlockData()).setLayers(snowconeData[x][z]);
                }
            }
        }
        v.owner().storeUndo(undo);
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        switch (getTargetBlock().getType())
        {
            case SNOW:
                this.addSnow(v, this.getTargetBlock());
                break;
            default:
                Block blockAbove = getTargetBlock().getRelative(BlockFace.UP);
                if (blockAbove != null && blockAbove.getType() == Material.AIR)
                {
                    addSnow(v, blockAbove);
                }
                else
                {
                    v.owner().getPlayer().sendMessage(ChatColor.RED + "Error: Center block neither snow nor air.");
                }
                break;
        }
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName("Snow Cone");
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        if (par[1].equalsIgnoreCase("info"))
        {
            v.sendMessage(ChatColor.GOLD + "Snow Cone Parameters:");
        }
    }

    @Override
    public String getPermissionNode()
    {
        return "voxelsniper.brush.snowcone";
    }
}
