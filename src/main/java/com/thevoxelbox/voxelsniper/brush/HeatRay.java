/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.undo.vUndo;
import com.thevoxelbox.voxelsniper.vData;
import com.thevoxelbox.voxelsniper.vMessage;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.SimplexNoiseGenerator;

/**
 * THIS BRUSH SHOULD NOT USE PERFORMERS
 * 
 * @author Gavjenks (derived from Piotr'w ball replace brush)
 */
public class HeatRay extends Brush {

    private static final double REQUIRED_OBSIDIAN_DENSITY = 0.6;
    private static final double REQUIRED_COBBLE_DENSITY = 0.5;
    private static final double REQUIRED_FIRE_DENSITY = -0.25;
    private static final double REQUIRED_AIR_DENSITY = 0;

    private static final ArrayList<Material> FLAMABLE_BLOCKS = new ArrayList<Material>();

    private enum FlameableBlock {

        WOOD(Material.WOOD), SAPLING(Material.SAPLING), LOG(Material.LOG), LEAVES(Material.LEAVES), SPONGE(Material.SPONGE), WEB(Material.WEB), LONG_GRASS(
                Material.LONG_GRASS), DEAD_BUSH(Material.DEAD_BUSH), WOOL(Material.WOOL), YELLOW_FLOWER(Material.YELLOW_FLOWER), RED_ROSE(Material.RED_ROSE), TORCH(
                Material.TORCH), FIRE(Material.FIRE), WOOD_STAIRS(Material.WOOD_STAIRS), CROPS(Material.CROPS), SIGN_POST(Material.SIGN_POST), WOODEN_DOOR(
                Material.WOODEN_DOOR), LADDER(Material.LADDER), WALL_SIGN(Material.WALL_SIGN), WOOD_PLATE(Material.WOOD_PLATE), SNOW(Material.SNOW), ICE(
                Material.ICE), SUGAR_CANE_BLOCK(Material.SUGAR_CANE_BLOCK), FENCE(Material.FENCE), TRAP_DOOR(Material.TRAP_DOOR), VINE(Material.VINE), FENCE_GATE(
                Material.FENCE_GATE), WATER_LILLY(Material.WATER_LILY);

        public Material material;

        FlameableBlock(Material material) {
            this.material = material;
        }
    }

    static {
        for (FlameableBlock _flameableBlock : FlameableBlock.values()) {
            FLAMABLE_BLOCKS.add(_flameableBlock.material);
        }
    }

    public HeatRay() {
        name = "Heat Ray";
    }

    @Override
    protected void arrow(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        heatRay(v);
    }

    @Override
    protected void powder(com.thevoxelbox.voxelsniper.vData v) {
        bx = tb.getX();
        by = tb.getY();
        bz = tb.getZ();
        heatRay(v);
    }

    @Override
    public void info(vMessage vm) {
        vm.brushName(name);
        vm.size();
    }

    public void heatRay(vData v) {
        SimplexNoiseGenerator _airGenerator = new SimplexNoiseGenerator(new Random());
        SimplexNoiseGenerator _fireGenerator = new SimplexNoiseGenerator(new Random());
        SimplexNoiseGenerator _cobbleGenerator = new SimplexNoiseGenerator(new Random());
        SimplexNoiseGenerator _obsidianGenerator = new SimplexNoiseGenerator(new Random());

        double _bSquare = (v.brushSize + 0.5) * (v.brushSize + 0.5);

        Vector _targetLocation = tb.getLocation().toVector();
        Location _currentLocation = new Location(tb.getWorld(), 0, 0, 0);
        Block _currentBlock = null;
        vUndo _undo = new vUndo(tb.getWorld().getName());

        for (int _z = v.brushSize; _z >= -v.brushSize; _z--) {
            for (int _x = v.brushSize; _x >= -v.brushSize; _x--) {
                for (int _y = v.brushSize; _y >= -v.brushSize; _y--) {
                    _currentLocation.setX(bx + _x);
                    _currentLocation.setY(by + _y);
                    _currentLocation.setZ(bz + _z);

                    if (_currentLocation.toVector().isInSphere(_targetLocation, v.brushSize)) {
                        _currentBlock = _currentLocation.getBlock();
                        if (_currentBlock == null || _currentBlock.getType() == Material.CHEST) {
                            continue;
                        }

                        if (_currentBlock.isLiquid()) {
                            _undo.put(_currentBlock);
                            _currentBlock.setType(Material.AIR);
                            continue;
                        }

                        if (FLAMABLE_BLOCKS.contains(_currentBlock.getType())) {
                            _undo.put(_currentBlock);
                            _currentBlock.setType(Material.FIRE);
                            continue;
                        }

                        if (!_currentBlock.getType().equals(Material.AIR)) {
                            double _airDensity = _airGenerator.noise(_currentLocation.getX(), _currentLocation.getY(), _currentLocation.getZ());
                            double _fireDensity = _fireGenerator.noise(_currentLocation.getX(), _currentLocation.getY(), _currentLocation.getZ());
                            double _cobbleDensity = _cobbleGenerator.noise(_currentLocation.getX(), _currentLocation.getY(), _currentLocation.getZ());
                            double _obsidianDensity = _obsidianGenerator.noise(_currentLocation.getX(), _currentLocation.getY(), _currentLocation.getZ());

                            if (_obsidianDensity >= REQUIRED_OBSIDIAN_DENSITY) {
                                if (_currentBlock.getType() != Material.OBSIDIAN) {
                                    _undo.put(_currentBlock);
                                    _currentBlock.setType(Material.OBSIDIAN);
                                }
                            } else if (_cobbleDensity >= REQUIRED_COBBLE_DENSITY) {
                                if (_currentBlock.getType() != Material.COBBLESTONE) {
                                    _undo.put(_currentBlock);
                                    _currentBlock.setType(Material.COBBLESTONE);
                                }
                            } else if (_fireDensity >= REQUIRED_FIRE_DENSITY) {
                                if (_currentBlock.getType() != Material.FIRE) {
                                    _undo.put(_currentBlock);
                                    _currentBlock.setType(Material.FIRE);
                                }
                            } else if (_airDensity >= REQUIRED_AIR_DENSITY) {
                                if (_currentBlock.getType() != Material.AIR) {
                                    _undo.put(_currentBlock);
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
}
