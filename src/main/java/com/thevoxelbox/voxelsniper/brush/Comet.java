package com.thevoxelbox.voxelsniper.brush;

import net.minecraft.server.EntitySmallFireball;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.entity.CraftSmallFireball;
import org.bukkit.util.Vector;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;

/**
 * 
 * @author Gavjenks Heavily revamped from ruler brush blockPositionY Giltwist
 */
public class Comet extends Brush {

    protected boolean passCorrect = false;
    protected boolean first = true;
    protected double[] origincoords = new double[3];
    protected double[] targetcoords = new double[3];
    protected int[] currentcoords = new int[3];
    protected int[] previouscoords = new int[3];
    protected double[] slopevector = new double[3];
    private Location player_loc;

    private static int timesUsed = 0;

    public Comet() {
        this.setName("Comet");
    }

    public final void dofireball(final vData v) {
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
        final EntitySmallFireball entityfireball = new EntitySmallFireball(((CraftWorld) v.owner().getPlayer().getWorld()).getHandle(), ((CraftPlayer) v
                .owner().getPlayer()).getHandle(), this.slopevector[0] * linelength, this.slopevector[1] * linelength, this.slopevector[2] * linelength);
        final CraftSmallFireball craftfireball = new CraftSmallFireball((CraftServer) v.owner().getPlayer().getServer(), entityfireball);
        final Vector velocity = new Vector();
        velocity.setX(this.slopevector[0]);
        velocity.setY(this.slopevector[1]);
        velocity.setZ(this.slopevector[2]);
        craftfireball.setVelocity(velocity);
        ((CraftWorld) v.owner().getPlayer().getWorld()).getHandle().addEntity(entityfireball);
    }

    @Override
    public final int getTimesUsed() {
        return Comet.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.getName());
        vm.voxel();
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) { // borrowed from Force Brush at Ridge'world
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.AQUA + "This brush requires a password to function.");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            // which way is clockwise is less obvious for roll and pitch... should probably fix that / make it clear
            if (par[x].startsWith("Scorpion")) {
                this.passCorrect = true;
                continue;
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Comet.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
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
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        if (this.passCorrect) {
            this.arrow(v);
        }
    }
}
