/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;

/**
 *
 * @author giltwist
 */
public class Glow extends Brush{

    protected int glevel;
    
        public Glow() {
        name = "Glow";
    }

    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        glevel=0;
        doglow(v);
    }

    @Override
    public void powder(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        glevel=15;
        doglow(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
    }

    public void doglow (vSniper v) {
        
        //CraftWorld cWorld = (CraftWorld)v.p.getWorld();
//cWorld.getHandle().b(EnumSkyBlock.BLOCK, bx, by, bz. glevel); //not sure why this is throwing an error. - Giltwist
    
    }
    
}
