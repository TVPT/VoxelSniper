/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.tool;

import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.Sneak;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

/**
 *
 * @author Piotr <przerwap@gmail.com>
 */
public class SneakBrushTool extends BrushTool {

    protected static final Brush sneak = new Sneak();
    protected boolean arrowMode = true;

    public SneakBrushTool(vSniper owner, boolean useArrow) {
        super(owner);
        arrowMode = useArrow;
    }

    @Override
    public boolean snipe(Player playr, Action action, Material itemInHand, Block clickedBlock, BlockFace clickedFace) {
        return playr.isSneaking()
                ? sneak.perform(action, data, Material.ARROW, clickedBlock, clickedFace)
                : brush.perform(action, data, arrowMode ? Material.ARROW : Material.SULPHUR, clickedBlock, clickedFace);
    }
}
