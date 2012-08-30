/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.uBlock;
import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * THIS BRUSH SHOULD NOT USE PERFORMERS
 *
 * @author Piotr
 */
public class Erode extends Brush {

    private eBlock[][][] snap;
    private eBlock[][][] firstSnap;
    private int bsize;
    private int erodeFace;
    private int fillFace;
    private int brushSize;
    private int erodeRecursion = 1;
    private int fillRecursion = 1;
    private double trueCircle = 0.5;
    private boolean reverse = false;

    public Erode() {
        name = "Erode";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();

        bsize = v.brushSize;

        snap = new eBlock[0][0][0];
        reverse = false;

        erosion(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();

        bsize = v.brushSize;

        snap = new eBlock[0][0][0];
        reverse = true;

        erosion(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
        vm.custom(ChatColor.RED + "Litesnipers: This is a slow brush.  DO NOT SPAM it too much or hold down the mouse. ");
        vm.custom(ChatColor.AQUA + "Erosion minimum exposed faces set to " + erodeFace);
        vm.custom(ChatColor.BLUE + "Fill minumum touching faces set to " + fillFace);
        vm.custom(ChatColor.DARK_BLUE + "Erosion recursion amount set to " + erodeRecursion);
        vm.custom(ChatColor.DARK_GREEN + "Fill recursion amount set to " + fillRecursion);
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Erode brush parameters");
            v.sendMessage(ChatColor.RED + "NOT for litesnipers:");
            v.sendMessage(ChatColor.GREEN + "b[number] (ex:   b23) Sets your sniper brush size.");
            v.sendMessage(ChatColor.AQUA + "e[number] (ex:  e3) Sets the number of minimum exposed faces to erode a block.");
            v.sendMessage(ChatColor.BLUE + "f[number] (ex:  f5) Sets the number of minumum faces containing a block to place a block.");
            v.sendMessage(ChatColor.DARK_BLUE + "re[number] (ex:  re3) Sets the number of recursions the brush will perform erosion.");
            v.sendMessage(ChatColor.DARK_GREEN + "rf[number] (ex:  rf5) Sets the number of recursions the brush will perform filling.");
            v.sendMessage(ChatColor.AQUA + "/b d false -- will turn off true circle algorithm /b b true will switch back. (true is default for this brush.)");
            v.sendMessage(ChatColor.GOLD + "For user-friendly pre-sets, type /b e info2.");
            return;
        }
        if (par[1].equalsIgnoreCase("info2")) {
            v.sendMessage(ChatColor.GOLD + "User-friendly Preset Options.  These are for the arrow.  Powder will do reverse for the first two (for fast switching):");
            v.sendMessage(ChatColor.BLUE + "OK for litesnipers:");
            v.sendMessage(ChatColor.GREEN + "/b e melt -- for melting away protruding corners and edges.");
            v.sendMessage(ChatColor.AQUA + "/b e fill -- for building up inside corners");
            v.sendMessage(ChatColor.AQUA + "/b e smooth -- For the most part, does not change total number of blocks, but smooths the shape nicely.  Use as a finishing touch for the most part, before overlaying grass and trees, etc.");
            v.sendMessage(ChatColor.BLUE + "/b e lift-- More or less raises each block in the brush area by one");  // Giltwist
            return;
        }
        for (int x = 1; x < par.length; x++) {
            try {
                if (par[x].startsWith("melt")) {
                    fillRecursion = 1;
                    erodeRecursion = 1;
                    fillFace = 5;
                    erodeFace = 2;
                    v.owner().setBrushSize(10);
                    v.sendMessage(ChatColor.AQUA + "Melt mode. (/b e e2 f5 re1 rf1 b10)");
                    continue;
                } else if (par[x].startsWith("fill")) {
                    fillRecursion = 1;
                    erodeRecursion = 1;
                    fillFace = 2;
                    erodeFace = 5;
                    v.owner().setBrushSize(8);
                    v.sendMessage(ChatColor.AQUA + "Fill mode. (/b e e5 f2 re1 rf1 b8)");
                    continue;
                } else if (par[x].startsWith("smooth")) {
                    fillRecursion = 1;
                    erodeRecursion = 1;
                    fillFace = 3;
                    erodeFace = 3;
                    v.owner().setBrushSize(16);
                    v.sendMessage(ChatColor.AQUA + "Smooth mode. (/b e e3 f3 re1 rf1 b16)");
                    continue;
                } else if (par[x].startsWith("lift")) { //Giltwist
                    fillRecursion = 1;
                    erodeRecursion = 0;
                    fillFace = 1;
                    erodeFace = 6;
                    v.owner().setBrushSize(10);
                    v.sendMessage(ChatColor.AQUA + "Lift mode. (/b e e6 f1 re0 rf1 b10)");
                    continue;
                } else if (par[x].startsWith("true")) {
                    trueCircle = 0.5;
                    v.sendMessage(ChatColor.AQUA + "True circle mode ON." + erodeRecursion);
                    continue;
                } else if (par[x].startsWith("false")) {
                    trueCircle = 0;
                    v.sendMessage(ChatColor.AQUA + "True circle mode OFF." + erodeRecursion);
                    continue;
                } else if (par[x].startsWith("rf")) {
                    fillRecursion = Integer.parseInt(par[x].replace("rf", ""));
                    v.sendMessage(ChatColor.BLUE + "Fill recursion amount set to " + fillRecursion);
                    continue;
                } else if (par[x].startsWith("re")) {
                    erodeRecursion = Integer.parseInt(par[x].replace("re", ""));
                    v.sendMessage(ChatColor.AQUA + "Erosion recursion amount set to " + erodeRecursion);
                    continue;

                } else if (par[x].startsWith("f")) {
                    fillFace = Integer.parseInt(par[x].replace("f", ""));
                    v.sendMessage(ChatColor.BLUE + "Fill minumum touching faces set to " + fillFace);
                    continue;
                } else if (par[x].startsWith("b")) {
                    v.owner().setBrushSize(Integer.parseInt(par[x].replace("b", "")));
                    //v.sendMessage(ChatColor.GREEN + "Brush size set to " + v.brushSize);  // - setBrushSize(#) already prints info
                    continue;
                } else if (par[x].startsWith("e")) {
                    erodeFace = Integer.parseInt(par[x].replace("e", ""));
                    v.sendMessage(ChatColor.AQUA + "Erosion minimum exposed faces set to " + erodeFace);
                    continue;
                } else {
                    v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
                }
            } catch (Exception e) {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! \"" + par[x] + "\" is not a valid statement. Please use the 'info' parameter to display parameter info.");
            }
        }
    }

