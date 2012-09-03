package com.thevoxelbox.voxelsniper.brush;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;

/**
 * 
 * @author Piotr
 */
public class Entity extends Brush {
    private EntityType entityType = EntityType.ZOMBIE;
    private static int timesUsed = 0;

    public Entity() {
        this.setName("Entity");
    }
    
    private final void spawn(final SnipeData v) {
    	for (int _x = 0; _x < v.getBrushSize(); _x++) {
    		try {
    			final Class<? extends org.bukkit.entity.Entity> ent = this.entityType.getEntityClass();
    			this.getWorld().spawn(this.getLastBlock().getLocation(), ent);
    		} catch (final IllegalArgumentException ex) {
    			v.sendMessage(ChatColor.RED + "Cannot spawn entity!");
    		}
    	}
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.spawn(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.spawn(v);
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushMessage(ChatColor.LIGHT_PURPLE + "Entity brush" + " (" + this.entityType.getName() + ")");
    	vm.size();
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
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
    			this.entityType = cre;
    			v.sendMessage(ChatColor.GREEN + "Entity type set to " + this.entityType.getName());
    		} else {
    			v.sendMessage(ChatColor.RED + "This is not a valid entity!");
    		}
    	}
    }
    
    @Override
    public final int getTimesUsed() {
    	return Entity.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	Entity.timesUsed = tUsed;
    }
}
