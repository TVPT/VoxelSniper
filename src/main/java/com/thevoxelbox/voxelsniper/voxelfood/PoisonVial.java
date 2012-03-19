package com.thevoxelbox.voxelsniper.voxelfood;

/**
 *
 * @author Razorcane
 */

import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PoisonVial extends Food {

    @Override
    protected boolean rightAir(Player p, ItemStack inHand, Block clicked) {
        Random rand = new Random();
        int random = rand.nextInt(19);
        int blockamnt = inHand.getAmount();
        
        if(random == 10){
            p.sendMessage(ChatColor.GRAY + "You got lucky this time, punk.");
            if(blockamnt > 1){
                inHand.setAmount(--blockamnt);
            } else {
                p.getInventory().setItemInHand(null);
            }
        }
        else {
            p.getInventory().clear();
            p.chat(ChatColor.GRAY + "Goodbye, cruel world!");
            p.setHealth(0);
        }
        return false;
    }

    @Override
    protected boolean rightBlock(Player p, ItemStack inHand, Block clicked) {
	rightAir(p, inHand, clicked);
        return true;
    }

    @Override
    protected boolean leftAir(Player p, ItemStack inHand, Block clicked) {
	rightAir(p, inHand, clicked);
        return false;
    }

    @Override
    protected boolean leftBlock(Player p, ItemStack inHand, Block clicked) {
	rightAir(p, inHand, clicked);
        return true;
    }

    @Override
    protected boolean pressure(Player p, ItemStack inHand, Block clicked) {
	rightAir(p, inHand, clicked);
        return true;
    }
    
}
