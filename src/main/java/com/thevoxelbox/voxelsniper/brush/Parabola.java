/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;

/**
 *
 * @author Gavin
 */
public class Parabola extends Brush {
    
    public Parabola() {
        name = "Parabola";
    }

    @Override
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        vParabola(v);
    }

    @Override
    public void powder(vSniper v) {
        hParabola(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.voxel();
    }

    //default parameters
    //private int full = 0;
    private int height = 2;
    //private int up = 1;
    private int width = 2;

    @Override
    public void parameters(String[] par, vSniper v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.p.sendMessage(ChatColor.GOLD + "Parabola Brush Arrow Parameters:");
            v.p.sendMessage(ChatColor.AQUA + "/b p full -- will set the parabola to full step accuracy for the curve (less accurate than default half step). Type 'half' instead to return to default.  Will always be set to full if /v isn't a half block.");
            v.p.sendMessage(ChatColor.BLUE + "/b p h[number] -- sets the height of the parabola. This is measured from whichever of your two feet is LOWER.");
            v.p.sendMessage(ChatColor.GREEN + "/b p down -- Makes the parabola curve upward (the middle is LOWER than the feet).  '/b p up' which is default, does the opposite.");
            v.p.sendMessage(ChatColor.GOLD + "To use, set your height and click the two bases of the desired parabola.  These can be diagonal or different heights from each other.");
            v.p.sendMessage(ChatColor.GOLD + "Type /b p info2  for gunpowder parameters and instructions.");
            return;
        }
        if (par[1].equalsIgnoreCase("info2")) {
            v.p.sendMessage(ChatColor.GOLD + "Parabola Brush Gunpowder Parameters:");
            v.p.sendMessage(ChatColor.BLUE + "/b p h[number] -- sets the height of the parabola. Default 2.");
            v.p.sendMessage(ChatColor.LIGHT_PURPLE + "/b p w[number] -- sets the width of the parabola. Default 2.");
            v.p.sendMessage(ChatColor.GOLD + "To use, set your height and click the apex of the parabola.  It will orient itself based on which face you click.  If an even width, the apex is considered the middle block to your right.");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("full")) {
                //full = 1;
                v.p.sendMessage(ChatColor.AQUA + "Full step accuracy set (less accurate).");
                continue;
            } else if (par[x].startsWith("half")) {
                //full = 0;
                if (getBlockIdAt(bx, by, bz) != 44){
                    v.p.sendMessage(ChatColor.RED + "Half step accuracy not available for this block type. /v must be 44.");
                    //full = 1;
                } else {
                    v.p.sendMessage(ChatColor.AQUA + "Half step accuracy set (more accurate).");
                }
                continue;
            } else if (par[x].startsWith("h")) {
                height = Integer.parseInt(par[x].replace("h", ""));
                if (height < 0) {
                    height = 1;
                }
                if (height > 40) {
                    v.p.sendMessage(ChatColor.RED + "WARNING: High parabola.  Make sure you won't hit sky limit.");
                }
                v.p.sendMessage(ChatColor.BLUE + "Parabola height set to: " + height);
                continue;
            } else if (par[x].startsWith("up")) {
                //up = 1;
                v.p.sendMessage(ChatColor.AQUA + "Middle will be higher than feet.");
                continue;
            } else if (par[x].startsWith("down")) {
                //up = 0;
                v.p.sendMessage(ChatColor.AQUA + "Middle will be lower than feet.");
                continue;
            } else if (par[x].startsWith("w")) {
                width = Integer.parseInt(par[x].replace("w", ""));
                if (width < 0) {
                    width = 1;
                }
                v.p.sendMessage(ChatColor.BLUE + "Parabola width set to: " + width);
                continue;
            } else {
                v.p.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }

    }

    public void vParabola (vSniper v) {
        /* This block of code is never read.
        int bId = v.voxelId;
        int bIdLow = bId;
        if (bId == 44) {
            bIdLow = 43;
        }*/

        vUndo h = new vUndo(tb.getWorld().getName());


        //Desired features:
        //arrow allows a parameter of angle and of height and apex versus feet.  Click both feet (or the apex, depending on param) of the parabola, and it measures height from the lower one (or from apex to both feet).  Then makes a parabola between those feet skewed by the angle (default = 0, 90 = a horizontal parabola, 180 = downward arch), for weird slanty parabolas if you want.
        //powder makes a doubly parabolic dome or saddle roofing.  This one requires you to click the apex / zero point.  You input: height, x width, and z width.  It will make half of a paraboloid with all the relevant values and fill in the surface in half or full step accuracy.  Thus, you can make domes with parabolic height AND bases.  If you set x and z both negative, it will open the other way.
        //If you set only one of them negative, it will make a hyperbolic saddle roof (like a pringles chip).
        //powder should be able to operate in face mode, to make horizontal things.  Could use it to make instant billowing sails, for example.  However, this may be unnecessary work if we get that rotation brush.  Just make an upright one and rotate it.
        //Basically, this is a brush designed to make Featherblade obsolete =P


//ruler code - keep the double clicking stuff, change the math.
/*
if (xOff == 0 && yOff == 0 && zOff == 0) {

            //if (sel) {  //commented out - I am making arrow select first point always and powder report back distances from it as many times as you want.
                coords[0] = tb.getX();
                coords[1] = tb.getY();
                coords[2] = tb.getZ();
                v.p.sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
                sel = !sel;
            //}
            //else {
            //    double distance = Math.sqrt(Math.pow((coords[0] - tb.getX()), 2) + Math.pow((coords[1] - tb.getY()), 2) + Math.pow((coords[2] - tb.getZ()), 2));
            //    distance = roundTwoDecimals(distance);
            //    v.p.sendMessage(ChatColor.AQUA + "Euclidean distance = " + distance);
            //    sel = !sel;
            //}
        } else {
            vUndo h = new vUndo(tb.getWorld().getName());
            v.p.sendMessage(ChatColor.AQUA + "xyz tb xyz offset: " + bx + " " + by + " " + bz + " " + xOff + " " + yOff + " " + zOff);

            h.put(clampY(bx + xOff, by + yOff, bz + zOff));
            setBlockIdAt(bId, bx + xOff, by + yOff, bz + zOff);
            v.hashUndo.put(v.hashEn, h);
            v.hashEn++;
        }
 */




        v.hashUndo.put(v.hashEn, h);
        v.hashEn++;
    }

    public void hParabola (vSniper v) {
        //herp derp;
    }
}
