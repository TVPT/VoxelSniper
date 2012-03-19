/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vBlock;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 *
 * @author Piotr
 */
public class Rot2D extends Brush {

    protected int mode = 0;
    private int bsize;
    private int brushSize;
    private vBlock[][][] snap;
    private double se;
    
    public Rot2D() {
        name = "2D Rotation";
    }

    @Override
    public void arrow(vSniper v) {
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
                v.p.sendMessage(ChatColor.RED + "Something went wrong.");
                break;
        }
    }

    @Override
    public void powder(vSniper v) {
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
    }

    @Override
    public void parameters(String[] par, vSniper v) {
        se = Math.toRadians(Double.parseDouble(par[1]));
        v.p.sendMessage(ChatColor.GREEN + "Angle set to " + se);
    }

    private void rotate(vSniper v) {
        int xx;
        int zz;
        int yy;
        double newx;
        double newz;
        double bpow = Math.pow(bsize + 0.5, 2);
        double cos = Math.cos(se);
        double sin = Math.sin(se);
        boolean[][] doNotFill = new boolean[snap.length][snap.length];
        //I put y in the inside loop, since it doesn't have any power functions, should be much faster.
        //Also, new array keeps track of which x and z coords are being assigned in the rotated space so that we can
        //do a targeted filling of only those columns later that were left out.

        for (int x = 0; x < snap.length; x++) {
            xx = x - bsize;
            double xpow = Math.pow(xx, 2);
            for (int z = 0; z < snap.length; z++) {
                zz = z - bsize;
                if (xpow + Math.pow(zz, 2) <= bpow) {
                    newx = (xx * cos) - (zz * sin);
                    newz = (xx * sin) + (zz * cos);

                    doNotFill[(int) newx + bsize][(int) newz + bsize] = true;

                    for (int y = 0; y < snap.length; y++) {
                        yy = y - bsize;

                        vBlock vb = snap[x][y][z];
                        if (vb.id == 0) {
                            continue;
                        }
                        setBlockIdAt(vb.id, bx + (int) newx, by + yy, bz + (int) newz);
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
                if (xpow + Math.pow(z - bsize, 2) <= bpow) {
                    fz = z + bz - bsize;
                    if (!doNotFill[x][z]) {
                        //smart fill stuff

                        for (int y = 0; y < snap.length; y++) {
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
    }

    private void getMatrix() {
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
                if (xpow + Math.pow(z - bsize, 2) <= bpow) {
                    for (int y = 0; y < snap.length; y++) {
                        Block b = clampY(sx, sy, sz);  //why is this not sx + x, sy + y sz + z?
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
