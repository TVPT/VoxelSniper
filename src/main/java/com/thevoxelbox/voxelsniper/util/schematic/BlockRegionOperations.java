package com.thevoxelbox.voxelsniper.util.schematic;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.spout.nbt.CompoundTag;

import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.util.InvalidFormatException;
import com.thevoxelbox.voxelsniper.util.Rot3d;

/**
 * Utility for performing various operations on {@link BlockRegion}.
 * 
 * @author Deamon
 */
public class BlockRegionOperations
{
    private BlockRegionOperations()
    {
    }

    @SuppressWarnings("deprecation")
    public static void placeIntoWorldUnbuffered(BlockRegion region, World world, int x, int y, int z, Undo undo, BlockRegionMask mask, double yaw, double pitch, double roll, boolean xFlipped, boolean yFlipped, boolean zFlipped) throws InvalidFormatException
    {
        if (region == null)
        {
            return;
        }
        if (mask == null)
        {
            mask = BlockRegionMask.NONE;
        }
        boolean doRotation = (yaw != 0 || pitch != 0 || roll != 0);

        short[] ids = region.getBlockIds();
        byte[] data = region.getBlockData();
        Rot3d rotation = null;

        if (doRotation)
        {
            rotation = new Rot3d(yaw * Math.PI / 180d, pitch * Math.PI / 180d, roll * Math.PI / 180d);
        }
        double[] refPos = null;
        if (doRotation)
        {
            refPos = rotation.doRotation(region.getxOffset(), region.getyOffset(), region.getzOffset());
        }
        else
        {
            refPos = new double[] { region.getxOffset(), region.getyOffset(), region.getzOffset() };
        }

        for (int _x = 0; _x < region.getWidth(); _x++)
        {
            int _x0 = xFlipped ? region.getWidth() - 1 - _x : _x;
            for (int _y = 0; _y < region.getHeight(); _y++)
            {
                int _y0 = yFlipped ? region.getHeight() - 1 - _y : _y;
                for (int _z = 0; _z < region.getLength(); _z++)
                {
                    int _z0 = zFlipped ? region.getLength() - 1 - _z : _z;
                    double[] finalPos = null;
                    if (doRotation)
                    {
                        finalPos = rotation.doRotation(_x0, _y0, _z0);
                    }
                    else
                    {
                        finalPos = new double[] { _x0, _y0, _z0 };
                    }

                    finalPos[0] += refPos[0];
                    finalPos[1] += refPos[1];
                    finalPos[2] += refPos[2];

                    Block current = world.getBlockAt((int) Math.round(finalPos[0]) + x, (int) Math.round(finalPos[1]) + y, (int) Math.round(finalPos[2]) + z);

                    if (mask.getType() == BlockRegionMaskType.REPLACE)
                    {
                        if (mask.contains(new int[] { current.getTypeId(), current.getData() }))
                        {
                            short id = ids[_x + _z * region.getWidth() + _y * region.getWidth() * region.getLength()];
                            byte d = data[_x + _z * region.getWidth() + _y * region.getWidth() * region.getLength()];
                            d = MetadataRotation.getData(id, d, (int) yaw);
                            undo.put(current);
                            current.setTypeIdAndData(id, d, false);
                        }
                    }
                    else if (mask.getType() == BlockRegionMaskType.NEGATIVE_REPLACE)
                    {
                        if (!mask.contains(new int[] { current.getTypeId(), current.getData() }))
                        {
                            short id = ids[_x + _z * region.getWidth() + _y * region.getWidth() * region.getLength()];
                            byte d = data[_x + _z * region.getWidth() + _y * region.getWidth() * region.getLength()];
                            d = MetadataRotation.getData(id, d, (int) yaw);
                            undo.put(current);
                            current.setTypeIdAndData(id, d, false);
                        }
                    }
                    else
                    {
                        short id = ids[_x + _z * region.getWidth() + _y * region.getWidth() * region.getLength()];
                        byte d = data[_x + _z * region.getWidth() + _y * region.getWidth() * region.getLength()];
                        d = MetadataRotation.getData(id, d, (int) yaw);
                        undo.put(current);
                        current.setTypeIdAndData(id, d, false);
                    }
                }
            }
        }

        for (BlockVector v: region.getTileEntities().keySet())
        {
            CompoundTag tile = region.getTileEntities().get(v);
            short id = ids[v.getBlockX() + v.getBlockZ() * region.getWidth() + v.getBlockY() * region.getWidth() * region.getLength()];
            byte d = data[v.getBlockX() + v.getBlockZ() * region.getWidth() + v.getBlockY() * region.getWidth() * region.getLength()];
            d = MetadataRotation.getData(id, d, (int) yaw);
            int _x0 = xFlipped ? region.getWidth() - v.getBlockX() - 1 : v.getBlockX();
            int _y0 = yFlipped ? region.getHeight() - v.getBlockY() - 1 : v.getBlockY();
            int _z0 = zFlipped ? region.getLength() - v.getBlockZ() - 1 : v.getBlockZ();

            double[] finalPos = null;
            if (doRotation)
            {
                finalPos = rotation.doRotation(_x0, _y0, _z0);
            }
            else
            {
                finalPos = new double[] { _x0, _y0, _z0 };
            }

            finalPos[0] += refPos[0];
            finalPos[1] += refPos[1];
            finalPos[2] += refPos[2];

            TileEntityOperations.createTileEntity(world, (int) Math.round(finalPos[0]) + x, (int) Math.round(finalPos[1]) + y, (int) Math.round(finalPos[2]) + z, tile, id, d);
        }
    }

    public static void placeIntoWorldUnbuffered(BlockRegion region, World world, int x, int y, int z, Undo undo, BlockRegionMask mask, int yaw, int pitch, int roll) throws InvalidFormatException
    {
        placeIntoWorldUnbuffered(region, world, x, y, z, undo, mask, yaw, pitch, roll, false, false, false);
    }

    public static void placeIntoWorldUnbuffered(BlockRegion region, World world, int x, int y, int z, Undo undo, BlockRegionMask mask, boolean xFlipped, boolean yFlipped, boolean zFlipped) throws InvalidFormatException
    {
        placeIntoWorldUnbuffered(region, world, x, y, z, undo, mask, 0, 0, 0, xFlipped, yFlipped, zFlipped);
    }

    public static void placeIntoWorldUnbuffered(BlockRegion region, World world, int x, int y, int z, Undo undo, BlockRegionMask mask) throws InvalidFormatException
    {
        placeIntoWorldUnbuffered(region, world, x, y, z, undo, mask, 0, 0, 0, false, false, false);
    }

    public static void placeIntoWorldUnbuffered(BlockRegion region, World world, int x, int y, int z, Undo undo) throws InvalidFormatException
    {
        placeIntoWorldUnbuffered(region, world, x, y, z, undo, BlockRegionMask.NONE, 0, 0, 0, false, false, false);
    }

    public static void placeIntoWorldUnbuffered(BlockRegion region, World world, int x, int y, int z) throws InvalidFormatException
    {
        placeIntoWorldUnbuffered(region, world, x, y, z, null, BlockRegionMask.NONE, 0, 0, 0, false, false, false);
    }

    public static void placeIntoWorldUnbuffered(BlockRegion region, Location location) throws InvalidFormatException
    {
        placeIntoWorldUnbuffered(region, location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), null, BlockRegionMask.NONE);
    }
}
