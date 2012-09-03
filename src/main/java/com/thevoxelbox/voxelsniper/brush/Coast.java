package com.thevoxelbox.voxelsniper.brush;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Gavjenks
 * @deprecated Unfinished class.
 */
@Deprecated
public class Coast extends Brush {

    // defaults
    double eccentricity = 0.75; // 1 = centers of blobs can extend one radius out. 0 = centers must be within the original disc. 2= two radii out, etc. Size of
                                // blobs will automatically be between 0.5 radii - 1 radius of the original brush. So to ensure overlap, use eccentricity less
                                // than 0.5.
    int numBlobs = 5; // number of blobs to add on to the original disc brush.
    double sigmoid = 0.5; // hardness of the sigmoid. (harsher vs. softer selection)
    int strength = 10; // amount that the strongest topographic layer will attempt to scrape off.
    int bsize = 3; // brush size

    private static int timesUsed = 0;

    public Coast() {
        this.setName("Coast Creation");
    }

    public final void coast(final SnipeData v) {

        // things to declare in general
        // int bsize = v.brushSize;
        int r = 0; // for random numbers
        final Random generator = new Random();

        // 1) original disc - do it in a matrix mask form
        // actually hold off on this... after #4 will know full range of the brush

        // 2) set radii constraints for blobs
        // actually these will be input parameters directly, probably.
        final double maxBlobCenter = (this.bsize + 0.5) * this.eccentricity;
        final int centerDist = (int) maxBlobCenter;
        final double minBlobRadius = this.bsize * 0.5;
        final int radiusMin = (int) minBlobRadius; // I know... confusing, but I couldn't figure out any simple way to round a number in this crazy language.

        // 3) random center finding
        // derp, just random number generation within the constraints.
        int[][] centers = new int[3 + 1][this.numBlobs + 1];
        for (int i = 1; i == this.numBlobs; i++) { // populate cneterpoints of blobs in an array.
            r = generator.nextInt(centerDist * 2);
            r = r - centerDist; // negative or positive, around our origin
            centers[1][i] = r; // x coord

            r = generator.nextInt(centerDist * 2);
            r = r - centerDist;
            centers[2][i] = r; // z coord

            r = generator.nextInt(this.bsize - radiusMin);
            r = r + radiusMin; // radius of blob
            centers[3][i] = r;
        }

        // 4) create all the blobs and add to same mask
        final int[][] backupCenters = centers;
        Arrays.sort(centers[1]);
        Arrays.sort(centers[2]);
        // first, just take the furthest out centers in each direction and add on the radii of those blobs.
        final int minX = centers[1][0];
        final int maxX = centers[1][this.numBlobs];
        final int minZ = centers[2][0];
        final int maxZ = centers[2][this.numBlobs];
        final byte[][] mask = new byte[maxX - minX + 4][maxZ - minZ + 4]; // +1 just so that both ends of the range are inclusive. +2 more to give a border
                                                                          // around the mask which is needed later, and +1 more to make it so I don't have to
                                                                          // remember to type in annoying -1'world all the time to access arrays.
        centers = backupCenters;

        this.makeblob(mask, 0, 0, this.bsize); // the original disc is included in the mask
        for (int n = 0; n < this.numBlobs; n++) { // fill up the mask with zeros - the union of the original disc plus all blobs.
            this.makeblob(mask, centers[1][n], centers[2][n], centers[3][n]);
        }

        final Undo h = new Undo(this.getTargetBlock().getWorld().getName());

        // kill trees and store top four other block types in memory
        v.sendMessage(ChatColor.LIGHT_PURPLE + "maxX" + maxX);
        v.sendMessage(ChatColor.LIGHT_PURPLE + "minX" + minX);
        v.sendMessage(ChatColor.LIGHT_PURPLE + "maxZ" + maxZ);
        v.sendMessage(ChatColor.LIGHT_PURPLE + "minZ" + minZ);
        final int[][][] memory = new int[maxX - minX + 4][maxZ - minZ + 4][5]; // NOTE: This is not in the normal XYZ order. It is XZY!! To match up more
                                                                               // conveniently with the mask. But keep in mind.
        for (int x = 0; x < maxX - minX + 4; x++) {
            for (int y = 128; y > 0; y--) {
                for (int z = 0; z < maxZ - minZ + 4; z++) {

                    switch (this.getBlockIdAt(x, y, z)) {// kill trees and ice (just to avoid water annoyingness) and plants. Not the most elegant of solutions.
                                                         // But remembering trees and lowering them would make lopsided trees... Would have to memorize their
                                                         // trunk positions and generate brand new ones, or something.
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
                        h.put(this.clampY(x, y, z));
                        this.setBlockIdAt(0, x, y, z);
                        break;

                    default:
                        break;
                    }

                    // top four layers of natural ground types other than liquids
                    if (memory[x][z][5] == 0) { // if column hasn't been memorized already
                        if (this.getBlockIdAt(x, y - 1, z) != 0) { // if not a floating block (like one of Notch'world pools)
                            switch (this.getBlockIdAt(x, y, z)) {
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
                                memory[x][z][5] = 1; // stop it from checking that column any further
                                memory[x][z][1] = this.getBlockIdAt(x, y, z); // memorize top four block types.
                                memory[x][z][2] = this.getBlockIdAt(x, y - 1, z);
                                memory[x][z][3] = this.getBlockIdAt(x, y - 2, z);
                                memory[x][z][4] = this.getBlockIdAt(x, y - 3, z);
                                break;

                            default:
                                h.put(this.clampY(x, y, z)); // destroy anything above the top four layers that isn't natural or is floating blockPositionY itself
                                this.setBlockIdAt(0, x, y, z);
                                break;
                            }
                        }
                    }
                }
            }
        }

        // 7) figure out topographic sub masks
        // current idea, pending testing and resion: First calculate how many steps I need based on strength the user wants and size of brush. Will be an
        // estimate at first, I guess.
        // Then accrete two steps' worth of pixels to the mask from the outside, and then erode one step'world worth. Should result in a smooth-ish next layer. If
        // not, figure out a better smoothing method.
        // Keep track of layer blockPositionY adding a 1 to each point in the mask array that is no longer part of this layer.
        // Repeat until the accretion fills in the entire mask.
        // End result should be a mask with zeros on the outside, then the outermost layer will be [# of layers], e.g. 16, the next layer in will be 15s, etc.
        // The innermost layer = 1s.

        // 7.5) figure out how much to scrape off of each topographic layer blockPositionY applying it to a curve function (sigmoid, bezier, whatever)

        // 8) scrape, but not more than 8 below sea level
        // 9) fix topsoil using the undo (but not the same Y values obviously)
        // 10) fill in water to sea level

        v.storeUndo(h);
    }

