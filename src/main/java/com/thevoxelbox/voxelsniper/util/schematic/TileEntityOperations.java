package com.thevoxelbox.voxelsniper.util.schematic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.Jukebox;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.ShortTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.Tag;

import com.thevoxelbox.voxelsniper.util.InvalidFormatException;

/**
 * Utility for serializing and de-serializing TileEntity data to and from NBT data. Do note, the only alternative to this utility I have
 * been able to find is the calling of NMS code, namely net.minecraft.server.TileEntity.c(NBTTagCompound). But I think that would be a
 * dirtier implementation than this one when all is said and done.
 * 
 * @author Deamon
 */
public class TileEntityOperations
{
    
    private TileEntityOperations()
    {
    }
        
    /**
     * Extracts the Tile Entity from the given block and returns the metadata Map
     * 
     * @param b
     * @return the metadata map or null if the block does not have a Tile Entity
     */
    @SuppressWarnings("deprecation")
    public static CompoundTag extract(Block b, int xOffs, int yOffs, int zOffs)
    {
        if (!isKnownTileEntity(b))
        {
            return null;
        }
        Map<String, Tag<?>> tile = new HashMap<String, Tag<?>>();
        tile.put("x", new IntTag("x", b.getX() - xOffs));
        tile.put("y", new IntTag("y", b.getY() - yOffs));
        tile.put("z", new IntTag("z", b.getZ() - zOffs));
        if (b.getType() == Material.DROPPER && b.getState() instanceof Dropper)
        {
            Dropper dropper = (Dropper) b.getState();
            tile.put("id", new StringTag("id", "Dropper"));
            List<CompoundTag> items = new ArrayList<CompoundTag>();
            int slot = 0;
            for (ItemStack i: dropper.getInventory().getContents())
            {
                if (i == null)
                {
                    slot++;
                    continue;
                }
                Map<String, Tag<?>> item = new HashMap<String, Tag<?>>();
                item.put("id", new ShortTag("id", (short) i.getTypeId()));
                item.put("Damage", new ShortTag("Damage", i.getDurability()));
                item.put("Count", new ByteTag("Count", (byte) i.getAmount()));
                item.put("Slot", new ByteTag("Slot", (byte) slot));
                items.add(new CompoundTag("item", new CompoundMap(item)));
                slot++;
            }
            tile.put("Items", new ListTag<CompoundTag>("Items", CompoundTag.class, items));
        }
        else if (b.getType() == Material.DISPENSER && b.getState() instanceof Dispenser)
        {
            Dispenser dropper = (Dispenser) b.getState();
            tile.put("id", new StringTag("id", "Trap"));

            List<CompoundTag> items = new ArrayList<CompoundTag>();
            int slot = 0;
            for (ItemStack i: dropper.getInventory().getContents())
            {
                if (i == null)
                {
                    slot++;
                    continue;
                }
                Map<String, Tag<?>> item = new HashMap<String, Tag<?>>();
                item.put("id", new ShortTag("id", (short) i.getTypeId()));
                item.put("Damage", new ShortTag("Damage", i.getDurability()));
                item.put("Count", new ByteTag("Count", (byte) i.getAmount()));
                item.put("Slot", new ByteTag("Slot", (byte) slot));
                items.add(new CompoundTag("item", new CompoundMap(item)));
                slot++;
            }
            tile.put("Items", new ListTag<CompoundTag>("Items", CompoundTag.class, items));

        }
        else if (b.getType() == Material.SKULL && b.getState() instanceof Skull)
        {
            Skull skull = (Skull) b.getState();
            tile.put("id", new StringTag("id", "Skull"));
            tile.put("SkullType", new ByteTag("SkullType", (byte) getSkullType(skull.getSkullType())));
            tile.put("Rot", new ByteTag("Rot", (byte) getSkullRot(skull.getRotation())));
            if (skull.getOwner() == null)
            {
                tile.put("ExtraType", new StringTag("ExtraType", "Player"));
            }
            else
            {
                tile.put("ExtraType", new StringTag("ExtraType", skull.getOwner()));
            }

        }
        else if ((b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN) && b.getState() instanceof Sign)
        {
            Sign sign = (Sign) b.getState();
            tile.put("id", new StringTag("id", "Sign"));
            tile.put("Text1", new StringTag("Text1", sign.getLines()[0] == null ? "" : sign.getLines()[0]));
            tile.put("Text2", new StringTag("Text2", sign.getLines()[1] == null ? "" : sign.getLines()[1]));
            tile.put("Text3", new StringTag("Text3", sign.getLines()[2] == null ? "" : sign.getLines()[2]));
            tile.put("Text4", new StringTag("Text4", sign.getLines()[3] == null ? "" : sign.getLines()[3]));

        }
        else if (b.getType() == Material.JUKEBOX && b.getState() instanceof Jukebox)
        {
            Jukebox jukebox = (Jukebox) b.getState();
            tile.put("id", new StringTag("id", "RecordPlayer"));
            tile.put("Record", new IntTag("Record", jukebox.getPlaying().getId()));
        }
        else if (b.getType() == Material.NOTE_BLOCK && b.getState() instanceof NoteBlock)
        {
            NoteBlock note = (NoteBlock) b.getState();
            tile.put("id", new StringTag("id", "Music"));
            tile.put("note", new ByteTag("note", (byte) note.getNote().getOctave()));

        }
        else if (b.getType() == Material.BREWING_STAND && b.getState() instanceof BrewingStand)
        {
            BrewingStand stand = (BrewingStand) b.getState();
            tile.put("id", new StringTag("id", "Cauldron"));
            List<CompoundTag> items = new ArrayList<CompoundTag>();
            int slot = 0;
            for (ItemStack i: stand.getInventory().getContents())
            {
                if (i == null)
                {
                    slot++;
                    continue;
                }
                Map<String, Tag<?>> item = new HashMap<String, Tag<?>>();
                item.put("id", new ShortTag("id", (short) i.getTypeId()));
                item.put("Damage", new ShortTag("Damage", i.getDurability()));
                item.put("Count", new ByteTag("Count", (byte) i.getAmount()));
                item.put("Slot", new ByteTag("Slot", (byte) slot));
                items.add(new CompoundTag("item", new CompoundMap(item)));
                slot++;
            }
            tile.put("Items", new ListTag<CompoundTag>("Items", CompoundTag.class, items));
            tile.put("BrewTime", new IntTag("BrewTime", stand.getBrewingTime()));
        }
        else if (b.getType() == Material.CHEST && b.getState() instanceof Chest)
        {
            Chest chest = (Chest) b.getState();
            tile.put("id", new StringTag("id", "Chest"));
            List<CompoundTag> items = new ArrayList<CompoundTag>();
            int slot = 0;
            for (ItemStack i: chest.getInventory().getContents())
            {
                if (i == null)
                {
                    slot++;
                    continue;
                }
                Map<String, Tag<?>> item = new HashMap<String, Tag<?>>();
                item.put("id", new ShortTag("id", (short) i.getTypeId()));
                item.put("Damage", new ShortTag("Damage", i.getDurability()));
                item.put("Count", new ByteTag("Count", (byte) i.getAmount()));
                item.put("Slot", new ByteTag("Slot", (byte) slot));
                items.add(new CompoundTag("item", new CompoundMap(item)));
                slot++;
            }
            tile.put("Items", new ListTag<CompoundTag>("Items", CompoundTag.class, items));
        }
        else if (b.getType() == Material.COMMAND && b.getState() instanceof CommandBlock)
        {
            CommandBlock cmd = (CommandBlock) b.getState();
            tile.put("id", new StringTag("id", "Control"));
            tile.put("Command", new StringTag("Command", cmd.getCommand()));

        }
        else if (b.getType() == Material.FURNACE && b.getState() instanceof Furnace)
        {
            Furnace furnace = (Furnace) b.getState();
            tile.put("id", new StringTag("id", "Furnace"));
            List<CompoundTag> items = new ArrayList<CompoundTag>();
            int slot = 0;
            for (ItemStack i: furnace.getInventory().getContents())
            {
                if (i == null)
                {
                    slot++;
                    continue;
                }
                Map<String, Tag<?>> item = new HashMap<String, Tag<?>>();
                item.put("id", new ShortTag("id", (short) i.getTypeId()));
                item.put("Damage", new ShortTag("Damage", i.getDurability()));
                item.put("Count", new ByteTag("Count", (byte) i.getAmount()));
                item.put("Slot", new ByteTag("Slot", (byte) slot));
                items.add(new CompoundTag("item", new CompoundMap(item)));
                slot++;
            }
            tile.put("Items", new ListTag<CompoundTag>("Items", CompoundTag.class, items));
            tile.put("BurnTime", new ShortTag("BurnTime", furnace.getBurnTime()));
            tile.put("CookTime", new ShortTag("CookTime", furnace.getCookTime()));
        }
        else if (b.getType() == Material.HOPPER && b.getState() instanceof Hopper)
        {
            Hopper hopper = (Hopper) b.getState();
            tile.put("id", new StringTag("id", "Hopper"));
            List<CompoundTag> items = new ArrayList<CompoundTag>();
            int slot = 0;
            for (ItemStack i: hopper.getInventory().getContents())
            {
                if (i == null)
                {
                    slot++;
                    continue;
                }
                Map<String, Tag<?>> item = new HashMap<String, Tag<?>>();
                item.put("id", new ShortTag("id", (short) i.getTypeId()));
                item.put("Damage", new ShortTag("Damage", i.getDurability()));
                item.put("Count", new ByteTag("Count", (byte) i.getAmount()));
                item.put("Slot", new ByteTag("Slot", (byte) slot));
                items.add(new CompoundTag("item", new CompoundMap(item)));
                slot++;
            }
            tile.put("Items", new ListTag<CompoundTag>("Items", CompoundTag.class, items));
        }
        else if (b.getType() == Material.MOB_SPAWNER && b.getState() instanceof CreatureSpawner)
        {
            CreatureSpawner spawner = (CreatureSpawner) b.getState();
            tile.put("id", new StringTag("id", "MobSpawner"));
            tile.put("EntityId", new StringTag("EntityId", spawner.getCreatureTypeId()));
            tile.put("Delay", new ShortTag("Delay", (short) spawner.getDelay()));

        }
        return new CompoundTag("TileEntity", new CompoundMap(tile));
    }

