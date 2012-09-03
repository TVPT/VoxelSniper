package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;

/**
 * 
 * @author Gavjenks
 */
public class VoltMeter extends Brush {

    protected boolean first = true;
    protected double[] coords = new double[3];

    private static int timesUsed = 0;

    public VoltMeter() {
        this.setName("VoltMeter");
    }

    public final void data(final SnipeData v) {
        final int x = this.getTargetBlock().getX();
        final int y = this.getTargetBlock().getY();
        final int z = this.getTargetBlock().getZ();

        final Block block = this.clampY(x, y, z);
        final byte data = block.getData();
        v.sendMessage(ChatColor.AQUA + "Blocks until repeater needed: " + data);
    }

    @Override
    public final int getTimesUsed() {
        return VoltMeter.timesUsed;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.brushMessage("Right click with arrow to see if blocks/faces are powered. Powder measures wire current.");
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        VoltMeter.timesUsed = tUsed;
    }

    public final void volt(final SnipeData v) {
        final int x = this.getTargetBlock().getX();
        final int y = this.getTargetBlock().getY();
        final int z = this.getTargetBlock().getZ();

        final Block block = this.clampY(x, y, z);
        final boolean indirect = block.isBlockIndirectlyPowered();
        final boolean direct = block.isBlockPowered();
        v.sendMessage(ChatColor.AQUA + "Direct Power? " + direct + " Indirect Power? " + indirect);
        v.sendMessage(ChatColor.BLUE + "Top Direct? " + block.isBlockFacePowered(BlockFace.UP) + " Top Indirect? "
                + block.isBlockFaceIndirectlyPowered(BlockFace.UP));
        v.sendMessage(ChatColor.BLUE + "Bottom Direct? " + block.isBlockFacePowered(BlockFace.DOWN) + " Bottom Indirect? "
                + block.isBlockFaceIndirectlyPowered(BlockFace.DOWN));
        v.sendMessage(ChatColor.BLUE + "East Direct? " + block.isBlockFacePowered(BlockFace.EAST) + " East Indirect? "
                + block.isBlockFaceIndirectlyPowered(BlockFace.EAST));
        v.sendMessage(ChatColor.BLUE + "West Direct? " + block.isBlockFacePowered(BlockFace.WEST) + " West Indirect? "
                + block.isBlockFaceIndirectlyPowered(BlockFace.WEST));
        v.sendMessage(ChatColor.BLUE + "North Direct? " + block.isBlockFacePowered(BlockFace.NORTH) + " North Indirect? "
                + block.isBlockFaceIndirectlyPowered(BlockFace.NORTH));
        v.sendMessage(ChatColor.BLUE + "South Direct? " + block.isBlockFacePowered(BlockFace.SOUTH) + " South Indirect? "
                + block.isBlockFaceIndirectlyPowered(BlockFace.SOUTH));

        // Set the block to a sign
        // block.setType(Material.SIGN_POST);
        // block.setData((byte) 0x06);

        // Set the metadata to a torch
        // block.getState().setData(new org.bukkit.material.Sign(Material.SIGN_POST));
        // block.getState().setData(new org.bukkit.material.Sign(Material.REDSTONE_TORCH_ON));

        // Get the metadata
        // Sign sign = (Sign) block.getState();

        // Set the lines
        // sign.setLine(0, "line1");
        // sign.setLine(1, "line2");
        // sign.setLine(2, "line3");
        // sign.setLine(3, "line4");

        // Send the update
        // sign.update();

        // get lines
        // String string = sign.getLine(2);
        // v.sendMessage(ChatColor.RED + "Sign line 2:" + string);

        // blockMat.Redstone();

        // newBlockMat = Material(76);
        // block.Material(76);
        // block.setType(newBlockMat); //block class

    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.setBlockPositionX(this.getTargetBlock().getX());
        this.setBlockPositionY(this.getTargetBlock().getY());
        this.setBlockPositionZ(this.getTargetBlock().getZ());
        this.volt(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.SnipeData v) {
        this.data(v);
    }
}
