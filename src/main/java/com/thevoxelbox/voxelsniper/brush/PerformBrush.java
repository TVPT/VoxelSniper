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
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.Optional;

public abstract class PerformBrush extends Brush {

    protected PerformerType place = PerformerType.TYPE;
    protected PerformerType replace = PerformerType.NONE;

    public void parse(String[] args, SnipeData v) {
        String handle = args[0];
        if (handle.length() == 1 || handle.length() == 2) {
            PerformerType pl = null;
            char p = handle.charAt(0);
            if (p == 'm' || p == 'M') {
                pl = PerformerType.TYPE;
            } else if (p == 'i' || p == 'I') {
                pl = PerformerType.STATE;
                v.sendMessage(TextColors.RED, "Warning: Ink performers are currently unsupported in version 8.0.0 due to changes in world format");
            } else if (p == 'c' || p == 'C') {
                pl = PerformerType.COMBO;
            } else {
                parameters(args, v);
                return;
            }
            if (handle.length() == 2) {
                char r = handle.charAt(1);
                if (r == 'm' || r == 'M') {
                    this.replace = PerformerType.TYPE;
                } else if (r == 'i' || r == 'I') {
                    this.replace = PerformerType.STATE;
                    v.sendMessage(TextColors.RED,
                            "Warning: Ink performers are currently unsupported in version 8.0.0 due to changes in world format");
                } else if (r == 'c' || r == 'C') {
                    this.replace = PerformerType.COMBO;
                } else {
                    parameters(args, v);
                    return;
                }
            } else {
                this.replace = PerformerType.NONE;
            }
            // we defer setting the place performer until here incase the replace doesn't match and we want to pass the args to the brush instead
            this.place = pl;
            parameters(Arrays.copyOfRange(args, 1, args.length), v);
        } else {
            parameters(args, v);
        }
    }

    public void showInfo(Message vm) {
        String name = this.place.name().toLowerCase();
        if (this.replace != PerformerType.NONE) {
            name += " " + this.replace.name().toLowerCase();
        }
        vm.performerName(name);
        vm.voxel();
        if (this.replace != PerformerType.NONE) {
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
        if (this.replace != PerformerType.NONE) {
            BlockState current = this.world.getBlock(x, y, z);
            switch (this.replace) {
                case TYPE:
                    if (current.getType() != v.getReplaceIdState().getType()) {
                        return false;
                    }
                    break;
                case STATE:
                    // @Todo filter by key and value
                    break;
                case COMBO:
                    if (current != v.getReplaceIdState()) {
                        return false;
                    }
                    break;
                case NONE:
                default:
                    break;
            }
        }
        switch (this.place) {
            case TYPE:
                setBlockType(x, y, z, v.getVoxelIdState().getType());
                break;
            case STATE:
                BlockState current = this.world.getBlock(x, y, z);
                @SuppressWarnings({"unchecked", "rawtypes"})
                Optional<BlockState> place = current.with((Key) v.getVoxelInkKey(), v.getVoxelInkValue());
                if (!place.isPresent()) {
                    return false;
                }
                setBlockState(x, y, z, place.get());
                break;
            case COMBO:
                setBlockState(x, y, z, v.getVoxelIdState());
                break;
            case NONE:
            default:
                throw new IllegalStateException("Unsupported place type " + this.place.name());
        }
        return true;
    }

    public static enum PerformerType {
        TYPE,
        STATE,
        COMBO,
        NONE;
    }
}
