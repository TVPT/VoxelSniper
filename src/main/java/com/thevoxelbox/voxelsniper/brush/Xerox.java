package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * Xerox is a Copy/Paste and Stack brush
 * 
 * @author Panda
 */
public class Xerox extends PerformBrush {

    protected boolean first = true, cp = false, stack = false;
    protected Block block = null;
    protected int id;
    protected double[] firstCoords = new double[3], secondCoords = new double[3];
    protected vUndo h;

    private static int timesUsed = 0;

    public Xerox() {
        this.name = "Xerox";
    }

    @Override
    public final int getTimesUsed() {
        return Xerox.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        this.block = null;
        vm.brushName(this.name);
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.LIGHT_PURPLE + "Xerox Brush Instructions: ");
            v.sendMessage(ChatColor.LIGHT_PURPLE
                    + "   For Copy/Paste: "
                    + ChatColor.BLUE
                    + "Left Click with arrow for fist position(Note: The paste is relevant to the location of the first position NOT where the user is standing.) and right click with the arrow to select second point. Right click with gunpowder to paste.");
            v.sendMessage(ChatColor.BLUE + "      For more detailed information on Copy/Paste please type '/xr cp info' ");
            v.sendMessage(ChatColor.LIGHT_PURPLE + "   For Stack: " + ChatColor.BLUE
                    + "Left click with the gunpowder to stack in given ammount and given direction.");
            v.sendMessage(ChatColor.BLUE + "      For more detailed information on Stack please type '/xr stack info' ");
            return;
        }
        if (par[1].equalsIgnoreCase("stack")) {
            this.stack = true;
            if (par[2].equalsIgnoreCase("info")) {
                v.sendMessage(ChatColor.BLUE + "Detailed Information on Stack:");
                v.sendMessage(ChatColor.LIGHT_PURPLE + "  To use Stack type /b xr stack [direction] [ammount]");
                v.sendMessage(ChatColor.LIGHT_PURPLE
                        + "      [direction]: 'U' (Up), 'D' (Down), 'N' or 'F' (North/Forward), 'S' or 'B'(South/Backward), 'E' or 'R' (East/Right), 'W' or 'L' (West/Left)");
                v.sendMessage(ChatColor.LIGHT_PURPLE + "      [ammount]: Number value for how many blocks you want to stack in the given direction");
                v.sendMessage(ChatColor.LIGHT_PURPLE
                        + " Example Usage: '/b xr stack N 4' would stack the block right clicked on with gunpowder 4 blocks to the North");
            }
        }
        if (par[1].equalsIgnoreCase("cp")) {
            this.cp = true;
            // TODO: Write detailed information on Coopy/Paste
            // TODO: Write the copy/paste code >.<
        }
    }

    @Override
    public final boolean perform(final Action action, final com.thevoxelbox.voxelsniper.vData v, final Material heldItem, final Block clickedBlock,
            final BlockFace clickedFace) {
        switch (action) {
        case RIGHT_CLICK_AIR:
        case RIGHT_CLICK_BLOCK:
            switch (heldItem) {
            case ARROW:
                if (this.stack) {
                    this.bx = this.tb.getX();
                    this.by = this.tb.getY();
                    this.bz = this.tb.getZ();

                } else if (this.cp) {
                } else {
                    return false;
                }
            case SULPHUR:
            default:
                break;
            }
        case LEFT_CLICK_AIR:
        case LEFT_CLICK_BLOCK:
        case PHYSICAL:
            return false;
        default:
            v.owner().getPlayer()
                    .sendMessage(ChatColor.RED + "Something went wrong! Please report this to PandaNati0n or any other sniper dev! (Action Error)");

        }
        return false;
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Xerox.timesUsed = tUsed;
    }

    private boolean XeroxA(final Block block1) {
        if (this.block == null) {
            this.block = block1;
            return true;
        } else {
            this.block = null;
            return false;
        }
    }
}
