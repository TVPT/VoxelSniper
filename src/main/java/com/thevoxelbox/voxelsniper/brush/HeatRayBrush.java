package com.thevoxelbox.voxelsniper.brush;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.PerlinNoiseGenerator;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;

/**
 * 
 * @author Gavjenks
 */
public class HeatRayBrush extends Brush {

    /**
     * @author MikeMatrix
     * 
     */
    private enum FlameableBlock {
        WOOD(Material.WOOD), SAPLING(Material.SAPLING), LOG(Material.LOG), LEAVES(Material.LEAVES), SPONGE(Material.SPONGE), WEB(Material.WEB), LONG_GRASS(
                Material.LONG_GRASS), DEAD_BUSH(Material.DEAD_BUSH), WOOL(Material.WOOL), YELLOW_FLOWER(Material.YELLOW_FLOWER), RED_ROSE(Material.RED_ROSE), TORCH(
                Material.TORCH), FIRE(Material.FIRE), WOOD_STAIRS(Material.WOOD_STAIRS), CROPS(Material.CROPS), SIGN_POST(Material.SIGN_POST), WOODEN_DOOR(
                Material.WOODEN_DOOR), LADDER(Material.LADDER), WALL_SIGN(Material.WALL_SIGN), WOOD_PLATE(Material.WOOD_PLATE), SNOW(Material.SNOW), ICE(
                Material.ICE), SUGAR_CANE_BLOCK(Material.SUGAR_CANE_BLOCK), FENCE(Material.FENCE), TRAP_DOOR(Material.TRAP_DOOR), VINE(Material.VINE), FENCE_GATE(
                Material.FENCE_GATE), WATER_LILLY(Material.WATER_LILY);

        private Material material;
        
        FlameableBlock(final Material material) {
            this.material = material;
        }
    }

    private static final double REQUIRED_OBSIDIAN_DENSITY = 0.6;
    private static final double REQUIRED_COBBLE_DENSITY = 0.5;
    private static final double REQUIRED_FIRE_DENSITY = -0.25;
    private static final double REQUIRED_AIR_DENSITY = 0;
    
    private static final ArrayList<Material> FLAMABLE_BLOCKS = new ArrayList<Material>();
    
    private static int timesUsed = 0;
    private int octaves = 5;
    private double frequency = 1;

    private double amplitude = 0.3;

    static {
        for (final FlameableBlock _flameableBlock : FlameableBlock.values()) {
            HeatRayBrush.FLAMABLE_BLOCKS.add(_flameableBlock.material);
        }
    }


    /**
     * Default Constructor.
     */
    public HeatRayBrush() {
        this.setName("Heat Ray");
    }

