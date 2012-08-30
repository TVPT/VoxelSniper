package com.thevoxelbox.voxelsniper.brush;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;

/**
 * @author GavJenks, adapted from MikeMatrxi original
 * This brush only looks for solid blocks, and then changes those plus any air blocks touching them.
 * If it works, this brush should be faster than the original by an amount proportional to the 
 *  volume of a snipe selection area / the  number of blocks touching air in the selection. 
 * This is because every solid block surrounded by others should take equally long to check and not change
 * as it would take MC to change them and then check and find no lighting to update.  For air blocks
 * surrounded by other air blocks, this brush saves about 80-100 checks by not updating them or their lighting.
 * And for air blocks touching solids, this brush is slower, because it replaces the air once per solid block it is
 * touching.  I assume on average this is about 2 blocks.  So every air block touching a solid negates one air block
 * floating in air.  Thus, for selections that have more air blocks surrounded by air than air blocks touching solids,
 * this brush will be faster, which is almost always the case, especially for undeveloped terrain and for larger brush sizes 
 * (unlike the original brush, this should only slow down by the square of the brush size, not the cube of the brush size). For
 * typical terrain, by my calculations, overall speed increase is about a factor of 5-6 for a size 20 brush.  For a complicated city or
 * ship, etc., this may be only a factor of about 2.  In a hypothetical worst case scenario of a 3d checkerboard 
 * of stone and air every other block, this brush should only be about 1.5x slower than the original brush.  Savings increase for larger brushes.
 * 
 * 
 */
public class BlockResetBrushSurface extends Brush {
	 private static int timesUsed = 0;

    private static final ArrayList<Material> DENIED_UPDATES = new ArrayList<Material>();

    static {
        DENIED_UPDATES.add(Material.SIGN);
        DENIED_UPDATES.add(Material.SIGN_POST);
        DENIED_UPDATES.add(Material.WALL_SIGN);
        DENIED_UPDATES.add(Material.CHEST);
        DENIED_UPDATES.add(Material.FURNACE);
        DENIED_UPDATES.add(Material.BURNING_FURNACE);
        DENIED_UPDATES.add(Material.REDSTONE_TORCH_OFF);
        DENIED_UPDATES.add(Material.REDSTONE_TORCH_ON);
        DENIED_UPDATES.add(Material.REDSTONE_WIRE);
        DENIED_UPDATES.add(Material.DIODE_BLOCK_OFF);
        DENIED_UPDATES.add(Material.DIODE_BLOCK_ON);
        DENIED_UPDATES.add(Material.WOODEN_DOOR);
        DENIED_UPDATES.add(Material.WOOD_DOOR);
        DENIED_UPDATES.add(Material.IRON_DOOR);
        DENIED_UPDATES.add(Material.IRON_DOOR_BLOCK);
        DENIED_UPDATES.add(Material.FENCE_GATE);
        DENIED_UPDATES.add(Material.AIR);
    }

    /**
     *
     */
    public BlockResetBrushSurface() {
        this.name = "Block Reset Brush Surface Only";
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.name);
    }

    @Override
    protected final void arrow(final vData v) {
        w = tb.getWorld();
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();

        for (int _z = -v.brushSize; _z <= v.brushSize; _z++) {
            for (int _x = -v.brushSize; _x <= v.brushSize; _x++) {
                for (int _y = -v.brushSize; _y <= v.brushSize; _y++) {
                    Block _block = w.getBlockAt(bx + _x, by + _y, bz + _z);
                    if (DENIED_UPDATES.contains(_block.getType())) {
                        continue;
                    }
                    byte _oldData;
                    boolean airFound = false;
                    if (w.getBlockAt(bx + _x + 1, by + _y, bz + _z).getTypeId() == 0) {
                        _block = w.getBlockAt(bx + _x + 1, by + _y, bz + _z);
                        _oldData = _block.getData();
                        _block.setTypeIdAndData(_block.getTypeId(), (byte) ((_block.getData() + 1) & 0xf), true);
                        _block.setTypeIdAndData(_block.getTypeId(), _oldData, true);
                        airFound = true;
                    }
                    if (w.getBlockAt(bx + _x - 1, by + _y, bz + _z).getTypeId() == 0 ) {
                        _block = w.getBlockAt(bx + _x - 1, by + _y, bz + _z);
                        _oldData = _block.getData();
                        _block.setTypeIdAndData(_block.getTypeId(), (byte) ((_block.getData() + 1) & 0xf), true);
                        _block.setTypeIdAndData(_block.getTypeId(), _oldData, true);
                        airFound = true;
                    }
                    if (w.getBlockAt(bx + _x, by + _y + 1, bz + _z).getTypeId() == 0 ) {
                        _block = w.getBlockAt(bx + _x, by + _y + 1, bz + _z);
                        _oldData = _block.getData();
                        _block.setTypeIdAndData(_block.getTypeId(), (byte) ((_block.getData() + 1) & 0xf), true);
                        _block.setTypeIdAndData(_block.getTypeId(), _oldData, true);
                        airFound = true;
                    }
                    if (w.getBlockAt(bx + _x, by + _y - 1, bz + _z).getTypeId() == 0) {
                        _block = w.getBlockAt(bx + _x, by + _y - 1, bz + _z);
                        _oldData = _block.getData();
                        _block.setTypeIdAndData(_block.getTypeId(), (byte) ((_block.getData() + 1) & 0xf), true);
                        _block.setTypeIdAndData(_block.getTypeId(), _oldData, true);
                        airFound = true;
                    }
                    if (w.getBlockAt(bx + _x, by + _y, bz + _z + 1).getTypeId() == 0 ) {
                        _block = w.getBlockAt(bx + _x, by + _y, bz + _z + 1);
                        _oldData = _block.getData();
                        _block.setTypeIdAndData(_block.getTypeId(), (byte) ((_block.getData() + 1) & 0xf), true);
                        _block.setTypeIdAndData(_block.getTypeId(), _oldData, true);
                        airFound = true;
                    }
                    if (w.getBlockAt(bx + _x, by + _y, bz + _z - 1).getTypeId() == 0 ) {
                        _block = w.getBlockAt(bx + _x, by + _y, bz + _z - 1);
                        _oldData = _block.getData();
                        _block.setTypeIdAndData(_block.getTypeId(), (byte) ((_block.getData() + 1) & 0xf), true);
                        _block.setTypeIdAndData(_block.getTypeId(), _oldData, true);
                        airFound = true;
                    }
                    if (airFound) {
                        _block = w.getBlockAt(bx + _x, by + _y, bz + _z);
                        _oldData = _block.getData();
                        _block.setTypeIdAndData(_block.getTypeId(), (byte) ((_block.getData() + 1) & 0xf), true);
                        _block.setTypeIdAndData(_block.getTypeId(), _oldData, true);
                    }
                }
            }
        }
    }

    @Override
    protected final void powder(final vData v) {
        this.arrow(v);
    }
    
    @Override
	public int getTimesUsed() {
		return timesUsed;
	}

	@Override
	public void setTimesUsed(int tUsed) {
		timesUsed = tUsed; 
	}
}
