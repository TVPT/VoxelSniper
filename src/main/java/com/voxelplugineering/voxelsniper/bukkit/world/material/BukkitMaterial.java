/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 The Voxel Plugineering Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.voxelplugineering.voxelsniper.bukkit.world.material;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.voxelplugineering.voxelsniper.registry.WeakWrapper;
import com.voxelplugineering.voxelsniper.world.material.MaterialState;
import com.voxelplugineering.voxelsniper.world.material.MaterialStateCache;

/**
 * A wrapper for bukkit {@link org.bukkit.Material}s.
 */
public class BukkitMaterial extends WeakWrapper<org.bukkit.Material>implements com.voxelplugineering.voxelsniper.world.material.Material
{
    
    /**
     * A static set of materials which are liquids.
     */
    private static List<org.bukkit.Material> LIQUIDS = Lists.newArrayListWithCapacity(4);
    /**
     * A static set of materials which can break on physics updates.
     */
    private static List<org.bukkit.Material> FALLOFF_MATERIALS = Lists.newArrayListWithCapacity(47);

    static
    {
        LIQUIDS.add(org.bukkit.Material.WATER);
        LIQUIDS.add(org.bukkit.Material.LAVA);
        LIQUIDS.add(org.bukkit.Material.STATIONARY_LAVA);
        LIQUIDS.add(org.bukkit.Material.STATIONARY_WATER);

        FALLOFF_MATERIALS.add(org.bukkit.Material.ACTIVATOR_RAIL);
        FALLOFF_MATERIALS.add(org.bukkit.Material.BROWN_MUSHROOM);
        FALLOFF_MATERIALS.add(org.bukkit.Material.CACTUS);
        FALLOFF_MATERIALS.add(org.bukkit.Material.CARPET);
        FALLOFF_MATERIALS.add(org.bukkit.Material.DEAD_BUSH);
        FALLOFF_MATERIALS.add(org.bukkit.Material.DETECTOR_RAIL);
        FALLOFF_MATERIALS.add(org.bukkit.Material.DIODE_BLOCK_OFF);
        FALLOFF_MATERIALS.add(org.bukkit.Material.DIODE_BLOCK_ON);
        FALLOFF_MATERIALS.add(org.bukkit.Material.DOUBLE_PLANT);
        FALLOFF_MATERIALS.add(org.bukkit.Material.FLOWER_POT);
        FALLOFF_MATERIALS.add(org.bukkit.Material.IRON_DOOR);
        FALLOFF_MATERIALS.add(org.bukkit.Material.ITEM_FRAME);
        FALLOFF_MATERIALS.add(org.bukkit.Material.LADDER);
        FALLOFF_MATERIALS.add(org.bukkit.Material.LEVER);
        FALLOFF_MATERIALS.add(org.bukkit.Material.LONG_GRASS);
        FALLOFF_MATERIALS.add(org.bukkit.Material.MELON_STEM);
        FALLOFF_MATERIALS.add(org.bukkit.Material.NETHER_STALK);
        FALLOFF_MATERIALS.add(org.bukkit.Material.NETHER_WARTS);
        FALLOFF_MATERIALS.add(org.bukkit.Material.PAINTING);
        FALLOFF_MATERIALS.add(org.bukkit.Material.PISTON_EXTENSION);
        FALLOFF_MATERIALS.add(org.bukkit.Material.PISTON_MOVING_PIECE);
        FALLOFF_MATERIALS.add(org.bukkit.Material.PORTAL);
        FALLOFF_MATERIALS.add(org.bukkit.Material.POTATO);
        FALLOFF_MATERIALS.add(org.bukkit.Material.POWERED_RAIL);
        FALLOFF_MATERIALS.add(org.bukkit.Material.PUMPKIN_STEM);
        FALLOFF_MATERIALS.add(org.bukkit.Material.RAILS);
        FALLOFF_MATERIALS.add(org.bukkit.Material.RED_MUSHROOM);
        FALLOFF_MATERIALS.add(org.bukkit.Material.RED_ROSE);
        FALLOFF_MATERIALS.add(org.bukkit.Material.REDSTONE_COMPARATOR_OFF);
        FALLOFF_MATERIALS.add(org.bukkit.Material.REDSTONE_COMPARATOR_ON);
        FALLOFF_MATERIALS.add(org.bukkit.Material.REDSTONE_TORCH_OFF);
        FALLOFF_MATERIALS.add(org.bukkit.Material.REDSTONE_TORCH_ON);
        FALLOFF_MATERIALS.add(org.bukkit.Material.REDSTONE_WIRE);
        FALLOFF_MATERIALS.add(org.bukkit.Material.SAPLING);
        FALLOFF_MATERIALS.add(org.bukkit.Material.SIGN);
        FALLOFF_MATERIALS.add(org.bukkit.Material.SIGN_POST);
        FALLOFF_MATERIALS.add(org.bukkit.Material.SNOW);
        FALLOFF_MATERIALS.add(org.bukkit.Material.STONE_BUTTON);
        FALLOFF_MATERIALS.add(org.bukkit.Material.TORCH);
        FALLOFF_MATERIALS.add(org.bukkit.Material.TRAP_DOOR);
        FALLOFF_MATERIALS.add(org.bukkit.Material.TRIPWIRE_HOOK);
        FALLOFF_MATERIALS.add(org.bukkit.Material.VINE);
        FALLOFF_MATERIALS.add(org.bukkit.Material.WATER_LILY);
        FALLOFF_MATERIALS.add(org.bukkit.Material.WHEAT);
        FALLOFF_MATERIALS.add(org.bukkit.Material.WOODEN_DOOR);
        FALLOFF_MATERIALS.add(org.bukkit.Material.WOOD_BUTTON);
        FALLOFF_MATERIALS.add(org.bukkit.Material.YELLOW_FLOWER);

    }

    private final MaterialStateCache<Byte, BukkitMaterialState> cache;

    /**
     * Creates a new {@link BukkitMaterial}.
     * 
     * @param value the material to wrap, cannot be null
     */
    public BukkitMaterial(org.bukkit.Material value)
    {
        super(value);
        this.cache = new MaterialStateCache<Byte, BukkitMaterialState>(new MaterialStateBuilder(this));
    }

    @Override
    public String getName()
    {
        return getThis().name();
    }

    @Override
    public boolean isBlock()
    {
        return getThis().isBlock();
    }

    @Override
    public boolean isAffectedByGravity()
    {
        return getThis().hasGravity();
    }

    @Override
    public boolean isSolid()
    {
        return getThis().isSolid();
    }

    @Override
    public boolean isLiquid()
    {
        return LIQUIDS.contains(getThis());
    }

    @Override
    public boolean isReliantOnEnvironment()
    {
        return FALLOFF_MATERIALS.contains(getThis());
    }

    @Override
    public String toString()
    {
        return "BukkitMaterial {name=" + getName() + "}";
    }

    @Override
    public MaterialState getDefaultState()
    {
        return this.cache.get((byte) 0);
    }

    public MaterialState getState(byte data)
    {
        return this.cache.get(data);
    }
    
    public static class MaterialStateBuilder implements Function<Byte, BukkitMaterialState> {
        
        private final BukkitMaterial mat;
        
        public MaterialStateBuilder(BukkitMaterial mat) {
            this.mat = mat;
        }

        @Override
        public BukkitMaterialState apply(Byte input)
        {
            return new BukkitMaterialState(this.mat, input);
        }
        
    }

}
