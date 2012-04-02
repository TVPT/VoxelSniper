/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 *
 * @author Gavjenks
 */
public class ForceBrush extends Brush {

    protected boolean passCorrect = false;
    protected int RADIUS = 10;

    public ForceBrush() {
        name = "ForceBrush";
    }

    @Override
    public void arrow(vSniper v) {
        bx = lb.getX();
        by = lb.getY();
        bz = lb.getZ();
        Attract(v);
    }

    @Override
    public void powder(vSniper v) {
        bx = lb.getX();
        by = lb.getY();
        bz = lb.getZ();
        Repel(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.brushMessage("Force Brush. Keep your friends close and ... wait, now I'm confused.");
    }

    @Override
    public void parameters(String[] par, vSniper v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.p.sendMessage(ChatColor.AQUA + "This brush requires a password to function.");
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

    public void Attract(vSniper v) {

        if (passCorrect) {
            RADIUS = v.brushSize;
            if (RADIUS > 500 || RADIUS < -500) {
                        RADIUS = 500;
                        v.brushSize = 500;
                        v.p.sendMessage(ChatColor.AQUA + "Maximum 500 radius.  Brush size changed for you.");
                    }
            if (RADIUS != -3) {
                List<LivingEntity> nearbyEnts = w.getLivingEntities();
                Location loc = clampY(bx, by, bz).getLocation(); //location of snipe
                try {
                    for (int i = 0; i < nearbyEnts.size(); i++) {

                        Entity thisEnt = (Entity) nearbyEnts.get(i);

                        Location otherLoc = thisEnt.getLocation();
                        double xdist = otherLoc.getX() - bx;
                        double ydist = otherLoc.getY() - by;
                        double zdist = otherLoc.getZ() - bz;
                        if (Math.pow(xdist, 2) + Math.pow(ydist, 2) + Math.pow(zdist, 2) <= Math.pow(RADIUS, 2)) {
                            thisEnt.teleport(loc);
                        }


                    }
                } catch (Exception e) {
                    //may not be any entities, or whatever other problem.
                }
            } else {
                List<LivingEntity> nearbyEnts = w.getLivingEntities();
                Location loc = clampY(bx, by, bz).getLocation(); //location of snipe
                try {
                    for (int i = 0; i < nearbyEnts.size(); i++) {

                        Entity thisEnt = (Entity) nearbyEnts.get(i);
                        thisEnt.teleport(loc);
                    }
                } catch (Exception e) {
                    //may not be any entities, or whatever other problem.
                }//teleport EVERYONE
            }
        }
    }

    public void Repel(vSniper v) {
        if (passCorrect) {
            List<LivingEntity> nearbyEnts = w.getLivingEntities();
            //Location loc = clampY(bx, by, bz).getLocation(); //location of snipe
            try {
                for (int i = 0; i < nearbyEnts.size(); i++) {
                    Entity thisEnt = (Entity) nearbyEnts.get(i);
                    Location otherLoc = thisEnt.getLocation();
                    //teleport them outside the radius, or set velocities away from you.
                    double xdist = otherLoc.getX() - bx;
                    double ydist = otherLoc.getY() - by;
                    double zdist = otherLoc.getZ() - bz;
                    double xdist2 = Math.pow(xdist, 2);
                    double ydist2 = Math.pow(ydist, 2);
                    double zdist2 = Math.pow(zdist, 2);
                    if (xdist2 + ydist2 + zdist2 <= Math.pow(RADIUS, 2)) {
                        double howClose = Math.sqrt(xdist2 + ydist2 + zdist2) / RADIUS;
                        xdist = xdist * (1 / howClose);
                        ydist = ydist * (1 / howClose);
                        zdist = zdist * (1 / howClose);
                        Location toTel = new Location(w, bx + xdist, by + ydist, bz + zdist);
                        thisEnt.teleport(toTel);
                    }
                }
            } catch (Exception e) {
            }
        }
    }
}
