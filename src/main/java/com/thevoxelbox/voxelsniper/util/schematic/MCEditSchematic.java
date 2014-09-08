package com.thevoxelbox.voxelsniper.util.schematic;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.Tag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.util.InvalidFormatException;

/**
 * Utility for saving and loading MCEdit schematic files.
 * 
 * @author Deamon
 */
public class MCEditSchematic
{
    private static final String SCHEMATIC_EXTENSION = ".schematic";
    private static final String SCHEMATIC_FOLDER    = "stencils/";
    private static final int    MAX_SIZE            = 65535;       //the maximum size of any of the width, length, or height of a schematic.

    private MCEditSchematic()
    {
    }
    
    /**
     * Saves the given {@link BlockRegion} into the given file.
     * 
     * @param region
     * @param schematicFile
     * @throws IOException
     */
    public static void save(BlockRegion region, File schematicFile, Player player) throws IOException
    {
        region.setLastChanged(System.currentTimeMillis());
        int width = region.getWidth();
        int height = region.getHeight();
        int length = region.getLength();

        // length, width and height cannot be larger than the size of an unsigned short
        if (width > MAX_SIZE || height > MAX_SIZE || length > MAX_SIZE)
        {
            if (player == null)
            {
                VoxelSniper.getInstance().getLogger().warning("Failed to save schematic to " + schematicFile.getName() + ": Dimensions exceeded max size supported.");
            }
            else
            {
                player.sendMessage(ChatColor.RED + "Could not save schematic, dimensions exceeded maximum supported size!");
            }
            return;
        }

        // the map of tags for the main schematic compound tag
        Map<String, Tag<?>> schematic = new HashMap<String, Tag<?>>();
        // Store basic schematic data
        schematic.put("Width", new IntTag("Width", (short) width));
        schematic.put("Height", new IntTag("Height", (short) height));
        schematic.put("Length", new IntTag("Length", (short) length));
        schematic.put("Materials", new StringTag("Materials", "Alpha"));

        // Extract tile entities and load into the main schematic map

        List<CompoundTag> tiles = new ArrayList<CompoundTag>();

        for (BlockVector v: region.getTileEntities().keySet())
        {
            CompoundTag tile = region.getTileEntities().get(v);
            tiles.add(tile);
        }

        // Entities would go here, in the same format as the tile entities above

        // save the region reference point in the WorldEdit style for compatibility
        schematic.put("WEOffsetX", new IntTag("WEOffsetX", region.getxOffset()));
        schematic.put("WEOffsetY", new IntTag("WEOffsetY", region.getyOffset()));
        schematic.put("WEOffsetZ", new IntTag("WEOffsetZ", region.getzOffset()));

        // store the lower byte of the block Ids
        schematic.put("Blocks", new ByteArrayTag("Blocks", region.getLowerByteBlockIds()));
        // if the region needs the additional block id space, then store that too
        if (region.needsAdditionalBlocks())
        {
            schematic.put("AddBlocks", new ByteArrayTag("AddBlocks", region.getAddBlocks()));
        }
        schematic.put("Data", new ByteArrayTag("Data", region.getBlockData()));
        schematic.put("TileEntities", new ListTag<CompoundTag>("TileEntities", CompoundTag.class, tiles));

        CompoundTag schematicTag = new CompoundTag("Schematic", new CompoundMap(schematic));
        NBTOutputStream stream = new NBTOutputStream(new GZIPOutputStream(new FileOutputStream(schematicFile)));
        stream.writeTag(schematicTag);
        stream.close();
    }

    public static void save(BlockRegion region, String name, Player player) throws IOException
    {
        save(region, new File(VoxelSniper.getInstance().getDataFolder(), SCHEMATIC_FOLDER + name + SCHEMATIC_EXTENSION), player);
    }

    public static void save(BlockRegion region, File schematicFile) throws IOException
    {
        save(region, schematicFile, null);
    }

    public static void save(BlockRegion region, String name) throws IOException
    {
        save(region, new File(VoxelSniper.getInstance().getDataFolder(), SCHEMATIC_FOLDER + name + SCHEMATIC_EXTENSION), null);
    }

