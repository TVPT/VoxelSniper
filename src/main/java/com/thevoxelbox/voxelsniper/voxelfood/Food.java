/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.voxelfood;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Voxel
 */
public abstract class Food {

    protected abstract boolean rightAir(Player p, ItemStack inHand, Block clicked);
    protected abstract boolean rightBlock(Player p, ItemStack inHand, Block clicked);
    protected abstract boolean leftAir(Player p, ItemStack inHand, Block clicked);
    protected abstract boolean leftBlock(Player p, ItemStack inHand, Block clicked);
    protected abstract boolean pressure(Player p, ItemStack inHand, Block clicked);

    public boolean perform(Action action, Player player, ItemStack heldItem, Block clickedBlock) {
        switch (action) {
            case RIGHT_CLICK_AIR:
                return rightAir(player, heldItem, clickedBlock);

            case RIGHT_CLICK_BLOCK:
                return rightBlock(player, heldItem, clickedBlock);

            case LEFT_CLICK_AIR:
                return leftAir(player, heldItem, clickedBlock);

            case LEFT_CLICK_BLOCK:
                return leftBlock(player, heldItem, clickedBlock);

            case PHYSICAL:
                return pressure(player, heldItem, clickedBlock);

            default:
                player.sendMessage(ChatColor.RED + "Something is not right. Report this to przerwap. (Perform Error)");
                return true;
        }
    }
}
