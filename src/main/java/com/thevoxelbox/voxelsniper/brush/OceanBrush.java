package com.thevoxelbox.voxelsniper.brush;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_OCEANATOR_5000
 * @author Voxel
 */
public class OceanBrush extends Brush {
    private final static int WATER_LEVEL_DEFAULT = 62; // y=63 -- we are using array indices here
    private final static int WATER_LEVEL_MIN = 12;
    private final static List<Material> EXCLUDED_MATERIALS = new LinkedList<Material>();
    static {
    	EXCLUDED_MATERIALS.add(Material.AIR);
    	EXCLUDED_MATERIALS.add(Material.SAPLING);
    	EXCLUDED_MATERIALS.add(Material.WATER);
    	EXCLUDED_MATERIALS.add(Material.STATIONARY_WATER);
    	EXCLUDED_MATERIALS.add(Material.LAVA);
    	EXCLUDED_MATERIALS.add(Material.STATIONARY_LAVA);
    	EXCLUDED_MATERIALS.add(Material.LOG);
    	EXCLUDED_MATERIALS.add(Material.LEAVES);
    	EXCLUDED_MATERIALS.add(Material.YELLOW_FLOWER);
    	EXCLUDED_MATERIALS.add(Material.RED_ROSE);
    	EXCLUDED_MATERIALS.add(Material.RED_MUSHROOM);
    	EXCLUDED_MATERIALS.add(Material.BROWN_MUSHROOM);
    	EXCLUDED_MATERIALS.add(Material.MELON_BLOCK);
    	EXCLUDED_MATERIALS.add(Material.MELON_STEM);
    	EXCLUDED_MATERIALS.add(Material.PUMPKIN);
    	EXCLUDED_MATERIALS.add(Material.PUMPKIN_STEM);
    	EXCLUDED_MATERIALS.add(Material.COCOA);
    	EXCLUDED_MATERIALS.add(Material.SNOW);
    	EXCLUDED_MATERIALS.add(Material.SNOW_BLOCK);
    	EXCLUDED_MATERIALS.add(Material.ICE);
    	EXCLUDED_MATERIALS.add(Material.SUGAR_CANE_BLOCK);
    	EXCLUDED_MATERIALS.add(Material.LONG_GRASS);
    	EXCLUDED_MATERIALS.add(Material.SNOW);
    }
    
    private int waterLevel = WATER_LEVEL_DEFAULT;
    private boolean coverFloor = false;

    private static int timesUsed = 0;

    /**
     * 
     */
    public OceanBrush() {
        this.setName("OCEANATOR 5000(tm)");
    }

    private final int getHeight(final int bx, final int bz) {
        for (int _y = this.getWorld().getHighestBlockYAt(bx, bz); _y > 0; _y--) {
        	final Material _mat = this.clampY(bx, _y, bz).getType();
        	if(EXCLUDED_MATERIALS.contains(_mat)) {
        		continue;
			} else {
				return _y;
			}
		}
        return 0;
    }