    private void erosion(vData v) {
        if (reverse) {
            int temp = erodeFace;
            erodeFace = fillFace;
            fillFace = temp;
            temp = erodeRecursion;
            erodeRecursion = fillRecursion;
            fillRecursion = temp;
        }
        vUndo h = new vUndo(tb.getWorld().getName());

        if (erodeFace >= 0 && erodeFace <= 6) {
            for (int er = 0; er < erodeRecursion; er++) {
                getMatrix();

                int derp = bsize + 1;

                double bpow = Math.pow(bsize + trueCircle, 2);
                for (int z = 1; z < snap.length - 1; z++) {

                    double zpow = Math.pow(z - derp, 2);
                    for (int x = 1; x < snap.length - 1; x++) {

                        double xpow = Math.pow(x - derp, 2);
                        for (int y = 1; y < snap.length - 1; y++) {

                            if (((xpow + Math.pow(y - derp, 2) + zpow) <= bpow)) {
                                if (erode(x, y, z)) {
                                    snap[x][y][z].b.setTypeId(0);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (fillFace >= 0 && fillFace <= 6) {
            for (int fr = 0; fr < fillRecursion; fr++) {
                getMatrix();

                int derp = bsize + 1;

                double bpow = Math.pow(bsize + 0.5, 2);
                for (int z = 1; z < snap.length - 1; z++) {

                    double zpow = Math.pow(z - derp, 2);
                    for (int x = 1; x < snap.length - 1; x++) {

                        double xpow = Math.pow(x - derp, 2);
                        for (int y = 1; y < snap.length - 1; y++) {

                            if (((xpow + Math.pow(y - derp, 2) + zpow) <= bpow)) {
                                if (fill(x, y, z)) {
                                    snap[x][y][z].b.setTypeId(snap[x][y][z].id);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (int x = 0; x < firstSnap.length; x++) {
            for (int y = 0; y < firstSnap.length; y++) {
                for (int z = 0; z < firstSnap.length; z++) {
                    eBlock e = firstSnap[x][y][z];
                    if (e.i != e.b.getTypeId()) {
                        h.put(new uBlock(e.b, e.i));
                    }
                }
            }
        }

        v.storeUndo(h);
        if (reverse) { //if you dont put it back where it was, powder flips back and forth from fill to erode each time
            int temp = erodeFace;
            erodeFace = fillFace;
            fillFace = temp;
            temp = erodeRecursion;
            erodeRecursion = fillRecursion;
            fillRecursion = temp;
        }
    }

    /* private void filling(vSniper v) {
     * vUndo h = new vUndo(tb.getWorld().getName());
     *
     * if (fillFace >= 0 && fillFace <= 6) {
     * for (int fr = 0; fr < fillRecursion; fr++) {
     * getMatrix();
     *
     * int derp = bsize + 1;
     *
     * double bpow = Math.pow(bsize + 0.5, 2);
     * for (int z = 1; z < snap.length - 1; z++) {
     *
     * double zpow = Math.pow(z - derp, 2);
     * for (int x = 1; x < snap.length - 1; x++) {
     *
     * double xpow = Math.pow(x - derp, 2);
     * for (int y = 1; y < snap.length - 1; y++) {
     *
     * if (((xpow + Math.pow(y - derp, 2) + zpow) <= bpow)) {
     * if (fill(x, y, z)) {
     * snap[x][y][z].b.setTypeId(snap[x][y][z].id);
     * }
     * }
     * }
     * }
     * }
     * }
     * }
     * if (erodeFace >= 0 && erodeFace <= 6) {
     * for (int er = 0; er < erodeRecursion; er++) {
     * getMatrix();
     *
     * int derp = bsize + 1;
     *
     * double bpow = Math.pow(bsize + trueCircle, 2);
     * for (int z = 1; z < snap.length - 1; z++) {
     *
     * double zpow = Math.pow(z - derp, 2);
     * for (int x = 1; x < snap.length - 1; x++) {
     *
     * double xpow = Math.pow(x - derp, 2);
     * for (int y = 1; y < snap.length - 1; y++) {
     *
     * if (((xpow + Math.pow(y - derp, 2) + zpow) <= bpow)) {
     * if (erode(x, y, z)) {
     * snap[x][y][z].b.setTypeId(0);
     * }
     * }
     * }
     * }
     * }
     * }
     * }
     *
     * for (int x = 0; x < firstSnap.length; x++) {
     * for (int y = 0; y < firstSnap.length; y++) {
     * for (int z = 0; z < firstSnap.length; z++) {
     * eBlock e = firstSnap[x][y][z];
     * if (e.i != e.b.getTypeId()) {
     * h.put(new vBlock(e.b, e.i));
     * }
     * }
     * }
     * }
     *
     * v.hashUndo.put(v.hashEn, h);
     * v.hashEn++;
     * }
     *
     */
    private void getMatrix() {
        brushSize = ((bsize + 1) * 2) + 1;

        if (snap.length == 0) {
            snap = new eBlock[brushSize][brushSize][brushSize];

            int derp = (bsize + 1);
            int sx = bx - (bsize + 1);
            int sy = by - (bsize + 1);
            int sz = bz - (bsize + 1);
            for (int x = 0; x < snap.length; x++) {
                sz = bz - derp;
                for (int z = 0; z < snap.length; z++) {
                    sy = by - derp;
                    for (int y = 0; y < snap.length; y++) {
                        snap[x][y][z] = new eBlock(clampY(sx, sy, sz));
                        sy++;
                    }
                    sz++;
                }
                sx++;
            }
            firstSnap = snap.clone();
        } else {
            snap = new eBlock[brushSize][brushSize][brushSize];

            int derp = (bsize + 1);
            int sx = bx - (bsize + 1);
            int sy = by - (bsize + 1);
            int sz = bz - (bsize + 1);
            for (int x = 0; x < snap.length; x++) {
                sz = bz - derp;
                for (int z = 0; z < snap.length; z++) {
                    sy = by - derp;
                    for (int y = 0; y < snap.length; y++) {
                        snap[x][y][z] = new eBlock(clampY(sx, sy, sz));
                        sy++;
                    }
                    sz++;
                }
                sx++;
            }
        }
    }

    private boolean erode(int x, int y, int z) {
        if (snap[x][y][z].solid) {
            int d = 0;
            if (!snap[x + 1][y][z].solid) {
                d++;
            }
            if (!snap[x - 1][y][z].solid) {
                d++;
            }
            if (!snap[x][y + 1][z].solid) {
                d++;
            }
            if (!snap[x][y - 1][z].solid) {
                d++;
            }
            if (!snap[x][y][z + 1].solid) {
                d++;
            }
            if (!snap[x][y][z - 1].solid) {
                d++;
            }
            if (d >= erodeFace) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean fill(int x, int y, int z) {
        if (snap[x][y][z].solid) {
            return false;
        } else {
            int d = 0;
            if (snap[x + 1][y][z].solid) {
                snap[x][y][z].id = snap[x + 1][y][z].b.getTypeId();
                d++;
            }
            if (snap[x - 1][y][z].solid) {
                snap[x][y][z].id = snap[x - 1][y][z].b.getTypeId();
                d++;
            }
            if (snap[x][y + 1][z].solid) {
                snap[x][y][z].id = snap[x][y + 1][z].b.getTypeId();
                d++;
            }
            if (snap[x][y - 1][z].solid) {
                snap[x][y][z].id = snap[x][y - 1][z].b.getTypeId();
                d++;
            }
            if (snap[x][y][z + 1].solid) {
                snap[x][y][z].id = snap[x][y][z + 1].b.getTypeId();
                d++;
            }
            if (snap[x][y][z - 1].solid) {
                snap[x][y][z].id = snap[x][y][z - 1].b.getTypeId();
                d++;
            }
            if (d >= fillFace) {
                return true;
            } else {
                return false;
            }
        }
    }

    private class eBlock {

        public boolean solid;
        Block b;
        public int id;
        public int i;

        public eBlock(Block bl) {
            b = bl;
            i = bl.getTypeId();
            switch (bl.getType()) {
                case AIR:
                    solid = false;
                    break;

                case WATER:
                    solid = false;
                    break;

                case STATIONARY_WATER:
                    solid = false;
                    break;

                case STATIONARY_LAVA:
                    solid = false;
                    break;
                case LAVA:
                    solid = false;
                    break;

                default:
                    solid = true;
            }
        }
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
