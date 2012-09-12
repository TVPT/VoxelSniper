package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;

/**
 * The CloneStamp class is used to create a collection of blocks in a cylinder shape according to the selection the player has set
 * 
 * @author Voxel
 */
public class CloneStamp extends Stamp {
    private static int timesUsed = 0;

    public CloneStamp() {
        this.setName("Clone");
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
    private final void clone(final SnipeData v) {
        this.clone.clear();
        this.fall.clear();
        this.drop.clear();
        this.solid.clear();
        final int _brushSize = v.getBrushSize();
        this.sorted = false;

        int _starringPoint = this.getBlockPositionY() + v.getcCen();
        int _yTopEnd = this.getBlockPositionY() + v.getVoxelHeight() + v.getcCen();
        if (_starringPoint < 0) {
            _starringPoint = 0;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
        } else if (_starringPoint > this.getWorld().getMaxHeight() - 1) {
            _starringPoint = this.getWorld().getMaxHeight() - 1;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
        }
        if (_yTopEnd < 0) {
            _yTopEnd = 0;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
        } else if (_yTopEnd > this.getWorld().getMaxHeight() - 1) {
            _yTopEnd = this.getWorld().getMaxHeight() - 1;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
        }
        final double _bpow = Math.pow(_brushSize, 2);
        for (int _z = _starringPoint; _z < _yTopEnd; _z++) {
            this.clone.add(new cBlock(this.clampY(this.getBlockPositionX(), _z, this.getBlockPositionZ()), 0, _z - _starringPoint, 0));
            for (int _y2 = 1; _y2 <= _brushSize; _y2++) {
                this.clone.add(new cBlock(this.clampY(this.getBlockPositionX(), _z, this.getBlockPositionZ() + _y2), 0, _z - _starringPoint, _y2));
                this.clone.add(new cBlock(this.clampY(this.getBlockPositionX(), _z, this.getBlockPositionZ() - _y2), 0, _z - _starringPoint, -_y2));
                this.clone.add(new cBlock(this.clampY(this.getBlockPositionX() + _y2, _z, this.getBlockPositionZ()), _y2, _z - _starringPoint, 0));
                this.clone.add(new cBlock(this.clampY(this.getBlockPositionX() - _y2, _z, this.getBlockPositionZ()), -_y2, _z - _starringPoint, 0));
            }
            for (int _x = 1; _x <= _brushSize; _x++) {
                final double _xpow = Math.pow(_x, 2);
                for (int _y = 1; _y <= _brushSize; _y++) {
                    if ((_xpow + Math.pow(_y, 2)) <= _bpow) {
                        this.clone.add(new cBlock(this.clampY(this.getBlockPositionX() + _x, _z, this.getBlockPositionZ() + _y), _x, _z - _starringPoint, _y));
                        this.clone.add(new cBlock(this.clampY(this.getBlockPositionX() + _x, _z, this.getBlockPositionZ() - _y), _x, _z - _starringPoint, -_y));
                        this.clone.add(new cBlock(this.clampY(this.getBlockPositionX() - _x, _z, this.getBlockPositionZ() + _y), -_x, _z - _starringPoint, _y));
                        this.clone.add(new cBlock(this.clampY(this.getBlockPositionX() - _x, _z, this.getBlockPositionZ() - _y), -_x, _z - _starringPoint, -_y));
                    }
                }
            }
        }
        v.sendMessage("" + ChatColor.GREEN + this.clone.size() + ChatColor.AQUA + " blocks copied sucessfully.");
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.clone(v);
    }
    
    @Override
    protected final void arrow(final SnipeData v) {
        this.clone(v);
    }
    
    @Override
    public final void info(final Message vm) {
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
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
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
    		v.setcCen(Integer.parseInt(par[1].replace("c", "")));
    		v.sendMessage(ChatColor.BLUE + "Center set to " + v.getcCen());
    	}
    }
    
    @Override
    public final int getTimesUsed() {
    	return CloneStamp.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	CloneStamp.timesUsed = tUsed;
    }
}
