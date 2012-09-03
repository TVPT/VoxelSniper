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

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * Moves a selection blockPositionY a certain amount.
 * 
 * @author MikeMatrix
 * 
 */
public class Move extends Brush {
    /**
     * Selection Helper class.
     * 
     * @author MikeMatrix
     * 
     */
    private class Selection {
        /**
         * Maximum amount of Blocks allowed blockPositionY the Selection.
         */
        private static final int MAX_BLOCK_COUNT = 5000000;
        /**
         * 
         */
        private Location location1 = null;
        /**
         * 
         */
        private Location location2 = null;

        /**
         * Calculated BlockStates of the selection.
         */
        private final ArrayList<BlockState> blockStates = new ArrayList<BlockState>();

        /**
         * Calculates region, then saves all Blocks as BlockState.
         * 
         * @return boolean success.
         * @throws Exception
         *             Message to be sent to the player.
         */
        public boolean calculateRegion() throws Exception {
            if (this.location1 != null && this.location2 != null) {
                if (this.location1.getWorld().equals(this.location2.getWorld())) {
                    final int _lowx = ((this.location1.getBlockX() <= this.location2.getBlockX()) ? this.location1.getBlockX() : this.location2.getBlockX());
                    final int _lowy = (this.location1.getBlockY() <= this.location2.getBlockY()) ? this.location1.getBlockY() : this.location2.getBlockY();
                    final int _lowz = (this.location1.getBlockZ() <= this.location2.getBlockZ()) ? this.location1.getBlockZ() : this.location2.getBlockZ();
                    final int _highx = (this.location1.getBlockX() >= this.location2.getBlockX()) ? this.location1.getBlockX() : this.location2.getBlockX();
                    final int _highy = (this.location1.getBlockY() >= this.location2.getBlockY()) ? this.location1.getBlockY() : this.location2.getBlockY();
                    final int _highz = (this.location1.getBlockZ() >= this.location2.getBlockZ()) ? this.location1.getBlockZ() : this.location2.getBlockZ();
                    if (Math.abs(_highx - _lowx) * Math.abs(_highz - _lowz) * Math.abs(_highy - _lowy) > Selection.MAX_BLOCK_COUNT) {
                        throw new Exception(ChatColor.RED + "Selection size above hardcoded limit, please use a smaller selection.");
                    }
                    final World _world = this.location1.getWorld();
                    for (int _y = _lowy; _y <= _highy; _y++) {
                        for (int _x = _lowx; _x <= _highx; _x++) {
                            for (int _z = _lowz; _z <= _highz; _z++) {
                                this.blockStates.add(_world.getBlockAt(_x, _y, _z).getState());
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
        public ArrayList<BlockState> getBlockStates() {
            return this.blockStates;
        }

        /**
         * @return Location
         */
        public Location getLocation1() {
            return this.location1;
        }

        /**
         * @return Location
         */
        public Location getLocation2() {
            return this.location2;
        }

        /**
         * 
         * @param location1
         */
        public void setLocation1(final Location location1) {
            this.location1 = location1;
        }

        /**
         * @param location2
         */
        public void setLocation2(final Location location2) {
            this.location2 = location2;
        }
    }

    /**
     * Saved selection.
     */
    private Selection selection = null;
    /**
     * Saved direction.
     */
    private final int[] moveDirections = { 0, 0, 0 };
    /**
     * Breakable Blocks to determine if no-physics should be used.
     */
    private static final Set<Material> breakableMaterials = new TreeSet<Material>();

    static {
        Move.breakableMaterials.add(Material.SAPLING);
        Move.breakableMaterials.add(Material.BED_BLOCK);
        Move.breakableMaterials.add(Material.POWERED_RAIL);
        Move.breakableMaterials.add(Material.DETECTOR_RAIL);
        Move.breakableMaterials.add(Material.LONG_GRASS);
        Move.breakableMaterials.add(Material.DEAD_BUSH);
        Move.breakableMaterials.add(Material.PISTON_EXTENSION);
        Move.breakableMaterials.add(Material.YELLOW_FLOWER);
        Move.breakableMaterials.add(Material.RED_ROSE);
        Move.breakableMaterials.add(Material.BROWN_MUSHROOM);
        Move.breakableMaterials.add(Material.RED_MUSHROOM);
        Move.breakableMaterials.add(Material.TORCH);
        Move.breakableMaterials.add(Material.FIRE);
        Move.breakableMaterials.add(Material.CROPS);
        Move.breakableMaterials.add(Material.SIGN_POST);
        Move.breakableMaterials.add(Material.WOODEN_DOOR);
        Move.breakableMaterials.add(Material.LADDER);
        Move.breakableMaterials.add(Material.RAILS);
        Move.breakableMaterials.add(Material.WALL_SIGN);
        Move.breakableMaterials.add(Material.LEVER);
        Move.breakableMaterials.add(Material.STONE_PLATE);
        Move.breakableMaterials.add(Material.IRON_DOOR_BLOCK);
        Move.breakableMaterials.add(Material.WOOD_PLATE);
        Move.breakableMaterials.add(Material.REDSTONE_TORCH_OFF);
        Move.breakableMaterials.add(Material.REDSTONE_TORCH_ON);
        Move.breakableMaterials.add(Material.STONE_BUTTON);
        Move.breakableMaterials.add(Material.SNOW);
        Move.breakableMaterials.add(Material.CACTUS);
        Move.breakableMaterials.add(Material.SUGAR_CANE_BLOCK);
        Move.breakableMaterials.add(Material.CAKE_BLOCK);
        Move.breakableMaterials.add(Material.DIODE_BLOCK_OFF);
        Move.breakableMaterials.add(Material.DIODE_BLOCK_ON);
        Move.breakableMaterials.add(Material.TRAP_DOOR);
        Move.breakableMaterials.add(Material.PUMPKIN_STEM);
        Move.breakableMaterials.add(Material.MELON_STEM);
        Move.breakableMaterials.add(Material.VINE);
        Move.breakableMaterials.add(Material.WATER_LILY);
        Move.breakableMaterials.add(Material.NETHER_WARTS);
    }

    private static int timesUsed = 0;

    /**
     * 
     */
    public Move() {
        this.setName("Move");
    }


    /**
     * Moves the given selection blockPositionY the amount given in direction and saves an undo for the player.
     * 
     * @param v
     * @param selection
     * @param direction
     */
    private void moveSelection(final SnipeData v, final Selection selection, final int[] direction) {
        if (selection.getBlockStates().size() > 0) {
            final World _world = selection.getBlockStates().get(0).getWorld();

            final Undo _vundo = new Undo(_world.getName());
            final HashSet<Block> _undoSet = new HashSet<Block>();

            final Selection _newSelection = new Selection();
            final Location _movedLocation1 = selection.getLocation1();
            _movedLocation1.add(direction[0], direction[1], direction[2]);
            final Location _movedLocation2 = selection.getLocation2();
            _movedLocation2.add(direction[0], direction[1], direction[2]);
            _newSelection.setLocation1(_movedLocation1);
            _newSelection.setLocation2(_movedLocation2);
            try {
                _newSelection.calculateRegion();
            } catch (final Exception _ex) {
                v.getVoxelMessage().brushMessage("The new Selection has more blocks than the original selection. This should never happen!");
            }

            for (final BlockState _blockState : selection.getBlockStates()) {
                _undoSet.add(_blockState.getBlock());
            }
            for (final BlockState _blockState : _newSelection.getBlockStates()) {
                _undoSet.add(_blockState.getBlock());
            }

            for (final Block _block : _undoSet) {
                _vundo.put(_block);
            }
            v.storeUndo(_vundo);

            for (final BlockState _blockState : selection.getBlockStates()) {
                _blockState.getBlock().setType(Material.AIR);
            }
            for (final BlockState _blockState : selection.getBlockStates()) {
                final Block _affectedBlock = _world.getBlockAt(_blockState.getX() + direction[0], _blockState.getY() + direction[1], _blockState.getZ()
                        + direction[2]);
                _affectedBlock.setTypeId(_blockState.getTypeId(), !Move.breakableMaterials.contains(_blockState.getType()));
                _affectedBlock.setData(_blockState.getRawData());
            }
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        if (this.selection == null) {
            this.selection = new Selection();
        }
        this.selection.setLocation1(this.getTargetBlock().getLocation());
        v.getVoxelMessage().brushMessage("Point 1 set.");

        try {
            if (this.selection.calculateRegion()) {
                this.moveSelection(v, this.selection, this.moveDirections);
                this.selection = null;
            }
        } catch (final Exception _ex) {
            v.sendMessage(_ex.getMessage());
        }
    }

    @Override
    protected final void powder(final SnipeData v) {
        if (this.selection == null) {
            this.selection = new Selection();
        }
        this.selection.setLocation2(this.getTargetBlock().getLocation());
        v.getVoxelMessage().brushMessage("Point 2 set.");

        try {
            if (this.selection.calculateRegion()) {
                this.moveSelection(v, this.selection, this.moveDirections);
                this.selection = null;
            }
        } catch (final Exception _ex) {
            v.sendMessage(_ex.getMessage());
        }
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.custom(ChatColor.BLUE + "Move selection blockPositionY " + ChatColor.GOLD + "x:" + this.moveDirections[0] + " y:" + this.moveDirections[1] + " z:"
    			+ this.moveDirections[2]);
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	if (par[1].equalsIgnoreCase("info")) {
    		v.getVoxelMessage().custom(ChatColor.GOLD + this.getName() + " Parameters:");
    		v.getVoxelMessage().custom(ChatColor.AQUA + "/b mv x[int] -- set the x direction (positive => east)");
    		v.getVoxelMessage().custom(ChatColor.AQUA + "/b mv y[int] -- set the y direction (positive => up)");
    		v.getVoxelMessage().custom(ChatColor.AQUA + "/b mv z[int] -- set the z direction (positive => south)");
    		v.getVoxelMessage().custom(ChatColor.AQUA + "/b mv reset -- reset the brush (x:0 y:0 z:0)");
    		v.getVoxelMessage().custom(ChatColor.AQUA + "Use arrow and gunpowder to define two points.");
    	}
    	
    	for (int _i = 1; _i < par.length; _i++) {
    		if (par[_i].equalsIgnoreCase("reset")) {
    			this.moveDirections[0] = 0;
    			this.moveDirections[1] = 0;
    			this.moveDirections[2] = 0;
    			v.getVoxelMessage().custom(ChatColor.AQUA + "X direction set to: " + this.moveDirections[0]);
    			v.getVoxelMessage().custom(ChatColor.AQUA + "Y direction set to: " + this.moveDirections[1]);
    			v.getVoxelMessage().custom(ChatColor.AQUA + "Z direction set to: " + this.moveDirections[2]);
    		}
    		if (par[_i].toLowerCase().startsWith("x")) {
    			this.moveDirections[0] = Integer.valueOf(par[_i].substring(1));
    			v.getVoxelMessage().custom(ChatColor.AQUA + "X direction set to: " + this.moveDirections[0]);
    		} else if (par[_i].toLowerCase().startsWith("y")) {
    			this.moveDirections[1] = Integer.valueOf(par[_i].substring(1));
    			v.getVoxelMessage().custom(ChatColor.AQUA + "Y direction set to: " + this.moveDirections[1]);
    		} else if (par[_i].toLowerCase().startsWith("z")) {
    			this.moveDirections[2] = Integer.valueOf(par[_i].substring(1));
    			v.getVoxelMessage().custom(ChatColor.AQUA + "Z direction set to: " + this.moveDirections[2]);
    		}
    	}
    }
    
    @Override
    public final int getTimesUsed() {
    	return Move.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	Move.timesUsed = tUsed;
    }
}
