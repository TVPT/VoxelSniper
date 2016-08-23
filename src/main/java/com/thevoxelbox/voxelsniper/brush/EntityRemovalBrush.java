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
package com.thevoxelbox.voxelsniper.brush;

import com.google.common.collect.Sets;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Chunk;

import java.util.Optional;
import java.util.Set;

public class EntityRemovalBrush extends Brush {

    private static final Set<EntityType> default_exemptions = Sets.newHashSet();

    static {
        default_exemptions.add(EntityTypes.PLAYER);
        default_exemptions.add(EntityTypes.PAINTING);
        default_exemptions.add(EntityTypes.ITEM_FRAME);
        default_exemptions.add(EntityTypes.ARMOR_STAND);
        default_exemptions.add(EntityTypes.ENDER_CRYSTAL);
    }

    private Set<EntityType> special_exemptions;

    public EntityRemovalBrush() {
        this.setName("Entity Removal");
    }

    private void radialRemoval(SnipeData v) {
        int cx = this.targetBlock.getChunkPosition().getX();
        int cz = this.targetBlock.getChunkPosition().getZ();
        int entityCount = 0;
        int chunkCount = 0;

        if (v.getBrushSize() < 16) {
            Optional<Chunk> chunk = this.world.getChunk(cx, 0, cz);
            if (chunk.isPresent()) {
                entityCount += removeEntities(chunk.get());
                chunkCount++;
            }
        } else {
            int radius = (int) Math.ceil(v.getBrushSize() / 16);
            int radiusSquared = radius * radius;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z <= radiusSquared) {
                        Optional<Chunk> chunk = this.world.getChunk(x + cx, 0, z + cz);
                        if (chunk.isPresent()) {
                            entityCount += removeEntities(chunk.get());
                            chunkCount++;
                        }
                    }
                }
            }
        }

        v.sendMessage(TextColors.GREEN, "Removed ", TextColors.RED, entityCount, TextColors.GREEN, " entities out of ", TextColors.BLUE,
                chunkCount, TextColors.GREEN, (chunkCount == 1 ? " chunk." : " chunks."));
    }

    private int removeEntities(Chunk chunk) {
        int entityCount = 0;
        Set<EntityType> exempt = this.special_exemptions;
        if (exempt == null) {
            exempt = default_exemptions;
        }
        for (Entity entity : chunk.getEntities()) {
            EntityType type = entity.getType();
            if (exempt.contains(type)) {
                continue;
            }
            entity.remove();
            entityCount++;
        }

        return entityCount;
    }

    @Override
    protected void arrow(SnipeData v) {
        this.radialRemoval(v);
    }

    @Override
    protected void powder(SnipeData v) {
        this.radialRemoval(v);
    }

    @Override
    public void info(Message vm) {
        vm.brushName(getName());
        Set<EntityType> exempt = this.special_exemptions;
        if (exempt == null) {
            exempt = default_exemptions;
        }
        StringBuilder types = new StringBuilder();
        for (EntityType type : exempt) {
            types.append(", ").append(type.getId());
        }
        vm.custom(TextColors.AQUA, "Exempted entity types:");
        vm.custom(types.toString().substring(2));

        vm.size();
    }

    @Override
    public void parameters(final String[] par, final SnipeData v) {
        if (par.length != 0 && par[0].equalsIgnoreCase("info")) {
            Set<EntityType> exempt = this.special_exemptions;
            if (exempt == null) {
                exempt = default_exemptions;
            }
            StringBuilder types = new StringBuilder();
            for (EntityType type : exempt) {
                types.append(", ").append(type.getId());
            }
            v.sendMessage(TextColors.AQUA, "Exempted entity types:");
            v.sendMessage(types.toString().substring(2));
            return;
        }
        for (final String currentParam : par) {
            if (currentParam.startsWith("+") || currentParam.startsWith("-")) {
                final boolean isAddOperation = currentParam.startsWith("+");
                Optional<EntityType> type = Sponge.getRegistry().getType(EntityType.class, currentParam.substring(1));
                if (type.isPresent()) {
                    if (this.special_exemptions == null) {
                        this.special_exemptions = Sets.newHashSet(default_exemptions);
                    }
                    if (isAddOperation) {
                        this.special_exemptions.add(type.get());
                        v.sendMessage(String.format("Added %s to entity exemptions list.", type.get().getId()));
                    } else {
                        this.special_exemptions.remove(type.get());
                        v.sendMessage(String.format("Removed %s to entity exemptions list.", type.get().getId()));
                    }
                }
            }
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.entityremoval";
    }
}
