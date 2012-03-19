/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.voxelfood;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Voxel
 */
public class CatapultCalzone extends Food {

    @Override
    protected boolean rightAir(Player p, ItemStack inHand, Block clicked) {

        return false;
    }

    @Override
    protected boolean rightBlock(Player p, ItemStack inHand, Block clicked) {

        return false;
    }

    @Override
    protected boolean leftAir(Player p, ItemStack inHand, Block clicked) {
        
        return true;
    }

    @Override
    protected boolean leftBlock(Player p, ItemStack inHand, Block clicked) {

        return false;
    }

    @Override
    protected boolean pressure(Player p, ItemStack inHand, Block clicked) {
        return false;
    }
}
