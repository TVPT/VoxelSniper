package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * Basically freezes water, quenches lava and torches, encases everything solid with a sheath of ice, generates random ice crystals that destroy
 * anything they intersect with, and then covers everything inside and out with snow.
 * @author Gavjenks
 */
public class FreezeRay extends Brush {
    private int height = 5;
    private double frequency = 200;
    private double sigmoid = 0.5;    
    private static int timesUsed = 0;

    public FreezeRay() {
        this.setName("Freeze Ray");
    }

    private final void freeyeRaz(final SnipeData v) {
        final int _brushSize = v.getBrushSize();

        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        int _octant = 0;
        int _octX = 0;
        int _octY = 0;
        int _octZ = 0;

        final double bpow = Math.pow(_brushSize + 0.5, 2);
        for (int z = _brushSize; z >= 0; z--) {
            for (int x = _brushSize; x >= 0; x--) {
                for (int y = _brushSize; y >= 0; y--) {
                    if ((Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)) <= bpow) {
                        for (int i = 1; i < 9; i++) {
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

                            _octant = this.getBlockIdAt(_octX, _octY, _octZ);

                            // Lava to obsidian
                            if (_octant == 10 || _octant == 11) {
                                _undo.put(this.clampY(_octX, _octY, _octZ));
                                this.setBlockIdAt(49, _octX, _octY, _octZ);
                            }

                            // Douse any fires
                            if (_octant == 51) {
                                _undo.put(this.clampY(_octX, _octY, _octZ));
                                this.setBlockIdAt(0, _octX, _octY, _octZ);
                            }

                            // freeze solid blocks
                            // water to ice
                            if (_octant == 8 || _octant == 9) {
                                _undo.put(this.clampY(_octX, _octY, _octZ));
                                this.setBlockIdAt(79, _octX, _octY, _octZ);
                            }

                            // quench torches
                            if (_octant == 50) {
                                _undo.put(this.clampY(_octX, _octY, _octZ));
                                this.setBlockIdAt(0, _octX, _octY, _octZ);
                            }

                            // Snow on everything (tops of crystals are handled above)
                            if (_octant == 0 && this.getBlockIdAt(_octX, _octY - 1, _octZ) != 0 && this.getBlockIdAt(_octX, _octY - 1, _octZ) != 78) {
                                _undo.put(this.clampY(_octX, _octY, _octZ));
                                this.setBlockIdAt(78, _octX, _octY, _octZ);
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
        this.freeyeRaz(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.freeyeRaz(v);
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.size();
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	if (par[1].equalsIgnoreCase("info")) {
    		v.sendMessage(ChatColor.LIGHT_PURPLE + "Freeze Ray Parameters:");
    		v.sendMessage(ChatColor.BLUE + "h[number] (ex:  h20) Maximum crystal height");
    		v.sendMessage(ChatColor.GOLD
    				+ "s[number] (ex:   s0.9) Sets the hardness of the sigmoid curve.  must be between 0 and 1.  Closer to zero = you will have sudden, big differences in crystal heights near the middle.");
    		v.sendMessage(ChatColor.DARK_GREEN + "f[number] (ex:   f200) 1/f = likelihood of a crystal growing out of any given block.");
    		return;
    	}
    	
    	for (int _i = 1; _i < par.length; _i++) {
    		if (par[_i].startsWith("h")) {
    			this.height = Integer.parseInt(par[_i].substring(0));
    			v.sendMessage(ChatColor.BLUE + "Max height of crystals set to " + this.height);
    		} else if (par[_i].startsWith("f")) {
    			this.frequency = Double.parseDouble(par[_i].substring(0));
    			v.sendMessage(ChatColor.DARK_GREEN + "1/f frequency of crystals set to " + this.frequency);
    		} else if (par[_i].startsWith("s")) {
    			this.sigmoid = Double.parseDouble(par[_i].substring(0));
    			if (this.sigmoid < 0) {
    				this.sigmoid = 0;
    			}
    			if (this.sigmoid > 1) {
    				this.sigmoid = 1;
    			}
    			v.sendMessage(ChatColor.GOLD + "Sigmoid set to " + this.sigmoid);
    		} else {
    			v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
    		}
    	}
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
