package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * THIS BRUSH SHOULD NOT USE PERFORMERS
 * 
 * @author Voxel
 */
public class ShellVoxel extends Brush {

    private static int timesUsed = 0;

    public ShellVoxel() {
        this.name = "Shell Voxel";
    }

    @Override
    public final int getTimesUsed() {
        return ShellVoxel.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        vm.size();
        vm.voxel();
        vm.replace();
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Shell Voxel Parameters:");
        } else {
            v.sendMessage(ChatColor.RED + "Invalid parameter - see the info message for help.");
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        ShellVoxel.timesUsed = tUsed;
    }

    public final void vshell(final vData v) {
        final int bsize = v.brushSize;
        final int bId = v.voxelId;
        final int brId = v.replaceId;
        final int[][][] oldmats = new int[2 * (bsize + 1) + 1][2 * (bsize + 1) + 1][2 * (bsize + 1) + 1]; // Array that holds the original materials plus a
                                                                                                          // buffer
        final int[][][] newmats = new int[2 * bsize + 1][2 * bsize + 1][2 * bsize + 1]; // Array that holds the hollowed materials

        // Log current materials into oldmats
        for (int x = 0; x <= 2 * (bsize + 1); x++) {
            for (int y = 0; y <= 2 * (bsize + 1); y++) {
                for (int z = 0; z <= 2 * (bsize + 1); z++) {
                    oldmats[x][y][z] = this.getBlockIdAt(this.bx - bsize - 1 + x, this.by - bsize - 1 + y, this.bz - bsize - 1 + z);
                }
            }
        }

        // Log current materials into newmats
        for (int x = 0; x <= 2 * bsize; x++) {
            for (int y = 0; y <= 2 * bsize; y++) {
                for (int z = 0; z <= 2 * bsize; z++) {
                    newmats[x][y][z] = oldmats[x + 1][y + 1][z + 1];
                }
            }
        }
        int temp;

        // Hollow Brush Area
        for (int x = 0; x <= 2 * bsize; x++) {
            for (int y = 0; y <= 2 * bsize; y++) {
                for (int z = 0; z <= 2 * bsize; z++) {
                    temp = 0;

                    if (oldmats[x + 1 + 1][y + 1][z + 1] == brId) {
                        temp++;
                    }
                    if (oldmats[x + 1 - 1][y + 1][z + 1] == brId) {
                        temp++;
                    }
                    if (oldmats[x + 1][y + 1 + 1][z + 1] == brId) {
                        temp++;
                    }
                    if (oldmats[x + 1][y + 1 - 1][z + 1] == brId) {
                        temp++;
                    }
                    if (oldmats[x + 1][y + 1][z + 1 + 1] == brId) {
                        temp++;
                    }
                    if (oldmats[x + 1][y + 1][z + 1 - 1] == brId) {
                        temp++;
                    }

                    if (temp == 0) {
                        newmats[x][y][z] = bId;
                    }
                }
            }
        }

        // Make the changes
        final vUndo h = new vUndo(this.tb.getWorld().getName());

        for (int x = 2 * bsize; x >= 0; x--) {
            for (int y = 0; y <= 2 * bsize; y++) {
                for (int z = 2 * bsize; z >= 0; z--) {

                    if (this.getBlockIdAt(this.bx - bsize + x, this.by - bsize + y, this.bz - bsize + z) != newmats[x][y][z]) {
                        h.put(this.clampY(this.bx - bsize + x, this.by - bsize + y, this.bz - bsize + z));
                    }
                    this.setBlockIdAt(newmats[x][y][z], this.bx - bsize + x, this.by - bsize + y, this.bz - bsize + z);
                }
            }
        }
        v.storeUndo(h);

        v.owner().getPlayer().sendMessage(ChatColor.AQUA + "Shell complete.");
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.vshell(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.lb.getX();
        this.by = this.lb.getY();
        this.bz = this.lb.getZ();
        this.vshell(v);
    }
}
