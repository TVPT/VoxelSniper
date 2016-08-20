package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

import com.flowpowered.math.GenericMath;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Same as overlay but for the bottom of blocks.
 */

public class UnderlayBrush extends PerformBrush {

    private static final int DEFAULT_DEPTH = 3;
    private int depth = DEFAULT_DEPTH;
    private boolean allBlocks = false;

    /**
     *
     */
    public UnderlayBrush() {
        this.setName("Underlay (Reverse Overlay)");
    }
    
    private void underlay(SnipeData v, Location<World> targetBlock) {
        double brushSize = v.getBrushSize();
        double brushSizeSquared = brushSize * brushSize;

        int minx = GenericMath.floor(targetBlock.getBlockX() - brushSize);
        int maxx = GenericMath.floor(targetBlock.getBlockX() + brushSize) + 1;
        int minz = GenericMath.floor(targetBlock.getBlockZ() - brushSize);
        int maxz = GenericMath.floor(targetBlock.getBlockZ() + brushSize) + 1;

        this.undo = new Undo(GenericMath.floor(Math.PI * (brushSize + 1) * (brushSize + 1)));

        // @Cleanup Should wrap this within a block worker so that it works
        // better with the cause tracker
        for (int x = minx; x <= maxx; x++) {
            double xs = (minx - x) * (minx - x);
            for (int z = minz; z <= maxz; z++) {
                double zs = (minz - z) * (minz - z);
                if (xs + zs < brushSizeSquared) {
                    
                    for(int y = targetBlock.getBlockY(); y < targetBlock.getBlockY() + this.depth; y++) {
                        if(this.world.getBlockType(x, y, z) != BlockTypes.AIR) {
                            perform(v, x, y, z);
                        }
                    }
                }
            }
        }

        v.owner().storeUndo(this.undo);
        this.undo = null;
        
    }