    /**
     * Attempts to load a MCEdit schematic from the given file.
     * 
     * @param schematicFile
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public static BlockRegion load(File schematicFile) throws IOException, InvalidFormatException
    {
        // Load in main schematic tag
        InputStream inputStream = new FileInputStream(schematicFile);
        NBTInputStream nbtStream = new NBTInputStream(new GZIPInputStream(inputStream));
        CompoundTag schematicTag = (CompoundTag) nbtStream.readTag();
        nbtStream.close();

        // if the first tag is not named "Schematic" than the file is of an unknown or malformed format
        if (!schematicTag.getName().equals("Schematic"))
        {
            throw new InvalidFormatException("Could not find \"Schematic\" tag in " + schematicFile.getName());
        }

        Set<String> tags = schematicTag.getValue().keySet();

        // if the file doesn't contain any blocks then why are we loading it...
        if (!tags.contains("Blocks"))
        {
            throw new InvalidFormatException("Could not find \"Blocks\" tag in " + schematicFile.getName());
        }

        // check if the materials are of the only supported format, I wonder if this will actually get used with the removal of magic numbers
        // we'll probably end up having to store some kind of dictionary of block names to local ids used in the serialization of the region
        if (!getTag(schematicTag, "Materials", StringTag.class).getValue().equals("Alpha"))
        {
            throw new InvalidFormatException("Schematic " + schematicFile.getName() + " uses an unknown \"Materials\" type.");
        }

        // extract the width (x axis size), length (z axis size), and height (y axis size) of the schematic
        int width = (Integer) getTag(schematicTag, "Width", IntTag.class).getValue();
        int height = (Integer) getTag(schematicTag, "Height", IntTag.class).getValue();
        int length = (Integer) getTag(schematicTag, "Length", IntTag.class).getValue();

        // extract the block ids and block data values
        // all of these arrays are index by the following key
        // index = x + z * width + y * width * length
        byte[] byteBlockIds = (byte[]) getTag(schematicTag, "Blocks", ByteArrayTag.class).getValue();
        byte[] blockData = (byte[]) getTag(schematicTag, "Data", ByteArrayTag.class).getValue();
        // Ultimately the block ids end up in a short array as ids can be up to 12 bits in size
        short[] blockIds = new short[byteBlockIds.length];
        // the nibble of the block id above 8 bits (if it exists) is stored in another byte array tag called 'AddBlocks'
        // the format of the array is that it is Blocks.length/2 size and each byte of the array is packed with two concurrent nibbles of additional id data
        if (tags.contains("AddBlocks"))
        {
            byte[] additionalIds = (byte[]) getTag(schematicTag, "AddBlocks", ByteArrayTag.class).getValue();
            for (int i = 0; i < byteBlockIds.length; i++)
            {
                if (i >> 1 > additionalIds.length)
                {
                    // if we're greater than the size of AddBlocks then just fill the rest with the regular data
                    blockIds[i] = byteBlockIds[i];
                }
                else
                {
                    // this cluster fuck of a line extracts the appropriate nibble from AddBlocks, depending on the current index and adds
                    // it to the regular byte
                    blockIds[i] = (short) ((((i & 1) == 0) ? (additionalIds[i >> 1] & 0x0F) << 8 : (additionalIds[i >> 1] & 0xF0) << 4) | byteBlockIds[i]);
                }
            }
        }
        else
        // if there is no additional block ids then just push the bytes into the short array
        {
            for (int i = 0; i < byteBlockIds.length; i++)
            {
                blockIds[i] = byteBlockIds[i];
            }
        }

        // Next Step: extract the tile entities
        // These are stored in a list of CompoundTags
        // where each CompoundTag is a map of the tile entities metadata
        // which most importantly contains three tags labeled x, y, and z which are the tiles position

        Map<BlockVector, CompoundTag> tileEntites = new HashMap<BlockVector, CompoundTag>();

        if (tags.contains("TileEntities"))
        {
            @SuppressWarnings("unchecked")
            List<CompoundTag> tiles = (List<CompoundTag>) schematicTag.getValue().get("TileEntities").getValue();
            for (CompoundTag tile: tiles)
            {
                int x = 0;
                int y = 0;
                int z = 0;

                // loop through the metadata and grab the x, y, and z values that we'll need to store the tile entity
                for (Map.Entry<String, Tag<?>> entry: tile.getValue().entrySet())
                {
                    if (entry.getKey().equals("x"))
                    {
                        if (entry.getValue() instanceof IntTag)
                        {
                            x = ((IntTag) entry.getValue()).getValue();
                        }
                    }
                    else if (entry.getKey().equals("y"))
                    {
                        if (entry.getValue() instanceof IntTag)
                        {
                            y = ((IntTag) entry.getValue()).getValue();
                        }
                    }
                    else if (entry.getKey().equals("z"))
                    {
                        if (entry.getValue() instanceof IntTag)
                        {
                            z = ((IntTag) entry.getValue()).getValue();
                        }
                    }
                }

                // and now we store the tile entity in our map, with the key as the tiles location in the world
                BlockVector vec = new BlockVector(x, y, z);
                tileEntites.put(vec, tile);
            }
        }

        // TODO: Load entities as well, they are stored in a similar structure to the Tile Entities
        // at the very least should load paintings and ItemFrames

        BlockRegion region = new BlockRegion(width, height, length, blockIds, blockData, tileEntites);

        // We'll support WorldEdit styled offsets for the sake of compatibility
        if (tags.contains("WEOffsetX"))
        {
            region.setxOffset((Integer) getTag(schematicTag, "WEOffsetX", IntTag.class).getValue());
        }
        if (tags.contains("WEOffsetY"))
        {
            region.setyOffset((Integer) getTag(schematicTag, "WEOffsetY", IntTag.class).getValue());
        }
        if (tags.contains("WEOffsetZ"))
        {
            region.setzOffset((Integer) getTag(schematicTag, "WEOffsetZ", IntTag.class).getValue());
        }

        // set the name in case we ever want to save it again after some editing perhaps
        region.setName(schematicFile.getName().replace(SCHEMATIC_EXTENSION, ""));
        region.setLastChanged(schematicFile.lastModified());
        return region;
    }

    @SuppressWarnings("rawtypes")
    private static Tag<?> getTag(CompoundTag tag, String key, Class<? extends Tag> expected) throws InvalidFormatException
    {
        CompoundMap value = tag.getValue();
        if (!value.containsKey(key))
        {
            throw new InvalidFormatException("Tag " + tag.getName() + " is missing a tag " + key + "!");
        }
        Tag<?> target = value.get(key);
        if (!expected.isInstance(target))
        {
            throw new InvalidFormatException("Tag " + key + " in " + tag.getName() + " is not of the expected type " + expected.getName() + "!");
        }
        return expected.cast(target);
    }

    public static BlockRegion load(String name) throws IOException, InvalidFormatException
    {
        return load(new File(VoxelSniper.getInstance().getDataFolder(), SCHEMATIC_FOLDER + name + SCHEMATIC_EXTENSION));
    }

    /**
     * Creates a new {@link BlockRegion} from the given world.
     * 
     * @param w
     * @return
     */
    @SuppressWarnings("deprecation")
    public static BlockRegion createFromWorld(World world, int[] first, int[] second, int[] reference)
    {
        // get the absolute min and max for each axis from the first and second selection points
        // clamp the y to 0..255 in the process
        // (I would use world.getMaxBuildHeight() but I am unsure of whether it returns 255 or 256 currently
        // and as there is no true support for differing world heights yet it can always be added in later)
        int xMin = Math.min(first[0], second[0]);
        int yMin = clamp(Math.min(first[1], second[1]), 0, 255);
        int zMin = Math.min(first[2], second[2]);
        int xMax = Math.max(first[0], second[0]);
        int yMax = clamp(Math.max(first[1], second[1]), 0, 255);
        int zMax = Math.max(first[2], second[2]);

        int xOffset = xMin - reference[0];
        int yOffset = yMin - reference[1];
        int zOffset = zMin - reference[2];

        int width = xMax - xMin + 1;
        int height = yMax - yMin + 1;
        int length = zMax - zMin + 1;

        short[] blockIds = new short[width * height * length];
        byte[] blockData = new byte[width * height * length];

        Map<BlockVector, CompoundTag> tiles = new HashMap<BlockVector, CompoundTag>();

        for (int x = xMin; x <= xMax; x++)
        {
            int x0 = x - xMin;
            for (int y = yMin; y <= yMax; y++)
            {
                int y0 = y - yMin;
                for (int z = zMin; z <= zMax; z++)
                {
                    int z0 = z - zMin;
                    int index = x0 + z0 * width + y0 * width * length;

                    Block b = world.getBlockAt(x, y, z);
                    // TODO these functions are deprecated
                    // In future once the block api thing is a little
                    // more defined we'll want to replace this with a local dictionary of IDs stored in the region
                    // and ultimately the schematic to allow for serialization
                    // (This is my current thought anyway on a possible implementation of named blocks without magic numbers attached,
                    // since we will still need some numerical identifier for a block for not-shit serialization. But rather
                    // than it be some magic number, it would be a locally defined number which when passed back through the
                    // dictionary will produce the proper name for the material)
                    //
                    // if a new 'official' mcedit schematic format is created without magic numbers that would obviously be preferred
                    blockIds[index] = (short) b.getTypeId();
                    blockData[index] = b.getData();
                    if (TileEntityOperations.isKnownTileEntity(b))
                    {
                        CompoundTag tile = TileEntityOperations.extract(b, xMin, yMin, zMin);
                        if (tile != null)
                        {
                            tiles.put(new BlockVector(x0, y0, z0), tile);
                        }
                    }
                }
            }
        }
        BlockRegion region = new BlockRegion(width, height, length, blockIds, blockData, tiles);
        region.setxOffset(xOffset);
        region.setyOffset(yOffset);
        region.setzOffset(zOffset);
        region.setName("Clipboard");
        return region;
    }

