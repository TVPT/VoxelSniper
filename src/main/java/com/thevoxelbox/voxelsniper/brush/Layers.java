package com.thevoxelbox.voxelsniper.brush;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.vMessage;

public class Layers extends Brush implements Listener{

	private int layerCount = 2, currentLayer = 1;
	private boolean start = false;
	
	public static ArrayList<Layer> layers = new ArrayList<Layer>();
	
	public Layers() {
		name = "layers";
		VoxelSniper.s.getPluginManager().registerEvents(this, VoxelSniper.instance);
	}
	
	@Override
	public void info(vMessage vm) {
		vm.brushName(name);
        vm.voxel();
	}
	
	private Layer getLayerById(int id) {
		for (Layer layer : layers)
			if (layer.id == id)
				return layer;
		return null;
	}

	@Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
		if (start) {
			if (currentLayer != layerCount)
				currentLayer++;
			else
				currentLayer = 1;
			
			updateLayers();
		}
    }
	
	private void updateLayers() {
		for (Layer layer : layers) {
			if (layer.id != currentLayer) {
				if (!layer.hiding)
					layer.hide();
			} else 
				layer.show();
			
		}
	}

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        arrow(v);
    }
    
    @Override
    public void parameters(String[] par, com.thevoxelbox.voxelsniper.vData v) {
        if (par[1].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Layer Brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b layers amount [amount] -- set the amount of layers");
            v.sendMessage(ChatColor.AQUA + "/b layers start -- starts the editing");
            v.sendMessage(ChatColor.AQUA + "/b layers stop -- stops the editing");
        } else if (par[1].equalsIgnoreCase("amount")) {
        	try {
        		if (!start) {
        			if (Integer.parseInt(par[2]) > 1) {
        				layerCount = Integer.parseInt(par[2]);										
        				v.sendMessage(ChatColor.GREEN + "Layer count sent.");
        			} else {
        				v.sendMessage(ChatColor.RED + "The layer count has to be greater than 1.");
        			}
        		} else {
        			v.sendMessage(ChatColor.RED + "Layer editing was already started! Type '/b layers stop' to stop the editing.");
        		}
        	} catch (Exception e) {																
        		v.sendMessage(ChatColor.RED + "Invalid brush parameters.");
        	}
        } else if (par[1].equalsIgnoreCase("start")) {
        	try {
        		if (!start) {
        			start = true;
        			
        			layers.clear();
        			
        			for (int i = 1; i <= layerCount; i++) {
        				layers.add(new Layer(i, v.getWorld()));
        				VoxelSniper.log.info("Layer " + i);
        			}
        			
        			v.sendMessage(ChatColor.GREEN + "The layer editing has started.");
        		}
        	} catch (Exception e) {																
        		v.sendMessage(ChatColor.RED + "Invalid brush parameters.");
        	}
        } else if (par[1].equalsIgnoreCase("stop")) {
        	try {
        		if (start) {
        			start = false;
        			for (Layer layer : layers)
        				layer.show();
        			v.sendMessage(ChatColor.GREEN + "The layer editing has stopped.");
        		}
        	} catch (Exception e) {																
        		v.sendMessage(ChatColor.RED + "Invalid brush parameters.");
        	}
        } else {
        	v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
        }
    }
    
    @EventHandler
	public void onBlockPlace (BlockPlaceEvent event) {
    	try {
    		if (start) {
    			getLayerById(currentLayer).blocks.add(event.getBlock());
    		}
    	} catch (Exception e) {
    		//Do nothing, an exception would come up sometimes
    	}
    }
 
    @EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
    	try {
	    	if (start) {
		    	for (Block block : getLayerById(currentLayer).blocks)
		    		if (block.getLocation() == event.getBlock().getLocation())
		    			getLayerById(currentLayer).blocks.remove(block);
	    	}
    	} catch (Exception e) {
    		//Do nothing, an exception would come up sometimes
    	}
    }
}

class Layer {
	
	public int id;
	public ArrayList<Block> blocks = new ArrayList<Block>();
	public ArrayList<BlockClone> copy = new ArrayList<BlockClone>();
	private World w;
	public boolean hiding = false;
	
	public Layer(int id, World w) {
		this.id = id;
		this.w = w;
	}	
	
	public void hide() {
		copy.clear();
		for (Block block : blocks) {
			if (block.getTypeId() != 0) {//extra check
				copy.add(new BlockClone(block.getTypeId(), block.getLocation()));
				block.setTypeId(0);
			}
		}
		hiding = true;
	}
	
	public void show() {
		blocks.clear();
		for (BlockClone block : copy) {
			Block b = w.getBlockAt(block.loc);
			if (block.type != 0) { //extra check for when they remove blocks because there are glitches in the other
				b.setTypeId(block.type);
				blocks.add(b);
			}
		}
		hiding = false;
	}
}

class BlockClone {
	
	public int type;
	public Location loc;
	
	public BlockClone(int type, Location loc) {
		this.type = type;
		this.loc = loc;
	}
}
