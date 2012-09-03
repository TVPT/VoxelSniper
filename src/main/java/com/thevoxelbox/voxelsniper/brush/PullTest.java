package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.vMessage;

/**
 * 
 * @author Piotr
 */
public class PullTest extends SoftSelection {

    protected int vh;
    protected sBlock[] sel;

    private static int timesUsed = 0;

    public PullTest() {
        this.setName("Soft Selection");
    }

    @Override
    public final int getTimesUsed() {
        return PullTest.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.height();
        vm.custom(ChatColor.AQUA + "Pinch " + (-this.c1 + 1));
        vm.custom(ChatColor.AQUA + "Bubble " + this.c2);
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        try {
            final double pinch = Double.parseDouble(par[1]);
            final double bubble = Double.parseDouble(par[2]);
            this.c1 = 1 - pinch;
            this.c2 = bubble;
        } catch (final Exception ex) {
            v.sendMessage(ChatColor.RED + "Invalid brush parameters!");
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        PullTest.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.vh = v.voxelHeight;
        this.getSurface(v);

        if (this.vh > 0) {
            for (final sBlock b : this.surface) {
                this.setBlock(b);
                // s.getBlockAt(b.x,(int) (b.y + (v.voxelHeight * b.str)), b.z).setTypeId(b.id);
            }
        } else if (this.vh < 0) {
            for (final sBlock b : this.surface) {
                this.setBlockDown(b);
                // s.getBlockAt(b.x,(int) (b.y + (v.voxelHeight * b.str)), b.z).setTypeId(b.id);
            }
        }
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        final int bsize = v.brushSize;
        // sel = new sBlock[(int)Math.pow(((bsize*2) + 1), 3)];

        this.vh = v.voxelHeight;

        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        this.surface.clear();

        int lasty;
        int newy;
        int laststr;
        double str;
        final double bpow = Math.pow(bsize + 0.5, 2);

        int id;

        // Are we pulling up ?
        if (this.vh > 0) {

            // Z - Axis
            for (int z = -bsize; z <= bsize; z++) {

                final int zpow = z * z;
                final int zz = this.getBlockPositionZ() + z;

                // X - Axis
                for (int x = -bsize; x <= bsize; x++) {

                    final int xpow = x * x;
                    final int xx = this.getBlockPositionX() + x;

                    // Down the Y - Axis
                    for (int y = bsize; y >= -bsize; y--) {

                        final double pow = zpow + xpow + (y * y);

                        // Is this in the range of the brush?
                        if (pow <= bpow && this.getWorld().getBlockTypeIdAt(xx, this.getBlockPositionY() + y, zz) != 0) {

                            int yy = this.getBlockPositionY() + y;

                            // Starting strength and new Position
                            str = this.getStr(pow / bpow);
                            laststr = (int) (this.vh * str);
                            lasty = yy + laststr;

                            this.clampY(xx, lasty, zz).setTypeId(this.getWorld().getBlockTypeIdAt(xx, yy, zz));

                            if (str == 1) {
                                str = 0.8;
                            }

                            while (laststr > 0) {
                                if (yy < this.getBlockPositionY()) {
                                    str = str * str;
                                }
                                laststr = (int) (this.vh * str);
                                newy = yy + laststr;
                                id = this.getWorld().getBlockTypeIdAt(xx, yy, zz);
                                for (int i = newy; i < lasty; i++) {
                                    this.clampY(xx, i, zz).setTypeId(id);
                                }
                                lasty = newy;
                                yy--;
                            }
                            break;
                        }
                    }

                    // double pow = (Math.pow(x, 2) + zpow);
                    // if (pow <= bpow) {
                    //
                    // int xx = blockPositionX + x;
                    //
                    // for (int y = max; y >= low; y--) {
                    // if (world.getBlockTypeIdAt(xx, y, zz) != 0) {
                    //
                    // //lasty = y + (int) (vh * getStr(pow / bpow));
                    // clampY(xx, y + (int) (vh * getStr(pow / bpow)), zz).setTypeId(world.getBlockTypeIdAt(xx, y, zz));
                    // y--;
                    //
                    // while (y >= low) {
                    // //lasty = y + (int) (vh * getStr(pow / bpow));
                    // clampY(xx, y + (int) (vh * getStr(pow / bpow)), zz).setTypeId(world.getBlockTypeIdAt(xx, y, zz));
                    // y--;
                    // }
                    // break;
                    // }
                    // }
                    // }

                    //
                    // for (int y = bsize; y >= -bsize; y--) {
                    // double pow = (xpow + Math.pow(y, 2) + zpow);
                    // if (pow <= bpow && world.getBlockTypeIdAt(xx, blockPositionY + y, zz) != 0) {
                    // int byy = blockPositionY + y;
                    // lasty = byy + (int) (vh * getStr(pow / bpow));
                    // clampY(xx, lasty, zz).setTypeId(world.getBlockTypeIdAt(xx, byy, zz));
                    // y--;
                    // pow = (xpow + Math.pow(y, 2) + zpow);
                    // while (pow <= bpow) {
                    // int blY = blockPositionY + y + (int) (vh * getStr(pow / bpow));
                    // int blId = world.getBlockTypeIdAt(xx, blockPositionY + y, zz);
                    // for (int i = blY; i < lasty; i++) {
                    // clampY(xx, i, zz).setTypeId(blId);
                    // }
                    // lasty = blY;
                    // y--;
                    // pow = (xpow + Math.pow(y, 2) + zpow);
                    // }
                    // break;
                    // }
                    // }
                }
            }
        } else {
            // double bpow = Math.pow(bsize, 2);
            for (int z = -bsize; z <= bsize; z++) {
                final double zpow = Math.pow(z, 2);
                final int zz = this.getBlockPositionZ() + z;
                for (int x = -bsize; x <= bsize; x++) {
                    final double xpow = Math.pow(x, 2);
                    final int xx = this.getBlockPositionX() + x;
                    for (int y = -bsize; y <= bsize; y++) {
                        double pow = (xpow + Math.pow(y, 2) + zpow);
                        if (pow <= bpow && this.getWorld().getBlockTypeIdAt(xx, this.getBlockPositionY() + y, zz) != 0) {
                            final int byy = this.getBlockPositionY() + y;
                            // int firsty = byy + (int) (vh * getStr(pow / bpow));
                            lasty = byy + (int) (this.vh * this.getStr(pow / bpow));
                            this.clampY(xx, lasty, zz).setTypeId(this.getWorld().getBlockTypeIdAt(xx, byy, zz));
                            y++;
                            pow = (xpow + Math.pow(y, 2) + zpow);
                            while (pow <= bpow) {
                                final int blY = this.getBlockPositionY() + y + (int) (this.vh * this.getStr(pow / bpow));
                                final int blId = this.getWorld().getBlockTypeIdAt(xx, this.getBlockPositionY() + y, zz);
                                for (int i = blY; i < lasty; i++) {
                                    this.clampY(xx, i, zz).setTypeId(blId);
                                }
                                lasty = blY;
                                y++;
                                pow = (xpow + Math.pow(y, 2) + zpow);
                            }
                            // for(int ii = firsty + 1; ii < )
                            break;
                        }
                    }
                }
            }
        }
    }

    protected final void setBlock(final sBlock b) {
        final Block bl = this.clampY(b.x, b.y + (int) (this.vh * b.str), b.z);
        if (this.getBlockIdAt(b.x, b.y - 1, b.z) == 0) {
            bl.setTypeId(b.id);
            bl.setData(b.d);
            for (int y = b.y; y < bl.getY(); y++) {
                this.setBlockIdAt(0, b.x, y, b.z);
            }
        } else {
            bl.setTypeId(b.id);
            bl.setData(b.d);
            for (int y = b.y - 1; y < bl.getY(); y++) {
                final Block blo = this.clampY(b.x, y, b.z);
                blo.setTypeId(b.id);
                blo.setData(b.d);
            }
        }
    }

    protected final void setBlockDown(final sBlock b) {
        final Block bl = this.clampY(b.x, b.y + (int) (this.vh * b.str), b.z);
        // if (getBlockIdAt(b.x, b.y - 1, b.z) == 0) {
        // bl.setTypeId(b.id);
        // bl.setData(b.d);
        // for (int y = b.y; y > bl.getY(); y--) {
        // Block blo = clampY(b.x, y, b.z);
        // blo.setTypeId(b.id);
        // blo.setData(b.d);
        // }
        // } else {
        bl.setTypeId(b.id);
        bl.setData(b.d);
        for (int y = b.y; y > bl.getY(); y--) {
            this.setBlockIdAt(0, b.x, y, b.z);
        }
        // }
    }
}
