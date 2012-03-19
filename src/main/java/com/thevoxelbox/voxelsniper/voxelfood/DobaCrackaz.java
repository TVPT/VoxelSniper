package com.thevoxelbox.voxelsniper.voxelfood;

/**
 *
 * @author Razorcane
 */

import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DobaCrackaz extends Food{
    World world;
    
    @Override
    protected boolean rightAir(Player p, ItemStack inHand, Block clicked) {
        
	Random rand = new Random();
	int randmessage = rand.nextInt(4);
        int blockamnt = inHand.getAmount();
	
	switch(randmessage){
	    case 0:
		p.sendMessage(ChatColor.GRAY + "You just got sizzled!");
		break;
	    case 1:
		p.sendMessage(ChatColor.GRAY + "Odin hates you.");
		break;
	    case 2:
		p.sendMessage(ChatColor.GRAY + "BOOM!");
		break;
            case 3:
                p.sendMessage(ChatColor.GRAY + "Thundercats, ho!");
                break;
            case 4:
                p.sendMessage(ChatColor.GRAY + "Oh dear, you appear to be burning.");
                break;
	}
        
        if(blockamnt > 1){
            inHand.setAmount(--blockamnt);
        } else {
            p.getInventory().setItemInHand(null);
        }
        
        world = p.getWorld();
        Location loc = p.getLocation();
        world.strikeLightning(loc);
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
