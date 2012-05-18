/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.craftbukkit.CraftWorld;

/**
 *
 * @author giltwist
 */
public class Glow extends Brush {

    protected int glevel;

    public Glow() {
        name = "Glow";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        glevel = 0;
        doglow(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        glevel = 15;
        doglow(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
    }

    public void doglow(vData v) {

        CraftWorld cWorld = (CraftWorld) v.owner().p.getWorld();
//cWorld.getHandle().b(EnumSkyBlock.BLOCK, bx, by, bz. glevel); //not sure why this is throwing an error. - Giltwist

    }
}
