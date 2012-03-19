package com.thevoxelbox.voxelsniper.voxelfood;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Razorcane
 */
public class NinewerksCoffee extends Food {
    Random rand = new Random();
    int randX;
    int randY;
    int randZ;

    @Override
    protected boolean rightAir(Player p, ItemStack inHand, Block clicked) {
        randX = rand.nextInt(200)-100;
        randY = rand.nextInt(200)-100;
        int blockamnt = inHand.getAmount();
        
        p.teleport(new Location(p.getWorld(), p.getLocation().getBlockX() + randX, 100, p.getLocation().getBlockZ() + randZ));
        
        if(blockamnt > 1){
            inHand.setAmount(--blockamnt);
        } else {
            p.getInventory().setItemInHand(null);
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
        randX = p.getLocation().getBlockX();
        randY = rand.nextInt(64)+64;
        randZ = p.getLocation().getBlockZ();
        int blockamnt = inHand.getAmount();
        
        p.teleport(new Location(p.getWorld(), randX, randY, randZ));
        
        if(blockamnt > 1){
            inHand.setAmount(--blockamnt);
        } else {
            p.getInventory().setItemInHand(null);
        }
        
        return false;
    }

    @Override
    protected boolean leftBlock(Player p, ItemStack inHand, Block clicked) {
        leftAir(p, inHand, clicked);
        return true;
    }

    @Override
    protected boolean pressure(Player p, ItemStack inHand, Block clicked) {
        rightAir(p, inHand, clicked);
        return true;
    }
    
}
