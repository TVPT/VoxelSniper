/*
 * This file is part of VoxelSniper, licensed under the MIT License (MIT).
 *
 * Copyright (c) The VoxelBox <http://thevoxelbox.com>
 * Copyright (c) contributors
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
package com.thevoxelbox.voxelsniper;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Set;

/**
 * Holds {@link BlockState}s that can be later on used to reset those block
 * locations back to the recorded states.
 */
public class Undo {

    private static final Set<BlockType> FALLING_MATERIALS = Sets.newHashSet(
            BlockTypes.WATER,
            BlockTypes.FLOWING_WATER,
            BlockTypes.LAVA,
            BlockTypes.FLOWING_LAVA,
            BlockTypes.SAND,
            BlockTypes.GRAVEL,
            BlockTypes.ANVIL);
    private static final Set<BlockType> FALLOFF_MATERIALS = Sets.newHashSet(
            BlockTypes.SAPLING,
            BlockTypes.BED,
            BlockTypes.GOLDEN_RAIL,
            BlockTypes.DETECTOR_RAIL,
            BlockTypes.TALLGRASS,
            BlockTypes.DEADBUSH,
            BlockTypes.PISTON_EXTENSION,
            BlockTypes.YELLOW_FLOWER,
            BlockTypes.RED_FLOWER,
            BlockTypes.BROWN_MUSHROOM,
            BlockTypes.RED_MUSHROOM,
            BlockTypes.TORCH,
            BlockTypes.FIRE,
            BlockTypes.WHEAT,
            BlockTypes.STANDING_SIGN,
            BlockTypes.WOODEN_DOOR,
            BlockTypes.LADDER,
            BlockTypes.RAIL,
            BlockTypes.WALL_SIGN,
            BlockTypes.LEVER,
            BlockTypes.STONE_PRESSURE_PLATE,
            BlockTypes.IRON_DOOR,
            BlockTypes.WOODEN_PRESSURE_PLATE,
            BlockTypes.REDSTONE_TORCH,
            BlockTypes.UNLIT_REDSTONE_TORCH,
            BlockTypes.REDSTONE_WIRE,
            BlockTypes.STONE_BUTTON,
            BlockTypes.SNOW,
            BlockTypes.CACTUS,
            BlockTypes.REEDS,
            BlockTypes.CAKE,
            BlockTypes.POWERED_REPEATER,
            BlockTypes.UNPOWERED_REPEATER,
            BlockTypes.TRAPDOOR,
            BlockTypes.PUMPKIN_STEM,
            BlockTypes.MELON_STEM,
            BlockTypes.VINE,
            BlockTypes.WATERLILY,
            BlockTypes.NETHER_WART,
            BlockTypes.POWERED_COMPARATOR,
            BlockTypes.UNPOWERED_COMPARATOR,
            BlockTypes.DOUBLE_PLANT);
    private final Set<Vector3i> containing = Sets.newHashSet();
    private final List<BlockSnapshot> all;
    private final List<BlockSnapshot> falloff;
    private final List<BlockSnapshot> dropdown;

    // @Performance this should use the brushes knowledge of the affected area
    // to create some kind of masked archetype volume of the area

    /**
     * Default constructor of a Undo container.
     */
    public Undo(int suggested_size) {
        this.all = Lists.newArrayListWithExpectedSize(suggested_size);
        this.falloff = Lists.newArrayList();
        this.dropdown = Lists.newArrayList();
    }

    /**
     * Get the number of blocks in the collection.
     *
     * @return size of the Undo collection
     */
    public int getSize() {
        return this.all.size() + this.falloff.size() + this.dropdown.size();
    }

    /**
     * Adds a Block to the collection.
     *
     * @param block Block to be added
     */
    public void put(Location<World> block) {
        Vector3i pos = block.getBlockPosition();
        if (this.containing.contains(pos)) {
            return;
        }
        this.containing.add(pos);
        if (Undo.FALLING_MATERIALS.contains(block.getBlockType())) {
            this.dropdown.add(block.createSnapshot());
        } else if (Undo.FALLOFF_MATERIALS.contains(block.getBlockType())) {
            this.falloff.add(block.createSnapshot());
        } else {
            this.all.add(block.createSnapshot());
        }
    }

    /**
     * Set the blockstates of all recorded blocks back to the state when they
     * were inserted.
     */
    public void undo() {

        for (BlockSnapshot blockState : this.all) {
            blockState.restore(true, BlockChangeFlags.NEIGHBOR);
        }

        for (BlockSnapshot blockState : this.falloff) {
            blockState.restore(true, BlockChangeFlags.NEIGHBOR);
        }

        for (BlockSnapshot blockState : this.dropdown) {
            blockState.restore(true, BlockChangeFlags.NEIGHBOR);
        }
    }
}
