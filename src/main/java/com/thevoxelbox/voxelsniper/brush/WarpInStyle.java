/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Voxel
 */
public class WarpInStyle extends Brush {

    public WarpInStyle() {
        name = "Warp Like a Boss";
    }

    @Override
    public void arrow(vSniper v) {
        tp(v.p, v.p.getLocation()); //arrow just warps you, which is still useful and not annoying.  Powder does the effects.  -GJ
    }                               //Ah, nice touch --prz

    @Override
    public void powder(vSniper v) {
        Location l = v.p.getLocation();
        //Explosion e = new Explosion(((CraftWorld) v.p.getWorld()).getHandle(), ((CraftPlayer) v.p).getHandle(), 0.0D, -300.0D, 0.0D, 0.1F);
       // ((CraftWorld) v.p.getWorld()).getHandle().server.serverConfigurationManager.a(l.getX(), l.getY(), l.getZ(), 64.0D, new Packet60Explosion(l.getX(), l.getY(), l.getZ(), v.brushSize, e.g));//.createExplosion(( (CraftPlayer)v.p).getHandle(), l.getX(), l.getY(), l.getZ(), v.brushSize, false);
        w.strikeLightning(l);
        tp(v.p, v.p.getLocation());
        w.strikeLightning(tb.getLocation());
    }

    protected void tp(Player p, Location l) {
        p.teleport(new Location(p.getWorld(), lb.getX(), lb.getY(), lb.getZ(), l.getYaw(), l.getPitch()));
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
    }
}
