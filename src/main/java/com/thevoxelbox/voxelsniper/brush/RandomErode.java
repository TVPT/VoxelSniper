package com.thevoxelbox.voxelsniper.brush;

import java.util.Random;

import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Piotr Randomized blockPositionY Giltwist
 */
public class RandomErode extends Brush {

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
    private final double trueCircle = 0.5;

    protected Random generator = new Random();

    private static int timesUsed = 0;

    public RandomErode() {
        this.setName("RandomErode");
    }

    @Override
    public final int getTimesUsed() {
        return RandomErode.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();

    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        RandomErode.timesUsed = tUsed;
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

    private void rerosion(final SnipeData v) {
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
    }

    private void rfilling(final SnipeData v) {
        final Undo h = new Undo(this.getTargetBlock().getWorld().getName());

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
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        this.bsize = v.getBrushSize();

        this.snap = new eBlock[0][0][0];

        this.erodeFace = this.generator.nextInt(5) + 1;
        this.fillFace = this.generator.nextInt(3) + 3;
        this.erodeRecursion = this.generator.nextInt(3);
        this.fillRecursion = this.generator.nextInt(3);

        if (this.fillRecursion == 0 && this.erodeRecursion == 0) { // if they are both zero, it will lead to a null pointer exception. Still want to give them a
                                                                   // chance to be zero though, for more interestingness -Gav
            this.erodeRecursion = this.generator.nextInt(2) + 1;
            this.fillRecursion = this.generator.nextInt(2) + 1;
        }

        this.rerosion(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        this.bsize = v.getBrushSize();

        this.snap = new eBlock[0][0][0];

        this.erodeFace = this.generator.nextInt(3) + 3;
        this.fillFace = this.generator.nextInt(5) + 1;
        this.erodeRecursion = this.generator.nextInt(3);
        this.fillRecursion = this.generator.nextInt(3);
        if (this.fillRecursion == 0 && this.erodeRecursion == 0) { // if they are both zero, it will lead to a null pointer exception. Still want to give them a
                                                                   // chance to be zero though, for more interestingness -Gav
            this.erodeRecursion = this.generator.nextInt(2) + 1;
            this.fillRecursion = this.generator.nextInt(2) + 1;
        }

        this.rfilling(v);
    }
}
