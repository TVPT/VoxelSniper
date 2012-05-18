/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.tool;

import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.Snipe;
import com.thevoxelbox.voxelsniper.brush.perform.Performer;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

/**
 *
 * @author Piotr
 */
public class BrushTool {

    protected Brush brush = new Snipe();
    public vData data;

    public BrushTool(vSniper owner) {
        data = new vData(owner);
        data.vm = new vMessage(data);
    }

    public void setBrush(Brush br) {
        brush = br;
    }

    public void setPerformer(String[] args) {
        if (brush instanceof Performer) {
            ((Performer) brush).parse(args, data);
        } else {
            data.vm.custom(ChatColor.GOLD + "This brush is not a Performer brush!");
        }
    }

    public void parse(String[] args) {
        if (brush instanceof Performer) {
            ((Performer) brush).parse(args, data);
        } else {
            brush.parameters(args, data);
        }
    }

    public void info() {
        brush.info(data.vm);
        if (brush instanceof Performer) {
            ((Performer) brush).showInfo(data.vm);
        }
    }

    public boolean snipe(Player playr, Action action, Material itemInHand, Block clickedBlock, BlockFace clickedFace) {
        return brush.perform(action, data, playr.isSneaking() ? Material.SULPHUR : Material.ARROW, clickedBlock, clickedFace);
    }
}
