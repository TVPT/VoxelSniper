package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;

/**
 * The CloneStamp class is used to create a collection of blocks in a cylinder shape according to the selection the player has set
 * 
 * @author Voxel
 */
public class CloneStamp extends Stamp {

    /**
     * The starring Y position At the bottom of the cylinder
     */
    protected int st;
    /**
     * End Y position At the top of the cylinder
     */
    protected int en;

    private static int timesUsed = 0;

    public CloneStamp() {
        this.setName("Clone");
    }

    @Override
    public final int getTimesUsed() {
        return CloneStamp.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.height();
        vm.center();
        switch (this.stamp) {
        case 0:
            vm.brushMessage("Default Stamp");
            break;

        case 1:
            vm.brushMessage("No-Air Stamp");
            break;

        case 2:
            vm.brushMessage("Fill Stamp");
            break;

        default:
            vm.custom(ChatColor.DARK_RED + "Error while stamping! Report");
            break;
        }
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Clone / Stamp Cylinder brush parameters");
            v.sendMessage(ChatColor.GREEN + "cs f -- Activates Fill mode");
            v.sendMessage(ChatColor.GREEN + "cs a -- Activates No-Air mode");
            v.sendMessage(ChatColor.GREEN + "cs d -- Activates Default mode");
        }
        if (par[1].equalsIgnoreCase("a")) {
            this.setStamp((byte) 1);
            this.reSort();
            v.sendMessage(ChatColor.AQUA + "No-Air stamp brush");
        } else if (par[1].equalsIgnoreCase("f")) {
            this.setStamp((byte) 2);
            this.reSort();
            v.sendMessage(ChatColor.AQUA + "Fill stamp brush");
        } else if (par[1].equalsIgnoreCase("d")) {
            this.setStamp((byte) 0);
            this.reSort();
            v.sendMessage(ChatColor.AQUA + "Default stamp brush");
        } else if (par[1].startsWith("c")) {
            v.cCen = Integer.parseInt(par[1].replace("c", ""));
            v.sendMessage(ChatColor.BLUE + "Center set to " + v.cCen);
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        CloneStamp.timesUsed = tUsed;
    }

    /**
     * The clone method is used to grab a snapshot of the selected area dictated blockPositionY targetBlock.x y z v.brushSize v.voxelHeight and v.cCen
     * 
     * x y z -- initial center of the selection v.brushSize -- the radius of the cylinder v.voxelHeight -- the heigth of the cylinder c.cCen -- the offset on
     * the Y axis of the selection ( bottom of the cylinder ) as blockPositionY: Bottom_Y = targetBlock.y + v.cCen;
     * 
     * @param v
     *            the caller
     */
    protected final void clone(final vData v) {
        this.clone.clear();
        this.fall.clear();
        this.drop.clear();
        this.solid.clear();
        final int bsize = v.brushSize;
        this.sorted = false;

        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        this.st = this.getBlockPositionY() + v.cCen;
        this.en = this.getBlockPositionY() + v.voxelHeight + v.cCen;
        if (this.st < 0) {
            this.st = 0;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
        } else if (this.st > 127) {
            this.st = 127;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
        }
        if (this.en < 0) {
            this.en = 0;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
        } else if (this.en > 127) {
            this.en = 127;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
        }
        final double bpow = Math.pow(bsize, 2);
        for (int z = this.st; z < this.en; z++) {
            this.clone.add(new cBlock(this.clampY(this.getBlockPositionX(), z, this.getBlockPositionZ()), 0, z - this.st, 0));
            for (int y2 = 1; y2 <= bsize; y2++) {
                this.clone.add(new cBlock(this.clampY(this.getBlockPositionX(), z, this.getBlockPositionZ() + y2), 0, z - this.st, y2));
                this.clone.add(new cBlock(this.clampY(this.getBlockPositionX(), z, this.getBlockPositionZ() - y2), 0, z - this.st, -y2));
                this.clone.add(new cBlock(this.clampY(this.getBlockPositionX() + y2, z, this.getBlockPositionZ()), y2, z - this.st, 0));
                this.clone.add(new cBlock(this.clampY(this.getBlockPositionX() - y2, z, this.getBlockPositionZ()), -y2, z - this.st, 0));
            }
            for (int x = 1; x <= bsize; x++) {
                final double xpow = Math.pow(x, 2);
                for (int y = 1; y <= bsize; y++) {
                    if ((xpow + Math.pow(y, 2)) <= bpow) {
                        this.clone.add(new cBlock(this.clampY(this.getBlockPositionX() + x, z, this.getBlockPositionZ() + y), x, z - this.st, y));
                        this.clone.add(new cBlock(this.clampY(this.getBlockPositionX() + x, z, this.getBlockPositionZ() - y), x, z - this.st, -y));
                        this.clone.add(new cBlock(this.clampY(this.getBlockPositionX() - x, z, this.getBlockPositionZ() + y), -x, z - this.st, y));
                        this.clone.add(new cBlock(this.clampY(this.getBlockPositionX() - x, z, this.getBlockPositionZ() - y), -x, z - this.st, -y));
                    }
                }
            }
        }
        v.sendMessage("" + ChatColor.GREEN + this.clone.size() + ChatColor.AQUA + " blocks copied sucessfully.");
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.clone(v);
    }
}
