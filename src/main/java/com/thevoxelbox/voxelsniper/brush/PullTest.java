/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 *
 * @author Piotr
 */
public class PullTest extends SoftSelection {

    protected int vh;
    protected sBlock[] sel;

    protected void setBlock(sBlock b) {
        Block bl = clampY(b.x, b.y + (int) (vh * b.str), b.z);
        if (getBlockIdAt(b.x, b.y - 1, b.z) == 0) {
            bl.setTypeId(b.id);
            bl.setData(b.d);
            for (int y = b.y; y < bl.getY(); y++) {
                this.setBlockIdAt(0, b.x, y, b.z);
            }
        } else {
            bl.setTypeId(b.id);
            bl.setData(b.d);
            for (int y = b.y - 1; y < bl.getY(); y++) {
                Block blo = clampY(b.x, y, b.z);
                blo.setTypeId(b.id);
                blo.setData(b.d);
            }
        }
    }

    protected void setBlockDown(sBlock b) {
        Block bl = clampY(b.x, b.y + (int) (vh * b.str), b.z);
//        if (getBlockIdAt(b.x, b.y - 1, b.z) == 0) {
//            bl.setTypeId(b.id);
//            bl.setData(b.d);
//            for (int y = b.y; y > bl.getY(); y--) {
//                Block blo = clampY(b.x, y, b.z);
//                blo.setTypeId(b.id);
//                blo.setData(b.d);
//            }
//        } else {
        bl.setTypeId(b.id);
        bl.setData(b.d);
        for (int y = b.y; y > bl.getY(); y--) {
            this.setBlockIdAt(0, b.x, y, b.z);
        }
//        }
    }