    @SuppressWarnings("deprecation")
    private void underlay(final SnipeData v) {
        final int[][] memory = new int[v.getBrushSize() * 2 + 1][v.getBrushSize() * 2 + 1];
        final double brushSizeSquared = Math.pow(v.getBrushSize() + 0.5, 2);

        for (int z = v.getBrushSize(); z >= -v.getBrushSize(); z--) {
            for (int x = v.getBrushSize(); x >= -v.getBrushSize(); x--) {
                for (int y = this.getTargetBlock().getY(); y < this.getTargetBlock().getY() + this.depth; y++) {
                    // start scanning from the height you clicked at
                    if (memory[x + v.getBrushSize()][z + v.getBrushSize()] != 1) {
                        // if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared) {
                            // if inside of the column...
                            if (!this.allBlocks) {
                                // if the override parameter has not been
                                // activated, go to the switch that filters out
                                // manmade stuff.
                                switch (this.getBlockIdAt(this.getTargetBlock().getX() + x, y, this.getTargetBlock().getZ() + z)) {
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 12:
                                    case 13:
                                    case 24:
                                        // These cases filter out any
                                        // manufactured or refined blocks, any
                                        // trees and leas, etc. that you don't
                                        // want to mess with.
                                    case 48:
                                    case 82:
                                    case 49:
                                    case 78:
                                        for (int d = 0; (d < this.depth); d++) {
                                            if (this.clampY(this.getTargetBlock().getX() + x, y + d, this.getTargetBlock().getZ() + z)
                                                    .getTypeId() != 0) {
                                                this.current.perform(
                                                        this.clampY(this.getTargetBlock().getX() + x, y + d, this.getTargetBlock().getZ() + z));
                                                // fills down as many layers as
                                                // you specify in
                                                // parameters
                                                memory[x + v.getBrushSize()][z + v.getBrushSize()] = 1;
                                                // stop it from checking any
                                                // other blocks in this vertical
                                                // 1x1 column.
                                            }
                                        }
                                        break;

                                    default:
                                        break;
                                }
                            } else {
                                for (int d = 0; (d < this.depth); d++) {
                                    if (this.clampY(this.getTargetBlock().getX() + x, y + d, this.getTargetBlock().getZ() + z).getTypeId() != 0) {
                                        this.current.perform(this.clampY(this.getTargetBlock().getX() + x, y + d, this.getTargetBlock().getZ() + z));
                                        // fills down as many layers as you
                                        // specify in
                                        // parameters
                                        memory[x + v.getBrushSize()][z + v.getBrushSize()] = 1;
                                        // stop it from checking any other
                                        // blocks in this vertical 1x1 column.
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        v.owner().storeUndo(this.current.getUndo());
    }

    private void underlay2(final SnipeData v) {
        final int[][] memory = new int[v.getBrushSize() * 2 + 1][v.getBrushSize() * 2 + 1];
        final double brushSizeSquared = Math.pow(v.getBrushSize() + 0.5, 2);

        for (int z = v.getBrushSize(); z >= -v.getBrushSize(); z--) {
            for (int x = v.getBrushSize(); x >= -v.getBrushSize(); x--) {
                for (int y = this.getTargetBlock().getY(); y < this.getTargetBlock().getY() + this.depth; y++) {
                    // start scanning from the height you clicked at
                    if (memory[x + v.getBrushSize()][z + v.getBrushSize()] != 1) {
                        // if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared) {
                            // if inside of the column...

                            if (!this.allBlocks) {
                                // if the override parameter has not been
                                // activated, go to the switch that filters out
                                // manmade stuff.

                                switch (this.getBlockIdAt(this.getTargetBlock().getX() + x, y, this.getTargetBlock().getZ() + z)) {
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 12:
                                    case 13:
                                    case 14:
                                        // These cases filter out any
                                        // manufactured or refined blocks, any
                                        // trees and leas, etc. that you don't
                                        // want to mess
                                        // with.
                                    case 15:
                                    case 16:
                                    case 24:
                                    case 48:
                                    case 82:
                                    case 49:
                                    case 78:
                                        for (int d = -1; (d < this.depth - 1); d++) {
                                            this.current
                                                    .perform(this.clampY(this.getTargetBlock().getX() + x, y - d, this.getTargetBlock().getZ() + z));
                                            // fills down as many layers as you
                                            // specify in
                                            // parameters
                                            memory[x + v.getBrushSize()][z + v.getBrushSize()] = 1;
                                            // stop it from checking any other
                                            // blocks in this vertical 1x1
                                            // column.
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                for (int d = -1; (d < this.depth - 1); d++) {
                                    this.current.perform(this.clampY(this.getTargetBlock().getX() + x, y - d, this.getTargetBlock().getZ() + z));
                                    // fills down as many layers as you specify
                                    // in
                                    // parameters
                                    memory[x + v.getBrushSize()][z + v.getBrushSize()] = 1;
                                    // stop it from checking any other blocks in
                                    // this vertical 1x1 column.
                                }
                            }
                        }
                    }
                }
            }
        }

        v.owner().storeUndo(this.current.getUndo());
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
        if (par.length < 0) {
            v.sendMessage(TextColors.AQUA, "Usage: /b reover d[#]");
            return;
        }
        if (par[0].equalsIgnoreCase("info")) {
            v.sendMessage(TextColors.GOLD, "Reverse Overlay brush parameters:");
            v.sendMessage(TextColors.AQUA, "d[number] (ex: d3) The number of blocks thick to change.");
            return;
        }
        if (par[0].startsWith("d")) {
            this.depth = Integer.parseInt(par[0].replace("d", ""));
            if (this.depth < 1) {
                this.depth = 1;
            }
            v.sendMessage(TextColors.AQUA, "Depth set to " + this.depth);
        } else {
            v.sendMessage(TextColors.RED, "Invalid parameter '" + par[0] + "'");
            v.sendMessage(TextColors.AQUA, "Usage: /b reover d[#]");
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.underlay";
    }
}
