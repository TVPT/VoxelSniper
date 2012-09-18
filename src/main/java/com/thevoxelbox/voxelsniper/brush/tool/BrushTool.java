/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.tool;

import com.thevoxelbox.voxelsniper.brush.IBrush;
import com.thevoxelbox.voxelsniper.brush.SnipeBrush;
import com.thevoxelbox.voxelsniper.brush.perform.Performer;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Sniper;
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

    protected IBrush brush = new SnipeBrush();
    public SnipeData data;

    public BrushTool(Sniper owner) {
        data = new SnipeData(owner);
        data.setVoxelMessage(new Message(data));
    }

    public void setBrush(IBrush br) {
        brush = br;
    }

    public void setPerformer(String[] args) {
        if (brush instanceof Performer) {
            ((Performer) brush).parse(args, data);
        } else {
            data.getVoxelMessage().custom(ChatColor.GOLD + "This brush is not a Performer brush!");
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
        brush.info(data.getVoxelMessage());
        if (brush instanceof Performer) {
            ((Performer) brush).showInfo(data.getVoxelMessage());
        }
    }

    public boolean snipe(Player playr, Action action, Material itemInHand, Block clickedBlock, BlockFace clickedFace) {
        return brush.perform(action, data, playr.isSneaking() ? Material.SULPHUR : Material.ARROW, clickedBlock, clickedFace);
    }
}
