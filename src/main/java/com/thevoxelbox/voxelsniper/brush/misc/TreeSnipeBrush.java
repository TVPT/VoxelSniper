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
package com.thevoxelbox.voxelsniper.brush.misc;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.Brush;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.gen.PopulatorObject;
import org.spongepowered.api.world.gen.type.BiomeTreeType;
import org.spongepowered.api.world.gen.type.BiomeTreeTypes;

import java.util.Optional;
import java.util.Random;

/**
 * Places trees.
 */
@Brush.BrushInfo(
    name = "Tree",
    aliases = {"t", "tree", "treesnipe"},
    permission = "voxelsniper.brush.treesnipe",
    category = Brush.BrushCategory.MISC
)
public class TreeSnipeBrush extends Brush {

    private Random random = new Random();
    private BiomeTreeType treeType = BiomeTreeTypes.OAK;
    private boolean large = false;

    public TreeSnipeBrush() {
    }

    private void single(final SnipeData v, Location<World> targetBlock) {
        // @Robustness how to store the undo for this operations
        // could place the tree into a proxy world and store the undo based on
        // that
        if (this.large) {
            Optional<PopulatorObject> obj = this.treeType.getLargePopulatorObject();
            if (obj.isPresent()) {
                obj.get().placeObject(this.world, this.random, targetBlock.getBlockX(), targetBlock.getBlockY(),
                        targetBlock.getBlockZ());
                return;
            }
        }
        this.treeType.getPopulatorObject().placeObject(this.world, this.random, targetBlock.getBlockX(), targetBlock.getBlockY(),
                targetBlock.getBlockZ());
    }

    private int getYOffset() {
        int y = 1;
        for (int y0 = this.targetBlock.getBlockY() + y; y0 < Brush.WORLD_HEIGHT; y0 = this.targetBlock.getBlockY() + (++y)) {
            if (this.world.getBlockType(this.targetBlock.getBlockX(), y0, this.targetBlock.getBlockZ()) == BlockTypes.AIR) {
                break;
            }
        }
        return y;
    }

    private void printTreeType(final Message vm) {
        vm.custom(TextColors.AQUA, "Currently selected tree type: ", TextColors.GRAY, this.treeType.getId() + (this.large ? "_large" : ""));
    }

    @Override
    protected final void arrow(final SnipeData v) {
        Location<World> targetBlock = this.targetBlock.add(0, getYOffset(), 0);
        this.single(v, targetBlock);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.single(v, this.lastBlock);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.info.name());
        this.printTreeType(vm);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par.length == 0 || par[0].equalsIgnoreCase("info")) {
            v.sendMessage(TextColors.GOLD + "Tree snipe brush:");
            v.sendMessage(TextColors.AQUA + "/b t treetype");
            this.printTreeType(v.getVoxelMessage());
            return;
        } else if (par[0].equalsIgnoreCase("types")) {
            StringBuilder types = new StringBuilder();
            for (BiomeTreeType type : Sponge.getRegistry().getAllOf(BiomeTreeType.class)) {
                types.append(", ").append(type.getId());
                if (type.getLargePopulatorObject().isPresent()) {
                    types.append(", ").append(type.getId() + "_large");
                }
            }
            v.sendMessage(TextColors.AQUA, "Available tree types:");
            v.sendMessage(types.toString().substring(2));
            return;
        }
        String typename = par[0];
        boolean large = false;
        if (typename.endsWith("_large")) {
            typename = typename.substring(0, typename.length() - 6);
            large = true;
        }
        Optional<BiomeTreeType> tree = Sponge.getRegistry().getType(BiomeTreeType.class, typename);
        if (tree.isPresent()) {
            this.treeType = tree.get();
            this.large = false;
            if (large) {
                if (!this.treeType.getLargePopulatorObject().isPresent()) {
                    v.sendMessage(TextColors.RED, "No large tree for that type");
                    return;
                }
                this.large = true;
            }
        } else {
            v.sendMessage(TextColors.RED, "Tree type not found. Use '/b tree types' to list all types.");
        }
    }
}
