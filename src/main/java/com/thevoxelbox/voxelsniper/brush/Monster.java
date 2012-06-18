/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
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
        name = "Entity";
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
        vm.brushMessage(ChatColor.LIGHT_PURPLE + "Entity brush" + " (" + ct.getName() + ")");
        vm.size();
    }

    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.BLUE + "The available entity types are as follows:");
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
                v.sendMessage(ChatColor.GREEN + "Entity type set to " + ct.getName());
            } else {
                v.sendMessage(ChatColor.RED + "This is not a valid entity!");
            }
        }
    }

    protected void spawn(vData v) {
        for (int x = 0; x < v.brushSize; x++) {
            try {
            	Class<? extends Entity> ent = ct.getEntityClass();
                w.spawn(lb.getLocation(), ent);
            } catch (ClassCastException ex) {
                v.sendMessage(ChatColor.RED + "Invalid entity");
            }
        }
    }
}
