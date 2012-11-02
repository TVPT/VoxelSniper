package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Underlay_Brush
 * @author jmck95 Credit to GavJenks for framework and 95 of code. Big Thank you to GavJenks
 */

public class UnderlayBrush extends PerformBrush {
	private static final int DEFAULT_DEPTH = 3;
	
	private static int timesUsed = 0;
    private int depth = DEFAULT_DEPTH;
    private boolean allBlocks = false;

    /**
     * 
     */
    public UnderlayBrush() {
        this.setName("Underlay (Reverse Overlay)");
    }

    private void underlay(final SnipeData v) {
        final int _bSize = v.getBrushSize();
        final int[][] _memory = new int[_bSize * 2 + 1][_bSize * 2 + 1];
        final double _bPow = Math.pow(_bSize + 0.5, 2);
        
        for (int _z = _bSize; _z >= -_bSize; _z--) {
            for (int _x = _bSize; _x >= -_bSize; _x--) {
                for (int _y = this.getBlockPositionY(); _y < this.getBlockPositionY() + this.depth; _y++) { // start scanning from the height you clicked at
                    if (_memory[_x + _bSize][_z + _bSize] != 1) { // if haven't already found the surface in this column
                        if ((Math.pow(_x, 2) + Math.pow(_z, 2)) <= _bPow) { // if inside of the column...
                            if (!this.allBlocks) { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
                                switch (this.getBlockIdAt(this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z)) {
                                case 1:
                                case 2:
                                case 3:
                                case 12:
                                case 13:
                                case 24:// These cases filter out any manufactured or refined blocks, any trees and leas, etc. that you don't want to mess with.
                                case 48:
                                case 82:
                                case 49:
                                case 78:
                                    for (int _d = 0; (_d < this.depth); _d++) {
                                        if (this.clampY(this.getBlockPositionX() + _x, _y + _d, this.getBlockPositionZ() + _z).getTypeId() != 0) {
                                            this.current.perform(this.clampY(this.getBlockPositionX() + _x, _y + _d, this.getBlockPositionZ() + _z)); // fills down as many layers as you specify in
                                                                                                                // parameters
                                            _memory[_x + _bSize][_z + _bSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                        }
                                    }
                                    break;

                                default:
                                    break;
                                }
                            } else {
                                for (int _d = 0; (_d < this.depth); _d++) {
                                    if (this.clampY(this.getBlockPositionX() + _x, _y + _d, this.getBlockPositionZ() + _z).getTypeId() != 0) {
                                        this.current.perform(this.clampY(this.getBlockPositionX() + _x, _y + _d, this.getBlockPositionZ() + _z)); // fills down as many layers as you specify in
                                                                                                            // parameters
                                        _memory[_x + _bSize][_z + _bSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
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

    private void underlay2(final SnipeData v) {
        final int _bSize = v.getBrushSize();
        final int[][] _memory = new int[_bSize * 2 + 1][_bSize * 2 + 1];
        final double _bPow = Math.pow(_bSize + 0.5, 2);
        
        for (int _z = _bSize; _z >= -_bSize; _z--) {
            for (int _x = _bSize; _x >= -_bSize; _x--) {
                for (int _y = this.getBlockPositionY(); _y < this.getBlockPositionY() + this.depth; _y++) { // start scanning from the height you clicked at
                    if (_memory[_x + _bSize][_z + _bSize] != 1) { // if haven't already found the surface in this column
                        if ((Math.pow(_x, 2) + Math.pow(_z, 2)) <= _bPow) { // if inside of the column...

                            if (!this.allBlocks) { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.

                                switch (this.getBlockIdAt(this.getBlockPositionX() + _x, _y, this.getBlockPositionZ() + _z)) {
                                case 1:
                                case 2:
                                case 3:
                                case 12:
                                case 13:
                                case 14: // These cases filter out any manufactured or refined blocks, any trees and leas, etc. that you don't want to mess
                                         // with.
                                case 15:
                                case 16:
                                case 24:
                                case 48:
                                case 82:
                                case 49:
                                case 78:
                                    for (int _d = -1; (_d < this.depth - 1); _d++) {
                                        this.current.perform(this.clampY(this.getBlockPositionX() + _x, _y - _d, this.getBlockPositionZ() + _z)); // fills down as many layers as you specify in
                                                                                                            // parameters
                                        _memory[_x + _bSize][_z + _bSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                    }
                                    break;

                                default:
                                    break;
                                }
                            } else {
                                for (int _d = -1; (_d < this.depth - 1); _d++) {
                                    this.current.perform(this.clampY(this.getBlockPositionX() + _x, _y - _d, this.getBlockPositionZ() + _z)); // fills down as many layers as you specify in
                                                                                                        // parameters
                                    _memory[_x + _bSize][_z + _bSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
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
    public final void arrow(final SnipeData v) {
        this.underlay(v);
    }

    @Override
    public final void powder(final SnipeData v) {
        this.underlay2(v);
    }
    
    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (int _i = 1; _i < par.length; _i++) {
        	if (par[_i].equalsIgnoreCase("info")) {
        		v.owner().getPlayer().sendMessage(ChatColor.GOLD + "Reverse Overlay brush parameters:");
        		v.owner().getPlayer().sendMessage(ChatColor.AQUA + "d[number] (ex: d3) The number of blocks thick to change.");
        		v.owner().getPlayer().sendMessage(ChatColor.BLUE + "all (ex: /b reover all) Sets the brush to affect ALL materials");
        		if (this.depth < 1) {
        			this.depth = 1;
        		}
        		return;
        	}
            if (par[_i].startsWith("d")) {
                this.depth = Integer.parseInt(par[_i].replace("d", ""));
                v.owner().getPlayer().sendMessage(ChatColor.AQUA + "Depth set to " + this.depth);

                continue;
            } else if (par[_i].startsWith("all")) {
                this.allBlocks = true;
                v.owner().getPlayer().sendMessage(ChatColor.BLUE + "Will underlay over any block." + this.depth);
                continue;
            } else if (par[_i].startsWith("some")) {
                this.allBlocks = false;
                v.owner().getPlayer().sendMessage(ChatColor.BLUE + "Will underlay only natural block types." + this.depth);
                continue;
            } else {
                v.owner().getPlayer().sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }

        }
    }
    
    @Override
    public final int getTimesUsed() {
        return UnderlayBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
        UnderlayBrush.timesUsed = tUsed;
    }
}
