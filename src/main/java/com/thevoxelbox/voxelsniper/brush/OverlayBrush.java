package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Gavjenks
 */
public class OverlayBrush extends PerformBrush {
	private static final int DEFAULT_DEPTH = 3;	
    private int depth = DEFAULT_DEPTH;
    private boolean allBlocks = false;

    private static int timesUsed = 0;

    public OverlayBrush() {
        this.setName("Overlay (Topsoil Filling)");
    }

    private final void overlay(final SnipeData v) {
        final int _brushSize = v.getBrushSize();
        final double _bPow = Math.pow(_brushSize + 0.5, 2);
        final int[][] _memory = new int[_brushSize * 2 + 1][_brushSize * 2 + 1];
        
        for (int _z = _brushSize; _z >= -_brushSize; _z--) {
            for (int _x = _brushSize; _x >= -_brushSize; _x--) {
                for (int _y = this.getBlockPositionY(); _y > 0; _y--) { // start scanning from the height you clicked at
                    if (_memory[_x + _brushSize][_z + _brushSize] != 1) { // if haven't already found the surface in this column
                        if ((Math.pow(_x, 2) + Math.pow(_z, 2)) <= _bPow) { // if inside of the column...
                            final int _check = this.getBlockIdAt(this.getBlockPositionX() + _x, _y + 1, this.getBlockPositionZ() + _z);
                            if (_check == 0 || _check == 8 || _check == 9) { // must start at surface... this prevents it filling stuff in if you click in a wall
                                                                          // and it starts out below surface.
                                if (!this.allBlocks) { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
                                    switch (this.getBlockIdAt(this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z)) {
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 12:
                                    case 13:
                                    case 24:
                                    case 48:
                                    case 82:
                                    case 49:
                                    case 78:
                                        for (int _d = 0; (_d < this.depth); _d++) {
                                            if (this.clampY(this.getBlockPositionX() + _x, _y - _d, this.getBlockPositionZ() + _z).getTypeId() != 0) {
                                                this.current.perform(this.clampY(this.getBlockPositionX() + _x, _y - _d, this.getBlockPositionZ() + _z)); // fills down as many layers as you specify
                                                                                                                    // in parameters
                                                _memory[_x + _brushSize][_z + _brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                            }
                                        }
                                        break;

                                    default:
                                        break;
                                    }
                                } else {
                                    for (int _d = 0; (_d < this.depth); _d++) {
                                        if (this.clampY(this.getBlockPositionX() + _x, _y - _d, this.getBlockPositionZ() + _z).getTypeId() != 0) {
                                            this.current.perform(this.clampY(this.getBlockPositionX() + _x, _y - _d, this.getBlockPositionZ() + _z)); // fills down as many layers as you specify in
                                                                                                                // parameters
                                            _memory[_x + _brushSize][_z + _brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    private final void overlayTwo(final SnipeData v) {
        final int _brushSize = v.getBrushSize();
        final double _bPow = Math.pow(_brushSize + 0.5, 2);
        final int[][] _memory = new int[_brushSize * 2 + 1][_brushSize * 2 + 1];
        
        for (int _z = _brushSize; _z >= -_brushSize; _z--) {
            for (int _x = _brushSize; _x >= -_brushSize; _x--) {
                for (int _y = this.getBlockPositionY(); _y > 0; _y--) { // start scanning from the height you clicked at
                    if (_memory[_x + _brushSize][_z + _brushSize] != 1) { // if haven't already found the surface in this column
                        if ((Math.pow(_x, 2) + Math.pow(_z, 2)) <= _bPow) { // if inside of the column...
                            if (this.getBlockIdAt(this.getBlockPositionX() + _x, _y - 1, this.getBlockPositionZ() + _z) != 0) { // if not a floating block (like one of Notch'world pools)
                                if (this.getBlockIdAt(this.getBlockPositionX() + _x, _y + 1, this.getBlockPositionZ() + _z) == 0) { // must start at surface... this prevents it filling stuff in if
                                                                                               // you click in a wall and it starts out below surface.
                                    if (!this.allBlocks) { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.

                                        switch (this.getBlockIdAt(this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z)) {
                                        case 1:
                                        case 2:
                                        case 3:
                                        case 12:
                                        case 13:
                                        case 14: 
                                        case 15:
                                        case 16:
                                        case 24:
                                        case 48:
                                        case 82:
                                        case 49:
                                        case 78:
                                            for (int _d = 1; (_d < this.depth + 1); _d++) {
                                                this.current.perform(this.clampY(this.getBlockPositionX() + _x, _y + _d, this.getBlockPositionZ() + _z)); // fills down as many layers as you specify
                                                                                                                    // in parameters
                                                _memory[_x + _brushSize][_z + _brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                            }
                                            break;

                                        default:
                                            break;
                                        }
                                    } else {
                                        for (int _d = 1; (_d < this.depth + 1); _d++) {
                                            this.current.perform(this.clampY(this.getBlockPositionX() + _x, _y + _d, this.getBlockPositionZ() + _z)); // fills down as many layers as you specify in
                                                                                                                // parameters
                                            _memory[_x + _brushSize][_z + _brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        v.storeUndo(this.current.getUndo());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.overlay(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.overlayTwo(v);
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.size();
    }
    

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (int _i = 1; _i < par.length; _i++) {
        	final String _param = par[_i];
        	
        	if (_param.equalsIgnoreCase("info")) {
        		v.sendMessage(ChatColor.GOLD + "Overlay brush parameters:");
        		v.sendMessage(ChatColor.AQUA + "d[number] (ex:  d3) How many blocks deep you want to replace from the surface.");
        		v.sendMessage(ChatColor.BLUE
        				+ "all (ex:  /b over all) Sets the brush to overlay over ALL materials, not just natural surface ones (will no longer ignore trees and buildings).  The parameter /some will set it back to default.");
        		return;
        	}
            if (_param.startsWith("d")) {
                this.depth = Integer.parseInt(_param.replace("d", ""));
                v.sendMessage(ChatColor.AQUA + "Depth set to " + this.depth);
                if (this.depth < 1) {
                    this.depth = 1;
                }
                continue;
            } else if (_param.startsWith("all")) {
                this.allBlocks = true;
                v.sendMessage(ChatColor.BLUE + "Will overlay over any block." + this.depth);
                continue;
            } else if (_param.startsWith("some")) {
                this.allBlocks = false;
                v.sendMessage(ChatColor.BLUE + "Will overlay only natural block types." + this.depth);
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }
    
    @Override
    public final int getTimesUsed() {
    	return OverlayBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	OverlayBrush.timesUsed = tUsed;
    }
}
