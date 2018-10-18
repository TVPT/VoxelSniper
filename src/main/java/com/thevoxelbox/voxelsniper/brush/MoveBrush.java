package com.thevoxelbox.voxelsniper.brush;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * Moves a selection blockPositionY a certain amount.
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Move_Brush
 *
 * @author MikeMatrix
 */
public class MoveBrush extends Brush
{
    /**
     * Breakable Blocks to determine if no-physics should be used.
     */
    private static final Set<Material> BREAKABLE_MATERIALS = new TreeSet<Material>();

    static
    {
        MoveBrush.BREAKABLE_MATERIALS.add(Material.OAK_SAPLING);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.JUNGLE_SAPLING);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.DARK_OAK_SAPLING);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.BIRCH_SAPLING);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.ACACIA_SAPLING);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.SPRUCE_SAPLING);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.BLACK_BED);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.BLUE_BED);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.BROWN_BED);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.CYAN_BED);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.GRAY_BED);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.GREEN_BED);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.LIGHT_BLUE_BED);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.LIGHT_GRAY_BED);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.LIME_BED);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.MAGENTA_BED);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.ORANGE_BED);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.PINK_BED);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.PURPLE_BED);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.RED_BED);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.WHITE_BED);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.YELLOW_BED);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.POWERED_RAIL);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.DETECTOR_RAIL);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.TALL_GRASS);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.DEAD_BUSH);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.PISTON_HEAD);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.DANDELION);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.POPPY);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.BROWN_MUSHROOM);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.RED_MUSHROOM);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.TORCH);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.FIRE);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.WHEAT);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.SIGN);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.DARK_OAK_DOOR);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.ACACIA_DOOR);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.BIRCH_DOOR);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.IRON_DOOR);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.JUNGLE_DOOR);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.OAK_DOOR);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.SPRUCE_DOOR);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.LADDER);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.RAIL);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.ACTIVATOR_RAIL);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.WALL_SIGN);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.LEVER);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.ACACIA_PRESSURE_PLATE);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.BIRCH_PRESSURE_PLATE);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.JUNGLE_PRESSURE_PLATE);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.OAK_PRESSURE_PLATE);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.SPRUCE_PRESSURE_PLATE);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.STONE_PRESSURE_PLATE);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.DARK_OAK_PRESSURE_PLATE);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.REDSTONE_TORCH);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.STONE_BUTTON);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.SNOW);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.CACTUS);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.SUGAR_CANE);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.CAKE);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.REPEATER);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.ACACIA_TRAPDOOR);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.BIRCH_TRAPDOOR);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.DARK_OAK_TRAPDOOR);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.IRON_TRAPDOOR);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.JUNGLE_TRAPDOOR);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.OAK_TRAPDOOR);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.SPRUCE_TRAPDOOR);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.PUMPKIN_STEM);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.MELON_STEM);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.VINE);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.LILY_PAD);
        MoveBrush.BREAKABLE_MATERIALS.add(Material.NETHER_WART);
    }

    /**
     * Saved direction.
     */
    private final int[] moveDirections = {0, 0, 0};
    /**
     * Saved selection.
     */
    private Selection selection = null;

    /**
     *
     */
    public MoveBrush()
    {
        this.setName("Move");
    }

    /**
     * Moves the given selection blockPositionY the amount given in direction and saves an undo for the player.
     *
     * @param v
     * @param selection
     * @param direction
     */
    private void moveSelection(final SnipeData v, final Selection selection, final int[] direction)
    {
        if (selection.getBlockStates().size() > 0)
        {
            final World world = selection.getBlockStates().get(0).getWorld();

            final Undo undo = new Undo();
            final HashSet<Block> undoSet = new HashSet<Block>();

            final Selection newSelection = new Selection();
            final Location movedLocation1 = selection.getLocation1();
            movedLocation1.add(direction[0], direction[1], direction[2]);
            final Location movedLocation2 = selection.getLocation2();
            movedLocation2.add(direction[0], direction[1], direction[2]);
            newSelection.setLocation1(movedLocation1);
            newSelection.setLocation2(movedLocation2);
            try
            {
                newSelection.calculateRegion();
            }
            catch (final Exception exception)
            {
                v.getVoxelMessage().brushMessage("The new Selection has more blocks than the original selection. This should never happen!");
            }

            for (final BlockState blockState : selection.getBlockStates())
            {
                undoSet.add(blockState.getBlock());
            }
            for (final BlockState blockState : newSelection.getBlockStates())
            {
                undoSet.add(blockState.getBlock());
            }

            for (final Block block : undoSet)
            {
                undo.put(block);
            }
            v.owner().storeUndo(undo);

            for (final BlockState blockState : selection.getBlockStates())
            {
                blockState.getBlock().setType(Material.AIR);
            }
            for (final BlockState blockState : selection.getBlockStates())
            {
                final Block affectedBlock = world.getBlockAt(blockState.getX() + direction[0], blockState.getY() + direction[1], blockState.getZ() + direction[2]);
                affectedBlock.setBlockData(blockState.getBlockData(), !MoveBrush.BREAKABLE_MATERIALS.contains(blockState.getType()));
            }
        }
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        if (this.selection == null)
        {
            this.selection = new Selection();
        }
        this.selection.setLocation1(this.getTargetBlock().getLocation());
        v.getVoxelMessage().brushMessage("Point 1 set.");

        try
        {
            if (this.selection.calculateRegion())
            {
                this.moveSelection(v, this.selection, this.moveDirections);
                this.selection = null;
            }
        }
        catch (final Exception exception)
        {
            v.sendMessage(exception.getMessage());
        }
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        if (this.selection == null)
        {
            this.selection = new Selection();
        }
        this.selection.setLocation2(this.getTargetBlock().getLocation());
        v.getVoxelMessage().brushMessage("Point 2 set.");

        try
        {
            if (this.selection.calculateRegion())
            {
                this.moveSelection(v, this.selection, this.moveDirections);
                this.selection = null;
            }
        }
        catch (final Exception exception)
        {
            v.sendMessage(exception.getMessage());
        }
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.custom(ChatColor.BLUE + "Move selection blockPositionY " + ChatColor.GOLD + "x:" + this.moveDirections[0] + " y:" + this.moveDirections[1] + " z:" + this.moveDirections[2]);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        for (int i = 1; i < par.length; i++)
        {
            if (par[i].equalsIgnoreCase("info"))
            {
                v.getVoxelMessage().custom(ChatColor.GOLD + this.getName() + " Parameters:");
                v.getVoxelMessage().custom(ChatColor.AQUA + "/b mv x[int] -- set the x direction (positive => east)");
                v.getVoxelMessage().custom(ChatColor.AQUA + "/b mv y[int] -- set the y direction (positive => up)");
                v.getVoxelMessage().custom(ChatColor.AQUA + "/b mv z[int] -- set the z direction (positive => south)");
                v.getVoxelMessage().custom(ChatColor.AQUA + "/b mv reset -- reset the brush (x:0 y:0 z:0)");
                v.getVoxelMessage().custom(ChatColor.AQUA + "Use arrow and gunpowder to define two points.");
            }
            if (par[i].equalsIgnoreCase("reset"))
            {
                this.moveDirections[0] = 0;
                this.moveDirections[1] = 0;
                this.moveDirections[2] = 0;
                v.getVoxelMessage().custom(ChatColor.AQUA + "X direction set to: " + this.moveDirections[0]);
                v.getVoxelMessage().custom(ChatColor.AQUA + "Y direction set to: " + this.moveDirections[1]);
                v.getVoxelMessage().custom(ChatColor.AQUA + "Z direction set to: " + this.moveDirections[2]);
            }
            if (par[i].toLowerCase().startsWith("x"))
            {
                this.moveDirections[0] = Integer.valueOf(par[i].substring(1));
                v.getVoxelMessage().custom(ChatColor.AQUA + "X direction set to: " + this.moveDirections[0]);
            }
            else if (par[i].toLowerCase().startsWith("y"))
            {
                this.moveDirections[1] = Integer.valueOf(par[i].substring(1));
                v.getVoxelMessage().custom(ChatColor.AQUA + "Y direction set to: " + this.moveDirections[1]);
            }
            else if (par[i].toLowerCase().startsWith("z"))
            {
                this.moveDirections[2] = Integer.valueOf(par[i].substring(1));
                v.getVoxelMessage().custom(ChatColor.AQUA + "Z direction set to: " + this.moveDirections[2]);
            }
        }
    }

    /**
     * Selection Helper class.
     *
     * @author MikeMatrix
     */
    private class Selection
    {
        /**
         * Maximum amount of Blocks allowed blockPositionY the Selection.
         */
        private static final int MAX_BLOCK_COUNT = 5000000;
        /**
         * Calculated BlockStates of the selection.
         */
        private final ArrayList<BlockState> blockStates = new ArrayList<BlockState>();
        /**
         *
         */
        private Location location1 = null;
        /**
         *
         */
        private Location location2 = null;

        /**
         * Calculates region, then saves all Blocks as BlockState.
         *
         * @return boolean success.
         * @throws Exception Message to be sent to the player.
         */
        public boolean calculateRegion() throws Exception
        {
            if (this.location1 != null && this.location2 != null)
            {
                if (this.location1.getWorld().equals(this.location2.getWorld()))
                {
                    final int lowX = ((this.location1.getBlockX() <= this.location2.getBlockX()) ? this.location1.getBlockX() : this.location2.getBlockX());
                    final int lowY = (this.location1.getBlockY() <= this.location2.getBlockY()) ? this.location1.getBlockY() : this.location2.getBlockY();
                    final int lowZ = (this.location1.getBlockZ() <= this.location2.getBlockZ()) ? this.location1.getBlockZ() : this.location2.getBlockZ();
                    final int highX = (this.location1.getBlockX() >= this.location2.getBlockX()) ? this.location1.getBlockX() : this.location2.getBlockX();
                    final int highY = (this.location1.getBlockY() >= this.location2.getBlockY()) ? this.location1.getBlockY() : this.location2.getBlockY();
                    final int highZ = (this.location1.getBlockZ() >= this.location2.getBlockZ()) ? this.location1.getBlockZ() : this.location2.getBlockZ();
                    if (Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY) > Selection.MAX_BLOCK_COUNT)
                    {
                        throw new Exception(ChatColor.RED + "Selection size above hardcoded limit, please use a smaller selection.");
                    }
                    final World world = this.location1.getWorld();
                    for (int y = lowY; y <= highY; y++)
                    {
                        for (int x = lowX; x <= highX; x++)
                        {
                            for (int z = lowZ; z <= highZ; z++)
                            {
                                this.blockStates.add(world.getBlockAt(x, y, z).getState());
                            }
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        /**
         * @return ArrayList<BlockState> calculated BlockStates of defined region.
         */
        public ArrayList<BlockState> getBlockStates()
        {
            return this.blockStates;
        }

        /**
         * @return Location
         */
        public Location getLocation1()
        {
            return this.location1;
        }

        /**
         * @param location1
         */
        public void setLocation1(final Location location1)
        {
            this.location1 = location1;
        }

        /**
         * @return Location
         */
        public Location getLocation2()
        {
            return this.location2;
        }

        /**
         * @param location2
         */
        public void setLocation2(final Location location2)
        {
            this.location2 = location2;
        }
    }

    @Override
    public String getPermissionNode()
    {
        return "voxelsniper.brush.move";
    }
}
