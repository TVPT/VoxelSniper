package com.thevoxelbox.voxelsniper.brush;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

// Proposal: Use /v and /vr for leave and wood material // or two more parameters -- Monofraps
/**
 * 
 * @author Ghost8700 @ Voxel
 * 
 */
public class GenerateTree extends Brush {	
	//
    // Tree Variables.
    //
    private Random randGenerator = new Random();
    private ArrayList<Block> branchBlocks = new ArrayList<Block>();
    private Undo undo;
    // If these default values are edited. Remember to change default values in the default preset.
    private byte leafType = 0;
    private byte woodType = 0;
    private boolean rootFloat = false;
    private int startHeight = 0;
    private int rootLength = 9;
    private int maxRoots = 2;
    private int minRoots = 1;
    private int thickness = 1;
    private int slopeChance = 40;
    private int twistChance = 5; // This is a hidden value not available through Parameters. Otherwise messy.
    private int heightMininmum = 14;
    private int heightMaximum = 18;
    private int branchLength = 8;
    private int nodeMax = 4;
    private int nodeMin = 3;

    private static int timesUsed = 0;

    public GenerateTree() {
        this.setName("Generate Tree");
    }

    // Branch Creation based on direction chosen from the parameters passed.
    private final void branchCreate(final int xDirection, final int zDirection) {

        // Sets branch origin.
        final int _originX = this.getBlockPositionX();
        final int _originY = this.getBlockPositionY();
        final int _originZ = this.getBlockPositionZ();

        // Sets direction preference.
        final int _xPreference = this.randGenerator.nextInt(60) + 20;
        final int _zPreference = this.randGenerator.nextInt(60) + 20;

        // Iterates according to branch length.
        for (int _r = 0; _r < this.branchLength; _r++) {

            // Alters direction according to preferences.
            if (this.randGenerator.nextInt(100) < _xPreference) {
                this.setBlockPositionX(this.getBlockPositionX() + 1 * xDirection);
            }
            if (this.randGenerator.nextInt(100) < _zPreference) {
                this.setBlockPositionZ(this.getBlockPositionZ() + 1 * zDirection);
            }

            // 50% chance to increase elevation every second block.
            if (Math.abs(_r % 2) == 1) {
                this.setBlockPositionY(this.getBlockPositionY() + this.randGenerator.nextInt(2));
            }

            // Add block to undo function.
            if (this.getBlockIdAt(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()) != Material.LOG.getId()) {
                this.undo.put(this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()));
            }

            // Creates a branch block.
            this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()).setTypeIdAndData(Material.LOG.getId(), this.woodType, false);
            this.branchBlocks.add(this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()));
        }

        // Resets the origin
        this.setBlockPositionX(_originX);
        this.setBlockPositionY(_originY);
        this.setBlockPositionZ(_originZ);
    }

    private final void leafNodeCreate() {
    	// Generates the node size.
    	final int _nodeRadius = this.randGenerator.nextInt(this.nodeMax - this.nodeMin + 1) + this.nodeMin;
    	final double _bPow = Math.pow(_nodeRadius + 0.5, 2);

        // Lowers the current block in order to start at the bottom of the node.
        this.setBlockPositionY(this.getBlockPositionY() - 2);


        for (int _z = _nodeRadius; _z >= 0; _z--) {
            final double _zPow = Math.pow(_z, 2);
            
            for (int _x = _nodeRadius; _x >= 0; _x--) {
                final double _xPow = Math.pow(_x, 2);
                
                for (int _y = _nodeRadius; _y >= 0; _y--) {
                    if ((_xPow + Math.pow(_y, 2) + _zPow) <= _bPow) {
                        // Chance to skip creation of a block.
                        if (this.randGenerator.nextInt(100) >= 30) {
                            // If block is Air, create a leaf block.
                            if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z) == Material.AIR.getId()) {
                                // Adds block to undo function.
                                if (this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z) != Material.LEAVES.getId()) {
                                    this.undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z));
                                }
                                // Creates block.
                                this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z).setTypeIdAndData(Material.LEAVES.getId(), this.leafType, false);
                            }
                        }
                        if (this.randGenerator.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _z) == Material.AIR.getId()) {
                                if (this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _z) != Material.LEAVES.getId()) {
                                    this.undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _z));
                                }
                                this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _z).setTypeIdAndData(Material.LEAVES.getId(), this.leafType, false);
                            }
                        }
                        if (this.randGenerator.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z) == Material.AIR.getId()) {
                                if (this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z) != Material.LEAVES.getId()) {
                                    this.undo.put(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z));
                                }
                                this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z).setTypeIdAndData(Material.LEAVES.getId(), this.leafType, false);
                            }
                        }
                        if (this.randGenerator.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _z) == Material.AIR.getId()) {
                                if (this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _z) != Material.LEAVES.getId()) {
                                    this.undo.put(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _z));
                                }
                                this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() - _z).setTypeIdAndData(Material.LEAVES.getId(), this.leafType, false);
                            }
                        }
                        if (this.randGenerator.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() + _z) == Material.AIR.getId()) {
                                if (this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() + _z) != Material.LEAVES.getId()) {
                                    this.undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() + _z));
                                }
                                this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() + _z).setTypeIdAndData(Material.LEAVES.getId(), this.leafType, false);
                            }
                        }
                        if (this.randGenerator.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() - _z) == Material.AIR.getId()) {
                                if (this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() - _z) != Material.LEAVES.getId()) {
                                    this.undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() - _z));
                                }
                                this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() - _z).setTypeIdAndData(Material.LEAVES.getId(), this.leafType, false);
                            }
                        }
                        if (this.randGenerator.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() + _z) == Material.AIR.getId()) {
                                if (this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() + _z) != Material.LEAVES.getId()) {
                                    this.undo.put(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() + _z));
                                }
                                this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() + _z).setTypeIdAndData(Material.LEAVES.getId(), this.leafType, false);
                            }
                        }
                        if (this.randGenerator.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() - _z) == Material.AIR.getId()) {
                                if (this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() - _z) != Material.LEAVES.getId()) {
                                    this.undo.put(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() - _z));
                                }
                                this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY() - _y, this.getBlockPositionZ() - _z).setTypeIdAndData(Material.LEAVES.getId(), this.leafType, false);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Code Concerning Root Generation
     * @param xDirection
     * @param zDirection
     */
    private final void rootCreate(final int xDirection, final int zDirection) {
        // Sets Origin.
        final int _originX = this.getBlockPositionX();
        final int _originY = this.getBlockPositionY();
        final int _originZ = this.getBlockPositionZ();

        // Generates the number of roots to create.
        final int roots = this.randGenerator.nextInt(this.maxRoots - this.minRoots + 1) + this.minRoots;

        // A roots preference to move along the X and Y axis.


        // Loops for each root to be created.
        for (int _i = 0; _i < roots; _i++) {
            // Pushes the root'world starting point out from the center of the tree.
            for (int _t = 0; _t < this.thickness - 1; _t++) {
                this.setBlockPositionX(this.getBlockPositionX() + xDirection);
                this.setBlockPositionZ(this.getBlockPositionZ() + zDirection);
            }

            // Generate directional preference between 30% and 70%
            final int _xPreference = this.randGenerator.nextInt(30) + 40;
            final int _zPreference = this.randGenerator.nextInt(30) + 40;

            for (int _j = 0; _j < this.rootLength; _j++) {
                // For the purposes of this algorithm, logs aren't considered solid.

                // Checks if location is solid.
                if (this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()).getType() == Material.AIR || this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()).getType() == Material.WATER
                        || this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()).getType() == Material.STATIONARY_WATER
                        || this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()).getType() == Material.SNOW
                        || this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()).getType() == Material.LOG) {
                }
                // If not solid then...
                // Save for undo function
                if (this.getBlockIdAt(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()) != Material.LOG.getId()) {
                    this.undo.put(this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()));

                    // Place log block.
                    this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()).setTypeIdAndData(Material.LOG.getId(), this.woodType, false);
                } else {
                    // If solid then...
                    // End loop
                    break;
                }

                // Checks is block below is solid
                if (this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - 1, this.getBlockPositionZ()).getType() == Material.AIR
                        || this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - 1, this.getBlockPositionZ()).getType() == Material.WATER
                        || this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - 1, this.getBlockPositionZ()).getType() == Material.STATIONARY_WATER
                        || this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - 1, this.getBlockPositionZ()).getType() == Material.SNOW
                        || this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - 1, this.getBlockPositionZ()).getType() == Material.LOG) {
                    // Mos down if solid.
                    this.setBlockPositionY(this.getBlockPositionY() - 1);
                    if (this.rootFloat == true) {
                        if (this.randGenerator.nextInt(100) < _xPreference) {
                            this.setBlockPositionX(this.getBlockPositionX() + xDirection);
                        }
                        if (this.randGenerator.nextInt(100) < _zPreference) {
                            this.setBlockPositionZ(this.getBlockPositionZ() + zDirection);
                        }
                    }
                } else {
                    // If solid then move.
                    if (this.randGenerator.nextInt(100) < _xPreference) {
                        this.setBlockPositionX(this.getBlockPositionX() + xDirection);
                    }
                    if (this.randGenerator.nextInt(100) < _zPreference) {
                        this.setBlockPositionZ(this.getBlockPositionZ() + zDirection);
                    }
                    // Checks if new location is solid, if not then move down.
                    if (this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - 1, this.getBlockPositionZ()).getType() == Material.AIR
                            || this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - 1, this.getBlockPositionZ()).getType() == Material.WATER
                            || this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - 1, this.getBlockPositionZ()).getType() == Material.STATIONARY_WATER
                            || this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - 1, this.getBlockPositionZ()).getType() == Material.SNOW
                            || this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - 1, this.getBlockPositionZ()).getType() == Material.LOG) {
                        this.setBlockPositionY(this.getBlockPositionY() - 1);
                    }
                }
            }

            // Reset origin.
            this.setBlockPositionX(_originX);
            this.setBlockPositionY(_originY);
            this.setBlockPositionZ(_originZ);

        }
    }

    private final void rootGen() {
        // Quadrant 1
        this.rootCreate(1, 1);

        // Quadrant 2
        this.rootCreate(-1, 1);

        // Quadrant 3
        this.rootCreate(1, -1);

        // Quadrant 4
        this.rootCreate(-1, -1);
    }

    private final void trunkCreate() {
        // Creates true circle discs of the set size using the wood type selected.
        final double _bPow = Math.pow(this.thickness + 0.5, 2);
        
        for (int _x = this.thickness; _x >= 0; _x--) {
            final double _xPow = Math.pow(_x, 2);
            
            for (int _z = this.thickness; _z >= 0; _z--) {
                if ((_xPow + Math.pow(_z, 2)) <= _bPow) {
                    // If block is air, then create a block.
                    if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _z) == Material.AIR.getId()) {
                        // Adds block to undo function.
                        if (this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _z) != Material.LOG.getId()) {
                            this.undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _z));
                        }
                        // Creates block.
                        this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() + _z).setTypeIdAndData(Material.LOG.getId(), this.woodType, false);
                    }
                    if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _z) == Material.AIR.getId()) {
                        if (this.getBlockIdAt(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _z) != Material.LOG.getId()) {
                            this.undo.put(this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _z));
                        }
                        this.clampY(this.getBlockPositionX() + _x, this.getBlockPositionY(), this.getBlockPositionZ() - _z).setTypeIdAndData(Material.LOG.getId(), this.woodType, false);
                    }
                    if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() + _z) == Material.AIR.getId()) {
                        if (this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() + _z) != Material.LOG.getId()) {
                            this.undo.put(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() + _z));
                        }
                        this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() + _z).setTypeIdAndData(Material.LOG.getId(), this.woodType, false);
                    }
                    if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() - _z) == Material.AIR.getId()) {
                        if (this.getBlockIdAt(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() - _z) != Material.LOG.getId()) {
                            this.undo.put(this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() - _z));
                        }
                        this.clampY(this.getBlockPositionX() - _x, this.getBlockPositionY(), this.getBlockPositionZ() - _z).setTypeIdAndData(Material.LOG.getId(), this.woodType, false);
                    }
                }
            }
        }
    }

    /*
     * 
     * Code Concerning Trunk Generation
     */
    private final void trunkGen() {
        // Sets Origin
        final int _originX = this.getBlockPositionX();
        final int _originY = this.getBlockPositionY();
        final int _originZ = this.getBlockPositionZ();

        // ----------
        // Main Trunk
        // ----------
        // Sets diretional preferences.
        int _xPreference = this.randGenerator.nextInt(this.slopeChance);
        int _zPreference = this.randGenerator.nextInt(this.slopeChance);

        // Sets direction.
        int _xDirection = 1;
        if (this.randGenerator.nextInt(100) < 50) {
            _xDirection = -1;
        }

        int _zDirection = 1;
        if (this.randGenerator.nextInt(100) < 50) {
            _zDirection = -1;
        }

        // Generates a height for trunk.
        int _height = this.randGenerator.nextInt(this.heightMaximum - this.heightMininmum + 1) + this.heightMininmum;

        for (int _p = 0; _p < _height; _p++) {
            if (_p > 3) {
                if (this.randGenerator.nextInt(100) <= this.twistChance) {
                    _xDirection *= -1;
                }
                if (this.randGenerator.nextInt(100) <= this.twistChance) {
                    _zDirection *= -1;
                }
                if (this.randGenerator.nextInt(100) < _xPreference) {
                    this.setBlockPositionX(this.getBlockPositionX() + _xDirection);
                }
                if (this.randGenerator.nextInt(100) < _zPreference) {
                    this.setBlockPositionZ(this.getBlockPositionZ() + _zDirection);
                }
            }

            // Creates trunk section
            this.trunkCreate();

            // Mos up for next section
            this.setBlockPositionY(this.getBlockPositionY() + 1);
        }

        // Generates branchs at top of trunk for each quadrant.
        this.branchCreate(1, 1);
        this.branchCreate(-1, 1);
        this.branchCreate(1, -1);
        this.branchCreate(-1, -1);

        // Reset Origin for next trunk.
        this.setBlockPositionX(_originX);
        this.setBlockPositionY(_originY + 4);
        this.setBlockPositionZ(_originZ);

        // ---------------
        // Secondary Trunk
        // ---------------
        // Sets diretional preferences.
        _xPreference = this.randGenerator.nextInt(this.slopeChance);
        _zPreference = this.randGenerator.nextInt(this.slopeChance);

        // Sets direction.
        _xDirection = 1;
        if (this.randGenerator.nextInt(100) < 50) {
            _xDirection = -1;
        }

        _zDirection = 1;
        if (this.randGenerator.nextInt(100) < 50) {
            _zDirection = -1;
        }

        // Generates a height for trunk.
        _height = this.randGenerator.nextInt(this.heightMaximum - this.heightMininmum + 1) + this.heightMininmum;

        if (_height > 4) {
            for (int _p = 0; _p < _height; _p++) {
                if (this.randGenerator.nextInt(100) <= this.twistChance) {
                    _xDirection *= -1;
                }
                if (this.randGenerator.nextInt(100) <= this.twistChance) {
                    _zDirection *= -1;
                }
                if (this.randGenerator.nextInt(100) < _xPreference) {
                    this.setBlockPositionX(this.getBlockPositionX() + 1 * _xDirection);
                }
                if (this.randGenerator.nextInt(100) < _zPreference) {
                    this.setBlockPositionZ(this.getBlockPositionZ() + 1 * _zDirection);
                }

                // Creates a trunk section
                this.trunkCreate();

                // Mos up for next section
                this.setBlockPositionY(this.getBlockPositionY() + 1);
            }

            // Generates branchs at top of trunk for each quadrant.
            this.branchCreate(1, 1);
            this.branchCreate(-1, 1);
            this.branchCreate(1, -1);
            this.branchCreate(-1, -1);
        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.undo = new Undo(this.getTargetBlock().getWorld().getName());

        this.branchBlocks.clear();

        // Sets the location variables.
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY() + this.startHeight);
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        // Generates the roots.
        this.rootGen();

        // Generates the trunk, which also generates branches.
        this.trunkGen();

        // Each branch block was saved in an array. This is now fed through an array.
        // This array takes each branch block and constructs a leaf node around it.
        for (final Block _b : this.branchBlocks) {
            this.setBlockPositionX(_b.getX());
            this.setBlockPositionY(_b.getY());
            this.setBlockPositionZ(_b.getZ());
            this.leafNodeCreate();
        }

        // Ends the undo function and mos on.
        v.storeUndo(this.undo);
    }

    // The Powder currently does nothing extra.
    @Override
    protected final void powder(final SnipeData v) {
        this.arrow(v);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (int _i = 1; _i < par.length; _i++) {
        	final String _param = par[_i];
        	
            try {
            	if (_param.equalsIgnoreCase("info")) {
            		v.sendMessage(ChatColor.GOLD + "This brush takes the following parameters:");
            		v.sendMessage(ChatColor.AQUA + "lt# - leaf type (data value)");
            		v.sendMessage(ChatColor.AQUA + "wt# - wood type (data value)");
            		v.sendMessage(ChatColor.AQUA + "tt# - tree thickness (whote number)");
            		v.sendMessage(ChatColor.AQUA + "rfX - root float (true or false)");
            		v.sendMessage(ChatColor.AQUA + "sh# - starting height (whole number)");
            		v.sendMessage(ChatColor.AQUA + "rl# - root length (whole number)");
            		v.sendMessage(ChatColor.AQUA + "ts# - trunk slope chance (0-100)");
            		v.sendMessage(ChatColor.AQUA + "bl# - branch length (whole number)");
            		v.sendMessage(ChatColor.AQUA + "info2 - more parameters");
            		return;
            	}
            	
            	if (_param.equalsIgnoreCase("info2")) {
            		v.sendMessage(ChatColor.GOLD + "This brush takes the following parameters:");
            		v.sendMessage(ChatColor.AQUA + "minr# - minimum roots (whole number)");
            		v.sendMessage(ChatColor.AQUA + "maxr# - maximum roots (whole number)");
            		v.sendMessage(ChatColor.AQUA + "minh# - minimum height (whole number)");
            		v.sendMessage(ChatColor.AQUA + "maxh# - maximum height (whole number)");
            		v.sendMessage(ChatColor.AQUA + "minl# - minimum leaf node size (whole number)");
            		v.sendMessage(ChatColor.AQUA + "maxl# - maximum leaf node size (whole number)");
            		v.sendMessage(ChatColor.AQUA + "default - restore default params");
            		return;
            	}
                if (_param.startsWith("lt")) { // Leaf Type
                    this.leafType = Byte.parseByte(_param.replace("lt", ""));
                    v.sendMessage(ChatColor.BLUE + "Leaf Type set to " + this.leafType);
                    continue;
                } else if (_param.startsWith("wt")) { // Wood Type
                    this.woodType = Byte.parseByte(_param.replace("wt", ""));
                    v.sendMessage(ChatColor.BLUE + "Wood Type set to " + this.woodType);
                    continue;
                } else if (_param.startsWith("tt")) { // Tree Thickness
                    this.thickness = Integer.parseInt(_param.replace("tt", ""));
                    v.sendMessage(ChatColor.BLUE + "Thickness set to " + this.thickness);
                    continue;
                } else if (_param.startsWith("rf")) { // Root Float
                    this.rootFloat = Boolean.parseBoolean(_param.replace("rf", ""));
                    v.sendMessage(ChatColor.BLUE + "Floating Roots set to " + this.rootFloat);
                    continue;
                } else if (_param.startsWith("sh")) { // Starting Height
                    this.startHeight = Integer.parseInt(_param.replace("sh", ""));
                    v.sendMessage(ChatColor.BLUE + "Starting Height set to " + this.startHeight);
                    continue;
                } else if (_param.startsWith("rl")) { // Root Length
                    this.rootLength = Integer.parseInt(_param.replace("rl", ""));
                    v.sendMessage(ChatColor.BLUE + "Root Length set to " + this.rootLength);
                    continue;
                } else if (_param.startsWith("minr")) { // Minimum Roots
                    this.minRoots = Integer.parseInt(_param.replace("minr", ""));
                    if (this.minRoots > this.maxRoots) {
                        this.minRoots = this.maxRoots;
                        v.sendMessage(ChatColor.RED + "Minimum Roots can't exceed Maximum Roots, has  been set to " + this.minRoots + " Instead!");
                    } else {
                        v.sendMessage(ChatColor.BLUE + "Minimum Roots set to " + this.minRoots);
                    }
                    continue;
                } else if (_param.startsWith("maxr")) { // Maximum Roots
                    this.maxRoots = Integer.parseInt(_param.replace("maxr", ""));
                    if (this.minRoots > this.maxRoots) {
                        this.maxRoots = this.minRoots;
                        v.sendMessage(ChatColor.RED + "Maximum Roots can't be lower than Minimum Roots, has been set to " + this.minRoots + " Instead!");
                    } else {
                        v.sendMessage(ChatColor.BLUE + "Maximum Roots set to " + this.maxRoots);
                    }
                    continue;
                } else if (_param.startsWith("ts")) { // Trunk Slope Chance
                    this.slopeChance = Integer.parseInt(_param.replace("ts", ""));
                    v.sendMessage(ChatColor.BLUE + "Trunk Slope set to " + this.slopeChance);
                    continue;
                } else if (_param.startsWith("minh")) { // Height Minimum
                    this.heightMininmum = Integer.parseInt(_param.replace("minh", ""));
                    if (this.heightMininmum > this.heightMaximum) {
                        this.heightMininmum = this.heightMaximum;
                        v.sendMessage(ChatColor.RED + "Minimum Height exceed than Maximum Height, has been set to " + this.heightMininmum + " Instead!");
                    } else {
                        v.sendMessage(ChatColor.BLUE + "Minimum Height set to " + this.heightMininmum);
                    }
                    continue;
                } else if (_param.startsWith("maxh")) { // Height Maximum
                    this.heightMaximum = Integer.parseInt(_param.replace("maxh", ""));
                    if (this.heightMininmum > this.heightMaximum) {
                        this.heightMaximum = this.heightMininmum;
                        v.sendMessage(ChatColor.RED + "Maximum Height can't be lower than Minimum Height, has been set to " + this.heightMaximum + " Instead!");
                    } else {
                        v.sendMessage(ChatColor.BLUE + "Maximum Roots set to " + this.heightMaximum);
                    }
                    continue;
                } else if (_param.startsWith("bl")) { // Branch Length
                    this.branchLength = Integer.parseInt(_param.replace("bl", ""));
                    v.sendMessage(ChatColor.BLUE + "Branch Length set to " + this.branchLength);
                    continue;
                } else if (_param.startsWith("maxl")) { // Leaf Node Max Size
                    this.nodeMax = Integer.parseInt(_param.replace("maxl", ""));
                    v.sendMessage(ChatColor.BLUE + "Leaf Max Thickness set to " + this.nodeMax + " (Default 4)");
                    continue;
                } else if (_param.startsWith("minl")) { // Leaf Node Min Size
                    this.nodeMin = Integer.parseInt(_param.replace("minl", ""));
                    v.sendMessage(ChatColor.BLUE + "Leaf Min Thickness set to " + this.nodeMin + " (Default 3)");
                    continue;

                    // -------
                    // Presets
                    // -------
                } else if (_param.startsWith("default")) { // Default settings.
                    this.leafType = 0;
                    this.woodType = 0;
                    this.rootFloat = false;
                    this.startHeight = 0;
                    this.rootLength = 9;
                    this.maxRoots = 2;
                    this.minRoots = 1;
                    this.thickness = 1;
                    this.slopeChance = 40;
                    this.heightMininmum = 14;
                    this.heightMaximum = 18;
                    this.branchLength = 8;
                    this.nodeMax = 4;
                    this.nodeMin = 3;
                    v.sendMessage(ChatColor.GOLD + "Brush reset to default parameters.");
                    continue;
                } else {
                    v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
                }
            } catch (final Exception e) {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! \"" + par[_i]
                        + "\" is not a valid statement. Please use the 'info' parameter to display parameter info.");
            }

        }
    }

    @Override
    public final int getTimesUsed() {
        return GenerateTree.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        GenerateTree.timesUsed = tUsed;
    }
}
