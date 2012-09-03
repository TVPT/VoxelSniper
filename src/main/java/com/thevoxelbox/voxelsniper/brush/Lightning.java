package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.Location;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;

/**
 * 
 * @author Gavjenks
 */
public class Lightning extends Brush {

    private static int timesUsed = 0;

    public Lightning() {
        this.name = "Lightning";
    }

    @Override
    public final int getTimesUsed() {
        return Lightning.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
        vm.brushMessage("Lightning Brush!  Please use in moderation.");
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Lightning.timesUsed = tUsed;
    }

    public final void Strike(final vData v) {

        final Location loc = this.clampY(this.bx, this.by, this.bz).getLocation();
        this.w.strikeLightning(loc);
    }

    public final void StrikeDestructive(final vData v) { // more to be added
    /*
     * for (int x = 1; x < par.length; x++) { if (par[x].startsWith("true")) { trueCircle = 0.5; v.p.sendMessage(ChatColor.AQUA + "True circle mode ON.");
     * continue; } else if (par[x].startsWith("false")) { trueCircle = 0; v.p.sendMessage(ChatColor.AQUA + "True circle mode OFF."); continue; } else {
     * v.p.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info."); } }
     */
        final Location loc = this.clampY(this.bx, this.by, this.bz).getLocation();
        this.w.strikeLightning(loc);
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.Strike(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.bx = this.tb.getX();
        this.by = this.tb.getY();
        this.bz = this.tb.getZ();
        this.StrikeDestructive(v);
    }
}
