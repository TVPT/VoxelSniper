/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.uBlock;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import com.thevoxelbox.voxelsniper.undo.vUndo;
import org.bukkit.block.Block;
import java.util.Random;

/**
 *
 * @author Piotr
 * Randomized by Giltwist
 */
public class RandomErode extends Brush {

    private eBlock[][][] snap;
    private eBlock[][][] firstSnap;
    private int bsize;
    private int erodeFace;
    private int fillFace;
    private int brushSize;
    private int erodeRecursion = 1;
    private int fillRecursion = 1;
    private double trueCircle = 0.5;
    protected Random generator = new Random();

    public RandomErode() {
        name = "RandomErode";
    }

    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        
        bsize = v.brushSize;

        snap = new eBlock[0][0][0];

        erodeFace = generator.nextInt(5) + 1;
        fillFace = generator.nextInt(3) + 3;
        erodeRecursion = generator.nextInt(3);
        fillRecursion = generator.nextInt(3);

        if (fillRecursion == 0 && erodeRecursion == 0) { //if they are both zero, it will lead to a null pointer exception.  Still want to give them a chance to be zero though, for more interestingness -Gav
            erodeRecursion = generator.nextInt(2) + 1;
            fillRecursion = generator.nextInt(2) + 1;
        }

        rerosion(v);
    }

    @Override
    public void powder(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();

        bsize = v.brushSize;

        snap = new eBlock[0][0][0];

        erodeFace = generator.nextInt(3) + 3;
        fillFace = generator.nextInt(5) + 1;
        erodeRecursion = generator.nextInt(3);
        fillRecursion = generator.nextInt(3);
        if (fillRecursion == 0 && erodeRecursion == 0) { //if they are both zero, it will lead to a null pointer exception.  Still want to give them a chance to be zero though, for more interestingness -Gav
            erodeRecursion = generator.nextInt(2) + 1;
            fillRecursion = generator.nextInt(2) + 1;
        }

        rfilling(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();

    }

    private void rerosion(vSniper v) {
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

        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }

    private void rfilling(vSniper v) {
        vUndo h = new vUndo(tb.getWorld().getName());

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

        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }

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
}
