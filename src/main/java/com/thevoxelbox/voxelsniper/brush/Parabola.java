package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * 
 * @author Gavin
 */
public class Parabola extends Brush {

    // default parameters
    private int full = 0;

    private int height = 2;

    private int up = 1;

    private int width = 2;
    private static int timesUsed = 0;

    public Parabola() {
        this.setName("Parabola");
    }

    @Override
    public final int getTimesUsed() {
        return Parabola.timesUsed;
    }

    public void hParabola(final vData v) {
        // herp derp;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.getName());
        vm.voxel();
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Parabola Brush Arrow Parameters:");
            v.sendMessage(ChatColor.AQUA
                    + "/b p full -- will set the parabola to full step accuracy for the curve (less accurate than default half step). Type 'half' instead to return to default.  Will always be set to full if /v isn't a half block.");
            v.sendMessage(ChatColor.BLUE + "/b p h[number] -- sets the height of the parabola. This is measured from whichever of your two feet is LOWER.");
            v.sendMessage(ChatColor.GREEN
                    + "/b p down -- Makes the parabola curve upward (the middle is LOWER than the feet).  '/b p up' which is default, does the opposite.");
            v.sendMessage(ChatColor.GOLD
                    + "To use, set your height and click the two bases of the desired parabola.  These can be diagonal or different heights from each other.");
            v.sendMessage(ChatColor.GOLD + "Type /b p info2  for gunpowder parameters and instructions.");
            return;
        }
        if (par[1].equalsIgnoreCase("info2")) {
            v.sendMessage(ChatColor.GOLD + "Parabola Brush Gunpowder Parameters:");
            v.sendMessage(ChatColor.BLUE + "/b p h[number] -- sets the height of the parabola. Default 2.");
            v.sendMessage(ChatColor.LIGHT_PURPLE + "/b p world[number] -- sets the width of the parabola. Default 2.");
            v.sendMessage(ChatColor.GOLD
                    + "To use, set your height and click the apex of the parabola.  It will orient itself based on which face you click.  If an even width, the apex is considered the middle block to your right.");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("full")) {
                this.full = 1;
                v.sendMessage(ChatColor.AQUA + "Full step accuracy set (less accurate).");
                continue;
            } else if (par[x].startsWith("half")) {
                this.full = 0;
                if (this.getBlockIdAt(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()) != 44) {
                    v.sendMessage(ChatColor.RED + "Half step accuracy not available for this block type. /v must be 44.");
                    this.full = 1;
                } else {
                    v.sendMessage(ChatColor.AQUA + "Half step accuracy set (more accurate).");
                }
                continue;
            } else if (par[x].startsWith("h")) {
                this.height = Integer.parseInt(par[x].replace("h", ""));
                if (this.height < 0) {
                    this.height = 1;
                }
                if (this.height > 40) {
                    v.sendMessage(ChatColor.RED + "WARNING: High parabola.  Make sure you won't hit sky limit.");
                }
                v.sendMessage(ChatColor.BLUE + "Parabola height set to: " + this.height);
                continue;
            } else if (par[x].startsWith("up")) {
                this.up = 1;
                v.sendMessage(ChatColor.AQUA + "Middle will be higher than feet.");
                continue;
            } else if (par[x].startsWith("down")) {
                this.up = 0;
                v.sendMessage(ChatColor.AQUA + "Middle will be lower than feet.");
                continue;
            } else if (par[x].startsWith("world")) {
                this.width = Integer.parseInt(par[x].replace("world", ""));
                if (this.width < 0) {
                    this.width = 1;
                }
                v.sendMessage(ChatColor.BLUE + "Parabola width set to: " + this.width);
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Parabola.timesUsed = tUsed;
    }

    public final void vParabola(final vData v) {
        final int bId = v.voxelId;
        if (bId == 44) {
        }

        final vUndo h = new vUndo(this.getTargetBlock().getWorld().getName());

        // Desired features:
        // arrow allows a parameter of angle and of height and apex versus feet. Click both feet (or the apex, depending on param) of the parabola, and it
        // measures height from the lower one (or from apex to both feet). Then makes a parabola between those feet skewed blockPositionY the angle (default = 0, 90 = a
        // horizontal parabola, 180 = downward arch), for weird slanty parabolas if you want.
        // powder makes a doubly parabolic dome or saddle roofing. This one requires you to click the apex / zero point. You input: height, x width, and z
        // width. It will make half of a paraboloid with all the relevant values and fill in the surface in half or full step accuracy. Thus, you can make domes
        // with parabolic height AND bases. If you set x and z both negative, it will open the other way.
        // If you set only one of them negative, it will make a hyperbolic saddle roof (like a pringles chip).
        // powder should be able to operate in face mode, to make horizontal things. Could use it to make instant billowing sails, for example. However, this
        // may be unnecessary work if we get that rotation brush. Just make an upright one and rotate it.
        // Basically, this is a brush designed to make Featherblade obsolete =P

        // ruler code - keep the double clicking stuff, change the math.
        /*
         * if (xOff == 0 && yOff == 0 && zOff == 0) {
         * 
         * //if (sel) { //commented out - I am making arrow select first point always and powder report back distances from it as many times as you want.
         * coords[0] = targetBlock.getX(); coords[1] = targetBlock.getY(); coords[2] = targetBlock.getZ(); v.sendMessage(ChatColor.DARK_PURPLE + "First point selected."); sel = !sel; //}
         * //else { // double distance = Math.sqrt(Math.pow((coords[0] - targetBlock.getX()), 2) + Math.pow((coords[1] - targetBlock.getY()), 2) + Math.pow((coords[2] -
         * targetBlock.getZ()), 2)); // distance = roundTwoDecimals(distance); // v.sendMessage(ChatColor.AQUA + "Euclidean distance = " + distance); // sel = !sel; //}
         * } else { vUndo h = new vUndo(targetBlock.getWorld().getName()); v.sendMessage(ChatColor.AQUA + "xyz targetBlock xyz offset: " + blockPositionX + " " + blockPositionY + " " + blockPositionZ + " " + xOff +
         * " " + yOff + " " + zOff);
         * 
         * h.put(clampY(blockPositionX + xOff, blockPositionY + yOff, blockPositionZ + zOff)); setBlockIdAt(bId, blockPositionX + xOff, blockPositionY + yOff, blockPositionZ + zOff); v.hashUndo.put(v.hashEn, h); v.hashEn++; }
         */

        v.storeUndo(h);
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.vParabola(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.hParabola(v);
    }
}
