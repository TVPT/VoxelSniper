/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.vMessage;
import com.thevoxelbox.voxelsniper.vSniper;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

/**
 *THIS BRUSH SHOULD NOT USE PERFORMERS
 * @author Piotr
 */
public class Monster extends Brush {

    protected EntityType ct = EntityType.ZOMBIE;
    
    public Monster() {
        name = "Monster";
    }

    @Override
    protected void arrow(vSniper v) {
        spawn(v);
    }

    @Override
    protected void powder(vSniper v) {
        spawn(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushMessage(ChatColor.LIGHT_PURPLE + "Monster brush" + " (" + ct.getName() + ")");
        vm.size();
    }

    @Override
    public void parameters(String[] par, vSniper v) {
        if(par[1].equalsIgnoreCase("info")) {
            v.p.sendMessage(ChatColor.BLUE + "The aviable creature types are as follows:");
            String names = "";
            for(EntityType cre : EntityType.values()) {
                names += ChatColor.AQUA + " | " + ChatColor.DARK_GREEN + cre.getName();
            }
            names += ChatColor.AQUA + " |";
            v.p.sendMessage(names);
            return;
        } else {
            EntityType cre = EntityType.fromName(par[1]);
            if(cre != null) {
                ct = cre;
                v.p.sendMessage(ChatColor.GREEN + "Creature type set to " + ct.getName());
            } else {
                v.p.sendMessage(ChatColor.RED + "This is not a valid creature!");
            }
        }
    }

    protected void spawn(vSniper v) {
        for(int x = 0; x < v.brushSize; x++) {
            w.spawnCreature(lb.getLocation(), ct);
        }
    }
}
