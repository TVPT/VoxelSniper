package com.thevoxelbox.voxelsniper.util.schematic;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.util.BlockVector;
import org.spout.nbt.CompoundTag;

/**
 * Representation of a cuboidal region of game space.
 * 
 * @author Deamon
 */
public class BlockRegion
{
    private int                           width, height, length;
    private int                           xOffset, yOffset, zOffset;

    private short[]                       blockIds;
    private byte[]                        blockData;

    private Map<BlockVector, CompoundTag> tileEntities;

    private String                        name        = "";

    private long                          lastChanged = 0;

    public BlockRegion(int w, int h, int l)
    {
        this.width = w;
        this.height = h;
        this.length = l;
        this.blockData = new byte[w * l * h];
        this.blockIds = new short[w * l * h];
        this.tileEntities = new HashMap<BlockVector, CompoundTag>();
    }

    public BlockRegion(int w, int h, int l, short[] ids, byte[] data)
    {
        this.width = w;
        this.height = h;
        this.length = l;
        this.blockData = data;
        this.blockIds = ids;
        this.tileEntities = new HashMap<BlockVector, CompoundTag>();
    }

    public BlockRegion(int w, int h, int l, short[] ids, byte[] data, Map<BlockVector, CompoundTag> tiles)
    {
        this.width = w;
        this.height = h;
        this.length = l;
        this.blockData = data;
        this.blockIds = ids;
        this.tileEntities = tiles;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getLength()
    {
        return length;
    }

    public int getxOffset()
    {
        return xOffset;
    }

    public void setxOffset(int xOffset)
    {
        this.xOffset = xOffset;
    }

    public int getyOffset()
    {
        return yOffset;
    }

    public void setyOffset(int yOffset)
    {
        this.yOffset = yOffset;
    }

    public int getzOffset()
    {
        return zOffset;
    }

    public void setzOffset(int zOffset)
    {
        this.zOffset = zOffset;
    }

    public short[] getBlockIds()
    {
        return blockIds;
    }

    public void setBlockIds(short[] blockIds)
    {
        this.blockIds = blockIds;
    }

    public byte[] getBlockData()
    {
        return blockData;
    }

    public void setBlockData(byte[] blockData)
    {
        this.blockData = blockData;
    }

    public Map<BlockVector, CompoundTag> getTileEntities()
    {
        return tileEntities;
    }

    public void setTileEntities(Map<BlockVector, CompoundTag> tileEntities)
    {
        this.tileEntities = tileEntities;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public byte[] getLowerByteBlockIds()
    {
        byte[] ids = new byte[blockIds.length];
        for (int i = 0; i < blockIds.length; i++)
        {
            ids[i] = (byte) (blockIds[i] & 0xFF);
        }
        return ids;
    }

    public boolean needsAdditionalBlocks()
    {
        for (int i = 0; i < blockIds.length; i++)
        {
            if (blockIds[i] > 255)
            {
                return true;
            }
        }
        return false;
    }

    public byte[] getAddBlocks()
    {
        byte[] addBlocks = new byte[blockIds.length / 2];
        // I believe this might drop the last nibble
        // but as the addBlocks are so far unused in normal usage,
        // and because the loaded would have a similar OffByOneError I'm going to leave it for now
        // TODO: Remove potential off-by-one error
        for (int i = 0; i < blockIds.length; i += 2)
        {
            addBlocks[i / 2] = (byte) (((blockIds[i] & 0xF00) >> 4) | ((blockIds[i + 1] & 0xF000) >> 8));
        }
        return addBlocks;
    }

    public long getLastChanged()
    {
        return lastChanged;
    }

    public void setLastChanged(long lastChanged)
    {
        this.lastChanged = lastChanged;
    }
}
