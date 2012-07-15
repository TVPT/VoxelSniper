/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

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

    public Xerox() {
        name = "Xerox";
    }

    @Override
    public void info(vMessage vm) {
        block = null;
        vm.brushName(name);
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.LIGHT_PURPLE + "Xerox Brush Instructions: ");
            v.sendMessage(ChatColor.LIGHT_PURPLE + "   For Copy/Paste: " + ChatColor.BLUE + "Left Click with arrow for fist position(Note: The paste is relevant to the location of the first position NOT where the user is standing.) and right click with the arrow to select second point. Right click with gunpowder to paste.");
            v.sendMessage(ChatColor.BLUE + "      For more detailed information on Copy/Paste please type '/xr cp info' ");
            v.sendMessage(ChatColor.LIGHT_PURPLE + "   For Stack: " + ChatColor.BLUE + "Left click with the gunpowder to stack in given ammount and given direction.");
            v.sendMessage(ChatColor.BLUE + "      For more detailed information on Stack please type '/xr stack info' ");
            return;
        }
        if (par[1].equalsIgnoreCase("stack")) {
            stack = true;
            if (par[2].equalsIgnoreCase("info")) {
                v.sendMessage(ChatColor.BLUE + "Detailed Information on Stack:");
                v.sendMessage(ChatColor.LIGHT_PURPLE + "  To use Stack type /b xr stack [direction] [ammount]");
                v.sendMessage(ChatColor.LIGHT_PURPLE + "      [direction]: 'U' (Up), 'D' (Down), 'N' or 'F' (North/Forward), 'S' or 'B'(South/Backward), 'E' or 'R' (East/Right), 'W' or 'L' (West/Left)");
                v.sendMessage(ChatColor.LIGHT_PURPLE + "      [ammount]: Number value for how many blocks you want to stack in the given direction");
                v.sendMessage(ChatColor.LIGHT_PURPLE + " Example Usage: '/b xr stack N 4' would stack the block right clicked on with gunpowder 4 blocks to the North");
                //completely unecessary parsing. If only Java 6 allowed switch statements on Strings -.- OR Minecraft/bukkit had been written in Java 7       -Panda
            } else if (par[2].equalsIgnoreCase("u")) {
            } else if (par[2].equalsIgnoreCase("d")) {
            } else if (par[2].equalsIgnoreCase("n") || par[2].equalsIgnoreCase("f")) {
            } else if (par[2].equalsIgnoreCase("s") || par[2].equalsIgnoreCase("b")) {
            } else if (par[2].equalsIgnoreCase("e") || par[2].equalsIgnoreCase("r")) {
            } else if (par[2].equalsIgnoreCase("w") || par[2].equalsIgnoreCase("l")) {
            }
        }
        if (par[1].equalsIgnoreCase("cp")) {
            cp = true;
            if (par[2].equalsIgnoreCase("info")) {
                //TODO: Write detailed information on Coopy/Paste
            }
            //TODO: Write the copy/paste code >.<
        }
    }

    @Override
    public boolean perform(Action action, com.thevoxelbox.voxelsniper.vData v, Material heldItem, Block clickedBlock, BlockFace clickedFace) {
        switch (action) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                switch (heldItem) {
                    case ARROW:
                        if (stack) {
                            bx = tb.getX();
                            by = tb.getY();
                            bz = tb.getZ();

                        } else if (cp) {
                        } else {
                            return false;
                        }
                    case SULPHUR:
                }
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
            case PHYSICAL:
                return false;
            default:
                v.owner().getPlayer().sendMessage(ChatColor.RED + "Something went wrong! Please report this to PandaNati0n or any other sniper dev! (Action Error)");

        }
        return false;
    }

    private boolean XeroxA(Block block1) {
        if (block == null) {
            block = block1;
            return true;
        } else {
            int lowx = (block.getX() <= block1.getX()) ? block.getX() : block1.getX();

            block = null;
            return false;
        }
    }
}