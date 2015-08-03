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
package com.voxelplugineering.voxelsniper.forge.world.material;

import java.util.List;

import com.google.common.collect.Lists;
import com.voxelplugineering.voxelsniper.registry.WeakWrapper;
import com.voxelplugineering.voxelsniper.world.material.Material;
import com.voxelplugineering.voxelsniper.world.material.MaterialState;
import com.voxelplugineering.voxelsniper.world.material.MaterialStateCache;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

/**
 * A wrapper for forge's materials ({@link net.minecraft.block.Block}).
 */
public class ForgeMaterial extends WeakWrapper<net.minecraft.block.Block> implements Material
{

    //TODO I dislike this solution as it does nothing to support blocks registered from other mods at runtime.
    //There is very possibly a better solution that I missed.
    private static List<net.minecraft.block.Block> FALLOFF_MATERIALS = Lists.newArrayListWithCapacity(48);

    static
    {

        FALLOFF_MATERIALS.add(Blocks.activator_rail);
        FALLOFF_MATERIALS.add(Blocks.brown_mushroom);
        FALLOFF_MATERIALS.add(Blocks.cactus);
        FALLOFF_MATERIALS.add(Blocks.carpet);
        FALLOFF_MATERIALS.add(Blocks.deadbush);
        FALLOFF_MATERIALS.add(Blocks.detector_rail);
        FALLOFF_MATERIALS.add(Blocks.unpowered_repeater);
        FALLOFF_MATERIALS.add(Blocks.powered_repeater);
        FALLOFF_MATERIALS.add(Blocks.double_plant);
        FALLOFF_MATERIALS.add(Blocks.flower_pot);
        FALLOFF_MATERIALS.add(Blocks.iron_door);
        FALLOFF_MATERIALS.add(Blocks.ladder);
        FALLOFF_MATERIALS.add(Blocks.lever);
        FALLOFF_MATERIALS.add(Blocks.tallgrass);
        FALLOFF_MATERIALS.add(Blocks.melon_stem);
        FALLOFF_MATERIALS.add(Blocks.nether_wart);
        FALLOFF_MATERIALS.add(Blocks.piston_extension);
        FALLOFF_MATERIALS.add(Blocks.piston_head);
        FALLOFF_MATERIALS.add(Blocks.portal);
        FALLOFF_MATERIALS.add(Blocks.potatoes);
        FALLOFF_MATERIALS.add(Blocks.golden_rail);
        FALLOFF_MATERIALS.add(Blocks.activator_rail);
        FALLOFF_MATERIALS.add(Blocks.pumpkin_stem);
        FALLOFF_MATERIALS.add(Blocks.rail);
        FALLOFF_MATERIALS.add(Blocks.red_mushroom);
        FALLOFF_MATERIALS.add(Blocks.red_flower);
        FALLOFF_MATERIALS.add(Blocks.unpowered_comparator);
        FALLOFF_MATERIALS.add(Blocks.powered_comparator);
        FALLOFF_MATERIALS.add(Blocks.redstone_torch);
        FALLOFF_MATERIALS.add(Blocks.redstone_wire);
        FALLOFF_MATERIALS.add(Blocks.sapling);
        FALLOFF_MATERIALS.add(Blocks.standing_sign);
        FALLOFF_MATERIALS.add(Blocks.wall_sign);
        FALLOFF_MATERIALS.add(Blocks.snow);
        FALLOFF_MATERIALS.add(Blocks.stone_button);
        FALLOFF_MATERIALS.add(Blocks.torch);
        FALLOFF_MATERIALS.add(Blocks.trapdoor);
        FALLOFF_MATERIALS.add(Blocks.tripwire_hook);
        FALLOFF_MATERIALS.add(Blocks.vine);
        FALLOFF_MATERIALS.add(Blocks.waterlily);
        FALLOFF_MATERIALS.add(Blocks.wheat);
        FALLOFF_MATERIALS.add(Blocks.dark_oak_door);
        FALLOFF_MATERIALS.add(Blocks.oak_door);
        FALLOFF_MATERIALS.add(Blocks.spruce_door);
        FALLOFF_MATERIALS.add(Blocks.birch_door);
        FALLOFF_MATERIALS.add(Blocks.acacia_door);
        FALLOFF_MATERIALS.add(Blocks.wooden_button);
        FALLOFF_MATERIALS.add(Blocks.yellow_flower);

    }
    
    private final MaterialStateCache<IBlockState, ForgeMaterialState> cache;

    /**
     * Creates a new {@link ForgeMaterial}.
     * 
     * @param block the block to wrap
     */
    public ForgeMaterial(net.minecraft.block.Block block, MaterialStateCache<IBlockState, ForgeMaterialState> cache)
    {
        super(block);
        this.cache = cache;
    }

    @Override
    public String toString()
    {
        ResourceLocation rs = (ResourceLocation) Block.blockRegistry.getNameForObject(getThis());
        return (!rs.getResourceDomain().equals("minecraft")? rs.getResourceDomain()+":" : "") + rs.getResourcePath();
    }

    @Override
    public boolean isBlock()
    {
        return true;
    }

    @Override
    public boolean isAffectedByGravity()
    {
        return getThis() instanceof net.minecraft.block.BlockFalling;
    }

    @Override
    public boolean isSolid()
    {
        return getThis().getMaterial().isSolid();
    }

    @Override
    public boolean isLiquid()
    {
        return getThis().getMaterial().isLiquid();
    }

    @Override
    public boolean isReliantOnEnvironment()
    {
        return FALLOFF_MATERIALS.contains(getThis());
    }

    @Override
    public String getName()
    {
        ResourceLocation rs = (ResourceLocation) Block.blockRegistry.getNameForObject(getThis());
        return (!rs.getResourceDomain().equals("minecraft")? rs.getResourceDomain()+":" : "") + rs.getResourcePath();
    }

    @Override
    public MaterialState getDefaultState()
    {
        return this.cache.get(getThis().getDefaultState());
    }

    public MaterialState getState(IBlockState state)
    {
        return this.cache.get(state);
    }

}
