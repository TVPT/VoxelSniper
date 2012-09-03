package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;

/**
 * 
 * @author Piotr
 */
public class Entity extends Brush {

    protected EntityType ct = EntityType.ZOMBIE;

    private static int timesUsed = 0;

    public Entity() {
        this.name = "Entity";
    }

    @Override
    public final int getTimesUsed() {
        return Entity.timesUsed;
    }

    @Override
    public final void info(final vMessage vm) {
        vm.brushMessage(ChatColor.LIGHT_PURPLE + "Entity brush" + " (" + this.ct.getName() + ")");
        vm.size();
    }

    @Override
    public final void parameters(final String[] par, final com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.BLUE + "The available entity types are as follows:");
            String names = "";
            for (final EntityType cre : EntityType.values()) {

                names += ChatColor.AQUA + " | " + ChatColor.DARK_GREEN + cre.getName();
            }
            names += ChatColor.AQUA + " |";
            v.sendMessage(names);
        } else {
            final EntityType cre = EntityType.fromName(par[1]);
            if (cre != null) {
                this.ct = cre;
                v.sendMessage(ChatColor.GREEN + "Entity type set to " + this.ct.getName());
            } else {
                v.sendMessage(ChatColor.RED + "This is not a valid entity!");
            }
        }
    }

    @Override
    public final void setTimesUsed(final int tUsed) {
        Entity.timesUsed = tUsed;
    }

    @Override
    protected final void arrow(final com.thevoxelbox.voxelsniper.vData v) {
        this.spawn(v);
    }

    @Override
    protected final void powder(final com.thevoxelbox.voxelsniper.vData v) {
        this.spawn(v);
    }

    protected final void spawn(final vData v) {
        for (int x = 0; x < v.brushSize; x++) {
            try {
                final Class<? extends org.bukkit.entity.Entity> ent = this.ct.getEntityClass();
                this.w.spawn(this.lb.getLocation(), ent);
            } catch (final IllegalArgumentException ex) {
                v.sendMessage(ChatColor.RED + "Cannot spawn entity!");
            }
        }
    }
}