    /**
     * Heat Ray executer.
     * 
     * @param v
     */
    public final void heatRay(final SnipeData v) {
        final PerlinNoiseGenerator _generator = new PerlinNoiseGenerator(new Random());

        final Vector _targetLocation = this.getTargetBlock().getLocation().toVector();
        final Location _currentLocation = new Location(this.getTargetBlock().getWorld(), 0, 0, 0);
        final Undo _undo = new Undo(this.getTargetBlock().getWorld().getName());
        Block _currentBlock = null;

        for (int _z = v.getBrushSize(); _z >= -v.getBrushSize(); _z--) {
            for (int _x = v.getBrushSize(); _x >= -v.getBrushSize(); _x--) {
                for (int _y = v.getBrushSize(); _y >= -v.getBrushSize(); _y--) {
                    _currentLocation.setX(this.getBlockPositionX() + _x);
                    _currentLocation.setY(this.getBlockPositionY() + _y);
                    _currentLocation.setZ(this.getBlockPositionZ() + _z);

                    if (_currentLocation.toVector().isInSphere(_targetLocation, v.getBrushSize())) {
                        _currentBlock = _currentLocation.getBlock();
                        if (_currentBlock == null || _currentBlock.getType() == Material.CHEST) {
                            continue;
                        }

                        if (_currentBlock.isLiquid()) {
                            _undo.put(_currentBlock);
                            _currentBlock.setType(Material.AIR);
                            continue;
                        }

                        if (HeatRayBrush.FLAMABLE_BLOCKS.contains(_currentBlock.getType())) {
                            _undo.put(_currentBlock);
                            _currentBlock.setType(Material.FIRE);
                            continue;
                        }

                        if (!_currentBlock.getType().equals(Material.AIR)) {
                            final double _airDensity = _generator.noise(_currentLocation.getX(), _currentLocation.getY(), _currentLocation.getZ(),
                                    this.octaves, this.frequency, this.amplitude);
                            final double _fireDensity = _generator.noise(_currentLocation.getX(), _currentLocation.getY(), _currentLocation.getZ(),
                                    this.octaves, this.frequency, this.amplitude);
                            final double _cobbleDensity = _generator.noise(_currentLocation.getX(), _currentLocation.getY(), _currentLocation.getZ(),
                                    this.octaves, this.frequency, this.amplitude);
                            final double _obsidianDensity = _generator.noise(_currentLocation.getX(), _currentLocation.getY(), _currentLocation.getZ(),
                                    this.octaves, this.frequency, this.amplitude);

                            if (_obsidianDensity >= HeatRayBrush.REQUIRED_OBSIDIAN_DENSITY) {
                                _undo.put(_currentBlock);
                                if (_currentBlock.getType() != Material.OBSIDIAN) {
                                    _currentBlock.setType(Material.OBSIDIAN);
                                }
                            } else if (_cobbleDensity >= HeatRayBrush.REQUIRED_COBBLE_DENSITY) {
                                _undo.put(_currentBlock);
                                if (_currentBlock.getType() != Material.COBBLESTONE) {
                                    _currentBlock.setType(Material.COBBLESTONE);
                                }
                            } else if (_fireDensity >= HeatRayBrush.REQUIRED_FIRE_DENSITY) {
                                _undo.put(_currentBlock);
                                if (_currentBlock.getType() != Material.FIRE) {
                                    _currentBlock.setType(Material.FIRE);
                                }
                            } else if (_airDensity >= HeatRayBrush.REQUIRED_AIR_DENSITY) {
                                _undo.put(_currentBlock);
                                if (_currentBlock.getType() != Material.AIR) {
                                    _currentBlock.setType(Material.AIR);
                                }
                            }
                        }
                    }

                }
            }
        }

        v.storeUndo(_undo);
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.heatRay(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
    	this.heatRay(v);
    }
    
    @Override
    public final void info(final Message vm) {
    	vm.brushName(this.getName());
    	vm.custom(ChatColor.GREEN + "Octaves: " + this.octaves);
    	vm.custom(ChatColor.GREEN + "Amplitude: " + this.amplitude);
    	vm.custom(ChatColor.GREEN + "Frequency: " + this.frequency);
    	vm.size();
    }
    
    @Override
    public final void parameters(final String[] par, final SnipeData v) {
    	for (int _i = 1; _i < par.length; _i++) {
    		final String _param = par[_i].toLowerCase();

    		if (_param.equalsIgnoreCase("info")) {
    			v.sendMessage(ChatColor.GOLD + "Heat Ray brush Parameters:");
    			v.sendMessage(ChatColor.AQUA + "/b hr oct[int] -- Octaves parameter for the noise generator.");
    			v.sendMessage(ChatColor.AQUA + "/b hr amp[float] -- Amplitude parameter for the noise generator.");
    			v.sendMessage(ChatColor.AQUA + "/b hr freq[float] -- Frequency parameter for the noise generator.");
    		}
    		if (_param.startsWith("oct")) {
    			this.octaves = Integer.valueOf(_param.substring(3));
    			v.getVoxelMessage().custom(ChatColor.GREEN + "Octaves: " + this.octaves);
    		} else if (_param.startsWith("amp")) {
    			this.amplitude = Double.valueOf(_param.substring(3));
    			v.getVoxelMessage().custom(ChatColor.GREEN + "Amplitude: " + this.amplitude);
    		} else if (_param.startsWith("freq")) {
    			this.frequency = Double.valueOf(_param.substring(4));
    			v.getVoxelMessage().custom(ChatColor.GREEN + "Frequency: " + this.frequency);
    		}
    	}
    }
    
    @Override
    public final int getTimesUsed() {
    	return HeatRayBrush.timesUsed;
    }
    
    @Override
    public final void setTimesUsed(final int tUsed) {
    	HeatRayBrush.timesUsed = tUsed;
    }
}
