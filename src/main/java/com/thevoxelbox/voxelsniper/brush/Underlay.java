package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author jmck95 Credit to GavJenks for framework and 95 of code. Big Thank you to GavJenks
 */

public class Underlay extends PerformBrush {

    int depth = 3;

    boolean allBlocks = false;

    private static int timesUsed = 0;

    public Underlay() {
        this.name = "Underlay (Reverse Overlay)";
    }

    @Override
    public final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.Underlay(v);
    }

    @Override
    public final int getTimesUsed() {
        return Underlay.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        vm.size();
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.owner().getPlayer().sendMessage(ChatColor.GOLD + "Reverse Overlay brush parameters:");
            v.owner().getPlayer().sendMessage(ChatColor.AQUA + "d[number] (ex: d3) The number of blocks thick to change.");
            v.owner().getPlayer().sendMessage(ChatColor.BLUE + "all (ex: /b reover all) Sets the brush to affect ALL materials");
            if (this.depth < 1) {
                this.depth = 1;
            }
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("d")) {
                this.depth = Integer.parseInt(par[x].replace("d", ""));
                v.owner().getPlayer().sendMessage(ChatColor.AQUA + "Depth set to " + this.depth);

                continue;
            } else if (par[x].startsWith("all")) {
                this.allBlocks = true;
                v.owner().getPlayer().sendMessage(ChatColor.BLUE + "Will underlay over any block." + this.depth);
                continue;
            } else if (par[x].startsWith("some")) {
                this.allBlocks = false;
                v.owner().getPlayer().sendMessage(ChatColor.BLUE + "Will underlay only natural block types." + this.depth);
                continue;
            } else {
                v.owner().getPlayer().sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }

        }
    }

    @Override
    public final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.Underlaytwo(v);

    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Underlay.timesUsed = tUsed;
    }

    public final void Underlay(final vData v) {
        final int bsize = v.brushSize;

        final int[][] memory = new int[bsize * 2 + 1][bsize * 2 + 1];
        final double bpow = Math.pow(bsize + 0.5, 2);
        for (int z = bsize; z >= -bsize; z--) {
            for (int x = bsize; x >= -bsize; x--) {
                for (int y = this.by; y < this.by + this.depth; y++) { // start scanning from the height you clicked at
                    if (memory[x + bsize][z + bsize] != 1) { // if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= bpow) { // if inside of the column...
                            if (!this.allBlocks) { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
                                switch (this.getBlockIdAt(this.bx + x, y, this.bz + z)) {
                                case 1:
                                case 2:
                                case 3:
                                case 12:
                                case 13:
                                    // case 14: //commented out the ores, since voxelbox uses these for structural materials.
                                    // case 15:
                                    // case 16:
                                case 24:// These cases filter out any manufactured or refined blocks, any trees and leas, etc. that you don't want to mess with.
                                case 48:
                                case 82:
                                case 49:
                                case 78:
                                    for (int d = 0; (d < this.depth); d++) {
                                        if (this.clampY(this.bx + x, y + d, this.bz + z).getTypeId() != 0) {
                                            this.current.perform(this.clampY(this.bx + x, y + d, this.bz + z)); // fills down as many layers as you specify in
                                                                                                                // parameters
                                            memory[x + bsize][z + bsize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                        }
                                    }
                                    break;

                                default:
                                    break;
                                }
                            } else {
                                for (int d = 0; (d < this.depth); d++) {
                                    if (this.clampY(this.bx + x, y + d, this.bz + z).getTypeId() != 0) {
                                        this.current.perform(this.clampY(this.bx + x, y + d, this.bz + z)); // fills down as many layers as you specify in
                                                                                                            // parameters
                                        memory[x + bsize][z + bsize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
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

    public final void Underlaytwo(final vData v) {
        final int bsize = v.brushSize;

        final int[][] memory = new int[bsize * 2 + 1][bsize * 2 + 1];
        final double bpow = Math.pow(bsize + 0.5, 2);
        for (int z = bsize; z >= -bsize; z--) {
            for (int x = bsize; x >= -bsize; x--) {
                for (int y = this.by; y < this.by + this.depth; y++) { // start scanning from the height you clicked at
                    if (memory[x + bsize][z + bsize] != 1) { // if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= bpow) { // if inside of the column...

                            if (!this.allBlocks) { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.

                                switch (this.getBlockIdAt(this.bx + x, y, this.bz + z)) {
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
                                    for (int d = -1; (d < this.depth - 1); d++) {
                                        this.current.perform(this.clampY(this.bx + x, y - d, this.bz + z)); // fills down as many layers as you specify in
                                                                                                            // parameters
                                        memory[x + bsize][z + bsize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                    }
                                    break;

                                default:
                                    break;
                                }
                            } else {
                                for (int d = -1; (d < this.depth - 1); d++) {
                                    this.current.perform(this.clampY(this.bx + x, y - d, this.bz + z)); // fills down as many layers as you specify in
                                                                                                        // parameters
                                    memory[x + bsize][z + bsize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                }
                            }

                        }
                    }
                }

            }
        }

        v.storeUndo(this.current.getUndo());
    }
}
