package com.thevoxelbox.voxelsniper.brush;

import net.minecraft.server.EntityFireball;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftFireball;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.util.Vector;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;

/**
 * 
 * @author Gavjenks
 * @author giltwist
 */
public class Meteor extends Brush {
    private double[] origincoords = new double[3];
    private double[] targetcoords = new double[3];
    private double[] slopevector = new double[3];
    private Location playerLocation;

    private static int timesUsed = 0;

    public Meteor() {
        this.setName("Meteor");
    }

    private final void doFireball(final SnipeData v) {
        double _lineLength = 0;

        // Calculate slope vector
        for (int _i = 0; _i < 3; _i++) {
            this.slopevector[_i] = this.targetcoords[_i] - this.origincoords[_i];
        }
        // Calculate line length
        _lineLength = Math.pow((Math.pow(this.slopevector[0], 2) + Math.pow(this.slopevector[1], 2) + Math.pow(this.slopevector[2], 2)), .5);

        // Unitize slope vector
        for (int _i = 0; _i < 3; _i++) {
            this.slopevector[_i] = this.slopevector[_i] / _lineLength;
        }

        // Hadoken!
        final EntityFireball _entityFireball = new EntityFireball(((CraftWorld) v.owner().getPlayer().getWorld()).getHandle(), ((CraftPlayer) v.owner()
                .getPlayer()).getHandle(), this.slopevector[0] * _lineLength, this.slopevector[1] * _lineLength, this.slopevector[2] * _lineLength);
        final CraftFireball _craftFireball = new CraftFireball((CraftServer) v.owner().getPlayer().getServer(), _entityFireball);
        _craftFireball.setVelocity(new Vector(this.slopevector[0], this.slopevector[1], this.slopevector[2]));
        ((CraftWorld) v.owner().getPlayer().getWorld()).getHandle().addEntity(_entityFireball);
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.playerLocation = v.owner().getPlayer().getLocation();

        this.origincoords[0] = this.playerLocation.getX();
        this.origincoords[1] = this.playerLocation.getY();
        this.origincoords[2] = this.playerLocation.getZ();

        this.targetcoords[0] = this.getTargetBlock().getX() + .5 * this.getTargetBlock().getX() / Math.abs(this.getTargetBlock().getX());
        this.targetcoords[1] = this.getTargetBlock().getY() + .5;
        this.targetcoords[2] = this.getTargetBlock().getZ() + .5 * this.getTargetBlock().getZ() / Math.abs(this.getTargetBlock().getZ());

        this.doFireball(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.arrow(v);
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.voxel();
    }
    
    @Override
    public final int getTimesUsed() {
    	return Meteor.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	Meteor.timesUsed = tUsed;
    }
}
