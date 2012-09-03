package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Voxel
 */
public class SetRedstoneRotate extends Brush { // Is this used anymore? -psa No worldEdit rotates properly, although it still doesn't flip -Deamon

    protected Block b = null;
    protected Undo h;

    private static int timesUsed = 0;

    public SetRedstoneRotate() {
        this.setName("Set Redstone Rotate");
    }

    @Override
    public final int getTimesUsed() {
        return SetRedstoneRotate.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        this.b = null;
        vm.brushName(this.getName());
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
        super.parameters(par, v);
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        SetRedstoneRotate.timesUsed = tUsed;
    }

    private boolean set(final Block bl) {
        if (this.b == null) {
            this.b = bl;
            return true;
        } else {
            this.h = new Undo(this.b.getWorld().getName());
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
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) { // Derp
        if (this.set(this.getTargetBlock())) {
            v.owner().getPlayer().sendMessage(ChatColor.GRAY + "Point one");
        } else {
            v.storeUndo(this.h);
        }
    }

    protected final void perform(final Block bl) {
        if (bl.getType() == Material.DIODE_BLOCK_ON || bl.getType() == Material.DIODE_BLOCK_OFF) {
            this.h.put(bl);
            // System.out.println(bl.getData());
            bl.setData((((bl.getData() % 4) + 1 < 5) ? (byte) (bl.getData() + 1) : (byte) (bl.getData() - 4)));
            // System.out.println(bl.getData());
        }
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        if (this.set(this.getLastBlock())) {
            v.owner().getPlayer().sendMessage(ChatColor.GRAY + "Point one");
        } else {
            v.storeUndo(this.h);
        }
    }
}
