package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.entity.Fireball;
import org.bukkit.util.Vector;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

/**
 * 
 * @author Gavjenks
 * @author giltwist
 */
public class MeteorBrush extends Brush {
	private static int timesUsed = 0;
	
	/**
	 * 
	 */
    public MeteorBrush() {
        this.setName("Meteor");
    }

    private final void doFireball(final SnipeData v) {       
    	final Vector _target = new Vector(
    			this.getTargetBlock().getX() + .5 * this.getTargetBlock().getX() / Math.abs(this.getTargetBlock().getX()), 
    			this.getTargetBlock().getY() + .5, 
    			this.getTargetBlock().getZ() + .5 * this.getTargetBlock().getZ() / Math.abs(this.getTargetBlock().getZ()));
    	final Vector _slope = _target.subtract(v.owner().getPlayer().getLocation().toVector()).normalize();        
        this.getWorld().spawn(v.owner().getPlayer().getLocation(), Fireball.class).setVelocity(_slope);
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.doFireball(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
    	this.doFireball(v);
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.voxel();
    }
    
    @Override
    public final int getTimesUsed() {
    	return MeteorBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	MeteorBrush.timesUsed = tUsed;
    }
}
