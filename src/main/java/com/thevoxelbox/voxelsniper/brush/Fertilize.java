/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author geekygenius
 */
public class Fertilize extends Brush {

    private Player dummy;
    private int brushSize;
    private double trueCircle;

    public Fertilize() {
        name = "Fertilize";
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
        vm.height();
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData s) {
        System.out.println("Fertilizing...");
        World world = tb.getWorld();
        int bsize = s.brushSize;
        double bpow = Math.pow(bsize + trueCircle, 2);
        for (int vert = (int) -s.voxelHeight / 2; vert < s.voxelHeight / 2; vert++) {
            for (int x = bsize; x >= 0; x--) {
                double xpow = Math.pow(x, 2);
                for (int y = bsize; y >= 0; y--) {
                    if ((xpow + Math.pow(y, 2)) <= bpow) {
                        boneMeal(world.getBlockAt(bx + x, vert + by, bz + y), s.owner().p);
                        boneMeal(world.getBlockAt(bx + x, vert + by, bz - y), s.owner().p);
                        boneMeal(world.getBlockAt(bx - x, vert + by, bz + y), s.owner().p);
                        boneMeal(world.getBlockAt(bx - x, vert + by, bz - y), s.owner().p);
                    }
                }
            }
        }
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData s) {
    }

    private void boneMeal(Block b, Player p) {//I think I need to make a legit bonemeal player.
        dummy = p;//Make a dummy referance so we don't modify the origional
        VoxelSniper.s.getPluginManager().callEvent(
                new PlayerInteractEvent(p, Action.RIGHT_CLICK_BLOCK, new ItemStack(351, 1, (short) 0, (byte) 15), b, BlockFace.UP));
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Fertilize Brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b fert true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b vg false will switch back. (false is default)");
            return;
        }
        for (int x = 1; x < par.length; x++) {
            if (par[x].startsWith("true")) {
                trueCircle = 0.5;
                v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
                continue;
            } else if (par[x].startsWith("false")) {
                trueCircle = 0;
                v.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
                continue;
            } else {
                v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }
}
