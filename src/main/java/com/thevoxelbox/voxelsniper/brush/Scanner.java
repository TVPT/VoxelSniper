/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

/**
 *
 * @author DivineRage
 */
public class Scanner extends Brush {
    private int depth = 24;
    private Material checkFor = Material.AIR;
    
    public Scanner() {
        name = "Scanner";
    }

    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        checkFor = Material.getMaterial(v.voxelId);

        scan(v, tb.getFace(lb));
    }

    @Override
    public void powder(vSniper v) {
        arrow(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.custom(ChatColor.GREEN + "Scanner depth set to " + depth);
        vm.custom(ChatColor.GREEN + "Scanner scans for " + checkFor + " (change with /v #)");
    }

    @Override
    public void parameters(String[] par, vSniper v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.p.sendMessage(ChatColor.GOLD + "Scanner brush Parameters:");
            v.p.sendMessage(ChatColor.AQUA + "/b sc d# -- will set the search depth to #. Clamps to 1 - 64.");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("d")) {
                depth = clamp(Integer.parseInt(par[x].substring(1)), 1, 64);
                v.p.sendMessage(ChatColor.AQUA + "Scanner depth set to " + clamp(Integer.parseInt(par[x].substring(1)), 1, 64));
                continue;
            } else {
                v.p.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }

    }

    private void scan(vSniper v, BlockFace bf) {
        if(bf == null) {
            return;
        }
        switch (bf) {
            case NORTH:
                // Scan south
                for (int i = 1; i < depth+1; i++) {
                    if (clampY(bx+i, by, bz).getType() == checkFor) {
                        v.p.sendMessage(ChatColor.GREEN + "" + checkFor + " found after " + i + " blocks.");
                        return;
                    }
                }
                v.p.sendMessage(ChatColor.GRAY + "Nope.");
                break;

            case SOUTH:
                // Scan north
                for (int i = 1; i < depth+1; i++) {
                    if (clampY(bx-i, by, bz).getType() == checkFor) {
                        v.p.sendMessage(ChatColor.GREEN + "" + checkFor + " found after " + i + " blocks.");
                        return;
                    }
                }
                v.p.sendMessage(ChatColor.GRAY + "Nope.");
                break;

            case EAST:
                // Scan west
                for (int i = 1; i < depth+1; i++) {
                    if (clampY(bx, by, bz+i).getType() == checkFor) {
                        v.p.sendMessage(ChatColor.GREEN + "" + checkFor + " found after " + i + " blocks.");
                        return;
                    }
                }
                v.p.sendMessage(ChatColor.GRAY + "Nope.");
                break;

            case WEST:
                // Scan east
                for (int i = 1; i < depth+1; i++) {
                    if (clampY(bx, by, bz-i).getType() == checkFor) {
                        v.p.sendMessage(ChatColor.GREEN + "" + checkFor + " found after " + i + " blocks.");
                        return;
                    }
                }
                v.p.sendMessage(ChatColor.GRAY + "Nope.");
                break;

            case UP:
                // Scan down
                for (int i = 1; i < depth+1; i++) {
                    if ((by-i) <= 0) {
                        break;
                    }
                    if (clampY(bx, by-i, bz).getType() == checkFor) {
                        v.p.sendMessage(ChatColor.GREEN + "" + checkFor + " found after " + i + " blocks.");
                        return;
                    }
                }
                v.p.sendMessage(ChatColor.GRAY + "Nope.");
                break;

            case DOWN:
                // Scan up
                for (int i = 1; i < depth+1; i++) {
                    if ((by+i) >= 127) {
                        break;
                    }
                    if (clampY(bx, by+i, bz).getType() == checkFor) {
                        v.p.sendMessage(ChatColor.GREEN + "" + checkFor + " found after " + i + " blocks.");
                        return;
                    }
                }
                v.p.sendMessage(ChatColor.GRAY + "Nope.");
                break;

            default:
                break;
        }
    }

    private int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        } else {
            return value;
        }
    }
}
