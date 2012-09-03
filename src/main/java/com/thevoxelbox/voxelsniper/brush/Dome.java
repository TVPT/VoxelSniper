package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Gavjenks
 */
public class Dome extends Brush {

    boolean fsa = true;
    boolean powder = false;

    // should only have the truecircle option this time.
    // param is just height -- will start with the face disc and instead of making the actual disc, will make a dome of that height.
    // defaul height = diameter of the disc. Other heights would make parabolic domes, thus I am developing this in concurrence with parabola brush. It will be
    // able to do everything this can, but would be harder to use.
    // for simplicity for this brush also, arrow will do half block accuracy, powder full block - will work eve for sideways dome, though maybe limited
    // usefulness there. If /v is not a half block, override to full block accuracy. If it is, make full double step stuff for all the blocks underneath the top
    // curve, in same material.
    double height = 1024; // just avoiding initiating bsize yet;

    private static int timesUsed = 0;

    public Dome() {
        this.setName("Dome");
    }

    public final void dome(final SnipeData v) {
        final int bsize = v.getBrushSize();
        final int bId = v.getVoxelId();

        final Undo h = new Undo(this.getTargetBlock().getWorld().getName());

        final double bpow = Math.pow(bsize + 0.5, 2);
        // double curvature = 1; //actually not necessary if base is circular.
        if (this.height == 1024) {
            this.height = bsize + 0.5;
        }
        final double curvature = this.height / (bsize + 0.5);
        double yManip = 0.5;
        double centerRef = 0;
        if (this.fsa || bId != 44) { // override half block accuracy if /v not set to a half block.
            yManip = 0.5; // whole block accuracy
            centerRef = 0;
        } else {
            yManip = 0.5; // half block accuracy
            centerRef = -0.25;
        }

        final int[][] heightmap = new int[bsize + 2][bsize + 2];
        for (int x = bsize; x >= 0; x--) {
            final double xpow = Math.pow(x, 2);
            for (int z = bsize; z >= 0; z--) {
                final double zpow = Math.pow(z, 2);
                for (int y = (int) this.height; y >= 0; y--) {
                    final double ypowminus = Math.pow(y - yManip + centerRef, 2);
                    final double ypow = Math.pow(y + centerRef, 2);
                    if ((xpow + (ypowminus / Math.pow(curvature, 2)) + zpow) <= bpow) { // If within the ellipse
                        final double ypowplus = Math.pow(y + yManip + centerRef, 2);
                        if ((xpow + (ypowplus / Math.pow(curvature, 2)) + zpow) > bpow) { // If nothing else further out (i.e. if on the surface)
                            h.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z));
                            h.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() - z));
                            h.put(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z)); // only want top of dome. So only 4 of these.
                            h.put(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() - z));

