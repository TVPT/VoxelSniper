package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * 
 * @author Gavjenks
 */
public class Overlay extends PerformBrush {

    int depth = 3;

    boolean allBlocks = false;

    private static int timesUsed = 0;

    public Overlay() {
        this.setName("Overlay (Topsoil Filling)");
    }

    @Override
    public final int getTimesUsed() {
        return Overlay.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.getName());
        vm.size();
        // vm.voxel();
    }

    public final void overlay(final vData v) {
        final int bsize = v.brushSize;

        final int[][] memory = new int[bsize * 2 + 1][bsize * 2 + 1];
        final double bpow = Math.pow(bsize + 0.5, 2);
        for (int z = bsize; z >= -bsize; z--) {
            for (int x = bsize; x >= -bsize; x--) {
                for (int y = this.getBlockPositionY(); y > 0; y--) { // start scanning from the height you clicked at
                    if (memory[x + bsize][z + bsize] != 1) { // if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= bpow) { // if inside of the column...
                            final int check = this.getBlockIdAt(this.getBlockPositionX() + x, y + 1, this.getBlockPositionZ() + z);
                            if (check == 0 || check == 8 || check == 9) { // must start at surface... this prevents it filling stuff in if you click in a wall
                                                                          // and it starts out below surface.
                                if (!this.allBlocks) { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
                                    switch (this.getBlockIdAt(this.getBlockPositionX() + x, y, this.getBlockPositionZ() + z)) {
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 12:
                                    case 13:
                                        // case 14: //commented out the ores, since voxelbox uses these for structural materials.
                                        // case 15:
                                        // case 16:
                                    case 24:// These cases filter out any manufactured or refined blocks, any trees and leas, etc. that you don't want to mess
                                            // with.
                                    case 48:
                                    case 82:
                                    case 49:
                                    case 78:
                                        for (int d = 0; (d < this.depth); d++) {
                                            if (this.clampY(this.getBlockPositionX() + x, y - d, this.getBlockPositionZ() + z).getTypeId() != 0) {
                                                this.current.perform(this.clampY(this.getBlockPositionX() + x, y - d, this.getBlockPositionZ() + z)); // fills down as many layers as you specify
                                                                                                                    // in parameters
                                                memory[x + bsize][z + bsize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                            }
                                        }
                                        break;

                                    default:
                                        break;
                                    }
                                } else {
                                    for (int d = 0; (d < this.depth); d++) {
                                        if (this.clampY(this.getBlockPositionX() + x, y - d, this.getBlockPositionZ() + z).getTypeId() != 0) {
                                            this.current.perform(this.clampY(this.getBlockPositionX() + x, y - d, this.getBlockPositionZ() + z)); // fills down as many layers as you specify in
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
        }

        v.storeUndo(this.current.getUndo());
    }

    public final void overlayTwo(final vData v) {
        final int bsize = v.brushSize;

        final int[][] memory = new int[bsize * 2 + 1][bsize * 2 + 1];
        final double bpow = Math.pow(bsize + 0.5, 2);
        for (int z = bsize; z >= -bsize; z--) {
            for (int x = bsize; x >= -bsize; x--) {
                for (int y = this.getBlockPositionY(); y > 0; y--) { // start scanning from the height you clicked at
                    if (memory[x + bsize][z + bsize] != 1) { // if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= bpow) { // if inside of the column...
                            if (this.getBlockIdAt(this.getBlockPositionX() + x, y - 1, this.getBlockPositionZ() + z) != 0) { // if not a floating block (like one of Notch'world pools)
                                if (this.getBlockIdAt(this.getBlockPositionX() + x, y + 1, this.getBlockPositionZ() + z) == 0) { // must start at surface... this prevents it filling stuff in if
                                                                                               // you click in a wall and it starts out below surface.
                                    if (!this.allBlocks) { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.

                                        switch (this.getBlockIdAt(this.getBlockPositionX() + x, y, this.getBlockPositionZ() + z)) {
                                        case 1:
                                        case 2:
                                        case 3:
                                        case 12:
                                        case 13:
                                        case 14: // These cases filter out any manufactured or refined blocks, any trees and leas, etc. that you don't want to
                                                 // mess with.
                                        case 15:
                                        case 16:
                                        case 24:
                                        case 48:
                                        case 82:
                                        case 49:
                                        case 78:
                                            for (int d = 1; (d < this.depth + 1); d++) {
                                                this.current.perform(this.clampY(this.getBlockPositionX() + x, y + d, this.getBlockPositionZ() + z)); // fills down as many layers as you specify
                                                                                                                    // in parameters
                                                memory[x + bsize][z + bsize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                            }
                                            break;

                                        default:
                                            break;
                                        }
                                    } else {
                                        for (int d = 1; (d < this.depth + 1); d++) {
                                            this.current.perform(this.clampY(this.getBlockPositionX() + x, y + d, this.getBlockPositionZ() + z)); // fills down as many layers as you specify in
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
        }

        v.storeUndo(this.current.getUndo());
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Overlay brush parameters:");
            v.sendMessage(ChatColor.AQUA + "d[number] (ex:  d3) How many blocks deep you want to replace from the surface.");
            v.sendMessage(ChatColor.BLUE
                    + "all (ex:  /b over all) Sets the brush to overlay over ALL materials, not just natural surface ones (will no longer ignore trees and buildings).  The parameter /some will set it back to default.");

            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("d")) {
                this.depth = Integer.parseInt(par[x].replace("d", ""));
                v.sendMessage(ChatColor.AQUA + "Depth set to " + this.depth);
                if (this.depth < 1) {
                    this.depth = 1;
                }
                continue;
            } else if (par[x].startsWith("all")) {
                this.allBlocks = true;
                v.sendMessage(ChatColor.BLUE + "Will overlay over any block." + this.depth);
                continue;
            } else if (par[x].startsWith("some")) {
                this.allBlocks = false;
                v.sendMessage(ChatColor.BLUE + "Will overlay only natural block types." + this.depth);
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Overlay.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.overlay(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.overlayTwo(v);
    }
}
