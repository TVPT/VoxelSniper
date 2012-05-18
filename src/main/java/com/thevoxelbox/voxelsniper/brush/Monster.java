/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

/**
 * THIS BRUSH SHOULD NOT USE PERFORMERS
 *
 * @author Piotr
 */
public class Monster extends Brush {

    protected EntityType ct = EntityType.ZOMBIE;

    public Monster() {
        name = "Monster";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        spawn(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        spawn(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushMessage(ChatColor.LIGHT_PURPLE + "Monster brush" + " (" + ct.getName() + ")");
        vm.size();
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.BLUE + "The aviable creature types are as follows:");
            String names = "";
            for (EntityType cre : EntityType.values()) {

                names += ChatColor.AQUA + " | " + ChatColor.DARK_GREEN + cre.getName();
            }
            names += ChatColor.AQUA + " |";
            v.sendMessage(names);
        } else {
            EntityType cre = EntityType.fromName(par[1]);
            if (cre != null) {
                ct = cre;
                v.sendMessage(ChatColor.GREEN + "Creature type set to " + ct.getName());
            } else {
                v.sendMessage(ChatColor.RED + "This is not a valid creature!");
            }
        }
    }

    protected void spawn(vData v) {
        for (int x = 0; x < v.brushSize; x++) {
            try {
                w.spawnCreature(lb.getLocation(), ct);
            } catch (ClassCastException ex) {
                v.sendMessage(ChatColor.RED + "Invalid living entity");
            }
        }
    }
}