    /**
     * 
     * @param v
     */
    protected final void oceanator(final SnipeData v, final Undo undo) {
    	final World _world = this.getWorld();

		final int _minX = (int) Math.floor((this.getTargetBlock().getX() - v.getBrushSize()));
		final int _minZ = (int) Math.floor((this.getTargetBlock().getZ() - v.getBrushSize()));
		final int _maxX = (int) Math.floor((this.getTargetBlock().getX() + v.getBrushSize()));
		final int _maxZ = (int) Math.floor((this.getTargetBlock().getZ() + v.getBrushSize()));

        for(int _x = _minX; _x <= _maxX; _x++) {
        	for(int _z = _minZ; _z <= _maxZ; _z++) {
        		final int _currentHeight = getHeight(_x, _z);
        		final int _wLevelDiff = _currentHeight - (this.waterLevel - 1);
        		final int _newSeaFloorLevel = ((this.waterLevel - _wLevelDiff) >= 12) ? this.waterLevel - _wLevelDiff : 12;        		       		
        		
        		final int _highestY = this.getWorld().getHighestBlockYAt(_x, _z);
        		
        		// go down from highest Y block down to new sea floor
        		for(int _y = _highestY; _y > _newSeaFloorLevel; _y--) {        			
        			final Block _block = _world.getBlockAt(_x, _y, _z);
        			if(!_block.getType().equals(Material.AIR)) {
        				undo.put(_block);
        				_block.setType(Material.AIR);
        			}
        		}
        		        		
        		
        		// go down from water level to new sea level
        		for(int _y = this.waterLevel; _y > _newSeaFloorLevel; _y--) {        			
        			final Block _block = _world.getBlockAt(_x, _y, _z);
        			if(!_block.getType().equals(Material.STATIONARY_WATER)) {
        				// do not put blocks into the undo we already put into
        				if(!_block.getType().equals(Material.AIR)) {
        					undo.put(_block);
        				}
        				_block.setType(Material.STATIONARY_WATER);
        			}
        		}
        		
        		// cover the sea floor of required
        		if(this.coverFloor && (_newSeaFloorLevel < this.waterLevel)) {
        			Block _block = _world.getBlockAt(_x, _newSeaFloorLevel, _z);
        			if(_block.getTypeId() != v.getVoxelId()) {
        				undo.put(_block);
        				_block.setTypeId(v.getVoxelId());
        			}
        		}
        	}
		}
    }

    @Override
    protected void arrow(final SnipeData v) {
    	Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        this.oceanator(v, _undo);
        v.storeUndo(_undo);
    }

    @Override
    protected void powder(final SnipeData v) {
        arrow(v);
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) { 
    	for(int _i = 0; _i < par.length; _i++) {
			final String _param = par[_i];

			try {
				if(_param.equalsIgnoreCase("info")) {
					v.sendMessage(ChatColor.BLUE + "Parameters:");
					v.sendMessage(ChatColor.GREEN + "-wlevel #  " + ChatColor.BLUE + "--  Sets the water level (e.g. -wlevel 64)");
					v.sendMessage(ChatColor.GREEN + "-cfloor [y|n]  " + ChatColor.BLUE + "--  Enables or disables sea floor cover (e.g. -cfloor y) (Cover material will be your voxel material)");
				}
				else if (_param.equalsIgnoreCase("-wlevel")) {
					if ((_i + 1) >= par.length) {
						v.sendMessage(ChatColor.RED + "Missing parameter. Correct syntax: -wlevel [#] (e.g. -wlevel 64)");
						continue;
					}

					int _tmp = Integer.parseInt(par[++_i]);
					
					if(_tmp <= WATER_LEVEL_MIN) {
						v.sendMessage(ChatColor.RED + "Error: Your specified water level was below 12.");
						continue;
					}
					
					this.waterLevel = _tmp - 1;
					v.sendMessage(ChatColor.BLUE + "Water level set to " + ChatColor.GREEN + (waterLevel + 1)); // +1 since we are working with 0-based array indices
				} else if (_param.equalsIgnoreCase("-cfloor") || _param.equalsIgnoreCase("-coverfloor")) {
					if ((_i + 1) >= par.length) {
						v.sendMessage(ChatColor.RED + "Missing parameter. Correct syntax: -cfloor [y|n] (e.g. -cfloor y)");
						continue;
					}
					
					this.coverFloor = par[++_i].equalsIgnoreCase("y"); 
					v.sendMessage(ChatColor.BLUE + String.format("Floor cover %s.", ChatColor.GREEN + (this.coverFloor ? "enabled" : "disabled")));
				}
			} catch (Exception _e) {
				v.sendMessage(ChatColor.RED + String.format("Error while parsing parameter: %s", _param));
				_e.printStackTrace();
			}
		}
	}

	@Override
    public void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.custom(ChatColor.BLUE + "Water level set to " + ChatColor.GREEN + (waterLevel + 1)); // +1 since we are working with 0-based array indices
    	vm.custom(ChatColor.BLUE + String.format("Floor cover %s.", ChatColor.GREEN + (this.coverFloor ? "enabled" : "disabled")));
    }
    
    @Override
    public int getTimesUsed() {
    	return OceanBrush.timesUsed;
    }
    
    @Override
    public void setTimesUsed(final int tUsed) {
    	OceanBrush.timesUsed = tUsed;
    }
}
