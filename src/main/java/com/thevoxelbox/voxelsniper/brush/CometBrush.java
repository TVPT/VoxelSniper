package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.Location;
import org.bukkit.entity.SmallFireball;
import org.bukkit.util.Vector;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

/**
 * 
 * @author Gavjenks Heavily revamped from ruler brush blockPositionY Giltwist
 */
public class CometBrush extends Brush {
	private static int timesUsed = 0;

	public CometBrush() {
		this.setName("Comet");
	}

	private final void doFireball(final SnipeData v) {
		final Location _playerLoc = v.owner().getPlayer().getLocation();
		final Vector _targetCoords = new Vector(this.getTargetBlock().getX() + .5 * this.getTargetBlock().getX() / Math.abs(this.getTargetBlock().getX()), this.getTargetBlock().getY() + .5, this.getTargetBlock().getZ() + .5 * this.getTargetBlock().getZ() / Math.abs(this.getTargetBlock().getZ()));
		
		// Hadoken!
		this.getWorld().spawn(_playerLoc, SmallFireball.class).setVelocity( _targetCoords.subtract(_playerLoc.toVector()).normalize());
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
		return CometBrush.timesUsed;
	}

	@Override
	public final void setTimesUsed(final int tUsed) {
		CometBrush.timesUsed = tUsed;
	}
}
