package com.thevoxelbox.voxelsniper.brush;

import net.minecraft.server.EntitySmallFireball;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.entity.CraftSmallFireball;
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
		double _lineLength = 0;

		Location _playerLoc = v.owner().getPlayer().getLocation();
		Vector _originCoords = _playerLoc.toVector();

		Vector _targetCoords = new Vector(this.getTargetBlock().getX() + .5 * this.getTargetBlock().getX() / Math.abs(this.getTargetBlock().getX()), this.getTargetBlock().getY() + .5, this.getTargetBlock().getZ() + .5 * this.getTargetBlock().getZ() / Math.abs(this.getTargetBlock().getZ()));
		
		Vector _slopeVector = _targetCoords.subtract(_originCoords);
		
		// Calculate line length
		_lineLength = Math.pow(_slopeVector.length(), .5);

		_slopeVector.setX(_slopeVector.getX() / _lineLength);
		_slopeVector.setY(_slopeVector.getY() / _lineLength);
		_slopeVector.setZ(_slopeVector.getZ() / _lineLength);
		
		// Hadoken!
		final EntitySmallFireball _entityFireball = new EntitySmallFireball(((CraftWorld) v.owner().getPlayer().getWorld()).getHandle(), ((CraftPlayer) v
				.owner().getPlayer()).getHandle(),_slopeVector.getX() * _lineLength, _slopeVector.getY() * _lineLength, _slopeVector.getZ() * _lineLength);
		final CraftSmallFireball _craftFireball = new CraftSmallFireball((CraftServer) v.owner().getPlayer().getServer(), _entityFireball);
		final Vector _velocity = _slopeVector;
		_craftFireball.setVelocity(_velocity);
		((CraftWorld) v.owner().getPlayer().getWorld()).getHandle().addEntity(_entityFireball);
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
