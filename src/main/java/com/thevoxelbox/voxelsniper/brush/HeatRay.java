/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import java.util.Random;

/**
 * THIS BRUSH SHOULD NOT USE PERFORMERS
 *
 * @author Gavjenks (derived from Piotr'w ball replace brush)
 */
public class HeatRay extends Brush {

    public HeatRay() {
        name = "Heat Ray";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        HeatRay(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        HeatRay(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
    }

    public void HeatRay(vData v) {

        //This is designed as a purely entertaining brush to use to destroy things you hate in a dramatic fashion.  E.g. Daro builds.  Just using a /b br with fire is not nearly as much fun as this:
        //Basically burns anything that would seem flammable in real life, smelts stone-like things into stone (or 10% chance of destroyign outright or charring into obsidian), kills plants, turns grass to dirt, evaporates water and snow and ice, turns sandy things to glass.
        //All the stuff one would expect from a sphere of intense heat...

        int bsize = v.brushSize;

        vUndo h = new vUndo(tb.getWorld().getName());
        int octant = 0;
        int octX = 0;
        int octY = 0;
        int octZ = 0;
        int r = 0;
        Random generator = new Random();
        double bpow = Math.pow(bsize + 0.5, 2);
        for (int z = bsize; z >= 0; z--) {
            for (int x = bsize; x >= 0; x--) {
                for (int y = bsize; y >= 0; y--) {
                    if ((Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)) <= bpow) {
                        for (int i = 1; i < 9; i++) { //this just avoids me copying and pasting all those huge bits of code below 8 times.  More compact to look at. -- You should make this a switch() {case(): } block, faster to use
                            if (i == 1) {
                                octX = bx + x;
                                octY = by + z;
                                octZ = bz + y;
                            }
                            if (i == 2) {
                                octX = bx + x;
                                octY = by + z;
                                octZ = bz - y;
                            }
                            if (i == 3) {
                                octX = bx + x;
                                octY = by - z;
                                octZ = bz + y;
                            }
                            if (i == 4) {
                                octX = bx + x;
                                octY = by - z;
                                octZ = bz - y;
                            }
                            if (i == 5) {
                                octX = bx - x;
                                octY = by + z;
                                octZ = bz + y;
                            }
                            if (i == 6) {
                                octX = bx - x;
                                octY = by + z;
                                octZ = bz - y;
                            }
                            if (i == 7) {
                                octX = bx - x;
                                octY = by - z;
                                octZ = bz + y;
                            }
                            if (i == 8) {
                                octX = bx - x;
                                octY = by - z;
                                octZ = bz - y;
                            }

                            octant = getBlockIdAt(octX, octY, octZ);

                            //Air with something below it to fire 50%, for sual effect
                            if (octant == 0 && getBlockIdAt(octX, octY - 1, octZ) != 0) {
                                r = generator.nextInt(10);
                                if (r < 6) {
                                    setBlockIdAt(51, octX, octY, octZ); //no need for undo, since fire will burn out.
                                }
                            }

                            //flammable stuff to fire
                            if (octant == 81 || octant == 83 || octant == 92 || octant == 76 || octant == 75 || octant == 84 || octant == 85 || octant == 86 || octant == 91 || octant == 5 || octant == 6 || octant == 17 || octant == 18 || octant == 19 || octant == 25 || octant == 26 || octant == 35 || octant == 27 || octant == 28 || octant == 35 || octant == 39 || octant == 40 || octant == 47 || octant == 50 || octant == 53 || octant == 54 || octant == 55 || octant == 58 || octant == 59 || octant == 63 || octant == 64 || octant == 65 || octant == 66 || octant == 68 || octant == 69 || octant == 72 || octant == 44) {
                                h.put(clampY(octX, octY, octZ));
                                setBlockIdAt(51, octX, octY, octZ);
                            }
                            //meltable things to air, without flames
                            if (octant == 78 || octant == 89 || octant == 71 || octant == 79 || octant == 80 || octant == 56 || octant == 73 || octant == 86 || octant == 91 || octant == 41 || octant == 42 || octant == 20) {
                                h.put(clampY(octX, octY, octZ));
                                setBlockIdAt(0, octX, octY, octZ);
                            }
                            //grass to dirt
                            if (octant == 2 || octant == 60) {
                                h.put(clampY(octX, octY, octZ));
                                setBlockIdAt(3, octX, octY, octZ);
                            }

                            //dirt can be destroyed
                            if (octant == 2) {
                                r = generator.nextInt(10);
                                if (r < 4) { //30% chance of destroying dirt
                                    h.put(clampY(octX, octY, octZ));
                                    setBlockIdAt(0, octX, octY, octZ);
                                }
                            }

                            //evaporate non ocean water
                            if ((octant == 8 || octant == 9) && octY > 63) {
                                h.put(clampY(octX, octY, octZ));
                                setBlockIdAt(0, octX, octY, octZ);
                            }

                            //stone mats to cobble, air, or obsidian
                            if (octant == 70 || octant == 12 || octant == 24 || octant == 49 || octant == 1 || octant == 4 || octant == 12 || octant == 24 || octant == 67 || octant == 61 || octant == 62 || octant == 48 || octant == 45 || octant == 43 || octant == 23 || octant == 13 || octant == 7 || octant == 14 || octant == 15 || octant == 16 || octant == 21) {
                                h.put(clampY(octX, octY, octZ));
                                r = generator.nextInt(10);
                                if (r < 2) { //10% chance of destroying stone stuff
                                    setBlockIdAt(0, octX, octY, octZ);
                                } else if (r > 2 && r < 5) { //20% chance of turning stone stuff to obsidian (might get rid of if ugly)
                                    setBlockIdAt(49, octX, octY, octZ);
                                } else {
                                    setBlockIdAt(4, octX, octY, octZ);
                                }
                            }

                        } //end for loop for 8 octants

                    }//end if for whether it'w in the brush or not.
                }//Y
            }//X
            v.storeUndo(h);
        }//Z
    }
}