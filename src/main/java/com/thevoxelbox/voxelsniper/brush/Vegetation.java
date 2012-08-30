/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import java.util.EnumMap;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Generates vegitation onto the landscape. Large things are trees and cactus as
 * of now. Bushes, and other things may be added as Minecraft is updated. Small
 * things are grass, flowers, and weeds in deserts.
 *
 * @author geekygenius
 */
public class Vegetation extends Brush {

    private float lgDencity;//Dencity of large things.
    private float smDencity;//Dencity of smaller things
    private float flowerProb;//Chance flowers will spawn rather then grass
    private int brushSize;
    private EnumMap<TreeType, Float> trees; //Type of tree to be generated. Cactus will be used instead of trees if on sand.

    public Vegetation() {
        name = "Vegetation";
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        generate(v);//Generate w/o trees going through vegitation
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        degenerate(v);
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        //Info commands

        if (par[1].equalsIgnoreCase("info") || par[1].equalsIgnoreCase("help")) {
            v.sendMessage(ChatColor.GOLD + "Vegetation brush parameters");
            v.sendMessage(ChatColor.GREEN + "b[number] (ex:  b23) Sets your sniper brush size.");
            v.sendMessage(ChatColor.GREEN + "ld[number] (ex: ld7.5) Sets the % dencity of large foliage.");
            v.sendMessage(ChatColor.GREEN + "sd[number] (ex: sd33.33) Sets the % dencity of small foliage.");
            v.sendMessage(ChatColor.GREEN + "fl[number] (ex: sd10.4) Sets the chance of flowers rather than grass.");
            v.sendMessage(ChatColor.GREEN + "t[number][tree type],[number][tree type] (ex: t:66.66%tree,33.33%bigtree) ");
            v.sendMessage(ChatColor.GREEN + "      Sets the type of tree to be generated, and the chance of them. ");
            v.sendMessage(ChatColor.GREEN + "      Use t:[treeType] for one kind of tree. ");
            v.sendMessage(ChatColor.GREEN + "      Can be: bigTree, birch, redwood, tallredwood, or tree.");
            v.sendMessage(ChatColor.GOLD + "For user-friendly pre-sets, type /b vg info2.");

            return;
        }
        if (par[1].equalsIgnoreCase("info2")) {
            v.sendMessage(ChatColor.GOLD + "User-friendly Preset Options.  These are for the arrow.  Powder will do reverse for the first two (for fast switching):");
            v.sendMessage(ChatColor.GREEN + "/b vg meadow -- for sparce, grass-filled areas.");
            v.sendMessage(ChatColor.GREEN + "/b vg forest -- for medium dencity forests.");
            v.sendMessage(ChatColor.GREEN + "/b vg [desert|thin] -- for desert areas or very thin areas.");
            v.sendMessage(ChatColor.GREEN + "/b vg flower -- generates a flower patch.");
            v.sendMessage(ChatColor.GREEN + "/b vg grass -- generates a patch of grass.");
            v.sendMessage(ChatColor.GREEN + "/b vg patch -- generates a patch of grass and flowers.");

            return;
        }
        for (int x = 1; x < par.length; x++) {
            try {
                //Presets first.
                if (par[x].startsWith("meadow")) {
                    brushSize = 16;
                    flowerProb = 10f;
                    lgDencity = 5f;
                    smDencity = 40f;
                    trees = new EnumMap<TreeType, Float>(TreeType.class);
                    trees.put(TreeType.TREE, 75f);
                    trees.put(TreeType.BIG_TREE, 25f);
                    v.sendMessage(ChatColor.AQUA + "Meadow mode. (/b vg b16 fl10 ld5 sd40 t75%tree,25%bigtree)");
                    continue;
                } else if (par[x].startsWith("forest")) {
                    brushSize = 16;
                    flowerProb = 10f;
                    lgDencity = 40f;
                    smDencity = 10f;
                    trees = new EnumMap<TreeType, Float>(TreeType.class);
                    trees.put(TreeType.TREE, 70f);//DAMN this is easy to use
                    trees.put(TreeType.BIG_TREE, 20f);
                    trees.put(TreeType.BIRCH, 10f);
                    v.sendMessage(ChatColor.AQUA + "Forest mode. (/b vg b16 fl10 ld5 sd40 t70%tree,20%bigtree,10%birch)");
                    continue;
                } else if (par[x].startsWith("desert") || par[x].startsWith("thin")) {
                    brushSize = 16;
                    flowerProb = 0f;
                    lgDencity = 5f;
                    smDencity = 10f;
                    trees = new EnumMap<TreeType, Float>(TreeType.class);
                    v.sendMessage(ChatColor.AQUA + "Derert/thin mode. (/b vg b16 fl0 ld5 sd10 t:normal)");
                    continue;
                } else if (par[x].startsWith("flower")) {
                    brushSize = 5;
                    flowerProb = 100f;
                    lgDencity = 0f;
                    smDencity = 70f;
                    trees = new EnumMap<TreeType, Float>(TreeType.class);
                    v.sendMessage(ChatColor.AQUA + "Flower patch mode. (/b vg b5 fl100 ld0 sd70)");
                    continue;
                } else if (par[x].startsWith("grass")) {
                    brushSize = 5;
                    flowerProb = 0f;
                    lgDencity = 0f;
                    smDencity = 70f;
                    trees = new EnumMap<TreeType, Float>(TreeType.class);
                    v.sendMessage(ChatColor.AQUA + "Grass patch mode. (/b vg b5 fl0 ld0 sd70)");
                    continue;
                } else if (par[x].startsWith("patch")) {
                    brushSize = 5;
                    flowerProb = 25f;
                    lgDencity = 0f;
                    smDencity = 70f;
                    trees = new EnumMap<TreeType, Float>(TreeType.class);
                    v.sendMessage(ChatColor.AQUA + "Patch mode. (/b vg b5 fl25 ld0 sd70)");
                    continue;
                } //Peramiters
                else if (par[x].startsWith("ld")) {
                    lgDencity = Float.parseFloat(par[x].replace("ld", ""));
                    v.sendMessage(ChatColor.BLUE + "Dencity of large vegetation set to " + lgDencity);
                    continue;
                } else if (par[x].startsWith("sd")) {
                    smDencity = Float.parseFloat(par[x].replace("sd", ""));
                    v.sendMessage(ChatColor.BLUE + "Dencity of small vegetation set to " + smDencity);
                    continue;
                } else if (par[x].startsWith("fl")) {
                    smDencity = Float.parseFloat(par[x].replace("fl", ""));
                    v.sendMessage(ChatColor.BLUE + "Chance of flowers set to " + flowerProb);
                    continue;
                } else if (par[x].startsWith("b")) {
                    v.owner().setBrushSize(Integer.parseInt(par[x].replace("b", "")));
                    brushSize = v.brushSize;
                    v.sendMessage(ChatColor.BLUE + "Size of brush set to " + brushSize);
                    continue;
                } else if (par[x].startsWith("t")) {//Parse tree args into the hash map simmalar to sEdit
                    String arg = par[x].replace("t", "");
                    trees = new EnumMap<TreeType, Float>(TreeType.class);//Clear the old map

                    if (arg.startsWith(":")) {//Handle one kind of tree
                        trees.put(getTreeType(arg.replace(":", "")), 100f);
                        continue;
                    }

                    for (String s : arg.split(",")) {//Get each section
                        String[] info = s.split("%");
                        trees.put(getTreeType(info[1]), Float.parseFloat(info[0]));
                    }
                    v.sendMessage(ChatColor.BLUE + "Tree type set to: " + arg);
                    continue;
                }
                continue;
            } catch (Exception e) {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! \"" + par[x] + "\" is not a valid statement. Please use the 'info' parameter to display parameter info.");
            }
        }
    }

    private TreeType getTreeType(String type) {
        if (type.equalsIgnoreCase("bigtree")) {
            return TreeType.BIG_TREE;
        } else if (type.equalsIgnoreCase("birch")) {
            return TreeType.BIRCH;
        } else if (type.equalsIgnoreCase("redwood")) {
            return TreeType.REDWOOD;
        } else if (type.equalsIgnoreCase("tallredwood")) {
            return TreeType.TALL_REDWOOD;
        } else if (type.equalsIgnoreCase("tree") || type.equalsIgnoreCase("normal")) {//Allow normal and tree
            return TreeType.TREE;
        }
        return null;
    }

    private void generate(vData v) {
        brushSize = v.brushSize;
        Block top = null;//Allocate now to save memory
        Block edit;
        Random rand = new Random();
        for (int x = (int) (bx - brushSize / 2); x < (bx + brushSize / 2); x++) {
            for (int z = (int) (bz - brushSize / 2); z < (bz + brushSize / 2); z++) {
                //Do a small Veg pass first
                top = w.getHighestBlockAt(x, z);

                if (rand.nextFloat() < smDencity) {//Do a rand test to see if we can think about putting something here

                    if (rand.nextFloat() < flowerProb) {//Are we generating flowers?
                        if (rand.nextFloat() < .50) {//Red or yellow?- on voxel box blue or green
                            top.getRelative(BlockFace.UP, 1).setType(Material.RED_ROSE);
                        } else {
                            top.getRelative(BlockFace.UP, 1).setType(Material.YELLOW_FLOWER);
                        }
                    } //Grass/Shrub/Small tree thingy
                    else {
                        edit = top.getRelative(BlockFace.UP, 1);
                        edit.setType(Material.LONG_GRASS);//Definitly some kind of long grass

                        //Are we in a desert?
                        if (top.getType() == Material.SAND) {
                            edit.setData((byte) 0, false); //Make it a dead shrub
                        } else {
                            //Give it the chance of being a small redwood as a redwood spawns
                            float total = 0f;
                            Float value = trees.get(TreeType.REDWOOD);
                            if (value == null) {
                                value = 0f;
                            }
                            total += value;
                            value = trees.get(TreeType.TALL_REDWOOD);
                            if (value == null) {
                                value = 0f;
                            }
                            total += value;

                            if (rand.nextFloat() < total) {
                                edit.setData((byte) 2, false); //Make a redwood type veg
                            } else {
                                edit.setData((byte) 1, false);//Make it normal grass
                            }
                        }
                    }
                }

                //Do a large veg pass
                if (rand.nextFloat() < lgDencity) {
                    if (top.getType() == Material.SAND) {
                        //Gen cactus
                        int height = rand.nextInt(1) + 3;//Min of 3, max of 4
                        for (int i = 0; i < height; i++) {//Reviewer: can I put the height into the for? if so, put it there please. If not, remove this comment. I think its if not for the record.
                            top = top.getRelative(BlockFace.UP, 1);
                            top.setType(Material.CACTUS);
                        }
                    }

                    if (top.getType() == Material.GRASS) {
                        TreeType type = TreeType.TREE;//Normal by deafult

                        //Pick a random tree type from the list.
                        float selection = rand.nextFloat();

                        float total = 0;
                        for (TreeType t : trees.keySet()) {
                            total += trees.get(t);
                            if (total > selection) {
                                type = t;
                                break;
                            }
                        }

                        w.generateTree(new Location(w, x, w.getHighestBlockYAt(x, z) + 1, z), type);
                    }//No warning, many trees may be generated
                }
            }
        }
    }

    private void degenerate(vData v) {
        //Remove forest\//TODO make this do something :P
    }
    
    private static int timesUsed = 0;
	
    @Override
	public int getTimesUsed() {
		return timesUsed;
	}

	@Override
	public void setTimesUsed(int tUsed) {
		timesUsed = tUsed; 
	}
}
