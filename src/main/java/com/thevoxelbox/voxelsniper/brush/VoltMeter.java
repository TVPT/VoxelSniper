/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;
import org.bukkit.block.*;

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
    public void arrow(vSniper v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        volt(v);
    }

    @Override
    public void powder(vSniper v) {
        data(v);
    }

    @Override
    public void info(vMessage vm) {
            vm.brushName(name);
            vm.brushMessage("Right click with arrow to see if blocks/faces are powered. Powder measures wire current.");
    }

    
    public void volt(vSniper v) {
        //int bId = v.voxelId;
        int x = tb.getX();
        int y = tb.getY();
        int z = tb.getZ();


        Block block = clampY(x, y, z);
        boolean indirect = block.isBlockIndirectlyPowered();
        boolean direct = block.isBlockPowered();
        v.p.sendMessage(ChatColor.AQUA + "Direct Power? " + direct + " Indirect Power? " + indirect);
        v.p.sendMessage(ChatColor.BLUE + "Top Direct? " + block.isBlockFacePowered(BlockFace.UP) + " Top Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.UP));
        v.p.sendMessage(ChatColor.BLUE + "Bottom Direct? " + block.isBlockFacePowered(BlockFace.DOWN) + " Bottom Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.DOWN));
        v.p.sendMessage(ChatColor.BLUE + "East Direct? " + block.isBlockFacePowered(BlockFace.EAST) + " East Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.EAST));
        v.p.sendMessage(ChatColor.BLUE + "West Direct? " + block.isBlockFacePowered(BlockFace.WEST) + " West Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.WEST));
        v.p.sendMessage(ChatColor.BLUE + "North Direct? " + block.isBlockFacePowered(BlockFace.NORTH) + " North Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.NORTH));
        v.p.sendMessage(ChatColor.BLUE + "South Direct? " + block.isBlockFacePowered(BlockFace.SOUTH) + " South Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.SOUTH));

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
        //v.p.sendMessage(ChatColor.RED + "Sign line 2:" + string);

        //blockMat.Redstone();

        // newBlockMat = Material(76);
        //block.Material(76);
        //block.setType(newBlockMat); //block class

    }
    public void data(vSniper v){
        //int bId = v.voxelId;
        int x = tb.getX();
        int y = tb.getY();
        int z = tb.getZ();


        Block block = clampY(x, y, z);
        byte data = block.getData();
        v.p.sendMessage(ChatColor.AQUA + "Blocks until repeater needed: " + data);
    }

}
