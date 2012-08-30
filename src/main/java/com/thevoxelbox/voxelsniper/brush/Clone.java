package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;

/**
 * The Clone class is used to create a collection of blocks in a cylinder
 * shape according to the selection the player has set
 *
 * @author Voxel
 */
public class Clone extends Stamp {

    /**
     * The starring Y position
     * At the bottom of the cylinder
     */
    protected int st;
    /**
     * End Y position
     * At the top of the cylinder
     */
    protected int en;

    public Clone() {
        name = "Clone";
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        clone(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
        vm.height();
        vm.center();
        switch (stamp) {
            case 0:
                vm.brushMessage("Default Stamp");
                break;

            case 1:
                vm.brushMessage("No-Air Stamp");
                break;

            case 2:
                vm.brushMessage("Fill Stamp");
                break;

            default:
                vm.custom(ChatColor.DARK_RED + "Error while stamping! Report");
                break;
        }
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Clone / Stamp Cylinder brush parameters");
            v.sendMessage(ChatColor.GREEN + "cs f -- Activates Fill mode");
            v.sendMessage(ChatColor.GREEN + "cs a -- Activates No-Air mode");
            v.sendMessage(ChatColor.GREEN + "cs d -- Activates Default mode");
        }
        if (par[1].equalsIgnoreCase("a")) {
            setStamp((byte) 1);
            reSort();
            v.sendMessage(ChatColor.AQUA + "No-Air stamp brush");
        } else if (par[1].equalsIgnoreCase("f")) {
            setStamp((byte) 2);
            reSort();
            v.sendMessage(ChatColor.AQUA + "Fill stamp brush");
        } else if (par[1].equalsIgnoreCase("d")) {
            setStamp((byte) 0);
            reSort();
            v.sendMessage(ChatColor.AQUA + "Default stamp brush");
        } else if (par[1].startsWith("c")) {
            v.cCen = Integer.parseInt(par[1].replace("c", ""));
            v.sendMessage(ChatColor.BLUE + "Center set to " + v.cCen);
        }
    }

    /**
     * The clone method is used to grab a snapshot of the selected area
     * dictated by tb.x y z v.brushSize v.voxelHeight and v.cCen
     *
     * x y z -- initial center of the selection
     * v.brushSize -- the radius of the cylinder
     * v.voxelHeight -- the heigth of the cylinder
     * c.cCen -- the offset on the Y axis of the selection ( bottom of the
     * cylinder ) as by:
     * Bottom_Y = tb.y + v.cCen;
     *
     * @param v the caller
     */
    protected void clone(vData v) {
        this.clone.clear();
        fall.clear();
        drop.clear();
        solid.clear();
        int bsize = v.brushSize;
        sorted = false;

        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();

        st = by + v.cCen;
        en = by + v.voxelHeight + v.cCen;
        if (st < 0) {
            st = 0;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
        } else if (st > 127) {
            st = 127;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
        }
        if (en < 0) {
            en = 0;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
        } else if (en > 127) {
            en = 127;
            v.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
        }
        double bpow = Math.pow(bsize, 2);
        for (int z = st; z < en; z++) {
            clone.add(new cBlock(clampY(bx, z, bz), 0, z - st, 0));
            for (int y2 = 1; y2 <= bsize; y2++) {
                clone.add(new cBlock(clampY(bx, z, bz + y2), 0, z - st, y2));
                clone.add(new cBlock(clampY(bx, z, bz - y2), 0, z - st, -y2));
                clone.add(new cBlock(clampY(bx + y2, z, bz), y2, z - st, 0));
                clone.add(new cBlock(clampY(bx - y2, z, bz), -y2, z - st, 0));
            }
            for (int x = 1; x <= bsize; x++) {
                double xpow = Math.pow(x, 2);
                for (int y = 1; y <= bsize; y++) {
                    if ((xpow + Math.pow(y, 2)) <= bpow) {
                        clone.add(new cBlock(clampY(bx + x, z, bz + y), x, z - st, y));
                        clone.add(new cBlock(clampY(bx + x, z, bz - y), x, z - st, -y));
                        clone.add(new cBlock(clampY(bx - x, z, bz + y), -x, z - st, y));
                        clone.add(new cBlock(clampY(bx - x, z, bz - y), -x, z - st, -y));
                    }
                }
            }
        }
        v.sendMessage("" + ChatColor.GREEN + clone.size() + ChatColor.AQUA + " blocks copied sucessfully.");
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