    /**
     * Utility for placing new Tile Entities into the world based on NBT metadata.
     * 
     * @param world
     * @param x
     * @param y
     * @param z
     * @param nbtValues
     * @param blockID
     * @param blockData
     * @throws InvalidFormatException
     */
    @SuppressWarnings("deprecation")
    public static void createTileEntity(World world, int x, int y, int z, CompoundTag nbtValues, short blockID, byte blockData) throws InvalidFormatException
    {
        Map<String, Tag<?>> tags = nbtValues.getValue();
        String entityName = "";
        if (tags.keySet().contains("id"))
        {
            entityName = (String) tags.get("id").getValue();
            if (entityName.equals("Cauldron")) // Actually a brewing stand...
            {
                // Start by setting the block to the correct blockID and data value
                Block block = world.getBlockAt(x, y, z);
                block.setTypeIdAndData(blockID, blockData, false);
                // fetch the TileEntityClass
                BrewingStand brewingStand = (BrewingStand) block.getState();
                brewingStand.update();
                // brewing stands have inventories so load the list of items to place into it
                @SuppressWarnings("unchecked")
                List<CompoundTag> itemsList = (List<CompoundTag>) tags.get("Items").getValue();
                for (CompoundTag item: itemsList)
                {
                    // fetch the sub-map of values about the this item
                    short type = (Short) item.getValue().get("id").getValue();
                    short damage = (Short) item.getValue().get("Damage").getValue();
                    byte amount = (Byte) item.getValue().get("Count").getValue();
                    // create the item
                    ItemStack itemObject = new ItemStack(type, amount, damage);
                    byte slot = (Byte) item.getValue().get("Slot").getValue();
                    // place it into the inventory
                    brewingStand.getInventory().setItem(slot, itemObject);
                }
                short time = (Short) tags.get("BrewTime").getValue();
                brewingStand.setBrewingTime(time);
                // Update this Tile Entity so changes are recorded and pushed to the client
                brewingStand.update();
            }
            if (entityName.equals("Chest"))
            {
                Block block = world.getBlockAt(x, y, z);
                block.setTypeIdAndData(blockID, blockData, false);
                Chest chest = (Chest) block.getState();
                @SuppressWarnings("unchecked")
                List<CompoundTag> itemsList = (List<CompoundTag>) tags.get("Items").getValue();
                for (CompoundTag item: itemsList)
                {
                    short type = (Short) item.getValue().get("id").getValue();
                    short damage = (Short) item.getValue().get("Damage").getValue();
                    byte amount = (Byte) item.getValue().get("Count").getValue();
                    ItemStack itemObject = new ItemStack(type, amount, damage);
                    byte slot = (Byte) item.getValue().get("Slot").getValue();
                    chest.getInventory().setItem(slot, itemObject);
                }
                chest.update();
            }
            if (entityName.equals("Control")) // command blocks
            {
                Block block = world.getBlockAt(x, y, z);
                block.setTypeIdAndData(blockID, blockData, false);
                CommandBlock commandBlock = (CommandBlock) block.getState();
                commandBlock.setCommand((String) tags.get("Command").getValue());
                commandBlock.update();
            }
            if (entityName.equals("Furnace"))
            {
                Block block = world.getBlockAt(x, y, z);
                block.setTypeIdAndData(blockID, blockData, false);
                Furnace furnace = (Furnace) block.getState();
                @SuppressWarnings("unchecked")
                List<CompoundTag> itemsList = (List<CompoundTag>) tags.get("Items").getValue();
                for (CompoundTag item: itemsList)
                {
                    short type = (Short) item.getValue().get("id").getValue();
                    short damage = (Short) item.getValue().get("Damage").getValue();
                    byte amount = (Byte) item.getValue().get("Count").getValue();
                    ItemStack itemObject = new ItemStack(type, amount, damage);
                    byte slot = (Byte) item.getValue().get("Slot").getValue();
                    furnace.getInventory().setItem(slot, itemObject);
                }
                short time = (Short) tags.get("BurnTime").getValue();
                short time0 = (Short) tags.get("CookTime").getValue();
                furnace.setBurnTime(time);
                furnace.setCookTime(time0);
                furnace.update();
            }
            if (entityName.equals("Hopper"))
            {
                Block block = world.getBlockAt(x, y, z);
                block.setTypeIdAndData(blockID, blockData, false);
                Hopper hopper = (Hopper) block.getState();
                @SuppressWarnings("unchecked")
                List<CompoundTag> itemsList = (List<CompoundTag>) tags.get("Items").getValue();
                for (CompoundTag item: itemsList)
                {
                    short type = (Short) item.getValue().get("id").getValue();
                    short damage = (Short) item.getValue().get("Damage").getValue();
                    byte amount = (Byte) item.getValue().get("Count").getValue();
                    ItemStack itemObject = new ItemStack(type, amount, damage);
                    byte slot = (Byte) item.getValue().get("Slot").getValue();
                    hopper.getInventory().setItem(slot, itemObject);
                }
                hopper.update();
            }
            if (entityName.equals("MobSpawner"))
            {
                Block block = world.getBlockAt(x, y, z);
                block.setTypeIdAndData(blockID, blockData, false);
                CreatureSpawner spawner = (CreatureSpawner) block.getState();
                spawner.setCreatureTypeByName((String) tags.get("EntityId").getValue());
                spawner.setDelay((Integer) tags.get("Delay").getValue());
                spawner.update();
            }
            if (entityName.equals("Music")) // note blocks
            {
                Block block = world.getBlockAt(x, y, z);
                block.setTypeIdAndData(blockID, blockData, false);
                NoteBlock noteBlock = (NoteBlock) block.getState();
                noteBlock.setNote(new Note((Integer) tags.get("note").getValue()));
                noteBlock.update();
            }
            if (entityName.equals("RecordPlayer"))
            {
                Block block = world.getBlockAt(x, y, z);
                block.setTypeIdAndData(blockID, blockData, false);
                Jukebox jukebox = (Jukebox) block.getState();
                jukebox.setPlaying(Material.getMaterial((Integer) tags.get("Record").getValue()));
                jukebox.update();
            }
            if (entityName.equals("Sign"))
            {
                Block block = world.getBlockAt(x, y, z);
                block.setTypeIdAndData(blockID, blockData, false);
                Sign sign = (Sign) block.getState();
                sign.setLine(0, (String) tags.get("Text1").getValue());
                sign.setLine(1, (String) tags.get("Text2").getValue());
                sign.setLine(2, (String) tags.get("Text3").getValue());
                sign.setLine(3, (String) tags.get("Text4").getValue());
                sign.update();
            }
            if (entityName.equals("Skull"))
            {
                Block block = world.getBlockAt(x, y, z);
                block.setTypeIdAndData(blockID, blockData, false);
                Skull skull = (Skull) block.getState();
                skull.setSkullType(getSkullType((Byte) tags.get("SkullType").getValue()));
                skull.setRotation(getSkullRot((Byte) tags.get("Rot").getValue()));
                String owner = (String) tags.get("ExtraType").getValue();
                if (owner.equals(""))
                {
                    owner = "Player";
                }
                skull.setOwner(owner);
                skull.update();
            }
            if (entityName.equals("Trap")) // Dispensers
            {
                Block block = world.getBlockAt(x, y, z);
                block.setTypeIdAndData(blockID, blockData, false);
                Dispenser dispenser = (Dispenser) block.getState();
                dispenser.update(); // Update before data
                @SuppressWarnings("unchecked")
                List<CompoundTag> itemsList = (List<CompoundTag>) tags.get("Items").getValue();
                for (CompoundTag item: itemsList)
                {
                    short type = (Short) item.getValue().get("id").getValue();
                    short damage = (Short) item.getValue().get("Damage").getValue();
                    byte amount = (Byte) item.getValue().get("Count").getValue();
                    ItemStack itemObject = new ItemStack(type, amount, damage);
                    byte slot = (Byte) item.getValue().get("Slot").getValue();
                    dispenser.getInventory().setItem(slot, itemObject);
                }
                dispenser.update();
            }
            if (entityName.equals("Dropper"))
            {
                Block block = world.getBlockAt(x, y, z);
                block.setTypeIdAndData(blockID, blockData, false);
                Dropper dropper = (Dropper) block.getState();
                @SuppressWarnings("unchecked")
                List<CompoundTag> itemsList = (List<CompoundTag>) tags.get("Items").getValue();
                for (CompoundTag item: itemsList)
                {
                    short type = (Short) item.getValue().get("id").getValue();
                    short damage = (Short) item.getValue().get("Damage").getValue();
                    byte amount = (Byte) item.getValue().get("Count").getValue();
                    ItemStack itemObject = new ItemStack(type, amount, damage);
                    byte slot = (Byte) item.getValue().get("Slot").getValue();
                    dropper.getInventory().setItem(slot, itemObject);
                }
                dropper.update();
            }
        }
    }

