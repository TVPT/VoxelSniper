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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.util.BlockHelper;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PerformBrush extends Brush {

    private static final Pattern PERFORMER = Pattern.compile("([mic])([micnx]?)(p?)");

    protected PerformerType placeMethod = PerformerType.TYPE;
    protected PerformerType replaceMethod = PerformerType.NONE;
    protected boolean usePhysics = true;

    public void parse(String[] args, SnipeData v) {
        String handle = args[0].toLowerCase();
        Matcher performersMatch = PERFORMER.matcher(handle);

        if (performersMatch.matches()) {
            placeMethod = stringToMethod(performersMatch.group(1));
            if (placeMethod == PerformerType.NONE) {
                throw new IllegalArgumentException("Unknown placement method '" + performersMatch.group(1) + "'");
            }

            replaceMethod = stringToMethod(performersMatch.group(2));
            usePhysics = !performersMatch.group(3).equals("p");

            parameters(Arrays.copyOfRange(args, 1, args.length), v);
        } else {
            parameters(args, v);
        }

        v.getVoxelMessage().performerData(placeMethod, replaceMethod, usePhysics);
    }

    private PerformerType stringToMethod(String rawMethod) {
        switch (rawMethod) {
            case "m":
                return PerformerType.TYPE;
            case "i":
                return PerformerType.TRAITS;
            case "c":
                return PerformerType.COMBO;
            case "n":
                return PerformerType.LIST_INCLUDE;
            case "x":
                return PerformerType.LIST_EXCLUDE;
        }
        return PerformerType.NONE;
    }

    public void showInfo(Message vm) {
        String name = placeMethod.name().toLowerCase();
        if (replaceMethod != PerformerType.NONE) {
            name += replaceMethod.name().toLowerCase();
        }
        vm.performerData(placeMethod, replaceMethod, usePhysics);
        vm.voxel();
        if (replaceMethod != PerformerType.NONE) {
            vm.replace();
        }
    }

    protected boolean perform(SnipeData v, Location<World> pos) {
        return perform(v, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
    }

    protected boolean perform(SnipeData v, int x, int y, int z) {
        if (y < 0 || y >= Brush.WORLD_HEIGHT) {
            return false;
        }

        BlockState current = this.world.getBlock(x, y, z);
        switch (replaceMethod) {
            case TYPE:
                if (!sameBlockType(current, v.getReplaceState())) {
                    return false;
                }
                break;
            case TRAITS:
                if (!BlockHelper.hasTraits(current, v.getReplaceInkTraits())) {
                    return false;
                }
                break;
            case COMBO:
                if (!sameBlockType(current, v.getReplaceState()) ||
                    !BlockHelper.hasTraits(current, v.getReplaceInkTraits())) {
                    return false;
                }
                break;
            case LIST_INCLUDE:
                if (!v.getVoxelList().contains(current)) {
                    return false;
                }
                break;
            case LIST_EXCLUDE:
                if (v.getVoxelList().contains(current)) {
                    return false;
                }
                break;
            case NONE:
            default:
                break;
        }

        BlockChangeFlag physicsFlags = usePhysics ? BlockChangeFlags.ALL : BlockChangeFlags.NONE;
        switch (placeMethod) {
            case TYPE:
                setBlockType(x, y, z, v.getVoxelState().getType(), physicsFlags);
                break;
            case TRAITS:
                BlockState place = BlockHelper.addTraits(current, v.getVoxelInkTraits());
                setBlockState(x, y, z, place, physicsFlags);
                break;
            case COMBO:
                setBlockState(x, y, z, v.getVoxelState(), physicsFlags);
                break;
            case NONE:
            default:
                throw new IllegalStateException("Unsupported place type " + placeMethod.name());
        }
        return true;
    }

    private boolean sameBlockType(BlockState a, BlockState b) {
        return a.getType().equals(b.getType());
    }

    public enum PerformerType {
        TYPE,
        TRAITS,
        COMBO,
        LIST_INCLUDE,
        LIST_EXCLUDE,
        NONE;

        public String toString() {
            switch (this) {
                case TRAITS:
                    return "Ink";
                case TYPE:
                    return "Material";
                case COMBO:
                    return "Combo";
                case LIST_INCLUDE:
                    return "List Include";
                case LIST_EXCLUDE:
                    return "List Exclude";
                case NONE:
                    return "None";
            }

            return "Unknown";
        }
    }
}
