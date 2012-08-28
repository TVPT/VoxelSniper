/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vMessage;
import java.util.ArrayList;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

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
    //If these default values are edited. Remember to change default values in the default preset.
    protected byte leafType = 0;
    protected byte woodType = 0;
    protected boolean rootFloat = false;
    protected int startHeight = 0;
    protected int rootLength = 9;
    protected int maxRoots = 2;
    protected int minRoots = 1;
    protected int thickness = 1;
    protected int slopeChance = 40;
    protected int twistChance = 5; //This is a hidden value not available through Parameters. Otherwise messy.
    protected int heightMininmum = 14;
    protected int heightMaximum = 18;
    protected int branchLength = 8;
    protected int nodeMax = 4;
    protected int nodeMin = 3;

    //TODO: Info Parameter. 
    //TODO: Find an easier command syntax.
    public GenerateTree() {
        name = "Generate Tree";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {

        h = new vUndo(tb.getWorld().getName());

        branchBlocks.clear();

        //Sets the location variables.
        bx = tb.getX();
        by = tb.getY() + startHeight;
        bz = tb.getZ();

        //Generates the roots.
        rootGen();

        //Generates the trunk, which also generates branches.
        trunkGen();

        //Each branch block was saved in an array. This is now fed through an array.
        //This array takes each branch block and constructs a leaf node around it.
        for (Block b : branchBlocks) {
            bx = b.getX();
            by = b.getY();
            bz = b.getZ();
            leafNodeCreate();
        }

        //Ends the undo function and mos on.
        v.storeUndo(h);
    }

    //The Powder currently does nothing extra.
    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        arrow(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
    }

    /*
     *
     * Code Concerning Parameters
     *
     */
    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
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

        for (int x = 1;
                x < par.length;
                x++) {
            try {
                if (par[x].startsWith("lt")) { //Leaf Type
                    leafType = Byte.parseByte(par[x].replace("lt", ""));
                    v.sendMessage(ChatColor.BLUE + "Leaf Type set to " + leafType);
                    continue;
                } else if (par[x].startsWith("wt")) { //Wood Type
                    woodType = Byte.parseByte(par[x].replace("wt", ""));
                    v.sendMessage(ChatColor.BLUE + "Wood Type set to " + woodType);
                    continue;
                } else if (par[x].startsWith("tt")) { //Tree Thickness
                    thickness = Integer.parseInt(par[x].replace("tt", ""));
                    v.sendMessage(ChatColor.BLUE + "Thickness set to " + thickness);
                    continue;
                } else if (par[x].startsWith("rf")) { //Root Float
                    rootFloat = Boolean.parseBoolean(par[x].replace("rf", ""));
                    v.sendMessage(ChatColor.BLUE + "Floating Roots set to " + rootFloat);
                    continue;
                } else if (par[x].startsWith("sh")) { //Starting Height
                    startHeight = Integer.parseInt(par[x].replace("sh", ""));
                    v.sendMessage(ChatColor.BLUE + "Starting Height set to " + startHeight);
                    continue;
                } else if (par[x].startsWith("rl")) { //Root Length
                    rootLength = Integer.parseInt(par[x].replace("rl", ""));
                    v.sendMessage(ChatColor.BLUE + "Root Length set to " + rootLength);
                    continue;
                } else if (par[x].startsWith("minr")) { //Minimum Roots
                    minRoots = Integer.parseInt(par[x].replace("minr", ""));
                    if (minRoots > maxRoots) {
                        minRoots = maxRoots;
                        v.sendMessage(ChatColor.RED + "Minimum Roots can't exceed Maximum Roots, has  been set to " + minRoots + " Instead!");
                    } else {
                        v.sendMessage(ChatColor.BLUE + "Minimum Roots set to " + minRoots);
                    }
                    continue;
                } else if (par[x].startsWith("maxr")) { //Maximum Roots
                    maxRoots = Integer.parseInt(par[x].replace("maxr", ""));
                    if (minRoots > maxRoots) {
                        maxRoots = minRoots;
                        v.sendMessage(ChatColor.RED + "Maximum Roots can't be lower than Minimum Roots, has been set to " + minRoots + " Instead!");
                    } else {
                        v.sendMessage(ChatColor.BLUE + "Maximum Roots set to " + maxRoots);
                    }
                    continue;
                } else if (par[x].startsWith("ts")) { //Trunk Slope Chance
                    slopeChance = Integer.parseInt(par[x].replace("ts", ""));
                    v.sendMessage(ChatColor.BLUE + "Trunk Slope set to " + slopeChance);
                    continue;
                } else if (par[x].startsWith("minh")) { //Height Minimum
                    heightMininmum = Integer.parseInt(par[x].replace("minh", ""));
                    if (heightMininmum > heightMaximum) {
                        heightMininmum = heightMaximum;
                        v.sendMessage(ChatColor.RED + "Minimum Height exceed than Maximum Height, has been set to " + heightMininmum + " Instead!");
                    } else {
                        v.sendMessage(ChatColor.BLUE + "Minimum Height set to " + heightMininmum);
                    }
                    continue;
                } else if (par[x].startsWith("maxh")) { //Height Maximum
                    heightMaximum = Integer.parseInt(par[x].replace("maxh", ""));
                    if (heightMininmum > heightMaximum) {
                        heightMaximum = heightMininmum;
                        v.sendMessage(ChatColor.RED + "Maximum Height can't be lower than Minimum Height, has been set to " + heightMaximum + " Instead!");
                    } else {
                        v.sendMessage(ChatColor.BLUE + "Maximum Roots set to " + heightMaximum);
                    }
                    continue;
                } else if (par[x].startsWith("bl")) { //Branch Length
                    branchLength = Integer.parseInt(par[x].replace("bl", ""));
                    v.sendMessage(ChatColor.BLUE + "Branch Length set to " + branchLength);
                    continue;
                } else if (par[x].startsWith("maxl")) { //Leaf Node Max Size
                    nodeMax = Integer.parseInt(par[x].replace("maxl", ""));
                    v.sendMessage(ChatColor.BLUE + "Leaf Max Thickness set to " + nodeMax + " (Default 4)");
                    continue;
                } else if (par[x].startsWith("minl")) { //Leaf Node Min Size
                    nodeMin = Integer.parseInt(par[x].replace("minl", ""));
                    v.sendMessage(ChatColor.BLUE + "Leaf Min Thickness set to " + nodeMin + " (Default 3)");
                    continue;

                    //-------
                    //Presets
                    //-------
                } else if (par[x].startsWith("default")) { //Default settings.
                    leafType = 0;
                    woodType = 0;
                    rootFloat = false;
                    startHeight = 0;
                    rootLength = 9;
                    maxRoots = 2;
                    minRoots = 1;
                    thickness = 1;
                    slopeChance = 40;
                    heightMininmum = 14;
                    heightMaximum = 18;
                    branchLength = 8;
                    nodeMax = 4;
                    nodeMin = 3;
                    v.sendMessage(ChatColor.GOLD + "Brush reset to default parameters.");
                    continue;
                } else {
                    v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
                }
            } catch (Exception e) {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! \"" + par[x] + "\" is not a valid statement. Please use the 'info' parameter to display parameter info.");
            }

        }
    }
    /*
     *
     * Code Concerning Root Generation
     *
     */

    public void rootGen() {

        //Quadrant 1
        rootCreate(1, 1);

        //Quadrant 2
        rootCreate(-1, 1);

        //Quadrant 3
        rootCreate(1, -1);

        //Quadrant 4
        rootCreate(-1, -1);
    }

    /*
     *
     * Code Concerning Root Generation
     *
     */
    public void rootCreate(int xDirection, int zDirection) {

        //Sets Origin.
        int originX = bx;
        int originY = by;
        int originZ = bz;

        //A roots preference to move along the X and Y axis.
        int xPreference;
        int zPreference;

        //Generates the number of roots to create.
        int roots = generate.nextInt(maxRoots - minRoots + 1) + minRoots;

        //Loops for each root to be created.
        for (int i = 0; i < roots; i++) {

            //Pushes the root'w starting point out from the center of the tree.
            for (int t = 0; t < thickness - 1; t++) {
                bx += xDirection;
                bz += zDirection;
            }

            //Generate directional preference between 30% and 70%
            xPreference = generate.nextInt(30) + 40;
            zPreference = generate.nextInt(30) + 40;

            for (int j = 0; j < rootLength; j++) {
                //For the purposes of this algorithm, logs aren't considered solid.


                //Checks if location is solid.
                if (clampY(bx, by, bz).getType() == Material.AIR
                        || clampY(bx, by, bz).getType() == Material.WATER
                        || clampY(bx, by, bz).getType() == Material.STATIONARY_WATER
                        || clampY(bx, by, bz).getType() == Material.SNOW
                        || clampY(bx, by, bz).getType() == Material.LOG) {
                }
                //If not solid then...
                //Save for undo function
                if (getBlockIdAt(bx, by, bz) != 17) {
                    h.put(clampY(bx, by, bz));

                    //Place log block.
                    clampY(bx, by, bz).setTypeIdAndData(17, woodType, false);
                } else {
                    //If solid then...
                    //End loop
                    break;
                }

                //Checks is block below is solid
                if (clampY(bx, by - 1, bz).getType() == Material.AIR
                        || clampY(bx, by - 1, bz).getType() == Material.WATER
                        || clampY(bx, by - 1, bz).getType() == Material.STATIONARY_WATER
                        || clampY(bx, by - 1, bz).getType() == Material.SNOW
                        || clampY(bx, by - 1, bz).getType() == Material.LOG) {
                    //Mos down if solid.
                    by--;
                    if (rootFloat == true) {
                        if (generate.nextInt(100) < xPreference) {
                            bx += xDirection;
                        }
                        if (generate.nextInt(100) < zPreference) {
                            bz += zDirection;
                        }
                    }
                } else {
                    //If solid then move.
                    if (generate.nextInt(100) < xPreference) {
                        bx += xDirection;
                    }
                    if (generate.nextInt(100) < zPreference) {
                        bz += zDirection;
                    }
                    //Checks if new location is solid, if not then move down.
                    if (clampY(bx, by - 1, bz).getType() == Material.AIR
                            || clampY(bx, by - 1, bz).getType() == Material.WATER
                            || clampY(bx, by - 1, bz).getType() == Material.STATIONARY_WATER
                            || clampY(bx, by - 1, bz).getType() == Material.SNOW
                            || clampY(bx, by - 1, bz).getType() == Material.LOG) {
                        by--;
                    }
                }
            }

            //Reset origin.
            bx = originX;
            by = originY;
            bz = originZ;

        }
    }

    /*
     *
     * Code Concerning Trunk Generation
     *
     */
    public void trunkGen() {

        //Sets Origin
        int originX = bx;
        int originY = by;
        int originZ = bz;

        //----------
        //Main Trunk
        //----------
        //Sets diretional preferences.
        int xPreference = generate.nextInt(slopeChance);
        int zPreference = generate.nextInt(slopeChance);

        //Sets direction.
        int xDirection = 1;
        if (generate.nextInt(100) < 50) {
            xDirection = -1;
        }

        int zDirection = 1;
        if (generate.nextInt(100) < 50) {
            zDirection = -1;
        }

        //Generates a height for trunk.
        int height = generate.nextInt(heightMaximum - heightMininmum + 1) + heightMininmum;

        for (int p = 0; p < height; p++) {
            if (p > 3) {
                if (generate.nextInt(100) <= twistChance) {
                    xDirection *= -1;
                }
                if (generate.nextInt(100) <= twistChance) {
                    zDirection *= -1;
                }
                if (generate.nextInt(100) < xPreference) {
                    bx += 1 * xDirection;
                }
                if (generate.nextInt(100) < zPreference) {
                    bz += 1 * zDirection;
                }
            }

            //Creates trunk section
            trunkCreate();

            //Mos up for next section
            by++;
        }

        //Generates branchs at top of trunk for each quadrant.
        branchCreate(1, 1);
        branchCreate(-1, 1);
        branchCreate(1, -1);
        branchCreate(-1, -1);

        //Reset Origin for next trunk.
        bx = originX;
        by = originY + 4;
        bz = originZ;

        //---------------
        //Secondary Trunk
        //---------------
        //Sets diretional preferences.
        xPreference = generate.nextInt(slopeChance);
        zPreference = generate.nextInt(slopeChance);

        //Sets direction.
        xDirection = 1;
        if (generate.nextInt(100) < 50) {
            xDirection = -1;
        }

        zDirection = 1;
        if (generate.nextInt(100) < 50) {
            zDirection = -1;
        }

        //Generates a height for trunk.
        height = generate.nextInt(heightMaximum - heightMininmum + 1) + heightMininmum;

        if (height > 4) {
            for (int p = 0; p < height; p++) {
                if (generate.nextInt(100) <= twistChance) {
                    xDirection *= -1;
                }
                if (generate.nextInt(100) <= twistChance) {
                    zDirection *= -1;
                }
                if (generate.nextInt(100) < xPreference) {
                    bx += 1 * xDirection;
                }
                if (generate.nextInt(100) < zPreference) {
                    bz += 1 * zDirection;
                }

                //Creates a trunk section
                trunkCreate();

                //Mos up for next section
                by++;
            }

            //Generates branchs at top of trunk for each quadrant.
            branchCreate(1, 1);
            branchCreate(-1, 1);
            branchCreate(1, -1);
            branchCreate(-1, -1);
        }
    }

    //Branch Creation based on direction chosen from the parameters passed.
    public void branchCreate(int xDirection, int zDirection) {

        //Sets branch origin.
        int originX = bx;
        int originY = by;
        int originZ = bz;

        //Sets direction preference.
        int xPreference = generate.nextInt(60) + 20;
        int zPreference = generate.nextInt(60) + 20;

        //Iterates according to branch length.
        for (int r = 0; r < branchLength; r++) {

            //Alters direction according to preferences.
            if (generate.nextInt(100) < xPreference) {
                bx += 1 * xDirection;
            }
            if (generate.nextInt(100) < zPreference) {
                bz += 1 * zDirection;
            }

            //50% chance to increase elevation every second block.
            if (Math.abs(r % 2) == 1) {
                by += generate.nextInt(2);
            }

            //Add block to undo function.
            if (getBlockIdAt(bx, by, bz) != 17) {
                h.put(clampY(bx, by, bz));
            }

            //Creates a branch block.
            clampY(bx, by, bz).setTypeIdAndData(17, woodType, false);
            branchBlocks.add(clampY(bx, by, bz));
        }

        //Resets the origin
        bx = originX;
        by = originY;
        bz = originZ;
    }

    public void trunkCreate() {

        //Creates true circle discs of the set size using the wood type selected.
        double bpow = Math.pow(thickness + 0.5, 2);
        for (int x = thickness; x >= 0; x--) {
            double xpow = Math.pow(x, 2);
            for (int z = thickness; z >= 0; z--) {
                if ((xpow + Math.pow(z, 2)) <= bpow) {
                    //If block is air, then create a block.
                    if (w.getBlockTypeIdAt(bx + x, by, bz + z) == 0) {
                        //Adds block to undo function.
                        if (getBlockIdAt(bx + x, by, bz + z) != 17) {
                            h.put(clampY(bx + x, by, bz + z));
                        }
                        //Creates block.
                        clampY(bx + x, by, bz + z).setTypeIdAndData(17, woodType, false);
                    }
                    if (w.getBlockTypeIdAt(bx + x, by, bz - z) == 0) {
                        if (getBlockIdAt(bx + x, by, bz - z) != 17) {
                            h.put(clampY(bx + x, by, bz - z));
                        }
                        clampY(bx + x, by, bz - z).setTypeIdAndData(17, woodType, false);
                    }
                    if (w.getBlockTypeIdAt(bx - x, by, bz + z) == 0) {
                        if (getBlockIdAt(bx - x, by, bz + z) != 17) {
                            h.put(clampY(bx - x, by, bz + z));
                        }
                        clampY(bx - x, by, bz + z).setTypeIdAndData(17, woodType, false);
                    }
                    if (w.getBlockTypeIdAt(bx - x, by, bz - z) == 0) {
                        if (getBlockIdAt(bx - x, by, bz - z) != 17) {
                            h.put(clampY(bx - x, by, bz - z));
                        }
                        clampY(bx - x, by, bz - z).setTypeIdAndData(17, woodType, false);
                    }
                }
            }
        }
    }

    public void leafNodeCreate() {

        //Lowers the current block in order to start at the bottom of the node.
        by -= 2;

        //Generates the node size.
        int nodeRadius = generate.nextInt(nodeMax - nodeMin + 1) + nodeMin;

        double bpow = Math.pow(nodeRadius + 0.5, 2);
        for (int z = nodeRadius; z >= 0; z--) {
            double zpow = Math.pow(z, 2);
            for (int x = nodeRadius; x >= 0; x--) {
                double xpow = Math.pow(x, 2);
                for (int y = nodeRadius; y >= 0; y--) {
                    if ((xpow + Math.pow(y, 2) + zpow) <= bpow) {
                        //Chance to skip creation of a block.
                        if (generate.nextInt(100) >= 30) {
                            //If block is Air, create a leaf block.
                            if (w.getBlockTypeIdAt(bx + x, by + y, bz + z) == 0) {
                                //Adds block to undo function.
                                if (getBlockIdAt(bx + x, by + y, bz + z) != 18) {
                                    h.put(clampY(bx + x, by + y, bz + z));
                                }
                                //Creates block.
                                clampY(bx + x, by + y, bz + z).setTypeIdAndData(18, leafType, false);
                            }
                        }
                        if (generate.nextInt(100) >= 30) {
                            if (w.getBlockTypeIdAt(bx + x, by + y, bz - z) == 0) {
                                if (getBlockIdAt(bx + x, by + y, bz - z) != 18) {
                                    h.put(clampY(bx + x, by + y, bz - z));
                                }
                                clampY(bx + x, by + y, bz - z).setTypeIdAndData(18, leafType, false);
                            }
                        }
                        if (generate.nextInt(100) >= 30) {
                            if (w.getBlockTypeIdAt(bx - x, by + y, bz + z) == 0) {
                                if (getBlockIdAt(bx - x, by + y, bz + z) != 18) {
                                    h.put(clampY(bx - x, by + y, bz + z));
                                }
                                clampY(bx - x, by + y, bz + z).setTypeIdAndData(18, leafType, false);
                            }
                        }
                        if (generate.nextInt(100) >= 30) {
                            if (w.getBlockTypeIdAt(bx - x, by + y, bz - z) == 0) {
                                if (getBlockIdAt(bx - x, by + y, bz - z) != 18) {
                                    h.put(clampY(bx - x, by + y, bz - z));
                                }
                                clampY(bx - x, by + y, bz - z).setTypeIdAndData(18, leafType, false);
                            }
                        }
                        if (generate.nextInt(100) >= 30) {
                            if (w.getBlockTypeIdAt(bx + x, by - y, bz + z) == 0) {
                                if (getBlockIdAt(bx + x, by - y, bz + z) != 18) {
                                    h.put(clampY(bx + x, by - y, bz + z));
                                }
                                clampY(bx + x, by - y, bz + z).setTypeIdAndData(18, leafType, false);
                            }
                        }
                        if (generate.nextInt(100) >= 30) {
                            if (w.getBlockTypeIdAt(bx + x, by - y, bz - z) == 0) {
                                if (getBlockIdAt(bx + x, by - y, bz - z) != 18) {
                                    h.put(clampY(bx + x, by - y, bz - z));
                                }
                                clampY(bx + x, by - y, bz - z).setTypeIdAndData(18, leafType, false);
                            }
                        }
                        if (generate.nextInt(100) >= 30) {
                            if (w.getBlockTypeIdAt(bx - x, by - y, bz + z) == 0) {
                                if (getBlockIdAt(bx - x, by - y, bz + z) != 18) {
                                    h.put(clampY(bx - x, by - y, bz + z));
                                }
                                clampY(bx - x, by - y, bz + z).setTypeIdAndData(18, leafType, false);
                            }
                        }
                        if (generate.nextInt(100) >= 30) {
                            if (w.getBlockTypeIdAt(bx - x, by - y, bz - z) == 0) {
                                if (getBlockIdAt(bx - x, by - y, bz - z) != 18) {
                                    h.put(clampY(bx - x, by - y, bz - z));
                                }
                                clampY(bx - x, by - y, bz - z).setTypeIdAndData(18, leafType, false);
                            }
                        }
                    }
                }
            }
        }
    }
}
