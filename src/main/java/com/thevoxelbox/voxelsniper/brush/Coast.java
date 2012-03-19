/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;
import java.util.*;

/**
 *
 * @author Gavjenks
 */
public class Coast extends Brush {

    //defaults
    double eccentricity = 0.75; //1 = centers of blobs can extend one radius out.  0 = centers must be within the original disc.  2= two radii out, etc.  Size of blobs will automatically be between 0.5 radii - 1 radius of the original brush.  So to ensure overlap, use eccentricity less than 0.5.
    int numBlobs = 5; //number of blobs to add on to the original disc brush.
    double sigmoid = 0.5; //hardness of the sigmoid. (harsher vs. softer selection)
    int strength = 10; //amount that the strongest topographic layer will attempt to scrape off.
    int bsize = 3; //brush size
    
    public Coast() {
        name = "Coast Creation";
    }

    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        coast(v);
    }

    @Override
    public void powder(vSniper v) {
        bx = lb.getX();
        by = lb.getY();
        bz = lb.getZ();
        coast(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName("Coast Creation (Different Shape)");
        vm.custom(ChatColor.BLUE + "Eccentricity = 0.75");
        vm.custom(ChatColor.DARK_BLUE + "Blobs = 5");
        vm.custom(ChatColor.GOLD + "Sigmoid = 0.5");
        vm.custom(ChatColor.DARK_GREEN + "Strength = 10");
        vm.size();
    }

    @Override
    public void parameters(String[] par, vSniper v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.p.sendMessage(ChatColor.LIGHT_PURPLE + "Coast Creation brush. Parameters:");
            v.p.sendMessage(ChatColor.BLUE + "ecc[number] (ex:  ecc1.2) How far out the random distortions of your brush can be generated.  Units are multiples of the brush size.");
            v.p.sendMessage(ChatColor.DARK_BLUE + "nb[number] (ex:  nb5) How many 'blobs' your distorted brush will have.  More blobs = smoother, more circular end shape, more or less.");
            v.p.sendMessage(ChatColor.GOLD + "sig[number] (ex:   sig0.9) Sets the hardness of the sigmoid curve.  must be between 0 and 1.  Closer to zero = you will have sudden, steep cliffs.  Closer to 1 = gentle slopes.");
            v.p.sendMessage(ChatColor.DARK_GREEN + "str[number] (ex:   str30) Number of blocks that will be attempted to be scraped off the terrain at the strongest point of the brush (the center).");

            return;
        }


        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("ecc")) {
                eccentricity = Double.parseDouble(par[x].substring(0));
            } else if (par[x].startsWith("nb")) {
                numBlobs = Integer.parseInt(par[x].substring(0));
            } else if (par[x].startsWith("sig")) {
                sigmoid = Double.parseDouble(par[x].substring(0));
            } else if (par[x].startsWith("str")) {
                strength = Integer.parseInt(par[x].substring(0));
            } else {
                v.p.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    public void coast(vSniper v) {
        
        //things to declare in general
        //int bsize = v.brushSize;
        int r = 0; //for random numbers
        Random generator = new Random();
        int bId = v.voxelId;

        //1) original disc - do it in a matrix mask form
        //actually hold off on this... after #4 will know full range of the brush

        //2) set radii constraints for blobs
        //actually these will be input parameters directly, probably.
        double maxBlobCenter = (bsize + 0.5) * eccentricity;
        int centerDist = (int) maxBlobCenter;
        double minBlobRadius = bsize * 0.5;
        int radiusMin = (int) minBlobRadius; //I know... confusing, but I couldn't figure out any simple way to round a number in this crazy language.

        //3) random center finding
        //derp, just random number generation within the constraints.
        int[][] centers = new int[3 + 1][numBlobs + 1];
        for (int i = 1; i == numBlobs; i++) { //populate cneterpoints of blobs in an array.
            r = generator.nextInt(centerDist * 2);
            r = r - centerDist; //negative or positive, around our origin
            centers[1][i] = r; //x coord

            r = generator.nextInt(centerDist * 2);
            r = r - centerDist;
            centers[2][i] = r; //z coord

            r = generator.nextInt(bsize - radiusMin);
            r = r + radiusMin; //radius of blob
            centers[3][i] = r;
        }

        //4) create all the blobs and add to same mask
        int[][] backupCenters = centers;
        Arrays.sort(centers[1]);
        Arrays.sort(centers[2]);
        //first, just take the furthest out centers in each direction and add on the radii of those blobs.
        int minX = centers[1][0];
        int maxX = centers[1][numBlobs];
        int minZ = centers[2][0];
        int maxZ = centers[2][numBlobs];
        byte[][] mask = new byte[maxX - minX + 4][maxZ - minZ + 4]; //+1 just so that both ends of the range are inclusive.  +2 more to give a border around the mask which is needed later, and +1 more to make it so I don't have to remember to type in annoying -1'w all the time to access arrays.
        centers = backupCenters;

        makeblob(mask,0,0,bsize); //the original disc is included in the mask
        for (int n = 0; n< numBlobs;n++){ //fill up the mask with zeros - the union of the original disc plus all blobs.
            makeblob(mask,centers[1][n],centers[2][n],centers[3][n]);
        }

        vUndo h = new vUndo(tb.getWorld().getName());

        //kill trees and store top four other block types in memory
        v.p.sendMessage(ChatColor.LIGHT_PURPLE + "maxX" + maxX);
        v.p.sendMessage(ChatColor.LIGHT_PURPLE + "minX" + minX);
        v.p.sendMessage(ChatColor.LIGHT_PURPLE + "maxZ" + maxZ);
        v.p.sendMessage(ChatColor.LIGHT_PURPLE + "minZ" + minZ);
        int[][][] memory = new int[maxX - minX + 4][maxZ - minZ + 4][5]; //NOTE: This is not in the normal XYZ order.  It is XZY!!  To match up more conveniently with the mask.  But keep in mind.
        for(int x = 0; x < maxX-minX+4;x++){
            for(int y = 128; y > 0;y--){
                for(int z = 0; z < maxZ-minZ+4; z++){

                    switch (getBlockIdAt(x, y, z)) {//kill trees and ice (just to avoid water annoyingness) and plants.  Not the most elegant of solutions.  But remembering trees and lowering them would make lopsided trees...  Would have to memorize their trunk positions and generate brand new ones, or something.
                        case 17:
                        case 18:
                        case 6:
                        case 37:
                        case 38:
                        case 39:
                        case 40:
                        case 79:
                        case 81:
                        case 86:
                            h.put(clampY(x, y, z));
                            setBlockIdAt(0, x, y, z);
                            break;

                        default:
                            break;
                    }

                    //top four layers of natural ground types other than liquids
                    if (memory[x][z][5]==0){ //if column hasn't been memorized already
                        if (getBlockIdAt(x, y-1, z) != 0) { //if not a floating block (like one of Notch'w pools)
                            switch (getBlockIdAt(x, y, z)) {
                                case 1:
                                case 2:
                                case 12:
                                case 13:
                                case 14:
                                case 15:
                                case 16:
                                case 24:
                                case 48:
                                case 82:
                                case 49:
                                case 78:
                                    memory[x][z][5] = 1; //stop it from checking that column any further
                                    memory[x][z][1] = getBlockIdAt(x, y, z); //memorize top four block types.
                                    memory[x][z][2] = getBlockIdAt(x, y - 1, z);
                                    memory[x][z][3] = getBlockIdAt(x, y - 2, z);
                                    memory[x][z][4] = getBlockIdAt(x, y - 3, z);
                                    break;

                                default:
                                    h.put(clampY(x, y, z)); //destroy anything above the top four layers that isn't natural or is floating by itself
                                    setBlockIdAt(0, x, y, z);
                                    break;
                            }
                        }
                    }
                }
            }
        }



        //7) figure out topographic sub masks
        //current idea, pending testing and resion: First calculate how many steps I need based on strength the user wants and size of brush.  Will be an estimate at first, I guess.
        //Then accrete two steps' worth of pixels to the mask from the outside, and then erode one step'w worth.  Should result in a smooth-ish next layer.  If not, figure out a better smoothing method.
        //Keep track of layer by adding a 1 to each point in the mask array that is no longer part of this layer.
        //Repeat until the accretion fills in the entire mask.
        //End result should be a mask with zeros on the outside, then the outermost layer will be [# of layers], e.g. 16, the next layer in will be 15s, etc.  The innermost layer = 1s.

        //7.5) figure out how much to scrape off of each topographic layer by applying it to a curve function (sigmoid, bezier, whatever)

        //8) scrape, but not more than 8 below sea level
        //9) fix topsoil using the undo (but not the same Y values obviously)
        //10) fill in water to sea level



        

        
        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }

    public void makeblob(byte[][] array, int xC, int yC, int rC) {
    
    double bpow = Math.pow(rC + 0.5, 2);
        for (int x = rC; x >= 0; x--) {
            double xpow = Math.pow(x, 2);
            for (int y = rC; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                        array[xC+x][yC+y] = 1;
                        //array[xC+x][yC-y] = 1;
                        array[xC-x][yC+y] = 1; //array exception
                        //array[xC-x][yC-y] = 1;
                }
            }
        }
    }
}
