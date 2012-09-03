package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * 
 * @author Voxel
 */
public class SetRedstoneFlip extends Brush {

    protected Block b = null;
    protected vUndo h;
    private boolean northSouth = true;

    private static int timesUsed = 0;

    public SetRedstoneFlip() {
        this.setName("Set Redstone Flip");
    }

    @Override
    public final int getTimesUsed() {
        return SetRedstoneFlip.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        this.b = null;
        vm.brushName(this.getName());
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Set Repeater Flip Parameters:");
            v.sendMessage(ChatColor.AQUA
                    + "/b setrf <direction> -- valid direction inputs are(n,s,e,world), Set the direction that you wish to flip your repeaters, defaults to north/south.");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("n") || par[x].startsWith("s") || par[x].startsWith("ns")) {
                this.northSouth = true;
                v.sendMessage(ChatColor.AQUA + "Flip direction set to north/south");
                continue;
            } else if (par[x].startsWith("e") || par[x].startsWith("world") || par[x].startsWith("ew")) {
                this.northSouth = false;
                v.sendMessage(ChatColor.AQUA + "Flip direction set to east/west.");
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        SetRedstoneFlip.timesUsed = tUsed;
    }

    private boolean set(final Block bl) {
        if (this.b == null) {
            this.b = bl;
            return true;
        } else {
            this.h = new vUndo(this.b.getWorld().getName());
            final int lowx = (this.b.getX() <= bl.getX()) ? this.b.getX() : bl.getX();
            final int lowy = (this.b.getY() <= bl.getY()) ? this.b.getY() : bl.getY();
            final int lowz = (this.b.getZ() <= bl.getZ()) ? this.b.getZ() : bl.getZ();
            final int highx = (this.b.getX() >= bl.getX()) ? this.b.getX() : bl.getX();
            final int highy = (this.b.getY() >= bl.getY()) ? this.b.getY() : bl.getY();
            final int highz = (this.b.getZ() >= bl.getZ()) ? this.b.getZ() : bl.getZ();
            for (int y = lowy; y <= highy; y++) {
                for (int x = lowx; x <= highx; x++) {
                    for (int z = lowz; z <= highz; z++) {
                        this.perform(this.clampY(x, y, z));
                    }
                }
            }
            this.b = null;
            return false;
        }
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) { // Derp
        if (this.set(this.getTargetBlock())) {
            v.sendMessage(ChatColor.GRAY + "Point one");
        } else {
            v.storeUndo(this.h);
        }
    }

    protected final void perform(final Block bl) {
        if (bl.getType() == Material.DIODE_BLOCK_ON || bl.getType() == Material.DIODE_BLOCK_OFF) {
            if (this.northSouth) {
                if ((bl.getData() % 4) == 1) {
                    this.h.put(bl);
                    bl.setData((byte) (bl.getData() + 2));
                } else if ((bl.getData() % 4) == 3) {
                    this.h.put(bl);
                    bl.setData((byte) (bl.getData() - 2));
                }
            } else {
                if ((bl.getData() % 4) == 2) {
                    this.h.put(bl);
                    bl.setData((byte) (bl.getData() - 2));
                } else if ((bl.getData() % 4) == 0) {
                    this.h.put(bl);
                    bl.setData((byte) (bl.getData() + 2));
                }
            }
        }
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        if (this.set(this.getLastBlock())) {
            v.sendMessage(ChatColor.GRAY + "Point one");
        } else {
            v.storeUndo(this.h);
        }
    }
}
