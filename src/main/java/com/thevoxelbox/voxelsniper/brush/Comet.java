/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import net.minecraft.server.EntitySmallFireball;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.entity.CraftSmallFireball;
import org.bukkit.util.Vector;

/**
 * THIS BRUSH SHOULD NOT USE PERFORMERS
 *
 * @author Gavjenks
 * Heavily revamped from ruler brush by Giltwist
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

    public Comet() {
        name = "Comet";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        player_loc = v.owner().p.getLocation();

        origincoords[0] = player_loc.getX();
        origincoords[1] = player_loc.getY();
        origincoords[2] = player_loc.getZ();

        targetcoords[0] = tb.getX() + .5 * tb.getX() / Math.abs(tb.getX()); //I hate you sometimes, Notch.  Really? Every quadrant is different?
        targetcoords[1] = tb.getY() + .5;
        targetcoords[2] = tb.getZ() + .5 * tb.getZ() / Math.abs(tb.getZ());

        //didn't work.  I guess I don't understand where the origin of the fireball is determined in this code.  shrug.  -Gav
        // origincoords[0] = origincoords[0] + (int)(targetcoords[0] - origincoords[0])*0.1; //attempting to make fireballs not blow up in your face anymore.  -Gav
        // origincoords[1] = origincoords[1] + (int)(targetcoords[1] - origincoords[1])*0.1;
        // origincoords[2] = origincoords[2] + (int)(targetcoords[2] - origincoords[2])*0.1;
        dofireball(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        if (passCorrect) {
            arrow(v);
        }
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.voxel();
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) { //borrowed from Force Brush at Ridge'w
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.AQUA + "This brush requires a password to function.");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            //which way is clockwise is less obvious for roll and pitch... should probably fix that / make it clear
            if (par[x].startsWith("Scorpion")) {
                passCorrect = true;
                continue;
            }
        }
    }

    public void dofireball(vData v) {
        double linelength = 0;

        //Calculate slope vector
        for (int i = 0; i < 3; i++) {
            slopevector[i] = targetcoords[i] - origincoords[i];
        }
        //Calculate line length 
        linelength = Math.pow((Math.pow(slopevector[0], 2) + Math.pow(slopevector[1], 2) + Math.pow(slopevector[2], 2)), .5);

        //Unitize slope vector
        for (int i = 0; i < 3; i++) {
            slopevector[i] = slopevector[i] / linelength;
        }

        //Hadoken!
        EntitySmallFireball entityfireball = new EntitySmallFireball(((CraftWorld) v.owner().p.getWorld()).getHandle(), ((CraftPlayer) v.owner().p).getHandle(), slopevector[0] * linelength, slopevector[1] * linelength, slopevector[2] * linelength);
        CraftSmallFireball craftfireball = new CraftSmallFireball((CraftServer) v.owner().p.getServer(), entityfireball);
        Vector velocity = new Vector();
        velocity.setX(slopevector[0]);
        velocity.setY(slopevector[1]);
        velocity.setZ(slopevector[2]);
        craftfireball.setVelocity(velocity);
        ((CraftWorld) v.owner().p.getWorld()).getHandle().addEntity(entityfireball);
    }
}
