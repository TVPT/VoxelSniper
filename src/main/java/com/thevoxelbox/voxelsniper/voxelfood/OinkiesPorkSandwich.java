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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OinkiesPorkSandwich extends Food {
    int rand;
    protected World world;
    
    @Override
    protected boolean rightAir(Player p, ItemStack inHand, Block clicked) {
        world = p.getWorld();
        Random random = new Random();
        rand = random.nextInt(50);
        int blockamnt = inHand.getAmount();
        Location loc = p.getLocation();
        
        if(rand == 25){
            world.spawnCreature(loc, EntityType.PIG_ZOMBIE);
            world.strikeLightningEffect(loc);
            p.chat(ChatColor.GRAY + "Fear my pig zombie army!");
        }
        else {
            world.spawnCreature(loc, EntityType.PIG);
            p.chat(ChatColor.GRAY + "No! My pig experiments failed!");
        }
        
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
        world = p.getWorld();
        Random random = new Random();
        rand = random.nextInt(50);
        int blockamnt = inHand.getAmount();
        Location loc = p.getLocation();
        
        if(rand == 25){
            world.spawnCreature(loc, EntityType.PIG_ZOMBIE);
            world.strikeLightningEffect(loc);
            p.chat(ChatColor.GRAY + "Muahaha! This is the beginning of the end!");
        }
        else {
            world.spawnCreature(loc, EntityType.ZOMBIE);
            p.chat(ChatColor.GRAY + "I must have ran out of pigs for my experiments...");
        }
        
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
