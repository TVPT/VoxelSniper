package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vBlock;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * 
 * @author Gavjenks (from Gavjenks & Piotr'world 2D brush)
 */
public class Rot3D extends Brush {

    protected int mode = 0;
    private int bsize;
    private int brushSize;
    private vBlock[][][] snap;
    private double seYaw;
    private double sePitch;
    private double seRoll;

    private static int timesUsed = 0;

    public Rot3D() {
        this.setName("3D Rotation");
    }

    @Override
    public final int getTimesUsed() {
        return Rot3D.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.getName());
        vm.brushMessage("Rotates Yaw (XZ), then Pitch(XY), then Roll(ZY), in order.");
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Rotate brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "p[0-359] -- set degrees of pitch rotation (rotation about the Z axis).");
            v.sendMessage(ChatColor.BLUE + "r[0-359] -- set degrees of roll rotation (rotation about the X axis).");
            v.sendMessage(ChatColor.LIGHT_PURPLE + "y[0-359] -- set degrees of yaw rotation (Rotation about the Y axis).");

            return;
        }
        for (int x = 1; x < par.length; x++) {
            // which way is clockwise is less obvious for roll and pitch... should probably fix that / make it clear
            if (par[x].startsWith("p")) {
                this.sePitch = Math.toRadians(Double.parseDouble(par[x].replace("p", "")));
                v.sendMessage(ChatColor.AQUA + "Around Z-axis degrees set to " + this.sePitch);
                if (this.sePitch < 0 || this.sePitch > 359) {
                    v.sendMessage(ChatColor.RED + "Invalid brush parameters! Angles must be from 1-359");
                }
                continue;
            } else if (par[x].startsWith("r")) {
                this.seRoll = Math.toRadians(Double.parseDouble(par[x].replace("r", "")));
                v.sendMessage(ChatColor.AQUA + "Around X-axis degrees set to " + this.seRoll);
                if (this.seRoll < 0 || this.seRoll > 359) {
                    v.sendMessage(ChatColor.RED + "Invalid brush parameters! Angles must be from 1-359");
                }
                continue;
            } else if (par[x].startsWith("y")) {
                this.seYaw = Math.toRadians(Double.parseDouble(par[x].replace("y", "")));
                v.sendMessage(ChatColor.AQUA + "Around Y-axis degrees set to " + this.seYaw);
                if (this.seYaw < 0 || this.seYaw > 359) {
                    v.sendMessage(ChatColor.RED + "Invalid brush parameters! Angles must be from 1-359");
                }
                continue;
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Rot3D.timesUsed = tUsed;
    }

    private void getMatrix() {// only need to do once. But y needs to change + sphere
        this.brushSize = (this.bsize * 2) + 1;

        this.snap = new vBlock[this.brushSize][this.brushSize][this.brushSize];

        final int derp = this.bsize;
        int sx = this.getBlockPositionX() - this.bsize;
        int sy = this.getBlockPositionY() - this.bsize;
        int sz = this.getBlockPositionZ() - this.bsize;
        final double bpow = Math.pow(this.bsize + 0.5, 2);
        for (int x = 0; x < this.snap.length; x++) {
            sz = this.getBlockPositionZ() - derp;
            final double xpow = Math.pow(x - this.bsize, 2);
            for (int z = 0; z < this.snap.length; z++) {
                sy = this.getBlockPositionY() - derp;
                final double zpow = Math.pow(z - this.bsize, 2);
                for (int y = 0; y < this.snap.length; y++) {
                    if (xpow + zpow + Math.pow(y - this.bsize, 2) <= bpow) {
                        final Block b = this.clampY(sx, sy, sz);
                        this.snap[x][y][z] = new vBlock(b);
                        b.setTypeId(0);
                        sy++;
                    }
                }

                sz++;
            }
            sx++;
        }

    }

    private void rotate(final vData v) {
        // basically 1) make it a sphere we are rotating in, not a cylinder
        // 2) do three rotations in a row, one in each dimension, unless some dimensions are set to zero or udnefined or whatever, then skip those.
        // --> Why not utilize Sniper'world new oportunities and have arrow rotate all 3, powder rotate x, goldsisc y, otherdisc z. Or something like that. Or we
        // could just use arrow and powder and just differenciate between left and right click that gis 4 different situations
        // --> Well, there would be 7 different possibilities... X, Y, Z, XY, XZ, YZ, XYZ, and different numbers of parameters for each, so I think each having
        // and item is too confusing. How about this: arrow = rotate one dimension, based on the face you click, and takes 1 param... powder: rotates all three
        // at once, and takes 3 params.
        int xx;
        int zz;
        int yy;
        double newxzX;
        double newxzZ;
        double newxyY;
        double newxyX;
        double newyzY;
        double newyzZ;
        final double bpow = Math.pow(this.bsize + 0.5, 2);
        final double cosYaw = Math.cos(this.seYaw);
        final double sinYaw = Math.sin(this.seYaw);
        final double cosPitch = Math.cos(this.sePitch);
        final double sinPitch = Math.sin(this.sePitch);
        final double cosRoll = Math.cos(this.seRoll);
        final double sinRoll = Math.sin(this.seRoll);
        final boolean[][][] doNotFill = new boolean[this.snap.length][this.snap.length][this.snap.length];
        final vUndo h = new vUndo(this.getTargetBlock().getWorld().getName());

        for (int x = 0; x < this.snap.length; x++) {
            xx = x - this.bsize;
            final double xpow = Math.pow(xx, 2);
            for (int z = 0; z < this.snap.length; z++) {
                zz = z - this.bsize;
                final double zpow = Math.pow(zz, 2);
                newxzX = (xx * cosYaw) - (zz * sinYaw);
                newxzZ = (xx * sinYaw) + (zz * cosYaw);
                for (int y = 0; y < this.snap.length; y++) {
                    yy = y - this.bsize;
                    if (xpow + zpow + Math.pow(yy, 2) <= bpow) {
                        h.put(this.clampY(this.getBlockPositionX() + xx, this.getBlockPositionY() + yy, this.getBlockPositionZ() + zz)); // just store whole sphere in undo, too complicated otherwise, since this
                                                                                      // brush both adds and remos things unpredictably.

                        newxyX = (newxzX * cosPitch) - (yy * sinPitch);
                        newxyY = (newxzX * sinPitch) + (yy * cosPitch); // calculates all three in succession in precise math space
                        newyzY = (newxyY * cosRoll) - (newxzZ * sinRoll);
                        newyzZ = (newxyY * sinRoll) + (newxzZ * cosRoll);

                        // end point location = (newxyX, newyzY, newyzZ)

                        doNotFill[(int) newxyX + this.bsize][(int) newyzY + this.bsize][(int) newyzZ + this.bsize] = true; // only rounds off to nearest block
                                                                                                                           // after all three, though.

                        final vBlock vb = this.snap[x][y][z];
                        if (vb.id == 0) {
                            continue;
                        }
                        this.setBlockIdAt(vb.id, this.getBlockPositionX() + (int) newxyX, this.getBlockPositionY() + (int) newyzY, this.getBlockPositionZ() + (int) newyzZ);
                    }
                }
            }
        }
        int A;
        int B;
        int C;
        int D;
        int fx;
        int fy;
        int fz;
        int winner;
        for (int x = 0; x < this.snap.length; x++) {
            final double xpow = Math.pow(x - this.bsize, 2);
            fx = x + this.getBlockPositionX() - this.bsize;
            for (int z = 0; z < this.snap.length; z++) {
                final double zpow = Math.pow(z - this.bsize, 2);
                fz = z + this.getBlockPositionZ() - this.bsize;
                for (int y = 0; y < this.snap.length; y++) {
                    if (xpow + zpow + Math.pow(y - this.bsize, 2) <= bpow) {
                        if (!doNotFill[x][y][z]) {
                            // smart fill stuff
                            fy = y + this.getBlockPositionY() - this.bsize;
                            A = this.getBlockIdAt(fx + 1, fy, fz);
                            D = this.getBlockIdAt(fx - 1, fy, fz);
                            C = this.getBlockIdAt(fx, fy, fz + 1);
                            B = this.getBlockIdAt(fx, fy, fz - 1);
                            if (A == B || A == C || A == D) { // I figure that since we are already narrowing it down to ONLY the holes left behind, it should
                                                              // be fine to do all 5 checks needed to be legit about it.
                                winner = A;
                            } else if (B == D || C == D) {
                                winner = D;
                            } else {
                                winner = B; // blockPositionY making this default, it will also automatically cover situations where B = C;
                            }

                            this.setBlockIdAt(winner, fx, fy, fz);
                        }
                    }
                }
            }
        }
        v.storeUndo(h);
    }

    // after all rotations, compare snapshot to new state of world, and store changed blocks to undo?
    // --> agreed. Do what erode does and store one snapshot with Block pointers and int id of what the block started with, afterwards simply go thru that
    // matrix and compare Block.getId with 'id' if different undo.add( new vBlock ( Block, oldId ) )

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        this.bsize = v.brushSize;

        switch (this.mode) {
        case 0:
            this.getMatrix();
            this.rotate(v);
            break;

        default:
            v.owner().getPlayer().sendMessage(ChatColor.RED + "Something went wrong.");
            break;
        }
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.arrow(v);
    }
}
