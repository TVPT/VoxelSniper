package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * 
 * @author Voxel
 */
public class SnowCone extends Brush {

    double trueCircle = 0;

    private static int timesUsed = 0;

    public final void addsnow(final vData v) {
        int bsize;

        if (this.getBlockIdAt(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()) == 0) {
            bsize = 0;
        } else {
            bsize = this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ()).getData() + 1;
        }
        final int[][] snowcone = new int[2 * bsize + 1][2 * bsize + 1]; // Will hold block IDs
        final int[][] snowconedata = new int[2 * bsize + 1][2 * bsize + 1]; // Will hold data values for snowcone
        final int[][] yoffset = new int[bsize * 2 + 1][bsize * 2 + 1];
        // prime the arrays

        for (int x = 0; x <= 2 * bsize; x++) {
            for (int z = 0; z <= 2 * bsize; z++) {
                boolean flag = true;
                for (int i = 0; i < 10; i++) { // overlay
                    if (flag) {
                        if ((this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - bsize + z) == 0 || this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY()
                                - i, this.getBlockPositionZ() - bsize + z) == 78)
                                && this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i - 1, this.getBlockPositionZ() - bsize + z) != 0
                                && this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - i - 1, this.getBlockPositionZ() - bsize + z) != 78) {
                            flag = false;
                            yoffset[x][z] = i;
                        }
                    }
                }
                snowcone[x][z] = this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - yoffset[x][z], this.getBlockPositionZ() - bsize + z);
                snowconedata[x][z] = this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - yoffset[x][z], this.getBlockPositionZ() - bsize + z).getData();
            }
        }

        // figure out new snowheights
        for (int x = 0; x <= 2 * bsize; x++) {
            final double xpow = Math.pow(x - bsize, 2);
            for (int z = 0; z <= 2 * bsize; z++) {
                final double zpow = Math.pow(z - bsize, 2);
                final double dist = Math.pow(xpow + zpow, .5); // distance from center of array
                final int snowdata = bsize - (int) Math.ceil(dist);

                if (snowdata >= 0) { // no funny business
                    switch (snowdata) {
                    case 0:
                        if (snowcone[x][z] == 78) {
                            // snowconedata[x][z] = 1;
                        } else if (snowcone[x][z] == 0) {
                            snowcone[x][z] = 78;
                            snowconedata[x][z] = 0;
                        }
                        break;
                    case 7: // Turn largest snowtile into snowblock
                        if (snowcone[x][z] == 78) {
                            snowcone[x][z] = 80;
                            snowconedata[x][z] = 0;
                        }
                        break;
                    default: // Increase snowtile size, if smaller than target

                        if (snowdata > snowconedata[x][z]) {
                            switch (snowcone[x][z]) {
                            case 0:
                                snowconedata[x][z] = snowdata;
                                snowcone[x][z] = 78;
                            case 78:
                                snowconedata[x][z] = snowdata;
                                break;
                            default:
                                // v.sendMessage(ChatColor.RED+"Case: "+snowcone[x][z]);
                                break;

                            }
                        } else if (yoffset[x][z] > 0 && snowcone[x][z] == 78) {
                            snowconedata[x][z]++;
                            if (snowconedata[x][z] == 7) {
                                snowconedata[x][z] = 0;
                                snowcone[x][z] = 80;
                            }
                        }
                        break;
                    }
                }
            }
        }
        final vUndo h = new vUndo(this.getTargetBlock().getWorld().getName());

        for (int x = 0; x <= 2 * bsize; x++) {
            for (int z = 0; z <= 2 * bsize; z++) {

                if (this.getBlockIdAt(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - yoffset[x][z], this.getBlockPositionZ() - bsize + z) != snowcone[x][z]
                        || this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - yoffset[x][z], this.getBlockPositionZ() - bsize + z).getData() != snowconedata[x][z]) {
                    h.put(this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - yoffset[x][z], this.getBlockPositionZ() - bsize + z));
                }
                this.setBlockIdAt(snowcone[x][z], this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - yoffset[x][z], this.getBlockPositionZ() - bsize + z);
                this.clampY(this.getBlockPositionX() - bsize + x, this.getBlockPositionY() - yoffset[x][z], this.getBlockPositionZ() - bsize + z).setData((byte) snowconedata[x][z]);

            }
        }
        v.storeUndo(h);
    }

    @Override
    public final int getTimesUsed() {
        return SnowCone.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName("Snow Cone");
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Snow Cone Parameters:");
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        SnowCone.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        // delsnow(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        switch (this.getBlockIdAt(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ())) {
        case 78:
            this.addsnow(v);
            break;
        default:
            // Move up one if target is not snowtile
            if (this.getBlockIdAt(this.getBlockPositionX(), this.getBlockPositionY() + 1, this.getBlockPositionZ()) == 0) {
                this.setBlockPositionY(this.getBlockPositionY() + 1);
                this.addsnow(v);
            } else {
                v.owner().getPlayer().sendMessage(ChatColor.RED + "Error: Center block neither snow nor air.");
            }
            break;
        }
    }
}
