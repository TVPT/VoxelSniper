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
package com.voxelplugineering.voxelsniper.sponge.world.material;

import java.util.List;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;

import com.google.common.collect.Lists;
import com.voxelplugineering.voxelsniper.registry.WeakWrapper;
import com.voxelplugineering.voxelsniper.world.material.Material;
import com.voxelplugineering.voxelsniper.world.material.MaterialState;
import com.voxelplugineering.voxelsniper.world.material.MaterialStateCache;

/**
 * Wraps sponge's {@link BlockType}.
 */
public class SpongeMaterial extends WeakWrapper<BlockType> implements Material
{ //TODO I dislike this solution as it does nothing to support BlockTypes registered from other mods at runtime.

    //There is very possibly a better solution that I missed.
    private static List<BlockType> FALLOFF_MATERIALS = Lists.newArrayListWithCapacity(48);

    static
    {
        FALLOFF_MATERIALS.add(BlockTypes.ACTIVATOR_RAIL);
        FALLOFF_MATERIALS.add(BlockTypes.BROWN_MUSHROOM);
        FALLOFF_MATERIALS.add(BlockTypes.CACTUS);
        FALLOFF_MATERIALS.add(BlockTypes.CARPET);
        FALLOFF_MATERIALS.add(BlockTypes.DEADBUSH);
        FALLOFF_MATERIALS.add(BlockTypes.DETECTOR_RAIL);
        FALLOFF_MATERIALS.add(BlockTypes.UNPOWERED_REPEATER);
        FALLOFF_MATERIALS.add(BlockTypes.POWERED_REPEATER);
        FALLOFF_MATERIALS.add(BlockTypes.DOUBLE_PLANT);
        FALLOFF_MATERIALS.add(BlockTypes.FLOWER_POT);
        FALLOFF_MATERIALS.add(BlockTypes.IRON_DOOR);
        FALLOFF_MATERIALS.add(BlockTypes.LADDER);
        FALLOFF_MATERIALS.add(BlockTypes.LEVER);
        FALLOFF_MATERIALS.add(BlockTypes.TALLGRASS);
        FALLOFF_MATERIALS.add(BlockTypes.MELON_STEM);
        FALLOFF_MATERIALS.add(BlockTypes.NETHER_WART);
        FALLOFF_MATERIALS.add(BlockTypes.PISTON_EXTENSION);
        FALLOFF_MATERIALS.add(BlockTypes.PISTON_HEAD);
        FALLOFF_MATERIALS.add(BlockTypes.PORTAL);
        FALLOFF_MATERIALS.add(BlockTypes.POTATOES);
        FALLOFF_MATERIALS.add(BlockTypes.GOLDEN_RAIL);
        FALLOFF_MATERIALS.add(BlockTypes.ACTIVATOR_RAIL);
        FALLOFF_MATERIALS.add(BlockTypes.PUMPKIN_STEM);
        FALLOFF_MATERIALS.add(BlockTypes.RAIL);
        FALLOFF_MATERIALS.add(BlockTypes.RED_MUSHROOM);
        FALLOFF_MATERIALS.add(BlockTypes.RED_FLOWER);
        FALLOFF_MATERIALS.add(BlockTypes.UNPOWERED_COMPARATOR);
        FALLOFF_MATERIALS.add(BlockTypes.POWERED_COMPARATOR);
        FALLOFF_MATERIALS.add(BlockTypes.REDSTONE_TORCH);
        FALLOFF_MATERIALS.add(BlockTypes.REDSTONE_WIRE);
        FALLOFF_MATERIALS.add(BlockTypes.SAPLING);
        FALLOFF_MATERIALS.add(BlockTypes.STANDING_SIGN);
        FALLOFF_MATERIALS.add(BlockTypes.WALL_SIGN);
        FALLOFF_MATERIALS.add(BlockTypes.SNOW_LAYER);
        FALLOFF_MATERIALS.add(BlockTypes.STONE_BUTTON);
        FALLOFF_MATERIALS.add(BlockTypes.TORCH);
        FALLOFF_MATERIALS.add(BlockTypes.TRAPDOOR);
        FALLOFF_MATERIALS.add(BlockTypes.TRIPWIRE_HOOK);
        FALLOFF_MATERIALS.add(BlockTypes.VINE);
        FALLOFF_MATERIALS.add(BlockTypes.WATERLILY);
        FALLOFF_MATERIALS.add(BlockTypes.WHEAT);
        FALLOFF_MATERIALS.add(BlockTypes.DARK_OAK_DOOR);
        FALLOFF_MATERIALS.add(BlockTypes.WOODEN_DOOR);
        FALLOFF_MATERIALS.add(BlockTypes.SPRUCE_DOOR);
        FALLOFF_MATERIALS.add(BlockTypes.BIRCH_DOOR);
        FALLOFF_MATERIALS.add(BlockTypes.ACACIA_DOOR);
        FALLOFF_MATERIALS.add(BlockTypes.WOODEN_BUTTON);
        FALLOFF_MATERIALS.add(BlockTypes.YELLOW_FLOWER);

    }
    
    private final MaterialStateCache<BlockState, SpongeMaterialState> cache;
    
    /**
     * Creates {@link SpongeMaterial}.
     * 
     * @param type The type to wrap
     */
    public SpongeMaterial(BlockType type, MaterialStateCache<BlockState, SpongeMaterialState> cache)
    {
        super(type);
        this.cache = cache;
    }

    @Override
    public String toString()
    {
        return getName();
    }

    @Override
    public boolean isBlock()
    {
        return true;
    }

    @Override
    public boolean isAffectedByGravity()
    {
        return getThis().isAffectedByGravity();
    }

    @Override
    public boolean isSolid()
    {
        return getThis().isSolidCube();
    }

    @Override
    public boolean isLiquid()
    {
        return getThis().isLiquid();
    }

    @Override
    public boolean isReliantOnEnvironment()
    {
        return FALLOFF_MATERIALS.contains(getThis());
    }

    @Override
    public String getName()
    {
        return getThis().getId();
    }

    @Override
    public MaterialState getDefaultState()
    {
        return this.cache.get(getThis().getDefaultState());
    }

    public MaterialState getState(BlockState block)
    {
        return this.cache.get(block);
    }

}