    @Override
    public final int getTimesUsed() {
        return Coast.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName("Coast Creation (Different Shape)");
        vm.custom(ChatColor.BLUE + "Eccentricity = 0.75");
        vm.custom(ChatColor.DARK_BLUE + "Blobs = 5");
        vm.custom(ChatColor.GOLD + "Sigmoid = 0.5");
        vm.custom(ChatColor.DARK_GREEN + "Strength = 10");
        vm.size();
    }

    public final void makeblob(final byte[][] array, final int xC, final int yC, final int rC) {

        final double bpow = Math.pow(rC + 0.5, 2);
        for (int x = rC; x >= 0; x--) {
            final double xpow = Math.pow(x, 2);
            for (int y = rC; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                    array[xC + x][yC + y] = 1;
                    // array[xC+x][yC-y] = 1;
                    array[xC - x][yC + y] = 1; // array exception
                    // array[xC-x][yC-y] = 1;
                }
            }
        }
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.LIGHT_PURPLE + "Coast Creation brush. Parameters:");
            v.sendMessage(ChatColor.BLUE
                    + "ecc[number] (ex:  ecc1.2) How far out the random distortions of your brush can be generated.  Units are multiples of the brush size.");
            v.sendMessage(ChatColor.DARK_BLUE
                    + "nb[number] (ex:  nb5) How many 'blobs' your distorted brush will have.  More blobs = smoother, more circular end shape, more or less.");
            v.sendMessage(ChatColor.GOLD
                    + "sig[number] (ex:   sig0.9) Sets the hardness of the sigmoid curve.  must be between 0 and 1.  Closer to zero = you will have sudden, steep cliffs.  Closer to 1 = gentle slopes.");
            v.sendMessage(ChatColor.DARK_GREEN
                    + "str[number] (ex:   str30) Number of blocks that will be attempted to be scraped off the terrain at the strongest point of the brush (the center).");
            return;
        }

        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("ecc")) {
                this.eccentricity = Double.parseDouble(par[x].substring(0));
            } else if (par[x].startsWith("nb")) {
                this.numBlobs = Integer.parseInt(par[x].substring(0));
            } else if (par[x].startsWith("sig")) {
                this.sigmoid = Double.parseDouble(par[x].substring(0));
            } else if (par[x].startsWith("str")) {
                this.strength = Integer.parseInt(par[x].substring(0));
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Coast.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.coast(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getLastBlock().getX());
        this.setBlockPositionY(this.getLastBlock().getY());
        this.setBlockPositionZ(this.getLastBlock().getZ());
        this.coast(v);
    }
}
