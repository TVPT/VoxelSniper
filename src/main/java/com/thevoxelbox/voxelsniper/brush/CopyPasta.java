package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author giltwist
 */
public class CopyPasta extends Brush {

    protected boolean airmode = true; // False = no air, true = air
    protected int points = 0; //
    protected int numblocks = 0;
    protected int[] firstpoint = new int[3];
    protected int[] secondpoint = new int[3];
    protected int[] pastepoint = new int[3];
    protected int[] minpoint = new int[3];
    protected int[] offsetpoint = new int[3];
    protected int[] blockarray;
    protected byte[] dataarray;
    protected int[] arraysize = new int[3];
    protected int pivot = 0; // ccw degrees
    protected int blocklimit = 10000;

    private static int timesUsed = 0;

    public CopyPasta() {
        this.setName("CopyPasta");
    }

    public final void docopy(final SnipeData v) {
        this.setWorld(v.owner().getPlayer().getWorld());
        for (int i = 0; i < 3; i++) {
            this.arraysize[i] = Math.abs(this.firstpoint[i] - this.secondpoint[i]) + 1;
            this.minpoint[i] = Math.min(this.firstpoint[i], this.secondpoint[i]);
            this.offsetpoint[i] = this.minpoint[i] - this.firstpoint[i]; // will always be negative or zero
        }
        this.numblocks = (this.arraysize[0]) * (this.arraysize[1]) * (this.arraysize[2]);
        if (this.numblocks > 0 && this.numblocks < this.blocklimit) {
            this.blockarray = new int[this.numblocks];
            this.dataarray = new byte[this.numblocks];

            for (int i = 0; i < this.arraysize[0]; i++) {
                for (int j = 0; j < this.arraysize[1]; j++) {
                    for (int k = 0; k < this.arraysize[2]; k++) {
                        final int curpos = i + this.arraysize[0] * j + this.arraysize[0] * this.arraysize[1] * k;
                        this.blockarray[curpos] = this.getWorld().getBlockTypeIdAt(this.minpoint[0] + i, this.minpoint[1] + j, this.minpoint[2] + k);
                        this.dataarray[curpos] = this.clampY(this.minpoint[0] + i, this.minpoint[1] + j, this.minpoint[2] + k).getData();
                    }
                }
            }

            v.sendMessage(ChatColor.AQUA + "" + this.numblocks + " blocks copied.");
        } else {
            v.sendMessage(ChatColor.RED + "Copy area too big: " + this.numblocks + "(Limit: " + this.blocklimit + ")");
        }
    }

    public final void dopasta(final SnipeData v) {
        this.setWorld(v.owner().getPlayer().getWorld());
        final Undo h = new Undo(this.getTargetBlock().getWorld().getName());
        Block b;

        for (int i = 0; i < this.arraysize[0]; i++) {
            for (int j = 0; j < this.arraysize[1]; j++) {
                for (int k = 0; k < this.arraysize[2]; k++) {
                    final int curpos = i + this.arraysize[0] * j + this.arraysize[0] * this.arraysize[1] * k;
                    switch (this.pivot) {
                    case 180:
                        b = this.clampY(this.pastepoint[0] - this.offsetpoint[0] - i, this.pastepoint[1] + this.offsetpoint[1] + j, this.pastepoint[2]
                                - this.offsetpoint[2] - k);
                        break;
                    case 270:
                        b = this.clampY(this.pastepoint[0] + this.offsetpoint[2] + k, this.pastepoint[1] + this.offsetpoint[1] + j, this.pastepoint[2]
                                - this.offsetpoint[0] - i);
                        break;
                    case 90:
                        b = this.clampY(this.pastepoint[0] - this.offsetpoint[2] - k, this.pastepoint[1] + this.offsetpoint[1] + j, this.pastepoint[2]
                                + this.offsetpoint[0] + i);
                        break;
                    default: // assume no rotation
                        b = this.clampY(this.pastepoint[0] + this.offsetpoint[0] + i, this.pastepoint[1] + this.offsetpoint[1] + j, this.pastepoint[2]
                                + this.offsetpoint[2] + k);
                        break;
                    }

                    if (!(this.blockarray[curpos] == 0 && this.airmode == false)) {
                        if (b.getTypeId() != this.blockarray[curpos] || b.getData() != this.dataarray[curpos]) {
                            h.put(b);
                        }
                        b.setTypeIdAndData(this.blockarray[curpos], this.dataarray[curpos], true);
                    }
                }
            }
        }
        v.sendMessage(ChatColor.AQUA + "" + this.numblocks + " blocks pasted."); // at (" +pastepoint[0]+", "+pastepoint[1]+", "+pastepoint[2]+")"

        v.storeUndo(h);
    }

    @Override
    public final int getTimesUsed() {
        return CopyPasta.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.custom(ChatColor.GOLD + "Paste air: " + this.airmode);
        vm.custom(ChatColor.GOLD + "Pivot angle: " + this.pivot);
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.SnipeData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "CopyPasta Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b cp air -- toggle include (default) or exclude  air during paste");
            v.sendMessage(ChatColor.AQUA + "/b cp 0|90|180|270 -- toggle rotation (0 default)");
            return;
        }

        if (par[1].equalsIgnoreCase("air")) {
            if (this.airmode) {
                this.airmode = false;

            } else {
                this.airmode = true;
            }

            v.sendMessage(ChatColor.GOLD + "Paste air: " + this.airmode);
            return;
        }

        if (par[1].equalsIgnoreCase("90") || par[1].equalsIgnoreCase("180") || par[1].equalsIgnoreCase("270") || par[1].equalsIgnoreCase("0")) {
            this.pivot = Integer.parseInt(par[1]);
            v.sendMessage(ChatColor.GOLD + "Pivot angle: " + this.pivot);
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        CopyPasta.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        switch (this.points) {
        case 0:
            this.firstpoint[0] = this.getTargetBlock().getX();
            this.firstpoint[1] = this.getTargetBlock().getY();
            this.firstpoint[2] = this.getTargetBlock().getZ();
            v.sendMessage(ChatColor.GRAY + "First point");
            this.points = 1;
            break;
        case 1:
            this.secondpoint[0] = this.getTargetBlock().getX();
            this.secondpoint[1] = this.getTargetBlock().getY();
            this.secondpoint[2] = this.getTargetBlock().getZ();
            v.sendMessage(ChatColor.GRAY + "Second point");
            this.points = 2;
            break;
        default:
            this.firstpoint = new int[3];
            this.secondpoint = new int[3];
            this.numblocks = 0;
            this.blockarray = new int[1];
            this.dataarray = new byte[1];
            this.points = 0;
            v.sendMessage(ChatColor.GRAY + "Points cleared.");
            break;
        }
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        if (this.points == 2) {
            if (this.numblocks == 0) {
                this.docopy(v);
            } else if (this.numblocks > 0 && this.numblocks < this.blocklimit) {
                this.pastepoint[0] = this.getTargetBlock().getX();
                this.pastepoint[1] = this.getTargetBlock().getY();
                this.pastepoint[2] = this.getTargetBlock().getZ();
                this.dopasta(v);
            } else {
                v.sendMessage(ChatColor.RED + "C");
            }
        } else {
            v.sendMessage(ChatColor.RED + "You must select exactly two points.");
        }
    }
}
