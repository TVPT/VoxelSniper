package com.thevoxelbox.voxelsniper.brush;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;

/**
 * @author MikeMatrix
 * 
 */
public class BlockResetBrush extends Brush {

    private static final ArrayList<Material> DENIED_UPDATES = new ArrayList<Material>();

    static {
        BlockResetBrush.DENIED_UPDATES.add(Material.SIGN);
        BlockResetBrush.DENIED_UPDATES.add(Material.SIGN_POST);
        BlockResetBrush.DENIED_UPDATES.add(Material.WALL_SIGN);
        BlockResetBrush.DENIED_UPDATES.add(Material.CHEST);
        BlockResetBrush.DENIED_UPDATES.add(Material.FURNACE);
        BlockResetBrush.DENIED_UPDATES.add(Material.BURNING_FURNACE);
        BlockResetBrush.DENIED_UPDATES.add(Material.REDSTONE_TORCH_OFF);
        BlockResetBrush.DENIED_UPDATES.add(Material.REDSTONE_TORCH_ON);
        BlockResetBrush.DENIED_UPDATES.add(Material.REDSTONE_WIRE);
        BlockResetBrush.DENIED_UPDATES.add(Material.DIODE_BLOCK_OFF);
        BlockResetBrush.DENIED_UPDATES.add(Material.DIODE_BLOCK_ON);
        BlockResetBrush.DENIED_UPDATES.add(Material.WOODEN_DOOR);
        BlockResetBrush.DENIED_UPDATES.add(Material.WOOD_DOOR);
        BlockResetBrush.DENIED_UPDATES.add(Material.IRON_DOOR);
        BlockResetBrush.DENIED_UPDATES.add(Material.IRON_DOOR_BLOCK);
        BlockResetBrush.DENIED_UPDATES.add(Material.FENCE_GATE);
    }

    private static int timesUsed = 0;

    /**
     * 
     */
    public BlockResetBrush() {
        this.setName("Block Reset Brush");
    }

    @Override
    public final int getTimesUsed() {
        return BlockResetBrush.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushName(this.getName());
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        BlockResetBrush.timesUsed = tUsed;
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
                    final Block _block = this.getWorld().getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z);
                    if (BlockResetBrush.DENIED_UPDATES.contains(_block.getType())) {
                        continue;
                    }
                    final byte _oldData = _block.getData();
                    _block.setTypeIdAndData(_block.getTypeId(), (byte) ((_block.getData() + 1) & 0xf), true);
                    _block.setTypeIdAndData(_block.getTypeId(), _oldData, true);
                }
            }
        }
    }

    @Override
    protected final void powder(final vData v) {
        this.arrow(v);
    }
}