    /**
     * Returns whether the given block id is known to have a Tile Entity TODO: post non-magic number expansion: either remove or reference
     * local dictionary (or other currently pending structure for supporting the removal of magic numbers).
     * 
     * @param id
     * @return
     */
    @SuppressWarnings("deprecation")
    public static boolean isKnownTileEntity(int id)
    {
        for (Material m: KNOWN_TILE_ENTITIES)
        {
            if (m.getId() == id)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the given block is known to have a Tile Entity.
     * 
     * @param b
     * @return
     */
    public static boolean isKnownTileEntity(Block b)
    {
        for (Material m: KNOWN_TILE_ENTITIES)
        {
            if (m.equals(b.getType()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * A list of all known Tile Entities. (known implies that this utility knows how to load and place it into the world)
     */
    private static final Material[] KNOWN_TILE_ENTITIES = new Material[] { Material.HOPPER, Material.CHEST, Material.COMMAND, Material.NOTE_BLOCK, Material.JUKEBOX, Material.DISPENSER, Material.SIGN, Material.SIGN_POST, Material.WALL_SIGN, Material.DROPPER, Material.SKULL, Material.FURNACE, Material.MOB_SPAWNER, Material.CAULDRON };

    /**
     * Helper function for translating a numeric value into a BlockFace enum value
     * 
     * @param rotationValue
     * @return
     */
    private static BlockFace getSkullRot(Byte rotationValue)
    {
        if (rotationValue == 0)
        {
            return BlockFace.SOUTH;
        }
        else if (rotationValue == 1)
        {
            return BlockFace.SOUTH_SOUTH_WEST;
        }
        else if (rotationValue == 2)
        {
            return BlockFace.SOUTH_WEST;
        }
        else if (rotationValue == 3)
        {
            return BlockFace.WEST_SOUTH_WEST;
        }
        else if (rotationValue == 4)
        {
            return BlockFace.WEST;
        }
        else if (rotationValue == 5)
        {
            return BlockFace.WEST_NORTH_WEST;
        }
        else if (rotationValue == 6)
        {
            return BlockFace.NORTH_WEST;
        }
        else if (rotationValue == 7)
        {
            return BlockFace.NORTH_NORTH_WEST;
        }
        else if (rotationValue == 8)
        {
            return BlockFace.NORTH;
        }
        else if (rotationValue == 9)
        {
            return BlockFace.NORTH_NORTH_EAST;
        }
        else if (rotationValue == 10)
        {
            return BlockFace.NORTH_EAST;
        }
        else if (rotationValue == 11)
        {
            return BlockFace.EAST_NORTH_EAST;
        }
        else if (rotationValue == 12)
        {
            return BlockFace.EAST;
        }
        else if (rotationValue == 13)
        {
            return BlockFace.EAST_SOUTH_EAST;
        }
        else if (rotationValue == 14)
        {
            return BlockFace.SOUTH_EAST;
        }
        else if (rotationValue == 15)
        {
            return BlockFace.SOUTH_SOUTH_EAST;
        }
        return BlockFace.SOUTH; // Arbitrarily default to south...
    }

    private static int getSkullRot(BlockFace rotationValue)
    {
        if (rotationValue == BlockFace.SOUTH)
        {
            return 0;
        }
        else if (rotationValue == BlockFace.SOUTH_SOUTH_WEST)
        {
            return 1;
        }
        else if (rotationValue == BlockFace.SOUTH_WEST)
        {
            return 2;
        }
        else if (rotationValue == BlockFace.WEST_SOUTH_WEST)
        {
            return 3;
        }
        else if (rotationValue == BlockFace.WEST)
        {
            return 4;
        }
        else if (rotationValue == BlockFace.WEST_NORTH_WEST)
        {
            return 5;
        }
        else if (rotationValue == BlockFace.NORTH_WEST)
        {
            return 6;
        }
        else if (rotationValue == BlockFace.NORTH_NORTH_WEST)
        {
            return 7;
        }
        else if (rotationValue == BlockFace.NORTH)
        {
            return 8;
        }
        else if (rotationValue == BlockFace.NORTH_NORTH_EAST)
        {
            return 9;
        }
        else if (rotationValue == BlockFace.NORTH_EAST)
        {
            return 10;
        }
        else if (rotationValue == BlockFace.EAST_NORTH_EAST)
        {
            return 11;
        }
        else if (rotationValue == BlockFace.EAST)
        {
            return 12;
        }
        else if (rotationValue == BlockFace.EAST_SOUTH_EAST)
        {
            return 13;
        }
        else if (rotationValue == BlockFace.SOUTH_EAST)
        {
            return 14;
        }
        else if (rotationValue == BlockFace.SOUTH_SOUTH_EAST)
        {
            return 15;
        }
        return 0; // Arbitrarily default to south...
    }

    /**
     * Helper function for converting a numeric id into the correct SkullType enum value.
     * 
     * @param id
     * @return
     */
    private static SkullType getSkullType(int id)
    {
        if (id == 0)
        {
            return SkullType.SKELETON;
        }
        else if (id == 1)
        {
            return SkullType.WITHER;
        }
        else if (id == 2)
        {
            return SkullType.ZOMBIE;
        }
        else if (id == 3)
        {
            return SkullType.PLAYER;
        }
        else if (id == 4)
        {
            return SkullType.CREEPER;
        }
        else
        {
            return SkullType.PLAYER; // Default to player...for no particular reason
        }
    }

    private static int getSkullType(SkullType sk)
    {
        if (sk == SkullType.SKELETON)
        {
            return 0;
        }
        else if (sk == SkullType.WITHER)
        {
            return 1;
        }
        else if (sk == SkullType.ZOMBIE)
        {
            return 2;
        }
        else if (sk == SkullType.PLAYER)
        {
            return 3;
        }
        else if (sk == SkullType.CREEPER)
        {
            return 4;
        }
        else
        {
            return 3; // Default to player...for no particular reason
        }
    }
}
