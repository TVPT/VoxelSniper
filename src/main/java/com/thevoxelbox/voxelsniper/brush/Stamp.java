package com.thevoxelbox.voxelsniper.brush;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * 
 * @author Voxel
 * 
 *         Would it be possible to make this a performer brush, so people can use the Inclusion and Exclusion performers? -psa
 */
public class Stamp extends Brush {

    protected class cBlock {

        public int id;
        public int x;
        public int y;
        public int z;
        public byte d;

        public cBlock(final Block b, final int blx, final int bly, final int blz) {
            this.id = b.getTypeId();
            this.d = b.getData();
            this.x = blx;
            this.y = bly;
            this.z = blz;
        }

        private boolean holdsData(final int da) {
            switch (da) {

            case (54):
                return true;

            default:
                return false;
            }
        }
    }

    protected HashSet<cBlock> clone = new HashSet<cBlock>();
    protected HashSet<cBlock> fall = new HashSet<cBlock>();
    protected HashSet<cBlock> drop = new HashSet<cBlock>();
    protected HashSet<cBlock> solid = new HashSet<cBlock>();
    protected vUndo h;
    protected boolean sorted = false;

    protected byte stamp = 0;

    private static int timesUsed = 0;

    public Stamp() {
        this.setName("Stamp");
    }

    @Override
    public int getTimesUsed() {
        return Stamp.timesUsed;
    }

    @Override
    public void info(final vMessage vm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public final void reSort() {
        this.sorted = false;
    }

    @Override
    public void setTimesUsed(final int tUsed) {
        Stamp.timesUsed = tUsed;
    }

    @Override
    protected void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        switch (this.stamp) {
        case 0:
            this.stamp(v);
            break;

        case 1:
            this.stampNoAir(v);
            break;

        case 2:
            this.stampFill(v);
            break;

        default:
            v.sendMessage(ChatColor.DARK_RED + "Error while stamping! Report");
            break;
        }
    }

    protected final boolean falling(final int id) {
        if (id > 7 && id < 14) {
            return true;
        } else {
            return false;
        }
    }

    protected final boolean fallsOff(final int id) {
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
            return true; // Fire drops off of blocks?
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
            return true; // Added Wall signs

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
            return true; // Does snow drop snowballs when destroyed blockPositionY the game instead of the player?

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

    @Override
    protected void powder(final com.thevoxelbox.voxelsniper.vData v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected final void setBlock(final cBlock cb) {
        final Block b = this.clampY(this.getBlockPositionX() + cb.x, this.getBlockPositionY() + cb.y, this.getBlockPositionZ() + cb.z);
        this.h.put(b);
        b.setTypeId(cb.id);
        b.setData(cb.d);
    }

    protected final void setBlockFill(final cBlock cb) {
        final Block b = this.clampY(this.getBlockPositionX() + cb.x, this.getBlockPositionY() + cb.y, this.getBlockPositionZ() + cb.z);
        if (b.getTypeId() == 0) {
            this.h.put(b);
            b.setTypeId(cb.id);
            b.setData(cb.d);
        }
    }

    protected final void setStamp(final byte by) {
        this.stamp = by;
    }

    protected final void stamp(final vData v) {

        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY() + v.cCen);
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        this.h = new vUndo(this.getTargetBlock().getWorld().getName());

        if (this.sorted) {
            for (final cBlock cb : this.solid) {
                this.setBlock(cb);
            }
            for (final cBlock cb : this.drop) {
                this.setBlock(cb);
            }
            for (final cBlock cb : this.fall) {
                this.setBlock(cb);
            }
        } else {
            this.fall.clear();
            this.drop.clear();
            this.solid.clear();
            for (final cBlock cb : this.clone) {
                if (this.fallsOff(cb.id)) {
                    this.fall.add(cb);
                } else if (this.falling(cb.id)) {
                    this.drop.add(cb);
                } else {
                    this.solid.add(cb);
                    this.setBlock(cb);
                }
            }
            for (final cBlock cb : this.drop) {
                this.setBlock(cb);
            }
            for (final cBlock cb : this.fall) {
                this.setBlock(cb);
            }
            this.sorted = true;
        }

        v.storeUndo(this.h);
    }

    protected final void stampFill(final vData v) {

        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY() + v.cCen);
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        this.h = new vUndo(this.getTargetBlock().getWorld().getName());

        if (this.sorted) {
            for (final cBlock cb : this.solid) {
                this.setBlockFill(cb);
            }
            for (final cBlock cb : this.drop) {
                this.setBlockFill(cb);
            }
            for (final cBlock cb : this.fall) {
                this.setBlockFill(cb);
            }
        } else {
            this.fall.clear();
            this.drop.clear();
            this.solid.clear();
            for (final cBlock cb : this.clone) {
                if (this.fallsOff(cb.id)) {
                    this.fall.add(cb);
                } else if (this.falling(cb.id)) {
                    this.drop.add(cb);
                } else if (cb.id != 0) {
                    this.solid.add(cb);
                    this.setBlockFill(cb);
                }
            }
            for (final cBlock cb : this.drop) {
                this.setBlockFill(cb);
            }
            for (final cBlock cb : this.fall) {
                this.setBlockFill(cb);
            }
            this.sorted = true;
        }

        v.storeUndo(this.h);
    }

    protected final void stampNoAir(final vData v) {

        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY() + v.cCen);
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        this.h = new vUndo(this.getTargetBlock().getWorld().getName());

        if (this.sorted) {
            for (final cBlock cb : this.solid) {
                this.setBlock(cb);
            }
            for (final cBlock cb : this.drop) {
                this.setBlock(cb);
            }
            for (final cBlock cb : this.fall) {
                this.setBlock(cb);
            }
        } else {
            this.fall.clear();
            this.drop.clear();
            this.solid.clear();
            for (final cBlock cb : this.clone) {
                if (this.fallsOff(cb.id)) {
                    this.fall.add(cb);
                } else if (this.falling(cb.id)) {
                    this.drop.add(cb);
                } else if (cb.id != 0) {
                    this.solid.add(cb);
                    this.setBlock(cb);
                }
            }
            for (final cBlock cb : this.drop) {
                this.setBlock(cb);
            }
            for (final cBlock cb : this.fall) {
                this.setBlock(cb);
            }
            this.sorted = true;
        }

        v.storeUndo(this.h);
    }
}
