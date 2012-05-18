/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vBlock;
import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 *
 * @author Gavjenks (from Gavjenks & Piotr'w 2D brush)
 */
public class Rot3D extends Brush {

    protected int mode = 0;
    private int bsize;
    private int brushSize;
    private vBlock[][][] snap;
    private double seYaw;
    private double sePitch;
    private double seRoll;

    public Rot3D() {
        name = "3D Rotation";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();

        bsize = v.brushSize;

        switch (mode) {
            case 0:
                getMatrix();
                rotate(v);
                break;

            default:
                v.owner().p.sendMessage(ChatColor.RED + "Something went wrong.");
                break;
        }
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        arrow(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.brushMessage("Rotates Yaw (XZ), then Pitch(XY), then Roll(ZY), in order.");
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Rotate brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "p[0-359] -- set degrees of pitch rotation (rotation about the Z axis).");
            v.sendMessage(ChatColor.BLUE + "r[0-359] -- set degrees of roll rotation (rotation about the X axis).");
            v.sendMessage(ChatColor.LIGHT_PURPLE + "y[0-359] -- set degrees of yaw rotation (Rotation about the Y axis).");

            return;
        }
        for (int x = 1; x < par.length; x++) {
            //which way is clockwise is less obvious for roll and pitch... should probably fix that / make it clear
            if (par[x].startsWith("p")) {
                sePitch = Math.toRadians(Double.parseDouble(par[x].replace("p", "")));
                v.sendMessage(ChatColor.AQUA + "Around Z-axis degrees set to " + sePitch);
                if (sePitch < 0 || sePitch > 359) {
                    v.sendMessage(ChatColor.RED + "Invalid brush parameters! Angles must be from 1-359");
                }
                continue;
            } else if (par[x].startsWith("r")) {
                seRoll = Math.toRadians(Double.parseDouble(par[x].replace("r", "")));
                v.sendMessage(ChatColor.AQUA + "Around X-axis degrees set to " + seRoll);
                if (seRoll < 0 || seRoll > 359) {
                    v.sendMessage(ChatColor.RED + "Invalid brush parameters! Angles must be from 1-359");
                }
                continue;
            } else if (par[x].startsWith("y")) {
                seYaw = Math.toRadians(Double.parseDouble(par[x].replace("y", "")));
                v.sendMessage(ChatColor.AQUA + "Around Y-axis degrees set to " + seYaw);
                if (seYaw < 0 || seYaw > 359) {
                    v.sendMessage(ChatColor.RED + "Invalid brush parameters! Angles must be from 1-359");
                }
                continue;
            }
        }
    }

    private void rotate(vData v) {
        //basically 1) make it a sphere we are rotating in, not a cylinder
        // 2) do three rotations in a row, one in each dimension, unless some dimensions are set to zero or udnefined or whatever, then skip those.
        //  --> Why not utilize Sniper'w new oportunities and have arrow rotate all 3, powder rotate x, goldsisc y, otherdisc z. Or something like that. Or we could just use arrow and powder and just differenciate between left and right click that gis 4 different situations
        //      --> Well, there would be 7 different possibilities... X, Y, Z, XY, XZ, YZ, XYZ, and different numbers of parameters for each, so I think each having and item is too confusing.  How about this: arrow = rotate one dimension, based on the face you click, and takes 1 param... powder: rotates all three at once, and takes 3 params.
        int xx;
        int zz;
        int yy;
        double newxzX;
        double newxzZ;
        double newxyY;
        double newxyX;
        double newyzY;
        double newyzZ;
        double bpow = Math.pow(bsize + 0.5, 2);
        double cosYaw = Math.cos(seYaw);
        double sinYaw = Math.sin(seYaw);
        double cosPitch = Math.cos(sePitch);
        double sinPitch = Math.sin(sePitch);
        double cosRoll = Math.cos(seRoll);
        double sinRoll = Math.sin(seRoll);
        boolean[][][] doNotFill = new boolean[snap.length][snap.length][snap.length];
        vUndo h = new vUndo(tb.getWorld().getName());

        for (int x = 0; x < snap.length; x++) {
            xx = x - bsize;
            double xpow = Math.pow(xx, 2);
            for (int z = 0; z < snap.length; z++) {
                zz = z - bsize;
                double zpow = Math.pow(zz, 2);
                newxzX = (xx * cosYaw) - (zz * sinYaw);
                newxzZ = (xx * sinYaw) + (zz * cosYaw);
                for (int y = 0; y < snap.length; y++) {
                    yy = y - bsize;
                    if (xpow + zpow + Math.pow(yy, 2) <= bpow) {
                        h.put(clampY(bx + xx, by + yy, bz + zz)); //just store whole sphere in undo, too complicated otherwise, since this brush both adds and remos things unpredictably.

                        newxyX = (newxzX * cosPitch) - (yy * sinPitch);
                        newxyY = (newxzX * sinPitch) + (yy * cosPitch); //calculates all three in succession in precise math space
                        newyzY = (newxyY * cosRoll) - (newxzZ * sinRoll);
                        newyzZ = (newxyY * sinRoll) + (newxzZ * cosRoll);

                        //end point location = (newxyX,  newyzY, newyzZ)

                        doNotFill[(int) newxyX + bsize][(int) newyzY + bsize][(int) newyzZ + bsize] = true; //only rounds off to nearest block after all three, though.

                        vBlock vb = snap[x][y][z];
                        if (vb.id == 0) {
                            continue;
                        }
                        setBlockIdAt(vb.id, bx + (int) newxyX, by + (int) newyzY, bz + (int) newyzZ);
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
        for (int x = 0; x < snap.length; x++) {
            double xpow = Math.pow(x - bsize, 2);
            fx = x + bx - bsize;
            for (int z = 0; z < snap.length; z++) {
                double zpow = Math.pow(z - bsize, 2);
                fz = z + bz - bsize;
                for (int y = 0; y < snap.length; y++) {
                    if (xpow + zpow + Math.pow(y - bsize, 2) <= bpow) {
                        if (!doNotFill[x][y][z]) {
                            //smart fill stuff
                            fy = y + by - bsize;
                            A = getBlockIdAt(fx + 1, fy, fz);
                            D = getBlockIdAt(fx - 1, fy, fz);
                            C = getBlockIdAt(fx, fy, fz + 1);
                            B = getBlockIdAt(fx, fy, fz - 1);
                            if (A == B || A == C || A == D) {   //I figure that since we are already narrowing it down to ONLY the holes left behind, it should be fine to do all 5 checks needed to be legit about it.
                                winner = A;
                            } else if (B == D || C == D) {
                                winner = D;
                            } else {
                                winner = B; // by making this default, it will also automatically cover situations where B = C;
                            }

                            setBlockIdAt(winner, fx, fy, fz);
                        }
                    }
                }
            }
        }
        v.storeUndo(h);
    }
    //after all rotations, compare snapshot to new state of world, and store changed blocks to undo?
    // --> agreed. Do what erode does and store one snapshot with Block pointers and int id of what the block started with, afterwards simply go thru that matrix and compare Block.getId with 'id' if different undo.add( new vBlock ( Block, oldId ) )

    private void getMatrix() {// only need to do once.  But y needs to change + sphere
        brushSize = (bsize * 2) + 1;

        snap = new vBlock[brushSize][brushSize][brushSize];

        int derp = bsize;
        int sx = bx - bsize;
        int sy = by - bsize;
        int sz = bz - bsize;
        double bpow = Math.pow(bsize + 0.5, 2);
        for (int x = 0; x < snap.length; x++) {
            sz = bz - derp;
            double xpow = Math.pow(x - bsize, 2);
            for (int z = 0; z < snap.length; z++) {
                sy = by - derp;
                double zpow = Math.pow(z - bsize, 2);
                for (int y = 0; y < snap.length; y++) {
                    if (xpow + zpow + Math.pow(y - bsize, 2) <= bpow) {
                        Block b = clampY(sx, sy, sz);
                        snap[x][y][z] = new vBlock(b);
                        b.setTypeId(0);
                        sy++;
                    }
                }

                sz++;
            }
            sx++;
        }

    }
}
