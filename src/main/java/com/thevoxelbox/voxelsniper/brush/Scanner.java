package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;

/**
 * 
 * @author DivineRage
 */
public class Scanner extends Brush {

    private int depth = 24;
    private Material checkFor = Material.AIR;

    private static int timesUsed = 0;

    public Scanner() {
        this.setName("Scanner");
    }

    @Override
    public final int getTimesUsed() {
        return Scanner.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.getName());
        vm.custom(ChatColor.GREEN + "Scanner depth set to " + this.depth);
        vm.custom(ChatColor.GREEN + "Scanner scans for " + this.checkFor + " (change with /v #)");
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Scanner brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b sc d# -- will set the search depth to #. Clamps to 1 - 64.");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("d")) {
                this.depth = this.clamp(Integer.parseInt(par[x].substring(1)), 1, 64);
                v.sendMessage(ChatColor.AQUA + "Scanner depth set to " + this.clamp(Integer.parseInt(par[x].substring(1)), 1, 64));
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }

    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Scanner.timesUsed = tUsed;
    }

    private int clamp(final int value, final int min, final int max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        } else {
            return value;
        }
    }

    private void scan(final vData v, final BlockFace bf) {
        if (bf == null) {
            return;
        }
        switch (bf) {
        case NORTH:
            // Scan south
            for (int i = 1; i < this.depth + 1; i++) {
                if (this.clampY(this.getBlockPositionX() + i, this.getBlockPositionY(), this.getBlockPositionZ()).getType() == this.checkFor) {
                    v.sendMessage(ChatColor.GREEN + "" + this.checkFor + " found after " + i + " blocks.");
                    return;
                }
            }
            v.sendMessage(ChatColor.GRAY + "Nope.");
            break;

        case SOUTH:
            // Scan north
            for (int i = 1; i < this.depth + 1; i++) {
                if (this.clampY(this.getBlockPositionX() - i, this.getBlockPositionY(), this.getBlockPositionZ()).getType() == this.checkFor) {
                    v.sendMessage(ChatColor.GREEN + "" + this.checkFor + " found after " + i + " blocks.");
                    return;
                }
            }
            v.sendMessage(ChatColor.GRAY + "Nope.");
            break;

        case EAST:
            // Scan west
            for (int i = 1; i < this.depth + 1; i++) {
                if (this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ() + i).getType() == this.checkFor) {
                    v.sendMessage(ChatColor.GREEN + "" + this.checkFor + " found after " + i + " blocks.");
                    return;
                }
            }
            v.sendMessage(ChatColor.GRAY + "Nope.");
            break;

        case WEST:
            // Scan east
            for (int i = 1; i < this.depth + 1; i++) {
                if (this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ() - i).getType() == this.checkFor) {
                    v.sendMessage(ChatColor.GREEN + "" + this.checkFor + " found after " + i + " blocks.");
                    return;
                }
            }
            v.sendMessage(ChatColor.GRAY + "Nope.");
            break;

        case UP:
            // Scan down
            for (int i = 1; i < this.depth + 1; i++) {
                if ((this.getBlockPositionY() - i) <= 0) {
                    break;
                }
                if (this.clampY(this.getBlockPositionX(), this.getBlockPositionY() - i, this.getBlockPositionZ()).getType() == this.checkFor) {
                    v.sendMessage(ChatColor.GREEN + "" + this.checkFor + " found after " + i + " blocks.");
                    return;
                }
            }
            v.sendMessage(ChatColor.GRAY + "Nope.");
            break;

        case DOWN:
            // Scan up
            for (int i = 1; i < this.depth + 1; i++) {
                if ((this.getBlockPositionY() + i) >= 127) {
                    break;
                }
                if (this.clampY(this.getBlockPositionX(), this.getBlockPositionY() + i, this.getBlockPositionZ()).getType() == this.checkFor) {
                    v.sendMessage(ChatColor.GREEN + "" + this.checkFor + " found after " + i + " blocks.");
                    return;
                }
            }
            v.sendMessage(ChatColor.GRAY + "Nope.");
            break;

        default:
            break;
        }
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.checkFor = Material.getMaterial(v.voxelId);

        this.scan(v, this.getTargetBlock().getFace(this.getLastBlock()));
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.arrow(v);
    }
}
