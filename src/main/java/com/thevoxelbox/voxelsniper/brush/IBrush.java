package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;

/**
 * Brush Interface.
 * 
 * @author MikeMatrix
 */
public interface IBrush {

    /**
     * 
     * @param voxelMessage
     */
    void info(Message vm);

    /**
     * A Brush's custom command handler.
     * 
     * @param par
     *            Array of string containing parameters
     * @param v
     *            Sniper caller
     */
    void parameters(String[] par, SnipeData v);

    /**
     * 
     * @param action
     * @param v
     * @param heldItem
     * @param clickedBlock
     * @param clickedFace
     * @return boolean
     */
    boolean perform(Action action, SnipeData v, Material heldItem, Block clickedBlock, BlockFace clickedFace);

    /**
     * @return the name
     */
    String getName();

    /**
     * @return int
     */
    int getTimesUsed();
    
    /**
     * @param name
     *            the name to set
     */
    void setName(String name);

    /**
     * @param timesUsed
     */
    void setTimesUsed(int timesUsed);

    /**
     * 
     */
    void updateScale();

}
