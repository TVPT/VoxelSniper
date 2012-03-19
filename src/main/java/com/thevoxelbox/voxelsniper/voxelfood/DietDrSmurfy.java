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

public class DietDrSmurfy extends Food {

    Random rand = new Random();
    int randmessage;
    
    @Override
    protected boolean rightAir(Player p, ItemStack inHand, Block clicked) {
        randmessage = rand.nextInt(11);
        int blockamnt = inHand.getAmount();
        
        switch(randmessage){
            case 0:
                p.chat(ChatColor.GRAY + "how do i get blox?!");
                break;
            case 1:
                p.chat(ChatColor.GRAY + "can i be an op plz??!");
                break;
            case 2:
                p.chat(ChatColor.GRAY + "where can i build?!");
                break;
            case 3:
                p.chat(ChatColor.GRAY + "can i have the map file plz");
                break;
            case 4:
                p.chat(ChatColor.GRAY + "is captainsparklez on?!");
                break;
            case 5:
                p.chat(ChatColor.GRAY + "why cant i use the feather :(");
                break;
            case 6:
                p.chat(ChatColor.GRAY + "you cant ban for asshat!");
                break;
            case 7:
                p.chat(ChatColor.GRAY + "omg does yogscast come on here plz tell me");
                break;
            case 8:
                p.chat(ChatColor.GRAY + "y u deny my app?");
                break;
            case 9:
                p.chat(ChatColor.GRAY + "y cant i use voxel sniper tool??!");
                break;
            case 10:
                p.chat(ChatColor.GRAY + "how do i fly???");
                break;
            case 11:
                p.chat(ChatColor.GRAY + "can i use flymod!?");
                break;
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
