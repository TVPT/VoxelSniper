package com.thevoxelbox.voxelsniper.brush;

import java.util.Random;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * 
 * @author Gavjenks
 */
public class FreezeRay extends Brush {

    int height = 5;

    double frequency = 200;

    double sigmoid = .5;

    double rr = 0;
    private static int timesUsed = 0;

    public FreezeRay() {
        this.name = "Freeze Ray";
    }

    public final void FreezeRay(final vData v) {
        // This is designed as a purely entertaining brush to use to destroy things you hate in a dramatic fashion. E.g. Daro builds. Just using a /b br with
        // fire is not nearly as much fun as this:
        // Basically freezes water, quenches lava and torches, encases everything solid with a sheath of ice, generates random ice crystals that destroy
        // anything they intersect with, and then covers everything inside and out with snow.

        final int bsize = v.brushSize;

        final vUndo h = new vUndo(this.tb.getWorld().getName());
        int octant = 0;
        int octX = 0;
        int octY = 0;
        int octZ = 0;
        final int r = 0;
        /*
         * byte[][][] crystallized = new byte [bsize+1][bsize+1][bsize+1]; //will make it so that new crystals will not form over one another, thus not storing
         * ice into the undo memory for (int q = 0; q < bsize + 1; q++) { for (int w = 0; w < bsize + 1; w++) { for (int e = 0; e < bsize + 1; e++) {
         * crystallized[q][w][e] = 0; } } }
         */

        final Random generator = new Random();
        final double bpow = Math.pow(bsize + 0.5, 2);
        for (int z = bsize; z >= 0; z--) {
            for (int x = bsize; x >= 0; x--) {
                for (int y = bsize; y >= 0; y--) {
                    if ((Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)) <= bpow) {
                        for (int i = 1; i < 9; i++) { // this just avoids me copying and pasting all those huge bits of code below 8 times. More compact to look
                                                      // at.
                            if (i == 1) {
                                octX = this.bx + x;
                                octY = this.by + z;
                                octZ = this.bz + y;
                            }
                            if (i == 2) {
                                octX = this.bx + x;
                                octY = this.by + z;
                                octZ = this.bz - y;
                            }
                            if (i == 3) {
                                octX = this.bx + x;
                                octY = this.by - z;
                                octZ = this.bz + y;
                            }
                            if (i == 4) {
                                octX = this.bx + x;
                                octY = this.by - z;
                                octZ = this.bz - y;
                            }
                            if (i == 5) {
                                octX = this.bx - x;
                                octY = this.by + z;
                                octZ = this.bz + y;
                            }
                            if (i == 6) {
                                octX = this.bx - x;
                                octY = this.by + z;
                                octZ = this.bz - y;
                            }
                            if (i == 7) {
                                octX = this.bx - x;
                                octY = this.by - z;
                                octZ = this.bz + y;
                            }
                            if (i == 8) {
                                octX = this.bx - x;
                                octY = this.by - z;
                                octZ = this.bz - y;
                            }

                            octant = this.getBlockIdAt(octX, octY, octZ);

                            // Lava to obsidian
                            if (octant == 10 || octant == 11) {
                                h.put(this.clampY(octX, octY, octZ));
                                this.setBlockIdAt(49, octX, octY, octZ);
                            }

                            // Douse any fires
                            if (octant == 51) {
                                h.put(this.clampY(octX, octY, octZ));
                                this.setBlockIdAt(0, octX, octY, octZ);
                            }

                            // freeze solid blocks
                            this.rr = generator.nextDouble() * this.frequency;
                            if (r > 1 && r < (this.frequency * 0.8) && octant != 0) { // if no crystal, still 80% chance of freezing
                                h.put(this.clampY(octX, octY, octZ));
                                this.setBlockIdAt(79, octX, octY, octZ);
                            }

                            // Add destructive random ice crystals
                            if (r == 1 && octant != 0) {
                                // if (r == 1 && crystallized[octX][octY][octZ] == 0) { //0.5%% chance per block to make a small vertical elliptical sort of ice
                                // crystal centered at that point, which overwrites other stuff, if not other crystals too near by
                                // for (int a = octX-2; a < octX+3; a++) {
                                // for (int b = octZ-2; b < octZ+3; b++) {
                                // for (int c = octY-4; c < octY+5; c++) {
                                // try {
                                // crystallized[a][c][b] = 1; //store location of forbidden crystal sites so far
                                // } catch (IndexOutOfBoundsException e) {
                                // v.sendMessage(ChatColor.RED + "Matrix out of bounds. [debug]");
                                // }
                                // }
                                // }
                                // }
                                // sigmoid heigh modifier

                                // actual crystal

                                this.rr = generator.nextDouble() * this.height; // ice crystal of random height up to the set height from any solid base
                                final double radiusTop = (Math.pow(x, 2) + Math.pow(z, 2));
                                final double modifier = 1 / (1 + Math.pow(radiusTop, (-this.sigmoid))); // this should be euclidean to center of brush looking
                                                                                                        // down 2d, not rr
                                this.rr = modifier * this.rr;

                                // top and bottom
                                h.put(this.clampY(octX, octY + r, octZ));
                                this.setBlockIdAt(79, octX, octY + r, octZ);
                                h.put(this.clampY(octX, octY, octZ));
                                this.setBlockIdAt(79, octX, octY, octZ);
                                // column
                                for (int f = 1; f < this.rr; f++) {
                                    h.put(this.clampY(octX, octY + f, octZ));
                                    this.setBlockIdAt(79, octX, octY + f, octZ);
                                    h.put(this.clampY(octX + 1, octY + f, octZ));
                                    this.setBlockIdAt(79, octX + 1, octY + f, octZ);
                                    h.put(this.clampY(octX - 1, octY + f, octZ));
                                    this.setBlockIdAt(79, octX - 1, octY + f, octZ);
                                    h.put(this.clampY(octX, octY + f, octZ + 1));
                                    this.setBlockIdAt(79, octX, octY + f, octZ + 1);
                                    h.put(this.clampY(octX, octY + f, octZ - 1));
                                    this.setBlockIdAt(79, octX, octY + f, octZ - 1);
                                }

                            }

                            // water to ice
                            if (octant == 8 || octant == 9) {
                                h.put(this.clampY(octX, octY, octZ));
                                this.setBlockIdAt(79, octX, octY, octZ);
                            }

                            // quench torches
                            if (octant == 50) {
                                h.put(this.clampY(octX, octY, octZ));
                                this.setBlockIdAt(0, octX, octY, octZ);
                            }

                            // Anything solid (besides other ice) encase with ice (fill directly adjacent air blocks with ice)
                            // if (octant == 0 && (getBlockIdAt(octX, octY, octZ-1) != 0 || getBlockIdAt(octX, octY, octZ+1) != 0 || getBlockIdAt(octX+1, octY,
                            // octZ) != 0 || getBlockIdAt(octX-1, octY, octZ) != 0 || getBlockIdAt(octX, octY-1, octZ) != 0 || getBlockIdAt(octX, octY+1, octZ)
                            // != 0) && (getBlockIdAt(octX, octY, octZ-1) != 79 && getBlockIdAt(octX, octY, octZ+1) != 79 && getBlockIdAt(octX+1, octY, octZ) !=
                            // 79 && getBlockIdAt(octX-1, octY, octZ) != 79 && getBlockIdAt(octX, octY-1, octZ) != 79 && getBlockIdAt(octX, octY+1, octZ) !=
                            // 79)) {
                            // setBlockIdAt(79, octX, octY, octZ);
                            // }

                            // grass to dirt
                            // if (octant == 2) {
                            // h.put(clampY(octX, octY, octZ));
                            // setBlockIdAt(3, octX, octY, octZ);
                            // }

                            // Snow on everything (tops okf crystals are handled above)
                            if (octant == 0 && this.getBlockIdAt(octX, octY - 1, octZ) != 0 && this.getBlockIdAt(octX, octY - 1, octZ) != 78) {
                                h.put(this.clampY(octX, octY, octZ));
                                this.setBlockIdAt(78, octX, octY, octZ);
                            }
                        } // end for loop for 8 octants
                    }// end if for whether it'w in the brush or not.
                }// Y
            }// X
            v.storeUndo(h);
        }// Z
    }

    @Override
    public final int getTimesUsed() {
        return FreezeRay.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        vm.size();
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.LIGHT_PURPLE + "Freeze Ray Parameters:");
            v.sendMessage(ChatColor.BLUE + "h[number] (ex:  h20) Maximum crystal height");
            v.sendMessage(ChatColor.GOLD
                    + "s[number] (ex:   s0.9) Sets the hardness of the sigmoid curve.  must be between 0 and 1.  Closer to zero = you will have sudden, big differences in crystal heights near the middle.");
            v.sendMessage(ChatColor.DARK_GREEN + "f[number] (ex:   f200) 1/f = likelihood of a crystal growing out of any given block.");
            return;
        }

        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("h")) {
                this.height = Integer.parseInt(par[x].substring(0));
                v.sendMessage(ChatColor.BLUE + "Max height of crystals set to " + this.height);
            } else if (par[x].startsWith("f")) {
                this.frequency = Double.parseDouble(par[x].substring(0));
                v.sendMessage(ChatColor.DARK_GREEN + "1/f frequency of crystals set to " + this.frequency);
            } else if (par[x].startsWith("s")) {
                this.sigmoid = Double.parseDouble(par[x].substring(0));
                if (this.sigmoid < 0) {
                    this.sigmoid = 0;
                }
                if (this.sigmoid > 1) {
                    this.sigmoid = 1;
                }
                v.sendMessage(ChatColor.GOLD + "Sigmoid set to " + this.sigmoid);
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        FreezeRay.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.FreezeRay(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.FreezeRay(v);
    }
}
