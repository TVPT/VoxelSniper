package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Voxel
 */
public class SetRedstoneRotateBrush extends Brush { // Is this used anymore? -psa No worldEdit rotates properly, although it still doesn't flip -Deamon
	private static int timesUsed = 0;
    private Block block = null;
    private Undo undo;

    /**
     * 
     */
    public SetRedstoneRotateBrush() {
        this.setName("Set Redstone Rotate");
    }

    private boolean set(final Block bl) {
        if (this.block == null) {
            this.block = bl;
            return true;
        } else {
            this.undo = new Undo(this.block.getWorld().getName());            
            final int _lowx = (this.block.getX() <= bl.getX()) ? this.block.getX() : bl.getX();
            final int _lowy = (this.block.getY() <= bl.getY()) ? this.block.getY() : bl.getY();
            final int _lowz = (this.block.getZ() <= bl.getZ()) ? this.block.getZ() : bl.getZ();
            final int _highx = (this.block.getX() >= bl.getX()) ? this.block.getX() : bl.getX();
            final int _highy = (this.block.getY() >= bl.getY()) ? this.block.getY() : bl.getY();
            final int _highz = (this.block.getZ() >= bl.getZ()) ? this.block.getZ() : bl.getZ();
            
            for (int _y = _lowy; _y <= _highy; _y++) {
                for (int _x = _lowx; _x <= _highx; _x++) {
                    for (int _z = _lowz; _z <= _highz; _z++) {
                        this.perform(this.clampY(_x, _y, _z));
                    }
                }
            }
            this.block = null;
            return false;
        }
    }
    
    private final void perform(final Block bl) {
    	if (bl.getType() == Material.DIODE_BLOCK_ON || bl.getType() == Material.DIODE_BLOCK_OFF) {
    		this.undo.put(bl);
    		bl.setData((((bl.getData() % 4) + 1 < 5) ? (byte) (bl.getData() + 1) : (byte) (bl.getData() - 4)));
    	}
    }

    @Override
    protected final void arrow(final SnipeData v) {
        if (this.set(this.getTargetBlock())) {
            v.owner().getPlayer().sendMessage(ChatColor.GRAY + "Point one");
        } else {
            v.storeUndo(this.undo);
        }
    }

    @Override
    protected final void powder(final SnipeData v) {
        if (this.set(this.getLastBlock())) {
            v.owner().getPlayer().sendMessage(ChatColor.GRAY + "Point one");
        } else {
            v.storeUndo(this.undo);
        }
    }
    
    @Override
    public final void info(final Message vm) {
    	this.block = null;
    	vm.brushName(this.getName());
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	super.parameters(par, v);
    }
    
    @Override
    public final int getTimesUsed() {
    	return SetRedstoneRotateBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	SetRedstoneRotateBrush.timesUsed = tUsed;
    }
}
