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
package com.thevoxelbox.voxelsniper.util;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.property.block.MatterProperty;
import org.spongepowered.api.data.property.block.MatterProperty.Matter;
import org.spongepowered.api.data.property.block.SolidCubeProperty;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.Optional;

public class BlockHelper {

    public static boolean isLiquid(BlockState state) {
        Optional<MatterProperty> matter = state.getProperty(MatterProperty.class);
        if (matter.isPresent()) {
            Matter m = matter.get().getValue();
            if (m == Matter.LIQUID) {
                return true;
            }
        }
        return false;
    }

    public static boolean isLiquidOrGas(BlockState state) {
        Optional<MatterProperty> matter = state.getProperty(MatterProperty.class);
        if (matter.isPresent()) {
            Matter m = matter.get().getValue();
            if (m != Matter.SOLID) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSolid(BlockState state) {
        Optional<SolidCubeProperty> matter = state.getProperty(SolidCubeProperty.class);
        if (matter.isPresent()) {
            return matter.get().getValue();
        }
        return false;
    }

    public static boolean hasTraits(BlockState state, Map<BlockTrait<?>, Object> traits) {
        for (Map.Entry<BlockTrait<?>, ?> trait : state.getTraitMap().entrySet()) {
            Object expectedValue = traits.get(trait.getKey());
            if (!trait.getValue().equals(expectedValue)) {
                return false;
            }
        }

        return true;
    }

    public static BlockState addTraits(BlockState state, Map<BlockTrait<?>, Object> traits) {
        Optional<BlockState> nextState;
        for (Map.Entry<BlockTrait<?>, Object> traitEntry : traits.entrySet()) {
            nextState = state.withTrait(traitEntry.getKey(), traitEntry.getValue());
            if (nextState.isPresent()) {
                state = nextState.get();
            }
        }

        return state;
    }

    public static Optional<BlockState> stateOrWhereLooking(Optional<String> rawState, Player player) {
        if (rawState.isPresent()) {
            return Sponge.getRegistry().getType(BlockState.class, rawState.get());
        }

        Location<World> targetBlock = null;
        BlockRay.BlockRayBuilder<World> rayBuilder =
                BlockRay.from(player).stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1));
        BlockRay<World> ray = rayBuilder.build();
        while (ray.hasNext()) {
            targetBlock = ray.next().getLocation();
        }

        return Optional.of(targetBlock.getBlock());
    }
}
