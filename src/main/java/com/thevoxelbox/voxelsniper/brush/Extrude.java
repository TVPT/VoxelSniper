package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.undo.vUndo;

/**
 * 
 * @author psanker
 */
public class Extrude extends Brush {

    int level;
    double trueCircle;
    boolean awto;

    private static int timesUsed = 0;

    public Extrude() {
        this.setName("Extrude");
    }

    @Override
    public final int getTimesUsed() {
        return Extrude.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.height();
        vm.voxelList();
        if (this.trueCircle == 0.5) {
            vm.custom(ChatColor.AQUA + "True circle mode ON.");
        } else {
            vm.custom(ChatColor.AQUA + "True circle mode OFF.");
        }
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Extrude brush Parameters:");
            v.sendMessage(ChatColor.AQUA
                    + "/b ex true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b ex false will switch back. (false is default)");
            return;
        }

        for (int i = 1; i < par.length; i++) {
            try {
                if (par[i].startsWith("true")) {
                    this.trueCircle = 0.5;
                    v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
                    continue;
                } else if (par[i].startsWith("false")) {
                    this.trueCircle = 0;
                    v.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
                    continue;
                } else {
                    v.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
                    return;
                }
            } catch (final Exception e) {
                v.sendMessage(ChatColor.RED + "Incorrect parameter \"" + par[i] + "\"; use the \"info\" parameter.");
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Extrude.timesUsed = tUsed;
    }

    private void extrudeD(final vData v) {
        final int bsize = v.brushSize;

        vUndo h = new vUndo(this.getTargetBlock().getWorld().getName());

        final double bpow = Math.pow(bsize + this.trueCircle, 2);
        for (int x = bsize; x >= 0; x--) {
            final double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                    if (this.awto) {
                        for (int i = 0; i <= this.level - 1; i++) {
                            h = this.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - i, this.getBlockPositionZ() + y), this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - i - 1, this.getBlockPositionZ() + y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - y), this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - i - 1, this.getBlockPositionZ() - y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - i, this.getBlockPositionZ() + y), this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - i - 1, this.getBlockPositionZ() + y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - i, this.getBlockPositionZ() - y), this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - i - 1, this.getBlockPositionZ() - y), v, h);
                        }
                    } else {
                        for (int i = 0; i >= this.level + 1; i--) {
                            h = this.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - i, this.getBlockPositionZ() + y), this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - i + 1, this.getBlockPositionZ() + y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - i, this.getBlockPositionZ() - y), this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - i + 1, this.getBlockPositionZ() - y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - i, this.getBlockPositionZ() + y), this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - i + 1, this.getBlockPositionZ() + y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - i, this.getBlockPositionZ() - y), this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - i + 1, this.getBlockPositionZ() - y), v, h);
                        }
                    }
                }
            }
        }

        v.storeUndo(h);
    }

    private void extrudeE(final vData v) {
        final int bsize = v.brushSize;

        vUndo h = new vUndo(this.getTargetBlock().getWorld().getName());

        final double bpow = Math.pow(bsize + this.trueCircle, 2);
        for (int x = bsize; x >= 0; x--) {
            final double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                    if (this.awto) {
                        for (int i = 0; i <= this.level - 1; i++) {
                            h = this.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() - i), this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() - i - 1), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ() - i), this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ() - i - 1), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() - i), this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() - i - 1), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ() - i), this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ() - i - 1), v, h);
                        }
                    } else {
                        for (int i = 0; i >= this.level + 1; i--) {
                            h = this.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() - i), this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() - i + 1), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ() - i), this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ() - i + 1), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() - i), this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() - i + 1), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ() - i), this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ() - i + 1), v, h);
                        }
                    }
                }
            }
        }

        v.storeUndo(h);
    }

    private void extrudeN(final vData v) {
        final int bsize = v.brushSize;

        vUndo h = new vUndo(this.getTargetBlock().getWorld().getName());

        final double bpow = Math.pow(bsize + this.trueCircle, 2);
        for (int x = bsize; x >= 0; x--) {
            final double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                    if (this.awto) {
                        for (int i = 0; i <= this.level - 1; i++) {
                            h = this.perform(this.clampY(this.getBlockPositionX() - i, this.getBlockPositionY() + x, this.getBlockPositionZ() + y), this.clampY(this.getBlockPositionX() - i - 1, this.getBlockPositionY() + x, this.getBlockPositionZ() + y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - i, this.getBlockPositionY() + x, this.getBlockPositionZ() - y), this.clampY(this.getBlockPositionX() - i - 1, this.getBlockPositionY() + x, this.getBlockPositionZ() - y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - i, this.getBlockPositionY() - x, this.getBlockPositionZ() + y), this.clampY(this.getBlockPositionX() - i - 1, this.getBlockPositionY() - x, this.getBlockPositionZ() + y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - i, this.getBlockPositionY() - x, this.getBlockPositionZ() - y), this.clampY(this.getBlockPositionX() - i - 1, this.getBlockPositionY() - x, this.getBlockPositionZ() - y), v, h);
                        }
                    } else {
                        for (int i = 0; i >= this.level + 1; i--) {
                            h = this.perform(this.clampY(this.getBlockPositionX() - i, this.getBlockPositionY() + x, this.getBlockPositionZ() + y), this.clampY(this.getBlockPositionX() - i + 1, this.getBlockPositionY() + x, this.getBlockPositionZ() + y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - i, this.getBlockPositionY() + x, this.getBlockPositionZ() - y), this.clampY(this.getBlockPositionX() - i + 1, this.getBlockPositionY() + x, this.getBlockPositionZ() - y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - i, this.getBlockPositionY() - x, this.getBlockPositionZ() + y), this.clampY(this.getBlockPositionX() - i + 1, this.getBlockPositionY() - x, this.getBlockPositionZ() + y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - i, this.getBlockPositionY() - x, this.getBlockPositionZ() - y), this.clampY(this.getBlockPositionX() - i + 1, this.getBlockPositionY() - x, this.getBlockPositionZ() - y), v, h);
                        }
                    }
                }
            }
        }

        v.storeUndo(h);
    }

    private void extrudeS(final vData v) {
        final int bsize = v.brushSize;

        vUndo h = new vUndo(this.getTargetBlock().getWorld().getName());

        final double bpow = Math.pow(bsize + this.trueCircle, 2);
        for (int x = bsize; x >= 0; x--) {
            final double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                    if (this.awto) {
                        for (int i = 0; i <= this.level - 1; i++) {
                            h = this.perform(this.clampY(this.getBlockPositionX() + i, this.getBlockPositionY() + x, this.getBlockPositionZ() + y), this.clampY(this.getBlockPositionX() + i + 1, this.getBlockPositionY() + x, this.getBlockPositionZ() + y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() + i, this.getBlockPositionY() + x, this.getBlockPositionZ() - y), this.clampY(this.getBlockPositionX() + i + 1, this.getBlockPositionY() + x, this.getBlockPositionZ() - y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() + i, this.getBlockPositionY() - x, this.getBlockPositionZ() + y), this.clampY(this.getBlockPositionX() + i + 1, this.getBlockPositionY() - x, this.getBlockPositionZ() + y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() + i, this.getBlockPositionY() - x, this.getBlockPositionZ() - y), this.clampY(this.getBlockPositionX() + i + 1, this.getBlockPositionY() - x, this.getBlockPositionZ() - y), v, h);
                        }
                    } else {
                        for (int i = 0; i >= this.level + 1; i--) {
                            h = this.perform(this.clampY(this.getBlockPositionX() + i, this.getBlockPositionY() + x, this.getBlockPositionZ() + y), this.clampY(this.getBlockPositionX() + i - 1, this.getBlockPositionY() + x, this.getBlockPositionZ() + y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() + i, this.getBlockPositionY() + x, this.getBlockPositionZ() - y), this.clampY(this.getBlockPositionX() + i - 1, this.getBlockPositionY() + x, this.getBlockPositionZ() - y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() + i, this.getBlockPositionY() - x, this.getBlockPositionZ() + y), this.clampY(this.getBlockPositionX() + i - 1, this.getBlockPositionY() - x, this.getBlockPositionZ() + y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() + i, this.getBlockPositionY() - x, this.getBlockPositionZ() - y), this.clampY(this.getBlockPositionX() + i - 1, this.getBlockPositionY() - x, this.getBlockPositionZ() - y), v, h);
                        }
                    }
                }
            }
        }

        v.storeUndo(h);
    }

    private void extrudeU(final vData v) {
        final int bsize = v.brushSize;

        vUndo h = new vUndo(this.getTargetBlock().getWorld().getName());

        final double bpow = Math.pow(bsize + this.trueCircle, 2);
        for (int x = bsize; x >= 0; x--) {
            final double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                    if (this.awto) {
                        for (int i = 0; i <= this.level - 1; i++) {
                            h = this.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + i, this.getBlockPositionZ() + y), this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + i + 1, this.getBlockPositionZ() + y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - y), this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + i + 1, this.getBlockPositionZ() - y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + i, this.getBlockPositionZ() + y), this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + i + 1, this.getBlockPositionZ() + y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + i, this.getBlockPositionZ() - y), this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + i + 1, this.getBlockPositionZ() - y), v, h);
                        }
                    } else {
                        for (int i = 0; i >= this.level + 1; i--) {
                            h = this.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + i, this.getBlockPositionZ() + y), this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + i - 1, this.getBlockPositionZ() + y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + i, this.getBlockPositionZ() - y), this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + i - 1, this.getBlockPositionZ() - y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + i, this.getBlockPositionZ() + y), this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + i - 1, this.getBlockPositionZ() + y), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + i, this.getBlockPositionZ() - y), this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + i - 1, this.getBlockPositionZ() - y), v, h);
                        }
                    }
                }
            }
        }

        v.storeUndo(h);
    }

    private void extrudeW(final vData v) {
        final int bsize = v.brushSize;

        vUndo h = new vUndo(this.getTargetBlock().getWorld().getName());

        final double bpow = Math.pow(bsize + this.trueCircle, 2);
        for (int x = bsize; x >= 0; x--) {
            final double xpow = Math.pow(x, 2);
            for (int y = bsize; y >= 0; y--) {
                if ((xpow + Math.pow(y, 2)) <= bpow) {
                    if (this.awto) {
                        for (int i = 0; i <= this.level - 1; i++) {
                            h = this.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() + i), this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() + i + 1), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ() + i), this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ() + i + 1), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() + i), this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() + i + 1), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ() + i), this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ() + i + 1), v, h);
                        }
                    } else {
                        for (int i = 0; i >= this.level + 1; i--) {
                            h = this.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() + i), this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() + y, this.getBlockPositionZ() + i - 1), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ() + i), this.clampY(this.getBlockPositionX() + x, this.getBlockPositionY() - y, this.getBlockPositionZ() + i - 1), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() + i), this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() + y, this.getBlockPositionZ() + i - 1), v, h);
                            h = this.perform(this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ() + i), this.clampY(this.getBlockPositionX() - x, this.getBlockPositionY() - y, this.getBlockPositionZ() + i - 1), v, h);
                        }
                    }
                }
            }
        }

        v.storeUndo(h);
    }

    private vUndo perform(final Block b1, final Block b2, final vData v, final vUndo h) {
        if ((b2.getY() > 128) || (b2.getY() < 0)) {
            return h;
        }

        if (v.voxelList.contains(this.getBlockIdAt(b1.getX(), b1.getY(), b1.getZ()))) {
            h.put(b2);
            this.setBlockIdAt(this.getBlockIdAt(b1.getX(), b1.getY(), b1.getZ()), b2.getX(), b2.getY(), b2.getZ());
            this.clampY(b2.getX(), b2.getY(), b2.getZ()).setData(this.clampY(b1.getX(), b1.getY(), b1.getZ()).getData());
        }

        return h;
    }

    private void pre(final vData v, final BlockFace bf) {
        if (bf == null) {
            return;
        }

        this.level = v.voxelHeight;

        if (this.level == 0) {
            return;
        } else if (!this.awto) {
            if (this.level > 0) {
                this.level = -1 * this.level;
            }
        } else if (this.awto) {
            if (this.level < 0) {
                this.level = -1 * this.level;
            }
        }

        switch (bf) {
        case NORTH:
            this.extrudeN(v);
            break;

        case SOUTH:
            this.extrudeS(v);
            break;

        case EAST:
            this.extrudeE(v);
            break;

        case WEST:
            this.extrudeW(v);
            break;

        case UP:
            this.extrudeU(v);
            break;

        case DOWN:
            this.extrudeD(v);
            break;

        default:
            break;
        }
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.awto = false;

        this.pre(v, this.getTargetBlock().getFace(this.getLastBlock()));
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.awto = true;

        this.pre(v, this.getTargetBlock().getFace(this.getLastBlock()));
    }
}
