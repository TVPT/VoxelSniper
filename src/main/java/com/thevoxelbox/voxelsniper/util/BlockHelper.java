/*
 * Copyright (c) 2015-2016 VoxelBox <http://engine.thevoxelbox.com>.
 * All Rights Reserved.
 */
package com.thevoxelbox.voxelsniper.util;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.property.block.MatterProperty;
import org.spongepowered.api.data.property.block.MatterProperty.Matter;
import org.spongepowered.api.data.property.block.SolidCubeProperty;

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

}
