/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 *
 * @author Gavjenks
 */
public class VoltMeter extends Brush {

    protected boolean first = true;
    protected double[] coords = new double[3];

    public VoltMeter() {
        name = "VoltMeter";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        volt(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        data(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.brushMessage("Right click with arrow to see if blocks/faces are powered. Powder measures wire current.");
    }

    public void volt(vData v) {
        int bId = v.voxelId;
        int x = tb.getX();
        int y = tb.getY();
        int z = tb.getZ();


        Block block = clampY(x, y, z);
        boolean indirect = block.isBlockIndirectlyPowered();
        boolean direct = block.isBlockPowered();
        v.sendMessage(ChatColor.AQUA + "Direct Power? " + direct + " Indirect Power? " + indirect);
        v.sendMessage(ChatColor.BLUE + "Top Direct? " + block.isBlockFacePowered(BlockFace.UP) + " Top Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.UP));
        v.sendMessage(ChatColor.BLUE + "Bottom Direct? " + block.isBlockFacePowered(BlockFace.DOWN) + " Bottom Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.DOWN));
        v.sendMessage(ChatColor.BLUE + "East Direct? " + block.isBlockFacePowered(BlockFace.EAST) + " East Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.EAST));
        v.sendMessage(ChatColor.BLUE + "West Direct? " + block.isBlockFacePowered(BlockFace.WEST) + " West Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.WEST));
        v.sendMessage(ChatColor.BLUE + "North Direct? " + block.isBlockFacePowered(BlockFace.NORTH) + " North Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.NORTH));
        v.sendMessage(ChatColor.BLUE + "South Direct? " + block.isBlockFacePowered(BlockFace.SOUTH) + " South Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.SOUTH));

// Set the block to a sign
        //block.setType(Material.SIGN_POST);
        //block.setData((byte) 0x06);

// Set the metadata to a torch
        //block.getState().setData(new org.bukkit.material.Sign(Material.SIGN_POST));
        //block.getState().setData(new org.bukkit.material.Sign(Material.REDSTONE_TORCH_ON));

// Get the metadata
        //Sign sign = (Sign) block.getState();

// Set the lines
        //sign.setLine(0, "line1");
        //sign.setLine(1, "line2");
        //sign.setLine(2, "line3");
        //sign.setLine(3, "line4");

// Send the update
        //sign.update();

//get lines
        //String string = sign.getLine(2);
        //v.sendMessage(ChatColor.RED + "Sign line 2:" + string);

        //blockMat.Redstone();

        // newBlockMat = Material(76);
        //block.Material(76);
        //block.setType(newBlockMat); //block class

    }

    public void data(vData v) {
        int bId = v.voxelId;
        int x = tb.getX();
        int y = tb.getY();
        int z = tb.getZ();


        Block block = clampY(x, y, z);
        byte data = block.getData();
        v.sendMessage(ChatColor.AQUA + "Blocks until repeater needed: " + data);
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
