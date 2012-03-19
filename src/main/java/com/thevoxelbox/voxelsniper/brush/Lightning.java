/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.Location;

/**
 *
 * @author Gavjenks
 */
public class Lightning extends Brush {
    
    public Lightning() {
        name = "Lightning";
    }

    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        Strike(v);
    }

    @Override
    public void powder(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        StrikeDestructive(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.brushMessage("Lightning Brush!  Please use in moderation.");
    }

    public void Strike(vSniper v) {

        Location loc = clampY(bx, by, bz).getLocation();
        w.strikeLightning(loc);
    }
    public void StrikeDestructive(vSniper v) { //more to be added
/*
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("true")) {
                trueCircle = 0.5;
                v.p.sendMessage(ChatColor.AQUA + "True circle mode ON.");
                continue;
            } else if (par[x].startsWith("false")) {
                trueCircle = 0;
                v.p.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
                continue;
            } else {
                v.p.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
 * 
 */
        Location loc = clampY(bx, by, bz).getLocation();
        w.strikeLightning(loc);
    }
}
