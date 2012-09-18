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
public class VoltMeterBrush extends Brush {
    private static int timesUsed = 0;

    /**
     * 
     */
    public VoltMeterBrush() {
        this.setName("VoltMeter");
    }

    private final void data(final SnipeData v) {
        final Block _block = this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ());
        final byte _data = _block.getData();
        v.sendMessage(ChatColor.AQUA + "Blocks until repeater needed: " + _data);
    }

    private final void volt(final SnipeData v) {
    	final Block _block = this.clampY(this.getBlockPositionX(), this.getBlockPositionY(), this.getBlockPositionZ());
        final boolean _indirect = _block.isBlockIndirectlyPowered();
        final boolean _direct = _block.isBlockPowered();
        v.sendMessage(ChatColor.AQUA + "Direct Power? " + _direct + " Indirect Power? " + _indirect);
        v.sendMessage(ChatColor.BLUE + "Top Direct? " + _block.isBlockFacePowered(BlockFace.UP) + " Top Indirect? "
                + _block.isBlockFaceIndirectlyPowered(BlockFace.UP));
        v.sendMessage(ChatColor.BLUE + "Bottom Direct? " + _block.isBlockFacePowered(BlockFace.DOWN) + " Bottom Indirect? "
                + _block.isBlockFaceIndirectlyPowered(BlockFace.DOWN));
        v.sendMessage(ChatColor.BLUE + "East Direct? " + _block.isBlockFacePowered(BlockFace.EAST) + " East Indirect? "
                + _block.isBlockFaceIndirectlyPowered(BlockFace.EAST));
        v.sendMessage(ChatColor.BLUE + "West Direct? " + _block.isBlockFacePowered(BlockFace.WEST) + " West Indirect? "
                + _block.isBlockFaceIndirectlyPowered(BlockFace.WEST));
        v.sendMessage(ChatColor.BLUE + "North Direct? " + _block.isBlockFacePowered(BlockFace.NORTH) + " North Indirect? "
                + _block.isBlockFaceIndirectlyPowered(BlockFace.NORTH));
        v.sendMessage(ChatColor.BLUE + "South Direct? " + _block.isBlockFacePowered(BlockFace.SOUTH) + " South Indirect? "
                + _block.isBlockFaceIndirectlyPowered(BlockFace.SOUTH));
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.volt(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.data(v);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.brushMessage("Right click with arrow to see if blocks/faces are powered. Powder measures wire current.");
    }
    
    @Override
    public final int getTimesUsed() {
    	return VoltMeterBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        VoltMeterBrush.timesUsed = tUsed;
    }
}
