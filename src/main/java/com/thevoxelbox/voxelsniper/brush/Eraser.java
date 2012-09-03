package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Voxel
 */
public class Eraser extends Brush {	
    private static int timesUsed = 0;

    public Eraser() {
        this.setName("Eraser");
    }

    private final void doErase(final SnipeData v, boolean keepWater) {
        final int _brushSize = v.getBrushSize();

        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        for (int _x = 2 * _brushSize; _x >= 0; _x--) {
            for (int _y = 0; _y <= 2 * _brushSize; _y++) {
                for (int _z = 2 * _brushSize; _z >= 0; _z--) {
                    final int _blockMaterialId = this.getBlockIdAt(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _brushSize + _y, this.getBlockPositionZ() - _brushSize + _z);
                    if (_blockMaterialId > 3 && _blockMaterialId != 12 && _blockMaterialId != 13) {
                        if (!(keepWater && (_blockMaterialId == 8 || _blockMaterialId == 9))) {
                            _undo.put(this.clampY(this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _brushSize + _y, this.getBlockPositionZ() - _brushSize + _z));
                            this.setBlockIdAt(0, this.getBlockPositionX() - _brushSize + _x, this.getBlockPositionY() - _brushSize + _y, this.getBlockPositionZ() - _brushSize + _z);
                        }
                    }
                }
            }
        }
        v.storeUndo(_undo);
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.doErase(v, false);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.doErase(v, true);
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.size();
    }
    
    @Override
    public final int getTimesUsed() {
    	return Eraser.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	Eraser.timesUsed = tUsed;
    }
}
