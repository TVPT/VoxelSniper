/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import java.util.HashSet;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 *
 * @author Voxel
 *
 * Would it be possible to make this a performer brush, so people can use the Inclusion and Exclusion performers? -psa
 */
public class Stamp extends Brush {

    protected HashSet<cBlock> clone = new HashSet<cBlock>();
    protected HashSet<cBlock> fall = new HashSet<cBlock>();
    protected HashSet<cBlock> drop = new HashSet<cBlock>();
    protected HashSet<cBlock> solid = new HashSet<cBlock>();
    protected vUndo h;
    protected boolean sorted = false;
    protected byte stamp = 0;

    public void reSort() {
        sorted = false;
    }

    protected void setStamp(byte by) {
        stamp = by;
    }

    public Stamp() {
        name = "Stamp";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        switch (stamp) {
            case 0:
                stamp(v);
                break;

            case 1:
                stampNoAir(v);
                break;

            case 2:
                stampFill(v);
                break;

            default:
                v.sendMessage(ChatColor.DARK_RED + "Error while stamping! Report");
                break;
        }
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void info(vMessage vm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void setBlock(cBlock cb) {
        Block b = clampY(bx + cb.x, by + cb.y, bz + cb.z);
        h.put(b);
        b.setTypeId(cb.id);
        b.setData(cb.d);
    }

    protected void setBlockFill(cBlock cb) {
        Block b = clampY(bx + cb.x, by + cb.y, bz + cb.z);
        if (b.getTypeId() == 0) {
            h.put(b);
            b.setTypeId(cb.id);
            b.setData(cb.d);
        }
    }

    protected void stamp(vData v) {

        bx = tb.getX();
        by = tb.getY() + v.cCen;
        bz = tb.getZ();

        h = new vUndo(tb.getWorld().getName());

        if (sorted) {
            for (cBlock cb : solid) {
                setBlock(cb);
            }
            for (cBlock cb : drop) {
                setBlock(cb);
            }
            for (cBlock cb : fall) {
                setBlock(cb);
            }
        } else {
            fall.clear();
            drop.clear();
            solid.clear();
            for (cBlock cb : clone) {
                if (fallsOff(cb.id)) {
                    fall.add(cb);
                } else if (falling(cb.id)) {
                    drop.add(cb);
                } else {
                    solid.add(cb);
                    setBlock(cb);
                }
            }
            for (cBlock cb : drop) {
                setBlock(cb);
            }
            for (cBlock cb : fall) {
                setBlock(cb);
            }
            sorted = true;
        }

        v.storeUndo(h);
    }

    protected void stampNoAir(vData v) {

        bx = tb.getX();
        by = tb.getY() + v.cCen;
        bz = tb.getZ();

        h = new vUndo(tb.getWorld().getName());

        if (sorted) {
            for (cBlock cb : solid) {
                setBlock(cb);
            }
            for (cBlock cb : drop) {
                setBlock(cb);
            }
            for (cBlock cb : fall) {
                setBlock(cb);
            }
        } else {
            fall.clear();
            drop.clear();
            solid.clear();
            for (cBlock cb : clone) {
                if (fallsOff(cb.id)) {
                    fall.add(cb);
                } else if (falling(cb.id)) {
                    drop.add(cb);
                } else if (cb.id != 0) {
                    solid.add(cb);
                    setBlock(cb);
                }
            }
            for (cBlock cb : drop) {
                setBlock(cb);
            }
            for (cBlock cb : fall) {
                setBlock(cb);
            }
            sorted = true;
        }

        v.storeUndo(h);
    }

    protected void stampFill(vData v) {

        bx = tb.getX();
        by = tb.getY() + v.cCen;
        bz = tb.getZ();

        h = new vUndo(tb.getWorld().getName());

        if (sorted) {
            for (cBlock cb : solid) {
                setBlockFill(cb);
            }
            for (cBlock cb : drop) {
                setBlockFill(cb);
            }
            for (cBlock cb : fall) {
                setBlockFill(cb);
            }
        } else {
            fall.clear();
            drop.clear();
            solid.clear();
            for (cBlock cb : clone) {
                if (fallsOff(cb.id)) {
                    fall.add(cb);
                } else if (falling(cb.id)) {
                    drop.add(cb);
                } else if (cb.id != 0) {
                    solid.add(cb);
                    setBlockFill(cb);
                }
            }
            for (cBlock cb : drop) {
                setBlockFill(cb);
            }
            for (cBlock cb : fall) {
                setBlockFill(cb);
            }
            sorted = true;
        }

        v.storeUndo(h);
    }

    protected boolean fallsOff(int id) {
        switch (id) {
            // 6, 37, 38, 39, 40, 50, 51, 55, 59, 63, 64, 65, 66, 69, 70, 71, 72, 75, 76, 77, 83
            case (6):
                return true;

            case (37):
                return true;

            case (38):
                return true;

            case (39):
                return true;

            case (40):
                return true;

            case (50):
                return true;

            case (51):
                return true;        // Fire drops off of blocks?
            // No, but it will burn out if placed before the fuel under it -- prz
            case (55):
                return true;

            case (59):
                return true;

            case (63):
                return true;

            case (64):
                return true;

            case (65):
                return true;

            case (66):
                return true;

            case (68):
                return true;        // Added Wall signs

            case (69):
                return true;

            case (70):
                return true;

            case (71):
                return true;

            case (72):
                return true;

            case (75):
                return true;

            case (76):
                return true;

            case (77):
                return true;

            case (78):
                return true;        // Does snow drop snowballs when destroyed by the game instead of the player?

            case (83):
                return true;

            case (93):
                return true;

            case (94):
                return true;

            default:
                return false;
        }
    }

    protected boolean falling(int id) {
        if (id > 7 && id < 14) {
            return true;
        } else {
            return false;
        }
    }

    protected class cBlock {

        public int id;
        public int x;
        public int y;
        public int z;
        public byte d;

        public cBlock(Block b, int blx, int bly, int blz) {
            id = b.getTypeId();
            d = b.getData();
            x = blx;
            y = bly;
            z = blz;
        }

        private boolean holdsData(int da) {
            switch (da) {

                case (54):
                    return true;

                default:
                    return false;
            }
        }
    }
}
