/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 *
 * @author giltwist
 */
// THIS BRUSH SHOULD NOT USE PERFORMERS
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
    protected int pivot = 0; //ccw degrees
    protected int blocklimit = 10000;

    public CopyPasta() {
        name = "CopyPasta";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        switch (points) {
            case 0:
                firstpoint[0] = tb.getX();
                firstpoint[1] = tb.getY();
                firstpoint[2] = tb.getZ();
                v.sendMessage(ChatColor.GRAY + "First point");
                points = 1;
                break;
            case 1:
                secondpoint[0] = tb.getX();
                secondpoint[1] = tb.getY();
                secondpoint[2] = tb.getZ();
                v.sendMessage(ChatColor.GRAY + "Second point");
                points = 2;
                break;
            default:
                firstpoint = new int[3];
                secondpoint = new int[3];
                numblocks = 0;
                blockarray = new int[1];
                dataarray = new byte[1];
                points = 0;
                v.sendMessage(ChatColor.GRAY + "Points cleared.");
                break;
        }
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        if (points == 2) {
            if (numblocks == 0) {
                docopy(v);
            } else if (numblocks > 0 && numblocks < blocklimit) {
                pastepoint[0] = tb.getX();
                pastepoint[1] = tb.getY();
                pastepoint[2] = tb.getZ();
                dopasta(v);
            } else {
                v.sendMessage(ChatColor.RED + "C");
            }
        } else {
            v.sendMessage(ChatColor.RED + "You must select exactly two points.");
        }
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.custom(ChatColor.GOLD + "Paste air: " + airmode);
        vm.custom(ChatColor.GOLD + "Pivot angle: " + pivot);
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "CopyPasta Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b cp air -- toggle include (default) or exclude  air during paste");
            v.sendMessage(ChatColor.AQUA + "/b cp 0|90|180|270 -- toggle rotation (0 default)");
            return;
        }

        if (par[1].equalsIgnoreCase("air")) {
            if (airmode) {
                airmode = false;

            } else {
                airmode = true;
            }

            v.sendMessage(ChatColor.GOLD + "Paste air: " + airmode);
            return;
        }

        if (par[1].equalsIgnoreCase("90") || par[1].equalsIgnoreCase("180") || par[1].equalsIgnoreCase("270") || par[1].equalsIgnoreCase("0")) {
            pivot = Integer.parseInt(par[1]);
            v.sendMessage(ChatColor.GOLD + "Pivot angle: " + pivot);
        }
    }

    public void docopy(vData v) {
        w = v.owner().getPlayer().getWorld();
        for (int i = 0; i < 3; i++) {
            arraysize[i] = Math.abs(firstpoint[i] - secondpoint[i]) + 1;
            minpoint[i] = Math.min(firstpoint[i], secondpoint[i]);
            offsetpoint[i] = minpoint[i] - firstpoint[i];  //will always be negative or zero
        }
        numblocks = (arraysize[0]) * (arraysize[1]) * (arraysize[2]);
        if (numblocks > 0 && numblocks < blocklimit) {
            blockarray = new int[numblocks];
            dataarray = new byte[numblocks];

            for (int i = 0; i < arraysize[0]; i++) {
                for (int j = 0; j < arraysize[1]; j++) {
                    for (int k = 0; k < arraysize[2]; k++) {
                        int curpos = i + arraysize[0] * j + arraysize[0] * arraysize[1] * k;
                        blockarray[curpos] = w.getBlockTypeIdAt(minpoint[0] + i, minpoint[1] + j, minpoint[2] + k);
                        dataarray[curpos] = clampY(minpoint[0] + i, minpoint[1] + j, minpoint[2] + k).getData();
                    }
                }
            }

            v.sendMessage(ChatColor.AQUA + "" + numblocks + " blocks copied.");
        } else {
            v.sendMessage(ChatColor.RED + "Copy area too big: " + numblocks + "(Limit: " + blocklimit + ")");
        }
    }

    public void dopasta(vData v) {
        w = v.owner().getPlayer().getWorld();
        vUndo h = new vUndo(tb.getWorld().getName());
        Block b;

        for (int i = 0; i < arraysize[0]; i++) {
            for (int j = 0; j < arraysize[1]; j++) {
                for (int k = 0; k < arraysize[2]; k++) {
                    int curpos = i + arraysize[0] * j + arraysize[0] * arraysize[1] * k;
                    switch (pivot) {
                        case 180:
                            b = clampY(pastepoint[0] - offsetpoint[0] - i, pastepoint[1] + offsetpoint[1] + j, pastepoint[2] - offsetpoint[2] - k);
                            break;
                        case 270:
                            b = clampY(pastepoint[0] + offsetpoint[2] + k, pastepoint[1] + offsetpoint[1] + j, pastepoint[2] - offsetpoint[0] - i);
                            break;
                        case 90:
                            b = clampY(pastepoint[0] - offsetpoint[2] - k, pastepoint[1] + offsetpoint[1] + j, pastepoint[2] + offsetpoint[0] + i);
                            break;
                        default: // assume no rotation
                            b = clampY(pastepoint[0] + offsetpoint[0] + i, pastepoint[1] + offsetpoint[1] + j, pastepoint[2] + offsetpoint[2] + k);
                            break;
                    }

                    if (!(blockarray[curpos] == 0 && airmode == false)) {
                        if (b.getTypeId() != blockarray[curpos] || b.getData() != dataarray[curpos]) {
                            h.put(b);
                        }
                        b.setTypeIdAndData(blockarray[curpos], dataarray[curpos], true);
                    }
                }
            }
        }
        v.sendMessage(ChatColor.AQUA + "" + numblocks + " blocks pasted."); //at (" +pastepoint[0]+", "+pastepoint[1]+", "+pastepoint[2]+")"

        v.storeUndo(h);
    }
    
    private static int timesUsed = 0;
	
    @Override
	public int getTimesUsed() {
		return timesUsed;
	}

	@Override
	public void setTimesUsed(int tUsed) {
		timesUsed = tUsed; 
	}
}
