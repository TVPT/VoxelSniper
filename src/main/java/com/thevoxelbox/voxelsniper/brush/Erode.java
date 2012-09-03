package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Piotr
 */
public class Erode extends Brush {

    private class eBlock {

        public boolean solid;
        Block b;
        public int id;
        public int i;

        public eBlock(final Block bl) {
            this.b = bl;
            this.i = bl.getTypeId();
            switch (bl.getType()) {
            case AIR:
                this.solid = false;
                break;

            case WATER:
                this.solid = false;
                break;

            case STATIONARY_WATER:
                this.solid = false;
                break;

            case STATIONARY_LAVA:
                this.solid = false;
                break;
            case LAVA:
                this.solid = false;
                break;

            default:
                this.solid = true;
            }
        }
    }

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

    private static int timesUsed = 0;

    public Erode() {
        this.setName("Erode");
    }

    @Override
    public final int getTimesUsed() {
        return Erode.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.custom(ChatColor.RED + "Litesnipers: This is a slow brush.  DO NOT SPAM it too much or hold down the mouse. ");
        vm.custom(ChatColor.AQUA + "Erosion minimum exposed faces set to " + this.erodeFace);
        vm.custom(ChatColor.BLUE + "Fill minumum touching faces set to " + this.fillFace);
        vm.custom(ChatColor.DARK_BLUE + "Erosion recursion amount set to " + this.erodeRecursion);
        vm.custom(ChatColor.DARK_GREEN + "Fill recursion amount set to " + this.fillRecursion);
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
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
            v.sendMessage(ChatColor.GOLD
                    + "User-friendly Preset Options.  These are for the arrow.  Powder will do reverse for the first two (for fast switching):");
            v.sendMessage(ChatColor.BLUE + "OK for litesnipers:");
            v.sendMessage(ChatColor.GREEN + "/b e melt -- for melting away protruding corners and edges.");
            v.sendMessage(ChatColor.AQUA + "/b e fill -- for building up inside corners");
            v.sendMessage(ChatColor.AQUA
                    + "/b e smooth -- For the most part, does not change total number of blocks, but smooths the shape nicely.  Use as a finishing touch for the most part, before overlaying grass and trees, etc.");
            v.sendMessage(ChatColor.BLUE + "/b e lift-- More or less raises each block in the brush area blockPositionY one"); // Giltwist
            return;
        }
        for (int x = 1; x < par.length; x++) {
            try {
                if (par[x].startsWith("melt")) {
                    this.fillRecursion = 1;
                    this.erodeRecursion = 1;
                    this.fillFace = 5;
                    this.erodeFace = 2;
                    v.owner().setBrushSize(10);
                    v.sendMessage(ChatColor.AQUA + "Melt mode. (/b e e2 f5 re1 rf1 b10)");
                    continue;
                } else if (par[x].startsWith("fill")) {
                    this.fillRecursion = 1;
                    this.erodeRecursion = 1;
                    this.fillFace = 2;
                    this.erodeFace = 5;
                    v.owner().setBrushSize(8);
                    v.sendMessage(ChatColor.AQUA + "Fill mode. (/b e e5 f2 re1 rf1 b8)");
                    continue;
                } else if (par[x].startsWith("smooth")) {
                    this.fillRecursion = 1;
                    this.erodeRecursion = 1;
                    this.fillFace = 3;
                    this.erodeFace = 3;
                    v.owner().setBrushSize(16);
                    v.sendMessage(ChatColor.AQUA + "Smooth mode. (/b e e3 f3 re1 rf1 b16)");
                    continue;
                } else if (par[x].startsWith("lift")) { // Giltwist
                    this.fillRecursion = 1;
                    this.erodeRecursion = 0;
                    this.fillFace = 1;
                    this.erodeFace = 6;
                    v.owner().setBrushSize(10);
                    v.sendMessage(ChatColor.AQUA + "Lift mode. (/b e e6 f1 re0 rf1 b10)");
                    continue;
                } else if (par[x].startsWith("true")) {
                    this.trueCircle = 0.5;
                    v.sendMessage(ChatColor.AQUA + "True circle mode ON." + this.erodeRecursion);
                    continue;
                } else if (par[x].startsWith("false")) {
                    this.trueCircle = 0;
                    v.sendMessage(ChatColor.AQUA + "True circle mode OFF." + this.erodeRecursion);
                    continue;
                } else if (par[x].startsWith("rf")) {
                    this.fillRecursion = Integer.parseInt(par[x].replace("rf", ""));
                    v.sendMessage(ChatColor.BLUE + "Fill recursion amount set to " + this.fillRecursion);
                    continue;
                } else if (par[x].startsWith("re")) {
                    this.erodeRecursion = Integer.parseInt(par[x].replace("re", ""));
                    v.sendMessage(ChatColor.AQUA + "Erosion recursion amount set to " + this.erodeRecursion);
                    continue;

                } else if (par[x].startsWith("f")) {
                    this.fillFace = Integer.parseInt(par[x].replace("f", ""));
                    v.sendMessage(ChatColor.BLUE + "Fill minumum touching faces set to " + this.fillFace);
                    continue;
                } else if (par[x].startsWith("b")) {
                    v.owner().setBrushSize(Integer.parseInt(par[x].replace("b", "")));
                    // v.sendMessage(ChatColor.GREEN + "Brush size set to " + v.brushSize); // - setBrushSize(#) already prints info
                    continue;
                } else if (par[x].startsWith("e")) {
                    this.erodeFace = Integer.parseInt(par[x].replace("e", ""));
                    v.sendMessage(ChatColor.AQUA + "Erosion minimum exposed faces set to " + this.erodeFace);
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

    @Override
    public final void setTimesUsed(final int tUsed) {
        Erode.timesUsed = tUsed;
    }

    private boolean erode(final int x, final int y, final int z) {
        if (this.snap[x][y][z].solid) {
            int d = 0;
            if (!this.snap[x + 1][y][z].solid) {
                d++;
            }
            if (!this.snap[x - 1][y][z].solid) {
                d++;
            }
            if (!this.snap[x][y + 1][z].solid) {
                d++;
            }
            if (!this.snap[x][y - 1][z].solid) {
                d++;
            }
            if (!this.snap[x][y][z + 1].solid) {
                d++;
            }
            if (!this.snap[x][y][z - 1].solid) {
                d++;
            }
            if (d >= this.erodeFace) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void erosion(final SnipeData v) {
        if (this.reverse) {
            int temp = this.erodeFace;
            this.erodeFace = this.fillFace;
            this.fillFace = temp;
            temp = this.erodeRecursion;
            this.erodeRecursion = this.fillRecursion;
            this.fillRecursion = temp;
        }
        final Undo h = new Undo(this.getTargetBlock().getWorld().getName());

        if (this.erodeFace >= 0 && this.erodeFace <= 6) {
            for (int er = 0; er < this.erodeRecursion; er++) {
                this.getMatrix();

                final int derp = this.bsize + 1;

                final double bpow = Math.pow(this.bsize + this.trueCircle, 2);
                for (int z = 1; z < this.snap.length - 1; z++) {

                    final double zpow = Math.pow(z - derp, 2);
                    for (int x = 1; x < this.snap.length - 1; x++) {

                        final double xpow = Math.pow(x - derp, 2);
                        for (int y = 1; y < this.snap.length - 1; y++) {

                            if (((xpow + Math.pow(y - derp, 2) + zpow) <= bpow)) {
                                if (this.erode(x, y, z)) {
                                    this.snap[x][y][z].b.setTypeId(0);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.fillFace >= 0 && this.fillFace <= 6) {
            for (int fr = 0; fr < this.fillRecursion; fr++) {
                this.getMatrix();

                final int derp = this.bsize + 1;

                final double bpow = Math.pow(this.bsize + 0.5, 2);
                for (int z = 1; z < this.snap.length - 1; z++) {

                    final double zpow = Math.pow(z - derp, 2);
                    for (int x = 1; x < this.snap.length - 1; x++) {

                        final double xpow = Math.pow(x - derp, 2);
                        for (int y = 1; y < this.snap.length - 1; y++) {

                            if (((xpow + Math.pow(y - derp, 2) + zpow) <= bpow)) {
                                if (this.fill(x, y, z)) {
                                    this.snap[x][y][z].b.setTypeId(this.snap[x][y][z].id);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (int x = 0; x < this.firstSnap.length; x++) {
            for (int y = 0; y < this.firstSnap.length; y++) {
                for (int z = 0; z < this.firstSnap.length; z++) {
                    final eBlock e = this.firstSnap[x][y][z];
                    if (e.i != e.b.getTypeId()) {
                        h.put(e.b);
                    }
                }
            }
        }

        v.storeUndo(h);
        if (this.reverse) { // if you dont put it back where it was, powder flips back and forth from fill to erode each time
            int temp = this.erodeFace;
            this.erodeFace = this.fillFace;
            this.fillFace = temp;
            temp = this.erodeRecursion;
            this.erodeRecursion = this.fillRecursion;
            this.fillRecursion = temp;
        }
    }

    private boolean fill(final int x, final int y, final int z) {
        if (this.snap[x][y][z].solid) {
            return false;
        } else {
            int d = 0;
            if (this.snap[x + 1][y][z].solid) {
                this.snap[x][y][z].id = this.snap[x + 1][y][z].b.getTypeId();
                d++;
            }
            if (this.snap[x - 1][y][z].solid) {
                this.snap[x][y][z].id = this.snap[x - 1][y][z].b.getTypeId();
                d++;
            }
            if (this.snap[x][y + 1][z].solid) {
                this.snap[x][y][z].id = this.snap[x][y + 1][z].b.getTypeId();
                d++;
            }
            if (this.snap[x][y - 1][z].solid) {
                this.snap[x][y][z].id = this.snap[x][y - 1][z].b.getTypeId();
                d++;
            }
            if (this.snap[x][y][z + 1].solid) {
                this.snap[x][y][z].id = this.snap[x][y][z + 1].b.getTypeId();
                d++;
            }
            if (this.snap[x][y][z - 1].solid) {
                this.snap[x][y][z].id = this.snap[x][y][z - 1].b.getTypeId();
                d++;
            }
            if (d >= this.fillFace) {
                return true;
            } else {
                return false;
            }
        }
    }

    /*
     * private void filling(Sniper v) { Undo h = new Undo(targetBlock.getWorld().getName());
     * 
     * if (fillFace >= 0 && fillFace <= 6) { for (int fr = 0; fr < fillRecursion; fr++) { getMatrix();
     * 
     * int derp = bsize + 1;
     * 
     * double bpow = Math.pow(bsize + 0.5, 2); for (int z = 1; z < snap.length - 1; z++) {
     * 
     * double zpow = Math.pow(z - derp, 2); for (int x = 1; x < snap.length - 1; x++) {
     * 
     * double xpow = Math.pow(x - derp, 2); for (int y = 1; y < snap.length - 1; y++) {
     * 
     * if (((xpow + Math.pow(y - derp, 2) + zpow) <= bpow)) { if (fill(x, y, z)) { snap[x][y][z].b.setTypeId(snap[x][y][z].id); } } } } } } } if (erodeFace >= 0
     * && erodeFace <= 6) { for (int er = 0; er < erodeRecursion; er++) { getMatrix();
     * 
     * int derp = bsize + 1;
     * 
     * double bpow = Math.pow(bsize + trueCircle, 2); for (int z = 1; z < snap.length - 1; z++) {
     * 
     * double zpow = Math.pow(z - derp, 2); for (int x = 1; x < snap.length - 1; x++) {
     * 
     * double xpow = Math.pow(x - derp, 2); for (int y = 1; y < snap.length - 1; y++) {
     * 
     * if (((xpow + Math.pow(y - derp, 2) + zpow) <= bpow)) { if (erode(x, y, z)) { snap[x][y][z].b.setTypeId(0); } } } } } } }
     * 
     * for (int x = 0; x < firstSnap.length; x++) { for (int y = 0; y < firstSnap.length; y++) { for (int z = 0; z < firstSnap.length; z++) { eBlock e =
     * firstSnap[x][y][z]; if (e.i != e.b.getTypeId()) { h.put(new BlockWrapper(e.b, e.i)); } } } }
     * 
     * v.hashUndo.put(v.hashEn, h); v.hashEn++; }
     */
    private void getMatrix() {
        this.brushSize = ((this.bsize + 1) * 2) + 1;

        if (this.snap.length == 0) {
            this.snap = new eBlock[this.brushSize][this.brushSize][this.brushSize];

            final int derp = (this.bsize + 1);
            int sx = this.getBlockPositionX() - (this.bsize + 1);
            int sy = this.getBlockPositionY() - (this.bsize + 1);
            int sz = this.getBlockPositionZ() - (this.bsize + 1);
            for (int x = 0; x < this.snap.length; x++) {
                sz = this.getBlockPositionZ() - derp;
                for (int z = 0; z < this.snap.length; z++) {
                    sy = this.getBlockPositionY() - derp;
                    for (int y = 0; y < this.snap.length; y++) {
                        this.snap[x][y][z] = new eBlock(this.clampY(sx, sy, sz));
                        sy++;
                    }
                    sz++;
                }
                sx++;
            }
            this.firstSnap = this.snap.clone();
        } else {
            this.snap = new eBlock[this.brushSize][this.brushSize][this.brushSize];

            final int derp = (this.bsize + 1);
            int sx = this.getBlockPositionX() - (this.bsize + 1);
            int sy = this.getBlockPositionY() - (this.bsize + 1);
            int sz = this.getBlockPositionZ() - (this.bsize + 1);
            for (int x = 0; x < this.snap.length; x++) {
                sz = this.getBlockPositionZ() - derp;
                for (int z = 0; z < this.snap.length; z++) {
                    sy = this.getBlockPositionY() - derp;
                    for (int y = 0; y < this.snap.length; y++) {
                        this.snap[x][y][z] = new eBlock(this.clampY(sx, sy, sz));
                        sy++;
                    }
                    sz++;
                }
                sx++;
            }
        }
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        this.bsize = v.getBrushSize();

        this.snap = new eBlock[0][0][0];
        this.reverse = false;

        this.erosion(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        this.bsize = v.getBrushSize();

        this.snap = new eBlock[0][0][0];
        this.reverse = true;

        this.erosion(v);
    }
}
