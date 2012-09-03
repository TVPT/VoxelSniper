package com.thevoxelbox.voxelsniper.brush;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * 
 * @author Ghost8700 @ Voxel
 * 
 */
public class GenerateTree extends Brush {

    //
    // Tree Variables.
    //
    protected Random generate = new Random();
    protected ArrayList<Block> branchBlocks = new ArrayList<Block>();
    protected vUndo h;
    // If these default values are edited. Remember to change default values in the default preset.
    protected byte leafType = 0;
    protected byte woodType = 0;
    protected boolean rootFloat = false;
    protected int startHeight = 0;
    protected int rootLength = 9;
    protected int maxRoots = 2;
    protected int minRoots = 1;
    protected int thickness = 1;
    protected int slopeChance = 40;
    protected int twistChance = 5; // This is a hidden value not available through Parameters. Otherwise messy.
    protected int heightMininmum = 14;
    protected int heightMaximum = 18;
    protected int branchLength = 8;
    protected int nodeMax = 4;
    protected int nodeMin = 3;

    private static int timesUsed = 0;

    // TODO: Info Parameter.
    // TODO: Find an easier command syntax.
    public GenerateTree() {
        this.setName("Generate Tree");
    }

    // Branch Creation based on direction chosen from the parameters passed.
    public final void branchCreate(final int xDirection, final int zDirection) {

        // Sets branch origin.
        final int originX = this.getBlockPositionX();
        final int originY = this.getBlockPositionY();
        final int originZ = this.getBlockPositionZ();

        // Sets direction preference.
        final int xPreference = this.generate.nextInt(60) + 20;
        final int zPreference = this.generate.nextInt(60) + 20;

        // Iterates according to branch length.
        for (int r = 0; r < this.branchLength; r++) {

            // Alters direction according to preferences.
            if (this.generate.nextInt(100) < xPreference) {
                this.setBlockPositionX(this.getBlockPositionX() + 1 * xDirection);
            }
            if (this.generate.nextInt(100) < zPreference) {
                this.setBlockPositionZ(this.getBlockPositionZ() + 1 * zDirection);
            }

            // 50% chance to increase elevation every second block.
            if (Math.abs(r % 2) == 1) {
                this.setBlockPositionY(this.getBlockPositionY() + this.generate.nextInt(2));
            }

            // Add block to undo function.
            if (this.getBlockIdAt(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()) != 17) {
                this.h.put(this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()));
            }

            // Creates a branch block.
            this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()).setTypeIdAndData(17, this.woodType, false);
            this.branchBlocks.add(this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()));
        }

        // Resets the origin
        this.setBlockPositionX(originX);
        this.setBlockPositionY(originY);
        this.setBlockPositionZ(originZ);
    }

    @Override
    public final int getTimesUsed() {
        return GenerateTree.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.getName());
    }

    public final void leafNodeCreate() {

        // Lowers the current block in order to start at the bottom of the node.
        this.setBlockPositionY(this.getBlockPositionY() - 2);

        // Generates the node size.
        final int nodeRadius = this.generate.nextInt(this.nodeMax - this.nodeMin + 1) + this.nodeMin;

        final double bpow = Math.pow(nodeRadius + 0.5, 2);
        for (int z = nodeRadius; z >= 0; z--) {
            final double zpow = Math.pow(z, 2);
            for (int x = nodeRadius; x >= 0; x--) {
                final double xpow = Math.pow(x, 2);
                for (int y = nodeRadius; y >= 0; y--) {
                    if ((xpow + Math.pow(y, 2) + zpow) <= bpow) {
                        // Chance to skip creation of a block.
                        if (this.generate.nextInt(100) >= 30) {
                            // If block is Air, create a leaf block.
                            if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z) == 0) {
                                // Adds block to undo function.
                                if (this.getBlockIdAt(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z) != 18) {
                                    this.h.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z));
                                }
                                // Creates block.
                                this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z).setTypeIdAndData(18, this.leafType, false);
                            }
                        }
                        if (this.generate.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() - z) == 0) {
                                if (this.getBlockIdAt(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() - z) != 18) {
                                    this.h.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() - z));
                                }
                                this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() - z).setTypeIdAndData(18, this.leafType, false);
                            }
                        }
                        if (this.generate.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z) == 0) {
                                if (this.getBlockIdAt(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z) != 18) {
                                    this.h.put(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z));
                                }
                                this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z).setTypeIdAndData(18, this.leafType, false);
                            }
                        }
                        if (this.generate.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() - z) == 0) {
                                if (this.getBlockIdAt(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() - z) != 18) {
                                    this.h.put(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() - z));
                                }
                                this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() - z).setTypeIdAndData(18, this.leafType, false);
                            }
                        }
                        if (this.generate.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ() + z) == 0) {
                                if (this.getBlockIdAt(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ() + z) != 18) {
                                    this.h.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ() + z));
                                }
                                this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ() + z).setTypeIdAndData(18, this.leafType, false);
                            }
                        }
                        if (this.generate.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ() - z) == 0) {
                                if (this.getBlockIdAt(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ() - z) != 18) {
                                    this.h.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ() - z));
                                }
                                this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ() - z).setTypeIdAndData(18, this.leafType, false);
                            }
                        }
                        if (this.generate.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ() + z) == 0) {
                                if (this.getBlockIdAt(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ() + z) != 18) {
                                    this.h.put(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ() + z));
                                }
                                this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ() + z).setTypeIdAndData(18, this.leafType, false);
                            }
                        }
                        if (this.generate.nextInt(100) >= 30) {
                            if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ() - z) == 0) {
                                if (this.getBlockIdAt(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ() - z) != 18) {
                                    this.h.put(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ() - z));
                                }
                                this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ() - z).setTypeIdAndData(18, this.leafType, false);
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * 
     * Code Concerning Parameters
     */
    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
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

        if (par[1].equalsIgnoreCase("info2")) {
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

        for (int x = 1; x < par.length; x++) {
            try {
                if (par[x].startsWith("lt")) { // Leaf Type
                    this.leafType = Byte.parseByte(par[x].replace("lt", ""));
                    v.sendMessage(ChatColor.BLUE + "Leaf Type set to " + this.leafType);
                    continue;
                } else if (par[x].startsWith("wt")) { // Wood Type
                    this.woodType = Byte.parseByte(par[x].replace("wt", ""));
                    v.sendMessage(ChatColor.BLUE + "Wood Type set to " + this.woodType);
                    continue;
                } else if (par[x].startsWith("tt")) { // Tree Thickness
                    this.thickness = Integer.parseInt(par[x].replace("tt", ""));
                    v.sendMessage(ChatColor.BLUE + "Thickness set to " + this.thickness);
                    continue;
                } else if (par[x].startsWith("rf")) { // Root Float
                    this.rootFloat = Boolean.parseBoolean(par[x].replace("rf", ""));
                    v.sendMessage(ChatColor.BLUE + "Floating Roots set to " + this.rootFloat);
                    continue;
                } else if (par[x].startsWith("sh")) { // Starting Height
                    this.startHeight = Integer.parseInt(par[x].replace("sh", ""));
                    v.sendMessage(ChatColor.BLUE + "Starting Height set to " + this.startHeight);
                    continue;
                } else if (par[x].startsWith("rl")) { // Root Length
                    this.rootLength = Integer.parseInt(par[x].replace("rl", ""));
                    v.sendMessage(ChatColor.BLUE + "Root Length set to " + this.rootLength);
                    continue;
                } else if (par[x].startsWith("minr")) { // Minimum Roots
                    this.minRoots = Integer.parseInt(par[x].replace("minr", ""));
                    if (this.minRoots > this.maxRoots) {
                        this.minRoots = this.maxRoots;
                        v.sendMessage(ChatColor.RED + "Minimum Roots can't exceed Maximum Roots, has  been set to " + this.minRoots + " Instead!");
                    } else {
                        v.sendMessage(ChatColor.BLUE + "Minimum Roots set to " + this.minRoots);
                    }
                    continue;
                } else if (par[x].startsWith("maxr")) { // Maximum Roots
                    this.maxRoots = Integer.parseInt(par[x].replace("maxr", ""));
                    if (this.minRoots > this.maxRoots) {
                        this.maxRoots = this.minRoots;
                        v.sendMessage(ChatColor.RED + "Maximum Roots can't be lower than Minimum Roots, has been set to " + this.minRoots + " Instead!");
                    } else {
                        v.sendMessage(ChatColor.BLUE + "Maximum Roots set to " + this.maxRoots);
                    }
                    continue;
                } else if (par[x].startsWith("ts")) { // Trunk Slope Chance
                    this.slopeChance = Integer.parseInt(par[x].replace("ts", ""));
                    v.sendMessage(ChatColor.BLUE + "Trunk Slope set to " + this.slopeChance);
                    continue;
                } else if (par[x].startsWith("minh")) { // Height Minimum
                    this.heightMininmum = Integer.parseInt(par[x].replace("minh", ""));
                    if (this.heightMininmum > this.heightMaximum) {
                        this.heightMininmum = this.heightMaximum;
                        v.sendMessage(ChatColor.RED + "Minimum Height exceed than Maximum Height, has been set to " + this.heightMininmum + " Instead!");
                    } else {
                        v.sendMessage(ChatColor.BLUE + "Minimum Height set to " + this.heightMininmum);
                    }
                    continue;
                } else if (par[x].startsWith("maxh")) { // Height Maximum
                    this.heightMaximum = Integer.parseInt(par[x].replace("maxh", ""));
                    if (this.heightMininmum > this.heightMaximum) {
                        this.heightMaximum = this.heightMininmum;
                        v.sendMessage(ChatColor.RED + "Maximum Height can't be lower than Minimum Height, has been set to " + this.heightMaximum + " Instead!");
                    } else {
                        v.sendMessage(ChatColor.BLUE + "Maximum Roots set to " + this.heightMaximum);
                    }
                    continue;
                } else if (par[x].startsWith("bl")) { // Branch Length
                    this.branchLength = Integer.parseInt(par[x].replace("bl", ""));
                    v.sendMessage(ChatColor.BLUE + "Branch Length set to " + this.branchLength);
                    continue;
                } else if (par[x].startsWith("maxl")) { // Leaf Node Max Size
                    this.nodeMax = Integer.parseInt(par[x].replace("maxl", ""));
                    v.sendMessage(ChatColor.BLUE + "Leaf Max Thickness set to " + this.nodeMax + " (Default 4)");
                    continue;
                } else if (par[x].startsWith("minl")) { // Leaf Node Min Size
                    this.nodeMin = Integer.parseInt(par[x].replace("minl", ""));
                    v.sendMessage(ChatColor.BLUE + "Leaf Min Thickness set to " + this.nodeMin + " (Default 3)");
                    continue;

                    // -------
                    // Presets
                    // -------
                } else if (par[x].startsWith("default")) { // Default settings.
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
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! \"" + par[x]
                        + "\" is not a valid statement. Please use the 'info' parameter to display parameter info.");
            }

        }
    }

    /*
     * 
     * Code Concerning Root Generation
     */

    /*
     * 
     * Code Concerning Root Generation
     */
    public final void rootCreate(final int xDirection, final int zDirection) {

        // Sets Origin.
        final int originX = this.getBlockPositionX();
        final int originY = this.getBlockPositionY();
        final int originZ = this.getBlockPositionZ();

        // A roots preference to move along the X and Y axis.
        int xPreference;
        int zPreference;

        // Generates the number of roots to create.
        final int roots = this.generate.nextInt(this.maxRoots - this.minRoots + 1) + this.minRoots;

        // Loops for each root to be created.
        for (int i = 0; i < roots; i++) {

            // Pushes the root'world starting point out from the center of the tree.
            for (int t = 0; t < this.thickness - 1; t++) {
                this.setBlockPositionX(this.getBlockPositionX() + xDirection);
                this.setBlockPositionZ(this.getBlockPositionZ() + zDirection);
            }

            // Generate directional preference between 30% and 70%
            xPreference = this.generate.nextInt(30) + 40;
            zPreference = this.generate.nextInt(30) + 40;

            for (int j = 0; j < this.rootLength; j++) {
                // For the purposes of this algorithm, logs aren't considered solid.

                // Checks if location is solid.
                if (this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()).getType() == Material.AIR || this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()).getType() == Material.WATER
                        || this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()).getType() == Material.STATIONARY_WATER
                        || this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()).getType() == Material.SNOW
                        || this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()).getType() == Material.LOG) {
                }
                // If not solid then...
                // Save for undo function
                if (this.getBlockIdAt(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()) != 17) {
                    this.h.put(this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()));

                    // Place log block.
                    this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()).setTypeIdAndData(17, this.woodType, false);
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
                        if (this.generate.nextInt(100) < xPreference) {
                            this.setBlockPositionX(this.getBlockPositionX() + xDirection);
                        }
                        if (this.generate.nextInt(100) < zPreference) {
                            this.setBlockPositionZ(this.getBlockPositionZ() + zDirection);
                        }
                    }
                } else {
                    // If solid then move.
                    if (this.generate.nextInt(100) < xPreference) {
                        this.setBlockPositionX(this.getBlockPositionX() + xDirection);
                    }
                    if (this.generate.nextInt(100) < zPreference) {
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
            this.setBlockPositionX(originX);
            this.setBlockPositionY(originY);
            this.setBlockPositionZ(originZ);

        }
    }

    public final void rootGen() {

        // Quadrant 1
        this.rootCreate(1, 1);

        // Quadrant 2
        this.rootCreate(-1, 1);

        // Quadrant 3
        this.rootCreate(1, -1);

        // Quadrant 4
        this.rootCreate(-1, -1);
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        GenerateTree.timesUsed = tUsed;
    }

    public final void trunkCreate() {

        // Creates true circle discs of the set size using the wood type selected.
        final double bpow = Math.pow(this.thickness + 0.5, 2);
        for (int x = this.thickness; x >= 0; x--) {
            final double xpow = Math.pow(x, 2);
            for (int z = this.thickness; z >= 0; z--) {
                if ((xpow + Math.pow(z, 2)) <= bpow) {
                    // If block is air, then create a block.
                    if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() + z) == 0) {
                        // Adds block to undo function.
                        if (this.getBlockIdAt(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() + z) != 17) {
                            this.h.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() + z));
                        }
                        // Creates block.
                        this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() + z).setTypeIdAndData(17, this.woodType, false);
                    }
                    if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() - z) == 0) {
                        if (this.getBlockIdAt(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() - z) != 17) {
                            this.h.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() - z));
                        }
                        this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY(), this.getBlockPositionZ() - z).setTypeIdAndData(17, this.woodType, false);
                    }
                    if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() + z) == 0) {
                        if (this.getBlockIdAt(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() + z) != 17) {
                            this.h.put(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() + z));
                        }
                        this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() + z).setTypeIdAndData(17, this.woodType, false);
                    }
                    if (this.getWorld().getBlockTypeIdAt(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() - z) == 0) {
                        if (this.getBlockIdAt(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() - z) != 17) {
                            this.h.put(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() - z));
                        }
                        this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY(), this.getBlockPositionZ() - z).setTypeIdAndData(17, this.woodType, false);
                    }
                }
            }
        }
    }

    /*
     * 
     * Code Concerning Trunk Generation
     */
    public final void trunkGen() {

        // Sets Origin
        final int originX = this.getBlockPositionX();
        final int originY = this.getBlockPositionY();
        final int originZ = this.getBlockPositionZ();

        // ----------
        // Main Trunk
        // ----------
        // Sets diretional preferences.
        int xPreference = this.generate.nextInt(this.slopeChance);
        int zPreference = this.generate.nextInt(this.slopeChance);

        // Sets direction.
        int xDirection = 1;
        if (this.generate.nextInt(100) < 50) {
            xDirection = -1;
        }

        int zDirection = 1;
        if (this.generate.nextInt(100) < 50) {
            zDirection = -1;
        }

        // Generates a height for trunk.
        int height = this.generate.nextInt(this.heightMaximum - this.heightMininmum + 1) + this.heightMininmum;

        for (int p = 0; p < height; p++) {
            if (p > 3) {
                if (this.generate.nextInt(100) <= this.twistChance) {
                    xDirection *= -1;
                }
                if (this.generate.nextInt(100) <= this.twistChance) {
                    zDirection *= -1;
                }
                if (this.generate.nextInt(100) < xPreference) {
                    this.setBlockPositionX(this.getBlockPositionX() + 1 * xDirection);
                }
                if (this.generate.nextInt(100) < zPreference) {
                    this.setBlockPositionZ(this.getBlockPositionZ() + 1 * zDirection);
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
        this.setBlockPositionX(originX);
        this.setBlockPositionY(originY + 4);
        this.setBlockPositionZ(originZ);

        // ---------------
        // Secondary Trunk
        // ---------------
        // Sets diretional preferences.
        xPreference = this.generate.nextInt(this.slopeChance);
        zPreference = this.generate.nextInt(this.slopeChance);

        // Sets direction.
        xDirection = 1;
        if (this.generate.nextInt(100) < 50) {
            xDirection = -1;
        }

        zDirection = 1;
        if (this.generate.nextInt(100) < 50) {
            zDirection = -1;
        }

        // Generates a height for trunk.
        height = this.generate.nextInt(this.heightMaximum - this.heightMininmum + 1) + this.heightMininmum;

        if (height > 4) {
            for (int p = 0; p < height; p++) {
                if (this.generate.nextInt(100) <= this.twistChance) {
                    xDirection *= -1;
                }
                if (this.generate.nextInt(100) <= this.twistChance) {
                    zDirection *= -1;
                }
                if (this.generate.nextInt(100) < xPreference) {
                    this.setBlockPositionX(this.getBlockPositionX() + 1 * xDirection);
                }
                if (this.generate.nextInt(100) < zPreference) {
                    this.setBlockPositionZ(this.getBlockPositionZ() + 1 * zDirection);
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
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {

        this.h = new vUndo(this.getTargetBlock().getWorld().getName());

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
        for (final Block b : this.branchBlocks) {
            this.setBlockPositionX(b.getX());
            this.setBlockPositionY(b.getY());
            this.setBlockPositionZ(b.getZ());
            this.leafNodeCreate();
        }

        // Ends the undo function and mos on.
        v.storeUndo(this.h);
    }

    // The Powder currently does nothing extra.
    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.arrow(v);
    }
}