                            h.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y - 1, this.getBlockPositionZ() + z));
                            h.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y - 1, this.getBlockPositionZ() - z)); // blocks right underneath each
                            h.put(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y - 1, this.getBlockPositionZ() + z));
                            h.put(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y - 1, this.getBlockPositionZ() - z));

                            if (!this.fsa && ((xpow + (ypow / Math.pow(curvature, 2)) + zpow) > bpow)) { // if half block accuracy is being used AND this is a
                                                                                                         // portion of the curve that is closer to matching a
                                                                                                         // half block than a full block...
                                this.setBlockIdAt(44, this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z); // set to a half block
                                this.setBlockIdAt(44, this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() - z);
                                this.setBlockIdAt(44, this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z);
                                this.setBlockIdAt(44, this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() - z);
                                // AND place a full double step underneath to prevent gaps (might be slightly bulkier than could be possible... but much simpler
                                // to code)
                                heightmap[x][z] = y - 1;
                                this.setBlockIdAt(43, this.getBlockPositionX() + x, this.getBlockPositionY() + y - 1, this.getBlockPositionZ() + z);
                                this.setBlockIdAt(43, this.getBlockPositionX() + x, this.getBlockPositionY() + y - 1, this.getBlockPositionZ() - z);
                                this.setBlockIdAt(43, this.getBlockPositionX() - x, this.getBlockPositionY() + y - 1, this.getBlockPositionZ() + z);
                                this.setBlockIdAt(43, this.getBlockPositionX() - x, this.getBlockPositionY() + y - 1, this.getBlockPositionZ() - z);
                            } else {
                                if (bId == 44) { // if half block accuracy, but this particular position conforms better to a full block
                                    heightmap[x][z] = y;
                                    this.setBlockIdAt(43, this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z); // set to a full double step
                                    this.setBlockIdAt(43, this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() - z);
                                    this.setBlockIdAt(43, this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z);
                                    this.setBlockIdAt(43, this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() - z);
                                } else { // if full block accuracy
                                    heightmap[x][z] = y;
                                    this.setBlockIdAt(bId, this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z); // set to a full block of whatever /v is.
                                    this.setBlockIdAt(bId, this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() - z);
                                    this.setBlockIdAt(bId, this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() + z);
                                    this.setBlockIdAt(bId, this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() - z);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int x = 0; x <= bsize; x++) {
            for (int z = 0; z <= bsize; z++) {
                for (int i = heightmap[x][z] - 1; i >= 0; i--) {
                    if (!this.powder && heightmap[x][z + 1] < i || heightmap[x + 1][z] < i) { // if annoying air gap in wall in x or z direction
                        h.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + i, this.getBlockPositionZ() + z));
                        h.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - z));
                        h.put(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + i, this.getBlockPositionZ() + z));
                        h.put(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + i, this.getBlockPositionZ() - z));

                        if (bId == 44) {
                            this.setBlockIdAt(43, this.getBlockPositionX() + x, this.getBlockPositionY() + i, this.getBlockPositionZ() + z);
                            this.setBlockIdAt(43, this.getBlockPositionX() + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - z);
                            this.setBlockIdAt(43, this.getBlockPositionX() - x, this.getBlockPositionY() + i, this.getBlockPositionZ() + z);
                            this.setBlockIdAt(43, this.getBlockPositionX() - x, this.getBlockPositionY() + i, this.getBlockPositionZ() - z);
                        } else {
                            this.setBlockIdAt(bId, this.getBlockPositionX() + x, this.getBlockPositionY() + i, this.getBlockPositionZ() + z);
                            this.setBlockIdAt(bId, this.getBlockPositionX() + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - z);
                            this.setBlockIdAt(bId, this.getBlockPositionX() - x, this.getBlockPositionY() + i, this.getBlockPositionZ() + z);
                            this.setBlockIdAt(bId, this.getBlockPositionX() - x, this.getBlockPositionY() + i, this.getBlockPositionZ() - z);
                        }
                    }
                    if (this.powder) { // fill in solid.
                        h.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + i, this.getBlockPositionZ() + z));
                        h.put(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - z));
                        h.put(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + i, this.getBlockPositionZ() + z));
                        h.put(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + i, this.getBlockPositionZ() - z));

                        if (bId == 44) {
                            this.setBlockIdAt(43, this.getBlockPositionX() + x, this.getBlockPositionY() + i, this.getBlockPositionZ() + z);
                            this.setBlockIdAt(43, this.getBlockPositionX() + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - z);
                            this.setBlockIdAt(43, this.getBlockPositionX() - x, this.getBlockPositionY() + i, this.getBlockPositionZ() + z);
                            this.setBlockIdAt(43, this.getBlockPositionX() - x, this.getBlockPositionY() + i, this.getBlockPositionZ() - z);
                        } else {
                            this.setBlockIdAt(bId, this.getBlockPositionX() + x, this.getBlockPositionY() + i, this.getBlockPositionZ() + z);
                            this.setBlockIdAt(bId, this.getBlockPositionX() + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - z);
                            this.setBlockIdAt(bId, this.getBlockPositionX() - x, this.getBlockPositionY() + i, this.getBlockPositionZ() + z);
                            this.setBlockIdAt(bId, this.getBlockPositionX() - x, this.getBlockPositionY() + i, this.getBlockPositionZ() - z);
                        }
                    }
                }
            }
        }

        v.storeUndo(h);
    }

    // ###### nothing below here fixed yet. Copy above code, but switch out all the y'world for whatever other axis... BUT complication: the checks for half block
    // accuracy must always still be y-oriented... harder.
    /*
     * public void domeNS(Sniper v) { int bsize = v.brushSize; int bId = v.voxelId; world = world;
     * 
     * 
     * Undo h = new Undo(targetBlock.getWorld().getName());
     * 
     * double bpow = Math.pow(bsize +0.5, 2); //double curvature = 1; //actually not necessary if base is circular. if (height == 1024) { height = bsize + 0.5;
     * } double curvature = height / (bsize + 0.5); double yManip = 0.5; double centerRef = 0; if (fsa || bId != 44) { //override half block accuracy if /v not
     * set to a half block. yManip = 0.5; //whole block accuracy centerRef = 0; } else { yManip = 0.5; //half block accuracy centerRef = -0.25; }
     * 
     * 
     * int[][] heightmap = new int[(int)height+2][bsize+2]; for (int x = (int)height; x >= 0; x--) { double xpow = Math.pow(x, 2); for (int z = bsize; z >= 0;
     * z--) { double zpow = Math.pow(z, 2); for (int y = bsize; y >= 0; y--) { double ypowminus = Math.pow(y - yManip + centerRef, 2); double ypow = Math.pow(y
     * + centerRef,2); if ((xpow/Math.pow(curvature, 2) + ypowminus + zpow) <= bpow) { //If within the ellipse double ypowplus = Math.pow(y + yManip +
     * centerRef, 2); if ((xpow/Math.pow(curvature, 2) + ypowplus + zpow) > bpow) { //If nothing else further out (i.e. if on the surface) #### or nothing below
     * (surface on bottom) h.put(clampY(blockPositionX + x, blockPositionY + y, blockPositionZ + z)); h.put(clampY(blockPositionX + x, blockPositionY + y, blockPositionZ - z)); h.put(clampY(blockPositionX + x, blockPositionY - y+1, blockPositionZ + z)); //only want
     * top of dome. So only 4 of these. h.put(clampY(blockPositionX + x, blockPositionY - y+1, blockPositionZ - z));
     * 
     * h.put(clampY(blockPositionX + x, blockPositionY + y -1, blockPositionZ + z)); h.put(clampY(blockPositionX + x, blockPositionY + y -1, blockPositionZ - z)); //blocks right underneath each h.put(clampY(blockPositionX + x, blockPositionY - y , blockPositionZ +
     * z)); h.put(clampY(blockPositionX + x, blockPositionY - y , blockPositionZ - z));
     * 
     * 
     * if (!fsa && ((xpow + (ypow / Math.pow(curvature, 2)) + zpow) > bpow)) { //if half block accuracy is being used AND this is a portion of the curve that is
     * closer to matching a half block than a full block... setBlockIdAt(44, blockPositionX + x, blockPositionY + y, blockPositionZ + z); //set to a half block setBlockIdAt(44, blockPositionX + x, blockPositionY + y, blockPositionZ
     * - z); setBlockIdAt(44, blockPositionX + x, blockPositionY - y+1, blockPositionZ + z); setBlockIdAt(44, blockPositionX + x, blockPositionY - y+1, blockPositionZ - z); //AND place a full double step underneath to prevent gaps
     * (might be slightly bulkier than could be possible... but much simpler to code) heightmap[x][z] = y - 1; setBlockIdAt(43, blockPositionX + x, blockPositionY + y - 1, blockPositionZ + z);
     * setBlockIdAt(43, blockPositionX + x, blockPositionY + y - 1, blockPositionZ - z); setBlockIdAt(43, blockPositionX + x, blockPositionY - y , blockPositionZ + z); setBlockIdAt(43, blockPositionX + x, blockPositionY - y , blockPositionZ - z); } else { if (bId ==
     * 44) { //if half block accuracy, but this particular position conforms better to a full block heightmap[x][z] = y; setBlockIdAt(43, blockPositionX + x, blockPositionY + y, blockPositionZ +
     * z); //set to a full double step setBlockIdAt(43, blockPositionX + x, blockPositionY + y, blockPositionZ - z); setBlockIdAt(43, blockPositionX + x, blockPositionY - y+1, blockPositionZ + z); setBlockIdAt(43, blockPositionX + x, blockPositionY - y+1,
     * blockPositionZ - z); } else { //if full block accuracy heightmap[x][z] = y; setBlockIdAt(bId, blockPositionX + x, blockPositionY + y, blockPositionZ + z); //set to a full block of whatever /v is.
     * setBlockIdAt(bId, blockPositionX + x, blockPositionY + y, blockPositionZ - z); setBlockIdAt(bId, blockPositionX + x, blockPositionY - y+1, blockPositionZ + z); setBlockIdAt(bId, blockPositionX + x, blockPositionY - y+1, blockPositionZ - z); } } } } } } } for
     * (int x = 0; x <= bsize; x++) { for (int z = 0; z <= bsize; z++) { for (int i = heightmap[x][z]-1; i >= 0; i--) { // zero should be bottom of curve for
     * top blocks, and top of curve with reverse counting up for the bottom if (!powder && heightmap[x][z + 1] < i || heightmap[x + 1][z] < i) { //if annoying
     * air gap in wall in x or z direction h.put(clampY(blockPositionX + x, blockPositionY + i, blockPositionZ + z)); h.put(clampY(blockPositionX + x, blockPositionY + i, blockPositionZ - z)); h.put(clampY(blockPositionX + x, blockPositionY + i, blockPositionZ + z));
     * h.put(clampY(blockPositionX + x, blockPositionY + i, blockPositionZ - z));
     * 
     * if (bId == 44) { setBlockIdAt(43, blockPositionX + x, blockPositionY + i, blockPositionZ + z); setBlockIdAt(43, blockPositionX + x, blockPositionY + i, blockPositionZ - z); setBlockIdAt(43, blockPositionX + x, blockPositionY + i, blockPositionZ + z);
     * setBlockIdAt(43, blockPositionX + x, blockPositionY + i, blockPositionZ - z); } else { setBlockIdAt(bId, blockPositionX + x, blockPositionY + i, blockPositionZ + z); setBlockIdAt(bId, blockPositionX + x, blockPositionY + i, blockPositionZ - z);
     * setBlockIdAt(bId, blockPositionX + x, blockPositionY + i, blockPositionZ + z); setBlockIdAt(bId, blockPositionX + x, blockPositionY + i, blockPositionZ - z); } } if (powder) { //fill in solid. h.put(clampY(blockPositionX + x, blockPositionY + i, blockPositionZ
     * + z)); h.put(clampY(blockPositionX + x, blockPositionY + i, blockPositionZ - z)); h.put(clampY(blockPositionX + x, blockPositionY + i, blockPositionZ + z)); h.put(clampY(blockPositionX + x, blockPositionY + i, blockPositionZ - z));
     * 
     * if (bId == 44) { setBlockIdAt(43, blockPositionX + x, blockPositionY + i, blockPositionZ + z); setBlockIdAt(43, blockPositionX + x, blockPositionY + i, blockPositionZ - z); setBlockIdAt(43, blockPositionX + x, blockPositionY + i, blockPositionZ + z);
     * setBlockIdAt(43, blockPositionX + x, blockPositionY + i, blockPositionZ - z); } else { setBlockIdAt(bId, blockPositionX + x, blockPositionY + i, blockPositionZ + z); setBlockIdAt(bId, blockPositionX + x, blockPositionY + i, blockPositionZ - z);
     * setBlockIdAt(bId, blockPositionX + x, blockPositionY + i, blockPositionZ + z); setBlockIdAt(bId, blockPositionX + x, blockPositionY + i, blockPositionZ - z); } }
     * 
     * } } }
     * 
     * v.hashUndo.put(v.hashEn, h); v.hashEn++; }
     * 
     * public void domeEW(Sniper v) { int bsize = v.brushSize; int bId = v.voxelId; world = world;
     * 
     * Undo h = new Undo(targetBlock.getWorld().getName());
     * 
     * double bpow = Math.pow(bsize+0.5, 2); for (int x = bsize; x >= 0; x--) { double xpow = Math.pow(x, 2); for (int y = bsize; y >= 0; y--) { if ((xpow +
     * Math.pow(y, 2)) <= bpow) { if (getBlockIdAt(blockPositionX, blockPositionY + x, blockPositionZ + y) != bId) { h.put(clampY(blockPositionX, blockPositionY + x, blockPositionZ + y)); } if (getBlockIdAt(blockPositionX, blockPositionY + x, blockPositionZ - y) !=
     * bId) { h.put(clampY(blockPositionX, blockPositionY + x, blockPositionZ - y)); } if (getBlockIdAt(blockPositionX, blockPositionY - x, blockPositionZ + y) != bId) { h.put(clampY(blockPositionX, blockPositionY - x, blockPositionZ + y)); } if (getBlockIdAt(blockPositionX, blockPositionY -
     * x, blockPositionZ - y) != bId) { h.put(clampY(blockPositionX, blockPositionY - x, blockPositionZ - y)); } setBlockIdAt(bId, blockPositionX, blockPositionY + x, blockPositionZ + y); setBlockIdAt(bId, blockPositionX, blockPositionY + x, blockPositionZ - y); setBlockIdAt(bId,
     * blockPositionX, blockPositionY - x, blockPositionZ + y); setBlockIdAt(bId, blockPositionX, blockPositionY - x, blockPositionZ - y); } } } v.hashUndo.put(v.hashEn, h); v.hashEn++; }
     */
    @Override
    public final int getTimesUsed() {
        return Dome.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.voxel();
        vm.height();
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Dome brush Parameters:");
            v.sendMessage(ChatColor.AQUA
                    + "/b dome h[number] -- set a custom dome height.  Default is the radius of the brush.  Anything else will make it a parabolic dome with circular base.  Cannot be negative.");
            v.sendMessage(ChatColor.BLUE
                    + "/b dome acc [or inacc] -- set brush to half (acc) or full step (inacc) accuracy.  if /v is anything other than 44, will override you and force full step accuracy.");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("h")) {
                this.height = Double.parseDouble(par[x].replace("h", ""));
                v.sendMessage(ChatColor.AQUA + "Dome height set to: " + this.height);
                continue;
            } else if (par[x].startsWith("inacc")) {
                this.fsa = true;
                v.sendMessage(ChatColor.BLUE + "Full step accuracy.");
                continue;
            } else if (par[x].startsWith("acc")) {
                if (v.getVoxelId() != 44) {
                    this.fsa = true;
                    v.sendMessage(ChatColor.BLUE + "Full step accuracy. (overridden since you don't have half steps selected)");
                } else {
                    this.fsa = false;
                    v.sendMessage(ChatColor.BLUE + "Half step accuracy.");
                }
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Dome.timesUsed = tUsed;
    }

    private void pre(final SnipeData v, final BlockFace bf) {
        if (bf == null) {
            return;
        }
        switch (bf) {
        case NORTH:
        case SOUTH:
            this.dome(v); // would be domeNS later
            break;

        case EAST:
        case WEST:
            this.dome(v); // would be domeEW later
            break;

        case UP:
        case DOWN:
            this.dome(v);
            break;

        default:
            break;
        }
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.powder = false;
        this.pre(v, this.getTargetBlock().getFace(this.getLastBlock()));
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getLastBlock().getX());
        this.setBlockPositionY(this.getLastBlock().getY());
        this.setBlockPositionZ(this.getLastBlock().getZ());
        this.powder = true;
        this.pre(v, this.getTargetBlock().getFace(this.getLastBlock()));
    }
}
