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
package com.voxelplugineering.voxelsniper.world.material;

import java.util.List;

import org.bukkit.Material;

import com.google.common.collect.Lists;
import com.voxelplugineering.voxelsniper.registry.WeakWrapper;

/**
 * A wrapper for bukkit {@link Material}s.
 */
public class BukkitMaterial extends WeakWrapper<Material> implements com.voxelplugineering.voxelsniper.api.world.material.Material
{

    /**
     * A static set of materials which are liquids.
     */
    private static List<Material> LIQUIDS = Lists.newArrayListWithCapacity(4);
    /**
     * A static set of materials which can break on physics updates.
     */
    private static List<Material> FALLOFF_MATERIALS = Lists.newArrayListWithCapacity(47);

    static
    {
        LIQUIDS.add(Material.WATER);
        LIQUIDS.add(Material.LAVA);
        LIQUIDS.add(Material.STATIONARY_LAVA);
        LIQUIDS.add(Material.STATIONARY_WATER);

        FALLOFF_MATERIALS.add(Material.ACTIVATOR_RAIL);
        FALLOFF_MATERIALS.add(Material.BROWN_MUSHROOM);
        FALLOFF_MATERIALS.add(Material.CACTUS);
        FALLOFF_MATERIALS.add(Material.CARPET);
        FALLOFF_MATERIALS.add(Material.DEAD_BUSH);
        FALLOFF_MATERIALS.add(Material.DETECTOR_RAIL);
        FALLOFF_MATERIALS.add(Material.DIODE_BLOCK_OFF);
        FALLOFF_MATERIALS.add(Material.DIODE_BLOCK_ON);
        FALLOFF_MATERIALS.add(Material.DOUBLE_PLANT);
        FALLOFF_MATERIALS.add(Material.FLOWER_POT);
        FALLOFF_MATERIALS.add(Material.IRON_DOOR);
        FALLOFF_MATERIALS.add(Material.ITEM_FRAME);
        FALLOFF_MATERIALS.add(Material.LADDER);
        FALLOFF_MATERIALS.add(Material.LEVER);
        FALLOFF_MATERIALS.add(Material.LONG_GRASS);
        FALLOFF_MATERIALS.add(Material.MELON_STEM);
        FALLOFF_MATERIALS.add(Material.NETHER_STALK);
        FALLOFF_MATERIALS.add(Material.NETHER_WARTS);
        FALLOFF_MATERIALS.add(Material.PAINTING);
        FALLOFF_MATERIALS.add(Material.PISTON_EXTENSION);
        FALLOFF_MATERIALS.add(Material.PISTON_MOVING_PIECE);
        FALLOFF_MATERIALS.add(Material.PORTAL);
        FALLOFF_MATERIALS.add(Material.POTATO);
        FALLOFF_MATERIALS.add(Material.POWERED_RAIL);
        FALLOFF_MATERIALS.add(Material.PUMPKIN_STEM);
        FALLOFF_MATERIALS.add(Material.RAILS);
        FALLOFF_MATERIALS.add(Material.RED_MUSHROOM);
        FALLOFF_MATERIALS.add(Material.RED_ROSE);
        FALLOFF_MATERIALS.add(Material.REDSTONE_COMPARATOR_OFF);
        FALLOFF_MATERIALS.add(Material.REDSTONE_COMPARATOR_ON);
        FALLOFF_MATERIALS.add(Material.REDSTONE_TORCH_OFF);
        FALLOFF_MATERIALS.add(Material.REDSTONE_TORCH_ON);
        FALLOFF_MATERIALS.add(Material.REDSTONE_WIRE);
        FALLOFF_MATERIALS.add(Material.SAPLING);
        FALLOFF_MATERIALS.add(Material.SIGN);
        FALLOFF_MATERIALS.add(Material.SIGN_POST);
        FALLOFF_MATERIALS.add(Material.SNOW);
        FALLOFF_MATERIALS.add(Material.STONE_BUTTON);
        FALLOFF_MATERIALS.add(Material.TORCH);
        FALLOFF_MATERIALS.add(Material.TRAP_DOOR);
        FALLOFF_MATERIALS.add(Material.TRIPWIRE_HOOK);
        FALLOFF_MATERIALS.add(Material.VINE);
        FALLOFF_MATERIALS.add(Material.WATER_LILY);
        FALLOFF_MATERIALS.add(Material.WHEAT);
        FALLOFF_MATERIALS.add(Material.WOODEN_DOOR);
        FALLOFF_MATERIALS.add(Material.WOOD_BUTTON);
        FALLOFF_MATERIALS.add(Material.YELLOW_FLOWER);

    }

    /**
     * Creates a new {@link BukkitMaterial}.
     * 
     * @param value the material to wrap, cannot be null
     */
    public BukkitMaterial(Material value)
    {
        super(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return getThis().name();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBlock()
    {
        return getThis().isBlock();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAffectedByGravity()
    {
        return getThis().hasGravity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSolid()
    {
        return getThis().isSolid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLiquid()
    {
        return LIQUIDS.contains(getThis());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReliantOnEnvironment()
    {
        return FALLOFF_MATERIALS.contains(getThis());
    }

}
