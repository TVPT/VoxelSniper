package com.thevoxelbox.voxelsniper.brush;

import java.util.Random;

import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Random-Erode_Brush
 * @author Piotr 
 * @author Giltwist (Randomized blockPositionY)
 */
public class RandomErodeBrush extends Brush {
    private class BlockWrapper {
        public boolean solid;
        public Block nativeBlock;
		public int id;
        public int i;

        /**
         * 
         * @param bl
         */
        public BlockWrapper(final Block bl) {
            this.nativeBlock = bl;
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

    private BlockWrapper[][][] snap;
    private BlockWrapper[][][] firstSnap;
    private int bsize;
    private int erodeFace;
    private int fillFace;
    private int brushSize;
    private int erodeRecursion = 1;
    private int fillRecursion = 1;
    private final double trueCircle = 0.5;

    private Random generator = new Random();

    private static int timesUsed = 0;

    /**
     * 
     */
    public RandomErodeBrush() {
        this.setName("RandomErode");
    }


    private boolean erode(final int x, final int y, final int z) {
        if (this.snap[x][y][z].solid) {
            int _d = 0;
            if (!this.snap[x + 1][y][z].solid) {
                _d++;
            }
            if (!this.snap[x - 1][y][z].solid) {
                _d++;
            }
            if (!this.snap[x][y + 1][z].solid) {
                _d++;
            }
            if (!this.snap[x][y - 1][z].solid) {
                _d++;
            }
            if (!this.snap[x][y][z + 1].solid) {
                _d++;
            }
            if (!this.snap[x][y][z - 1].solid) {
                _d++;
            }
            return (_d >= this.erodeFace);
        } else {
            return false;
        }
    }

    private boolean fill(final int x, final int y, final int z) {
        if (this.snap[x][y][z].solid) {
            return false;
        } else {
            int _d = 0;
            if (this.snap[x + 1][y][z].solid) {
                this.snap[x][y][z].id = this.snap[x + 1][y][z].nativeBlock.getTypeId();
                _d++;
            }
            if (this.snap[x - 1][y][z].solid) {
                this.snap[x][y][z].id = this.snap[x - 1][y][z].nativeBlock.getTypeId();
                _d++;
            }
            if (this.snap[x][y + 1][z].solid) {
                this.snap[x][y][z].id = this.snap[x][y + 1][z].nativeBlock.getTypeId();
                _d++;
            }
            if (this.snap[x][y - 1][z].solid) {
                this.snap[x][y][z].id = this.snap[x][y - 1][z].nativeBlock.getTypeId();
                _d++;
            }
            if (this.snap[x][y][z + 1].solid) {
                this.snap[x][y][z].id = this.snap[x][y][z + 1].nativeBlock.getTypeId();
                _d++;
            }
            if (this.snap[x][y][z - 1].solid) {
                this.snap[x][y][z].id = this.snap[x][y][z - 1].nativeBlock.getTypeId();
                _d++;
            }
            if (_d >= this.fillFace) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void getMatrix() {
        this.brushSize = ((this.bsize + 1) * 2) + 1;

        if (this.snap.length == 0) {
            this.snap = new BlockWrapper[this.brushSize][this.brushSize][this.brushSize];

            final int _choosUsefulNamesForYourFuckingVariables = (this.bsize + 1);
            int _sx = this.getBlockPositionX() - (this.bsize + 1);
            int _sy = this.getBlockPositionY() - (this.bsize + 1);
            int _sz = this.getBlockPositionZ() - (this.bsize + 1);
            
            for (int _x = 0; _x < this.snap.length; _x++) {
                _sz = this.getBlockPositionZ() - _choosUsefulNamesForYourFuckingVariables;
                for (int _z = 0; _z < this.snap.length; _z++) {
                    _sy = this.getBlockPositionY() - _choosUsefulNamesForYourFuckingVariables;
                    for (int _y = 0; _y < this.snap.length; _y++) {
                        this.snap[_x][_y][_z] = new BlockWrapper(this.clampY(_sx, _sy, _sz));
                        _sy++;
                    }
                    _sz++;
                }
                _sx++;
            }
            this.firstSnap = this.snap.clone();
        } else {
            this.snap = new BlockWrapper[this.brushSize][this.brushSize][this.brushSize];

            final int _choosUsefulNamesForYourFuckingVariables = (this.bsize + 1);
            int _sx = this.getBlockPositionX() - (this.bsize + 1);
            int _sy = this.getBlockPositionY() - (this.bsize + 1);
            int _sz = this.getBlockPositionZ() - (this.bsize + 1);
            
            for (int _x = 0; _x < this.snap.length; _x++) {
                _sz = this.getBlockPositionZ() - _choosUsefulNamesForYourFuckingVariables;
                for (int _z = 0; _z < this.snap.length; _z++) {
                    _sy = this.getBlockPositionY() - _choosUsefulNamesForYourFuckingVariables;
                    for (int _y = 0; _y < this.snap.length; _y++) {
                        this.snap[_x][_y][_z] = new BlockWrapper(this.clampY(_sx, _sy, _sz));
                        _sy++;
                    }
                    _sz++;
                }
                _sx++;
            }
        }
    }

    private void rerosion(final SnipeData v) {
        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());

        if (this.erodeFace >= 0 && this.erodeFace <= 6) {
            for (int _er = 0; _er < this.erodeRecursion; _er++) {
                this.getMatrix();

                final int _choosUsefulNamesForYourFuckingVariables = this.bsize + 1;

                final double _bPow = Math.pow(this.bsize + this.trueCircle, 2);
                for (int _z = 1; _z < this.snap.length - 1; _z++) {

                    final double _zPow = Math.pow(_z - _choosUsefulNamesForYourFuckingVariables, 2);
                    for (int _x = 1; _x < this.snap.length - 1; _x++) {

                        final double _xPow = Math.pow(_x - _choosUsefulNamesForYourFuckingVariables, 2);
                        for (int _y = 1; _y < this.snap.length - 1; _y++) {

                            if (((_xPow + Math.pow(_y - _choosUsefulNamesForYourFuckingVariables, 2) + _zPow) <= _bPow)) {
                                if (this.erode(_x, _y, _z)) {
                                    this.snap[_x][_y][_z].nativeBlock.setTypeId(0);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.fillFace >= 0 && this.fillFace <= 6) {
        	final int _choosUsefulNamesForYourFuckingVariables = this.bsize + 1;
        	final double _bPow = Math.pow(this.bsize + 0.5, 2);
        	
            for (int _fr = 0; _fr < this.fillRecursion; _fr++) {
                this.getMatrix();

                for (int _z = 1; _z < this.snap.length - 1; _z++) {

                    final double _zPow = Math.pow(_z - _choosUsefulNamesForYourFuckingVariables, 2);
                    for (int _x = 1; _x < this.snap.length - 1; _x++) {

                        final double _xPow = Math.pow(_x - _choosUsefulNamesForYourFuckingVariables, 2);
                        for (int _y = 1; _y < this.snap.length - 1; _y++) {

                            if (((_xPow + Math.pow(_y - _choosUsefulNamesForYourFuckingVariables, 2) + _zPow) <= _bPow)) {
                                if (this.fill(_x, _y, _z)) {
                                    this.snap[_x][_y][_z].nativeBlock.setTypeId(this.snap[_x][_y][_z].id);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (int _x = 0; _x < this.firstSnap.length; _x++) {
            for (int _y = 0; _y < this.firstSnap.length; _y++) {
                for (int _z = 0; _z < this.firstSnap.length; _z++) {
                    final BlockWrapper _block = this.firstSnap[_x][_y][_z];
                    if (_block.i != _block.nativeBlock.getTypeId()) {
                        _undo.put(_block.nativeBlock);
                    }
                }
            }
        }

        v.storeUndo(_undo);
    }

    private void rfilling(final SnipeData v) {
        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());

        if (this.fillFace >= 0 && this.fillFace <= 6) {
        	final int _choosUsefulNamesForYourFuckingVariables = this.bsize + 1;
        	final double _bpow = Math.pow(this.bsize + 0.5, 2);

        	for (int _fr = 0; _fr < this.fillRecursion; _fr++) {
                this.getMatrix();

                for (int _z = 1; _z < this.snap.length - 1; _z++) {

                    final double _zPow = Math.pow(_z - _choosUsefulNamesForYourFuckingVariables, 2);
                    for (int _x = 1; _x < this.snap.length - 1; _x++) {

                        final double _xPow = Math.pow(_x - _choosUsefulNamesForYourFuckingVariables, 2);
                        for (int _y = 1; _y < this.snap.length - 1; _y++) {

                            if (((_xPow + Math.pow(_y - _choosUsefulNamesForYourFuckingVariables, 2) + _zPow) <= _bpow)) {
                                if (this.fill(_x, _y, _z)) {
                                    this.snap[_x][_y][_z].nativeBlock.setTypeId(this.snap[_x][_y][_z].id);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.erodeFace >= 0 && this.erodeFace <= 6) {
        	final int _choosUsefulNamesForYourFuckingVariables = this.bsize + 1;
        	final double _bpow = Math.pow(this.bsize + this.trueCircle, 2);

        	for (int _er = 0; _er < this.erodeRecursion; _er++) {
                this.getMatrix();

                for (int _z = 1; _z < this.snap.length - 1; _z++) {

                    final double _zPow = Math.pow(_z - _choosUsefulNamesForYourFuckingVariables, 2);
                    for (int _x = 1; _x < this.snap.length - 1; _x++) {

                        final double _xPow = Math.pow(_x - _choosUsefulNamesForYourFuckingVariables, 2);
                        for (int _y = 1; _y < this.snap.length - 1; _y++) {

                            if (((_xPow + Math.pow(_y - _choosUsefulNamesForYourFuckingVariables, 2) + _zPow) <= _bpow)) {
                                if (this.erode(_x, _y, _z)) {
                                    this.snap[_x][_y][_z].nativeBlock.setTypeId(0);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (int _x = 0; _x < this.firstSnap.length; _x++) {
            for (int _y = 0; _y < this.firstSnap.length; _y++) {
                for (int _z = 0; _z < this.firstSnap.length; _z++) {
                    final BlockWrapper _block = this.firstSnap[_x][_y][_z];
                    if (_block.i != _block.nativeBlock.getTypeId()) {
                        _undo.put(_block.nativeBlock);
                    }
                }
            }
        }

        v.storeUndo(_undo);
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.bsize = v.getBrushSize();

        this.snap = new BlockWrapper[0][0][0];

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
    protected final void powder(final SnipeData v) {
        this.bsize = v.getBrushSize();

        this.snap = new BlockWrapper[0][0][0];

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
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.size();    	
    }
    
    @Override
    public final int getTimesUsed() {
    	return RandomErodeBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	RandomErodeBrush.timesUsed = tUsed;
    }
}