    public PullTest() {
        name = "Soft Selection";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        vh = v.voxelHeight;
        getSurface(v);

        if (vh > 0) {
            for (sBlock b : surface) {
                setBlock(b);
                //s.getBlockAt(b.x,(int) (b.y + (v.voxelHeight * b.str)), b.z).setTypeId(b.id);
            }
        } else if (vh < 0) {
            for (sBlock b : surface) {
                setBlockDown(b);
                //s.getBlockAt(b.x,(int) (b.y + (v.voxelHeight * b.str)), b.z).setTypeId(b.id);
            }
        }
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        int bsize = v.brushSize;
        // sel = new sBlock[(int)Math.pow(((bsize*2) + 1), 3)];

        vh = v.voxelHeight;

        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();

        surface.clear();

        int lasty;
        int newy;
        int laststr;
        double str;
        double bpow = Math.pow(bsize + 0.5, 2);

        int id;

        // Are we pulling up ?
        if (vh > 0) {

            // Z - Axis
            for (int z = -bsize; z <= bsize; z++) {

                int zpow = z * z;
                int zz = bz + z;

                // X - Axis
                for (int x = -bsize; x <= bsize; x++) {

                    int xpow = x * x;
                    int xx = bx + x;

                    // Down the Y - Axis
                    for (int y = bsize; y >= -bsize; y--) {

                        double pow = zpow + xpow + (y * y);

                        // Is this in the range of the brush?
                        if (pow <= bpow && w.getBlockTypeIdAt(xx, by + y, zz) != 0) {

                            int yy = by + y;

                            // Starting strength and new Position
                            str = getStr(pow / bpow);
                            laststr = (int) (vh * str);
                            lasty = yy + laststr;

                            clampY(xx, lasty, zz).setTypeId(w.getBlockTypeIdAt(xx, yy, zz));

                            if (str == 1) {
                                str = 0.8;
                            }

                            while (laststr > 0) {
                                if (yy < by) {
                                    str = str * str;
                                }
                                laststr = (int) (vh * str);
                                newy = yy + laststr;
                                id = w.getBlockTypeIdAt(xx, yy, zz);
                                for (int i = newy; i < lasty; i++) {
                                    clampY(xx, i, zz).setTypeId(id);
                                }
                                lasty = newy;
                                yy--;
                            }
                            break;
                        }
                    }

//                    double pow = (Math.pow(x, 2) + zpow);
//                    if (pow <= bpow) {
//
//                        int xx = bx + x;
//
//                        for (int y = max; y >= low; y--) {
//                            if (w.getBlockTypeIdAt(xx, y, zz) != 0) {
//
//                                //lasty = y + (int) (vh * getStr(pow / bpow));
//                                clampY(xx, y + (int) (vh * getStr(pow / bpow)), zz).setTypeId(w.getBlockTypeIdAt(xx, y, zz));
//                                y--;
//
//                                while (y >= low) {
//                                    //lasty = y + (int) (vh * getStr(pow / bpow));
//                                    clampY(xx, y + (int) (vh * getStr(pow / bpow)), zz).setTypeId(w.getBlockTypeIdAt(xx, y, zz));
//                                    y--;
//                                }
//                                break;
//                            }
//                        }
//                    }



//
//                    for (int y = bsize; y >= -bsize; y--) {
//                        double pow = (xpow + Math.pow(y, 2) + zpow);
//                        if (pow <= bpow && w.getBlockTypeIdAt(xx, by + y, zz) != 0) {
//                            int byy = by + y;
//                            lasty = byy + (int) (vh * getStr(pow / bpow));
//                            clampY(xx, lasty, zz).setTypeId(w.getBlockTypeIdAt(xx, byy, zz));
//                            y--;
//                            pow = (xpow + Math.pow(y, 2) + zpow);
//                            while (pow <= bpow) {
//                                int blY = by + y + (int) (vh * getStr(pow / bpow));
//                                int blId = w.getBlockTypeIdAt(xx, by + y, zz);
//                                for (int i = blY; i < lasty; i++) {
//                                    clampY(xx, i, zz).setTypeId(blId);
//                                }
//                                lasty = blY;
//                                y--;
//                                pow = (xpow + Math.pow(y, 2) + zpow);
//                            }
//                            break;
//                        }
//                    }
                }
            }
        } else {
            // double bpow = Math.pow(bsize, 2);
            for (int z = -bsize; z <= bsize; z++) {
                double zpow = Math.pow(z, 2);
                int zz = bz + z;
                for (int x = -bsize; x <= bsize; x++) {
                    double xpow = Math.pow(x, 2);
                    int xx = bx + x;
                    for (int y = -bsize; y <= bsize; y++) {
                        double pow = (xpow + Math.pow(y, 2) + zpow);
                        if (pow <= bpow && w.getBlockTypeIdAt(xx, by + y, zz) != 0) {
                            int byy = by + y;
                            //int firsty = byy + (int) (vh * getStr(pow / bpow));
                            lasty = byy + (int) (vh * getStr(pow / bpow));
                            clampY(xx, lasty, zz).setTypeId(w.getBlockTypeIdAt(xx, byy, zz));
                            y++;
                            pow = (xpow + Math.pow(y, 2) + zpow);
                            while (pow <= bpow) {
                                int blY = by + y + (int) (vh * getStr(pow / bpow));
                                int blId = w.getBlockTypeIdAt(xx, by + y, zz);
                                for (int i = blY; i < lasty; i++) {
                                    clampY(xx, i, zz).setTypeId(blId);
                                }
                                lasty = blY;
                                y++;
                                pow = (xpow + Math.pow(y, 2) + zpow);
                            }
                            //for(int ii = firsty + 1; ii  < )
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
        vm.height();
        vm.custom(ChatColor.AQUA + "Pinch " + (-c1 + 1));
        vm.custom(ChatColor.AQUA + "Bubble " + c2);
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        try {
            double pinch = Double.parseDouble(par[1]);
            double bubble = Double.parseDouble(par[2]);
            c1 = 1 - pinch;
            c2 = bubble;
        } catch (Exception ex) {
            v.sendMessage(ChatColor.RED + "Invalid brush parameters!");
        }
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
