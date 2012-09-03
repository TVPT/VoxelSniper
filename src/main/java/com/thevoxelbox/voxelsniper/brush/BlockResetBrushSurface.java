package com.thevoxelbox.voxelsniper.brush;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;

/**
 * This brush only looks for solid blocks, and then changes those plus any air blocks touching them. If it works, this brush should be faster than the original
 * blockPositionY an amount proportional to the volume of a snipe selection area / the number of blocks touching air in the selection. This is because every solid block
 * surrounded blockPositionY others should take equally long to check and not change as it would take MC to change them and then check and find no lighting to update. For
 * air blocks surrounded blockPositionY other air blocks, this brush saves about 80-100 checks blockPositionY not updating them or their lighting. And for air blocks touching solids,
 * this brush is slower, because it replaces the air once per solid block it is touching. I assume on average this is about 2 blocks. So every air block
 * touching a solid negates one air block floating in air. Thus, for selections that have more air blocks surrounded blockPositionY air than air blocks touching solids,
 * this brush will be faster, which is almost always the case, especially for undeveloped terrain and for larger brush sizes (unlike the original brush, this
 * should only slow down blockPositionY the square of the brush size, not the cube of the brush size). For typical terrain, blockPositionY my calculations, overall speed increase is
 * about a factor of 5-6 for a size 20 brush. For a complicated city or ship, etc., this may be only a factor of about 2. In a hypothetical worst case scenario
 * of a 3d checkerboard of stone and air every other block, this brush should only be about 1.5x slower than the original brush. Savings increase for larger
 * brushes.
 * 
 * @author GavJenks
 */
public class BlockResetBrushSurface extends Brush {
    private static int timesUsed = 0;

    private static final ArrayList<Material> DENIED_UPDATES = new ArrayList<Material>();

    static {
        BlockResetBrushSurface.DENIED_UPDATES.add(Material.SIGN);
        BlockResetBrushSurface.DENIED_UPDATES.add(Material.SIGN_POST);
        BlockResetBrushSurface.DENIED_UPDATES.add(Material.WALL_SIGN);
        BlockResetBrushSurface.DENIED_UPDATES.add(Material.CHEST);
        BlockResetBrushSurface.DENIED_UPDATES.add(Material.FURNACE);
        BlockResetBrushSurface.DENIED_UPDATES.add(Material.BURNING_FURNACE);
        BlockResetBrushSurface.DENIED_UPDATES.add(Material.REDSTONE_TORCH_OFF);
        BlockResetBrushSurface.DENIED_UPDATES.add(Material.REDSTONE_TORCH_ON);
        BlockResetBrushSurface.DENIED_UPDATES.add(Material.REDSTONE_WIRE);
        BlockResetBrushSurface.DENIED_UPDATES.add(Material.DIODE_BLOCK_OFF);
        BlockResetBrushSurface.DENIED_UPDATES.add(Material.DIODE_BLOCK_ON);
        BlockResetBrushSurface.DENIED_UPDATES.add(Material.WOODEN_DOOR);
        BlockResetBrushSurface.DENIED_UPDATES.add(Material.WOOD_DOOR);
        BlockResetBrushSurface.DENIED_UPDATES.add(Material.IRON_DOOR);
        BlockResetBrushSurface.DENIED_UPDATES.add(Material.IRON_DOOR_BLOCK);
        BlockResetBrushSurface.DENIED_UPDATES.add(Material.FENCE_GATE);
        BlockResetBrushSurface.DENIED_UPDATES.add(Material.AIR);
    }

    /**
     *
     */
    public BlockResetBrushSurface() {
        this.setName("Block Reset Brush Surface Only");
    }

    @Override
    public final int getTimesUsed() {
        return BlockResetBrushSurface.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.getName());
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        BlockResetBrushSurface.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final vData v) {
        this.setWorld(this.getTargetBlock().getWorld());
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());

        for (int _z = -v.brushSize; _z <= v.brushSize; _z++) {
            for (int _x = -v.brushSize; _x <= v.brushSize; _x++) {
                for (int _y = -v.brushSize; _y <= v.brushSize; _y++) {
                    Block _block = this.getWorld().getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z);
                    if (BlockResetBrushSurface.DENIED_UPDATES.contains(_block.getType())) {
                        continue;
                    }
                    byte _oldData;
                    boolean airFound = false;
                    if (this.getWorld().getBlockAt(this.getBlockPositionX() + _x + 1, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z).getTypeId() == 0) {
                        _block = this.getWorld().getBlockAt(this.getBlockPositionX() + _x + 1, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z);
                        _oldData = _block.getData();
                        _block.setTypeIdAndData(_block.getTypeId(), (byte) ((_block.getData() + 1) & 0xf), true);
                        _block.setTypeIdAndData(_block.getTypeId(), _oldData, true);
                        airFound = true;
                    }
                    if (this.getWorld().getBlockAt(this.getBlockPositionX() + _x - 1, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z).getTypeId() == 0) {
                        _block = this.getWorld().getBlockAt(this.getBlockPositionX() + _x - 1, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z);
                        _oldData = _block.getData();
                        _block.setTypeIdAndData(_block.getTypeId(), (byte) ((_block.getData() + 1) & 0xf), true);
                        _block.setTypeIdAndData(_block.getTypeId(), _oldData, true);
                        airFound = true;
                    }
                    if (this.getWorld().getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y + 1, this.getBlockPositionZ() + _z).getTypeId() == 0) {
                        _block = this.getWorld().getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y + 1, this.getBlockPositionZ() + _z);
                        _oldData = _block.getData();
                        _block.setTypeIdAndData(_block.getTypeId(), (byte) ((_block.getData() + 1) & 0xf), true);
                        _block.setTypeIdAndData(_block.getTypeId(), _oldData, true);
                        airFound = true;
                    }
                    if (this.getWorld().getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y - 1, this.getBlockPositionZ() + _z).getTypeId() == 0) {
                        _block = this.getWorld().getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y - 1, this.getBlockPositionZ() + _z);
                        _oldData = _block.getData();
                        _block.setTypeIdAndData(_block.getTypeId(), (byte) ((_block.getData() + 1) & 0xf), true);
                        _block.setTypeIdAndData(_block.getTypeId(), _oldData, true);
                        airFound = true;
                    }
                    if (this.getWorld().getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z + 1).getTypeId() == 0) {
                        _block = this.getWorld().getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z + 1);
                        _oldData = _block.getData();
                        _block.setTypeIdAndData(_block.getTypeId(), (byte) ((_block.getData() + 1) & 0xf), true);
                        _block.setTypeIdAndData(_block.getTypeId(), _oldData, true);
                        airFound = true;
                    }
                    if (this.getWorld().getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z - 1).getTypeId() == 0) {
                        _block = this.getWorld().getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z - 1);
                        _oldData = _block.getData();
                        _block.setTypeIdAndData(_block.getTypeId(), (byte) ((_block.getData() + 1) & 0xf), true);
                        _block.setTypeIdAndData(_block.getTypeId(), _oldData, true);
                        airFound = true;
                    }
                    if (airFound) {
                        _block = this.getWorld().getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z);
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
}
