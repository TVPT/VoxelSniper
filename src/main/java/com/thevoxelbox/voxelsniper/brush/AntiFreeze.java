/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;

/**
 *
 * @author Gavjenks
 */
public class AntiFreeze extends Brush {

    boolean bool = true;
    
    public AntiFreeze() {
        name = "AntiFreeze";
    }

    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        AF(v);
    }

    @Override
    public void powder(vSniper v) {
        bool = false;
        arrow(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushMessage(ChatColor.GOLD + "Arrow overlays insible wood stairs, powder is cobble stairs.  Use whichever one you have less of in your build for easier undoing later.  This may ruin builds with ice as a structural component.  DOES NOT UNDO DIRECTLY.");
        vm.brushName(name);
        vm.size();
    }

    
    public void AF(vSniper v) {
        int bsize = v.brushSize;
        //int bId = v.voxelId;
       

        double bpow = Math.pow(bsize + 0.5, 2);
        for (int x = bsize; x >= 0; x--) {
            double xpow = Math.pow(x, 2);
            for (int z = bsize; z >= 0; z--) {
                double zpow = Math.pow(z, 2);
                    if (xpow + zpow <= bpow) { 
                        for (int y = 1; y<127; y++){
                            if (getBlockIdAt(bx+x,y,bz+z) == 79 && getBlockIdAt(bx+x,y+1,bz+z) == 0){
                            if (bool){
                                setBlockIdAt(53, bx + x, y+1, bz + z);
                                if(getBlockIdAt(bx + x, y, bz + z) == 53) {
                                    setBlockIdAt(9, bx + x, y, bz + z);
                                }
                                setBlockIdAt(53, bx + x, y+1, bz - z);
                                if(getBlockIdAt(bx + x, y, bz - z) == 53) {
                                    setBlockIdAt(9, bx + x, y, bz - z);
                                }
                                setBlockIdAt(53, bx - x, y+1, bz + z);
                                if(getBlockIdAt(bx - x, y, bz + z) == 53) {
                                    setBlockIdAt(9, bx - x, y, bz + z);
                                }
                                setBlockIdAt(53, bx - x, y+1, bz - z);
                                if(getBlockIdAt(bx - x, y, bz - z) == 53) {
                                    setBlockIdAt(9, bx - x, y, bz - z);
                                }
                                w.getBlockAt(bx + x, y+1, bz + z).setData((byte)6);
                                w.getBlockAt(bx + x, y+1, bz - z).setData((byte)6);
                                w.getBlockAt(bx - x, y+1, bz + z).setData((byte)6);
                                w.getBlockAt(bx - x, y+1, bz - z).setData((byte)6);
                                setBlockIdAt(9, bx + x, y, bz + z);
                                setBlockIdAt(9, bx + x, y, bz - z);
                                setBlockIdAt(9, bx - x, y, bz + z);
                                setBlockIdAt(9, bx - x, y, bz - z);
                                setBlockIdAt(9, bx + x, y, bz + z);
                                setBlockIdAt(9, bx + x, y, bz - z);
                                setBlockIdAt(9, bx - x, y, bz + z);
                                setBlockIdAt(9, bx - x, y, bz - z);
                            }
                            else {
                               
                                setBlockIdAt(67, bx + x, y+1, bz + z);
                                if(getBlockIdAt(bx + x, y, bz + z) == 67) {
                                    setBlockIdAt(9, bx + x, y, bz + z);
                                }
                                setBlockIdAt(67, bx + x, y+1, bz - z);
                                if(getBlockIdAt(bx + x, y, bz - z) == 67) {
                                    setBlockIdAt(9, bx + x, y, bz - z);
                                }
                                setBlockIdAt(67, bx - x, y+1, bz + z);
                                if(getBlockIdAt(bx - x, y, bz + z) == 67) {
                                    setBlockIdAt(9, bx - x, y, bz + z);
                                }
                                setBlockIdAt(67, bx - x, y+1, bz - z);
                                if(getBlockIdAt(bx - x, y, bz - z) == 67) {
                                    setBlockIdAt(9, bx - x, y, bz - z);
                                }
                                w.getBlockAt(bx + x, y+1, bz + z).setData((byte)6);
                                w.getBlockAt(bx + x, y+1, bz - z).setData((byte)6);
                                w.getBlockAt(bx - x, y+1, bz + z).setData((byte)6);
                                w.getBlockAt(bx - x, y+1, bz - z).setData((byte)6);
                                setBlockIdAt(9, bx + x, y, bz + z); 
                                setBlockIdAt(9, bx + x, y, bz - z);
                                setBlockIdAt(9, bx - x, y, bz + z);
                                setBlockIdAt(9, bx - x, y, bz - z);
                                setBlockIdAt(9, bx + x, y, bz + z); //double set, in case it tries to do wonky stuff just because it'w ice and it is a paint in the ass.
                                setBlockIdAt(9, bx + x, y, bz - z);
                                setBlockIdAt(9, bx - x, y, bz + z);
                                setBlockIdAt(9, bx - x, y, bz - z);
                            
                                }
                            }
                        }
                }
            }
        }
    }
}
    