    /**
     * Returns i if it is within the bounds of min..max, otherwise returns the closest of min or max to i.
     * 
     * @param i
     * @param min
     * @param max
     * @return
     */
    private static int clamp(int i, int min, int max)
    {
        if (i < min)
        {
            return min;
        }
        if (i > max)
        {
            return max;
        }
        return i;
    }

    /**
     * Check for and convert if existing the legacy stencil format. Renames the stencil to name.vstencil_converted after conversion to
     * prevent multiple conversions.
     * 
     * @param stencilFile
     * @throws IOException
     */
    public static void checkStencil(String folder, String name) throws IOException
    {
        File old = new File(folder, name + ".vstencil");
        if (!old.exists())
        {
            return;
        }
        File schematic = new File(folder, name + SCHEMATIC_EXTENSION);

        /*
         * This reads in using the old stencil format which is as follows:
         * 
         * First read in three shorts, these are: in order, width (x-axis size), length (z-axis size), and height (y-axis size)
         * Next read three more shorts, these are an offset from the lowest point in the region to use as a reference point.
         * Next read a single integer, this is the number of groups that follows
         * Now for each of the groups:
         *     Read a single boolean, this is whether the following data is a single block or multiple blocks
         *     if the boolean is true (multiple blocks):
         *         Read three bytes
         *         The first is the number of blocks this group represents
         *         The second is the id of the blocks
         *         The third is the data value of these blocks
         *         Using these values for each block in the group:
         *             set the id of the current block
         *             increment the x-position, if it equals the width:
         *                 set x-position to zero
         *                 increment the z-position, if it equals the length:
         *                     set z-position to zero
         *                     increment the y-position
         *     else if the boolean is false (single block):
         *         Read two bytes, these are the id and data value of the current block
         *         set the id of the current block
         *         increment the x-position, if it equals the width:
         *             set x-position to zero
         *             increment the z-position, if it equals the length:
         *                 set z-position to zero
         *                 increment the y-position
         */

        final DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(old)));

        short width = in.readShort();
        short length = in.readShort();
        short height = in.readShort();

        short xOffset = in.readShort();
        short zOffset = in.readShort();
        short yOffset = in.readShort();

        short[] ids = new short[width * height * length];
        byte[] data = new byte[width * height * length];

        final int numRuns = in.readInt();

        int currX = 0;
        int currY = 0;
        int currZ = 0;

        short id;
        byte d;
        for (int i = 1; i < numRuns + 1; i++)
        {
            if (in.readBoolean())
            {
                final int numLoops = in.readByte() + 128;
                id = (short) (in.readByte() + 128);
                d = (byte) (in.readByte() + 128);
                for (int j = 0; j < numLoops; j++)
                {
                    ids[currX + currZ * width + currY * width * length] = id;
                    data[currX + currZ * width + currY * width * length] = d;

                    currX++;
                    if (currX == width)
                    {
                        currX = 0;
                        currZ++;
                        if (currZ == length)
                        {
                            currZ = 0;
                            currY++;
                        }
                    }
                }
            }
            else
            {
                id = (short) (in.readByte() + 128);
                d = (byte) (in.readByte() + 128);
                ids[currX + currZ * width + currY * width * length] = id;
                data[currX + currZ * width + currY * width * length] = d;
                currX++;
                if (currX == width)
                {
                    currX = 0;
                    currZ++;
                    if (currZ == length)
                    {
                        currZ = 0;
                        currY++;
                    }
                }
            }
        }
        in.close();

        // Create the BlockRegion
        BlockRegion region = new BlockRegion(width, height, length, ids, data);
        region.setxOffset(xOffset);
        region.setyOffset(yOffset);
        region.setzOffset(zOffset);
        region.setName(name);

        save(region, schematic); // Save the new schematic

        old.renameTo(new File(folder, name + ".vstencil_converted")); // Rename the old stencil to prevent it being converted multiple times
    }
}
