package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Brush Interface.
 *
 */
public interface IBrush
{

    /**
     * @param vm Message object
     */
    void info(Message vm);

    /**
     * Handles parameters passed to brushes.
     *
     * @param par Array of string containing parameters
     * @param v   Snipe Data
     */
    void parameters(String[] par, SnipeData v);

    /**
     * Handle brush actions and pass through to other methods.
     *
     * @param action Click action performed
     * @param v Snipe Data
     * @param heldItem Item in hand
     * @param clickedBlock Block clicked
     * @param clickedFace Face clicked
     * @return True on success, false otherwise
     */
    boolean perform(Action action, SnipeData v, Material heldItem, Block clickedBlock, BlockFace clickedFace);

    /**
     * @return The name of the Brush
     */
    String getName();

    /**
     * @param name New name for the Brush
     */
    void setName(String name);

    /**
     * @return Times the brush has been used
     */
    int getTimesUsed();

    /**
     * @param timesUsed Set the amount of times the brush has been used
     */
    void setTimesUsed(int timesUsed);

    /**
     *
     */
    void updateScale();

    /**
     * @return The name of the category the brush is in.
     */
    String getBrushCategory();
}
