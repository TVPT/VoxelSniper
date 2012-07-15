/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import net.minecraft.server.Explosion;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
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
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        tp(v.owner().getPlayer(), v.owner().getPlayer().getLocation()); // arrow just warps you, which is still useful and not annoying. Powder does the
                                                                        // effects. -GJ
    } // Ah, nice touch --prz

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        Location l = v.owner().getPlayer().getLocation();
        Explosion e = new Explosion(((CraftWorld) v.owner().getPlayer().getWorld()).getHandle(), ((CraftPlayer) v.owner().getPlayer()).getHandle(), 0.0D,
                -300.0D, 0.0D, 0.1F);
        // ((CraftWorld) v.p.getWorld()).getHandle().server.serverConfigurationManager.a(l.getX(), l.getY(), l.getZ(), 64.0D, new Packet60Explosion(l.getX(),
        // l.getY(), l.getZ(), v.brushSize, e.g));//.createExplosion(( (CraftPlayer)v.p).getHandle(), l.getX(), l.getY(), l.getZ(), v.brushSize, false);
        w.strikeLightning(l);
        tp(v.owner().getPlayer(), v.owner().getPlayer().getLocation());
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
