package com.thevoxelbox.voxelsniper.util;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.IntArrayTag;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.ShortTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.BlockVector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpongeSchematic
{

    private int width;
    private int height;
    private int length;
    private BlockVector offset;
    private BlockData[] blockData;

    private SpongeSchematic(int width, int height, int length, BlockVector offset, BlockData[] blockData)
    {
        this.width = width;
        this.height = height;
        this.length = length;
        this.offset = offset;
        this.blockData = blockData;
    }

    private SpongeSchematic(short width, short height, short length, BlockVector offset, BlockData[] blockData)
    {
        this(width & 0xFFFF, height & 0xFFFF, length & 0xFFFF, offset, blockData);
    }

    public static SpongeSchematic read(File file) throws IOException
    {
        NBTInputStream nbtIs = null;
        try
        {
            nbtIs = new NBTInputStream(new FileInputStream(file), true);
            Tag tag = nbtIs.readTag();
            return loadSchematic(tag);
        } finally
        {
            if (nbtIs != null)
            {
                nbtIs.close();
            }
        }
    }

    public static SpongeSchematic createFromWorld(World world, BlockVector firstPoint, BlockVector secondPoint,
            BlockVector pastePoint)
    {
        int startX = Math.min(firstPoint.getBlockX(), secondPoint.getBlockX());
        int startY = Math.min(firstPoint.getBlockY(), secondPoint.getBlockY());
        int startZ = Math.min(firstPoint.getBlockZ(), secondPoint.getBlockZ());

        int width = Math.abs(firstPoint.getBlockX() - secondPoint.getBlockX());
        int height = Math.abs(firstPoint.getBlockY() - secondPoint.getBlockY());
        int length = Math.abs(firstPoint.getBlockZ() - secondPoint.getBlockZ());
        BlockVector offset = pastePoint.clone();

        int blockDataSize = width * height * length;
        BlockData[] blockData = new BlockData[blockDataSize];
        for (int x = startX; x < width; x++)
        {
            for (int y = startY; y < length; y++)
            {
                for (int z = startZ; z < length; z++)
                {
                    blockData[x + z * width + y * width * length] = world.getBlockAt(x, y, z).getBlockData();
                }
            }
        }

        return new SpongeSchematic(width, height, length, offset, blockData);
    }

    private static SpongeSchematic loadSchematic(Tag tag) throws IOException
    {
        if(!(tag instanceof CompoundTag))
        {
            throw new IOException("Invalid Schematic");
        }

        CompoundMap map = (CompoundMap)tag.getValue();

        int version = getMapValue(map, "Version", IntTag.class).getValue();
        if (version != 1)
        {
            throw new IOException("Unsupported schematic version " + version);
        }

        short width = getMapValue(map, "Width", ShortTag.class).getValue();
        short height = getMapValue(map, "Height", ShortTag.class).getValue();
        short length = getMapValue(map, "Length", ShortTag.class).getValue();

        int[] offsetArr = getMapValue(map, "Offset", IntArrayTag.class).getValue();
        if (offsetArr.length != 3)
        {
            throw new IOException("Invalid length on schematic offset");
        }

        BlockVector offset = new BlockVector(offsetArr[0], offsetArr[1], offsetArr[2]);

        int paletteMax = getMapValue(map, "PaletteMax", IntTag.class).getValue();
        BlockData[] palette = new BlockData[paletteMax];

        CompoundMap paletteMap = getMapValue(map, "Palette", CompoundTag.class).getValue();
        if (paletteMap == null)
        {
            throw new IOException("No palette found for schematic. VoxelSniper only supports schematics with palettes");
        }

        for (Map.Entry<String, Tag<?>> paletteEntry : paletteMap.entrySet())
        {
            String key = paletteEntry.getKey();
            try
            {
                BlockData data = Bukkit.createBlockData(key);
                Tag<?> tagNum = paletteEntry.getValue();

                if (!(tagNum instanceof IntTag))
                {
                    throw new IOException("Unexpected tag type for palette value");
                }

                int id = (Integer)tagNum.getValue();
                palette[id] = data;
            }
            catch (IllegalArgumentException e)
            {
                throw new IOException("Not a valid block state: " + key);
            }
        }

        int[] blockDataPalette = getMapValue(map, "BlockData", IntArrayTag.class).getValue();
        int blockDataSize = width * height * length;
        if (blockDataPalette.length != blockDataSize)
        {
            throw new IOException("Expected " + blockDataSize + ", got " + blockDataPalette.length);
        }

        BlockData[] blockData = new BlockData[blockDataSize];
        for (int i = 0; i < blockDataPalette.length; i++)
        {
            blockData[i] = palette[blockDataPalette[i]];
        }

        return new SpongeSchematic(width, height, length, offset, blockData);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Tag<?>> T getMapValue(CompoundMap map, String key, Class<T> clazz) throws IOException
    {
        Tag<?> tag = map.get(key);
        if(tag == null || clazz.isInstance(tag))
        {
            return (T)tag;
        }
        else
        {
            throw new IOException("Unexpected type found for " + key);
        }
    }

    public void writeTo(File file) throws IOException
    {
        NBTOutputStream os = null;

        try
        {
            os = new NBTOutputStream(new FileOutputStream(file));
            os.writeTag(writeSchematic());
        } finally
        {
            if (os != null)
            {
                os.close();
            }
        }
    }

    private CompoundTag writeSchematic() throws IOException
    {
        CompoundMap map = new CompoundMap();
        map.put(new IntTag("Version", 1));
        map.put(new ShortTag("Width", (short)(width << 4)));
        map.put(new ShortTag("Height", (short)(height << 4)));
        map.put(new ShortTag("Length", (short)(length << 4)));
        map.put(new IntArrayTag("Offset", new int[] {offset.getBlockX(), offset.getBlockY(), offset.getBlockZ()}));

        Map<BlockData, Integer> idMap = new HashMap<BlockData, Integer>();
        int lastId = Integer.MIN_VALUE;
        for (BlockData data : blockData)
        {
            if (!idMap.containsKey(data))
            {
                int nextId = lastId++;
                if (nextId < lastId)
                {
                    throw new IOException("Ran out of pallete space. Too many block types");
                }

                idMap.put(data, nextId);
                lastId = nextId;
            }
        }

        map.put(new IntTag("PaletteMax", idMap.size()));

        CompoundMap pallete = new CompoundMap();
        for (Map.Entry<BlockData, Integer> entry : idMap.entrySet())
        {
            pallete.put(new IntTag(entry.getKey().getAsString(), entry.getValue()));
        }
        map.put(new CompoundTag("Pallete", pallete));

        int[] blockDataArr = new int[width * height * length];
        for (int i = 0; i < blockData.length; i++)
        {
            blockDataArr[i] = idMap.get(blockData[i]);
        }
        map.put(new IntArrayTag("BlockData", blockDataArr));

        return new CompoundTag("", map);
    }

    public BlockData getBlockAt(int x, int y, int z)
    {
        return blockData[x + z * width + y * width * length];
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

    public BlockVector getOffset()
    {
        return offset;
    }

    public BlockData[] getBlockData()
    {
        return blockData;
    }
}
