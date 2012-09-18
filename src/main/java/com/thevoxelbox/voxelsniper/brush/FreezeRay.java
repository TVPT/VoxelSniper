package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.Material;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * Basically freezes water, quenches lava and torches, encases everything solid with a sheath of ice, generates random ice crystals that destroy
 * anything they intersect with, and then covers everything inside and out with snow.
 * @author Gavjenks
 */
public class FreezeRay extends Brush {	
	private static int timesUsed = 0;

    public FreezeRay() {
        this.setName("Freeze Ray");
    }

    private final void applyFreezeRay(final SnipeData v) {
        final int _brushSize = v.getBrushSize();
        final double _bPow = Math.pow(_brushSize + 0.5, 2);
        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        
        for (int z = _brushSize; z >= 0; z--) {
            for (int x = _brushSize; x >= 0; x--) {
                for (int y = _brushSize; y >= 0; y--) {
                    if ((Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)) <= _bPow) {
                        for (int i = 1; i <= 8; i++) {
                            int _octX = 0;
                            int _octY = 0;
                            int _octZ = 0;
                            
                            if (i == 1) {
                                _octX = this.getBlockPositionX() + x;
                                _octY = this.getBlockPositionY() + z;
                                _octZ = this.getBlockPositionZ() + y;
                            }
                            if (i == 2) {
                                _octX = this.getBlockPositionX() + x;
                                _octY = this.getBlockPositionY() + z;
                                _octZ = this.getBlockPositionZ() - y;
                            }
                            if (i == 3) {
                                _octX = this.getBlockPositionX() + x;
                                _octY = this.getBlockPositionY() - z;
                                _octZ = this.getBlockPositionZ() + y;
                            }
                            if (i == 4) {
                                _octX = this.getBlockPositionX() + x;
                                _octY = this.getBlockPositionY() - z;
                                _octZ = this.getBlockPositionZ() - y;
                            }
                            if (i == 5) {
                                _octX = this.getBlockPositionX() - x;
                                _octY = this.getBlockPositionY() + z;
                                _octZ = this.getBlockPositionZ() + y;
                            }
                            if (i == 6) {
                                _octX = this.getBlockPositionX() - x;
                                _octY = this.getBlockPositionY() + z;
                                _octZ = this.getBlockPositionZ() - y;
                            }
                            if (i == 7) {
                                _octX = this.getBlockPositionX() - x;
                                _octY = this.getBlockPositionY() - z;
                                _octZ = this.getBlockPositionZ() + y;
                            }
                            if (i == 8) {
                                _octX = this.getBlockPositionX() - x;
                                _octY = this.getBlockPositionY() - z;
                                _octZ = this.getBlockPositionZ() - y;
                            }

                            final int _octant = this.getBlockIdAt(_octX, _octY, _octZ);

                            // Lava to obsidian
                            if (_octant == Material.LAVA.getId() || _octant == Material.STATIONARY_LAVA.getId()) {
                                _undo.put(this.clampY(_octX, _octY, _octZ));
                                this.setBlockIdAt(Material.OBSIDIAN.getId(), _octX, _octY, _octZ);
                            }

                            // Douse any fires
                            if (_octant == Material.FIRE.getId()) {
                                _undo.put(this.clampY(_octX, _octY, _octZ));
                                this.setBlockIdAt(Material.AIR.getId(), _octX, _octY, _octZ);
                            }

                            if ((_octant == Material.WATER.getId()) || (_octant == Material.STATIONARY_WATER.getId())) {
                                _undo.put(this.clampY(_octX, _octY, _octZ));
                                this.setBlockIdAt(Material.ICE.getId(), _octX, _octY, _octZ);
                            }

                            // quench torches
                            if (_octant == Material.TORCH.getId()) {
                                _undo.put(this.clampY(_octX, _octY, _octZ));
                                this.setBlockIdAt(Material.AIR.getId(), _octX, _octY, _octZ);
                            }

                            // Snow on everything (tops of crystals are handled above)
                            if (_octant == Material.AIR.getId() && this.getBlockIdAt(_octX, _octY - 1, _octZ) != Material.AIR.getId() && this.getBlockIdAt(_octX, _octY - 1, _octZ) != Material.SNOW.getId()) {
                                _undo.put(this.clampY(_octX, _octY, _octZ));
                                this.setBlockIdAt(Material.SNOW.getId(), _octX, _octY, _octZ);
                            }
                        } // end for loop for 8 octants
                    }// end if for whether it'world in the brush or not.
                }// Y
            }// X
            v.storeUndo(_undo);
        }// Z
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.applyFreezeRay(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.applyFreezeRay(v);
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.size();
    }
    
    @Override
    public final int getTimesUsed() {
    	return FreezeRay.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	FreezeRay.timesUsed = tUsed;
    }
}
