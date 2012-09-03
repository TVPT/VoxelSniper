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

    protected boolean first = true;
    protected double[] origincoords = new double[3];
    protected double[] targetcoords = new double[3];
    protected int[] currentcoords = new int[3];
    protected int[] previouscoords = new int[3];
    protected double[] slopevector = new double[3];
    private Location player_loc;

    private static int timesUsed = 0;

    public Meteor() {
        this.setName("Meteor");
    }

    public final void dofireball(final SnipeData v) {
        double linelength = 0;

        // Calculate slope vector
        for (int i = 0; i < 3; i++) {
            this.slopevector[i] = this.targetcoords[i] - this.origincoords[i];
        }
        // Calculate line length
        linelength = Math.pow((Math.pow(this.slopevector[0], 2) + Math.pow(this.slopevector[1], 2) + Math.pow(this.slopevector[2], 2)), .5);

        // Unitize slope vector
        for (int i = 0; i < 3; i++) {
            this.slopevector[i] = this.slopevector[i] / linelength;
        }

        // Hadoken!
        final EntityFireball entityfireball = new EntityFireball(((CraftWorld) v.owner().getPlayer().getWorld()).getHandle(), ((CraftPlayer) v.owner()
                .getPlayer()).getHandle(), this.slopevector[0] * linelength, this.slopevector[1] * linelength, this.slopevector[2] * linelength);
        final CraftFireball craftfireball = new CraftFireball((CraftServer) v.owner().getPlayer().getServer(), entityfireball);
        final Vector velocity = new Vector();
        velocity.setX(this.slopevector[0]);
        velocity.setY(this.slopevector[1]);
        velocity.setZ(this.slopevector[2]);
        craftfireball.setVelocity(velocity);
        ((CraftWorld) v.owner().getPlayer().getWorld()).getHandle().addEntity(entityfireball);
    }

    @Override
    public final int getTimesUsed() {
        return Meteor.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.voxel();
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Meteor.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.player_loc = v.owner().getPlayer().getLocation();

        this.origincoords[0] = this.player_loc.getX();
        this.origincoords[1] = this.player_loc.getY();
        this.origincoords[2] = this.player_loc.getZ();

        this.targetcoords[0] = this.getTargetBlock().getX() + .5 * this.getTargetBlock().getX() / Math.abs(this.getTargetBlock().getX()); // I hate you sometimes, Notch. Really? Every quadrant is
                                                                                                // different?
        this.targetcoords[1] = this.getTargetBlock().getY() + .5;
        this.targetcoords[2] = this.getTargetBlock().getZ() + .5 * this.getTargetBlock().getZ() / Math.abs(this.getTargetBlock().getZ());

        // didn't work. I guess I don't understand where the origin of the fireball is determined in this code. shrug. -Gav
        // origincoords[0] = origincoords[0] + (int)(targetcoords[0] - origincoords[0])*0.1; //attempting to make fireballs not blow up in your face anymore.
        // -Gav
        // origincoords[1] = origincoords[1] + (int)(targetcoords[1] - origincoords[1])*0.1;
        // origincoords[2] = origincoords[2] + (int)(targetcoords[2] - origincoords[2])*0.1;
        this.dofireball(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.arrow(v);
    }
}
