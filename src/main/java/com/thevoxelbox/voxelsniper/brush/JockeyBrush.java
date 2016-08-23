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

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.manipulator.mutable.entity.PassengerData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.format.TextColors;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public class JockeyBrush extends Brush {

    private boolean inverse = false;
    private WeakReference<Entity> sittingEntity;

    public JockeyBrush() {
        this.setName("Jockey");
    }

    @Override
    protected final void arrow(final SnipeData v) {
        if (this.sittingEntity != null && this.sittingEntity.get() != null) {
            Entity entity = this.sittingEntity.get();
            Optional<PassengerData> data = entity.get(PassengerData.class);
            if (data.isPresent()) {
                for (Iterator<EntitySnapshot> it = data.get().passengers().iterator(); it.hasNext();) {
                    EntitySnapshot e = it.next();
                    if (e.getType() == EntityTypes.PLAYER) {
                        Optional<Entity> re = e.restore();
                        if (re.isPresent() && re.get() == v.owner().getPlayer()) {
                            it.remove();
                            break;
                        }
                    }
                }
                entity.offer(data.get());
            }
            this.sittingEntity = null;
        }
        double distance = Double.MAX_VALUE;
        Entity nearest = null;
        Vector3d target = this.targetBlock.getPosition();
        Collection<Entity> entities = this.inverse ? this.world.getEntities((e) -> e instanceof Living) : this.world.getEntities();
        for (Entity e : entities) {
            double dist = e.getLocation().getPosition().distanceSquared(target);
            if (dist < distance) {
                nearest = e;
            }
        }
        if (nearest != null) {
            Optional<PassengerData> data = nearest.getOrCreate(PassengerData.class);
            if (data.isPresent()) {
                PassengerData passengers = data.get();
                passengers.addElement(v.owner().getPlayer().createSnapshot());
                nearest.offer(passengers);
            }
            this.sittingEntity = new WeakReference<>(nearest);
        }
    }

    @Override
    protected final void powder(final SnipeData v) {
        v.owner().getPlayer().remove(PassengerData.class);
        if (this.sittingEntity != null && this.sittingEntity.get() != null) {
            Entity entity = this.sittingEntity.get();
            Optional<PassengerData> data = entity.get(PassengerData.class);
            if (data.isPresent()) {
                for (Iterator<EntitySnapshot> it = data.get().passengers().iterator(); it.hasNext();) {
                    EntitySnapshot e = it.next();
                    if (e.getType() == EntityTypes.PLAYER) {
                        Optional<Entity> re = e.restore();
                        if (re.isPresent() && re.get() == v.owner().getPlayer()) {
                            it.remove();
                            break;
                        }
                    }
                }
                entity.offer(data.get());
            }
            this.sittingEntity = null;
        }
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.custom(TextColors.AQUA, "Inverse jockey mode: ", this.inverse ? TextColors.GREEN : TextColors.RED, this.inverse ? "on" : "off");
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par.length > 0 && par[0].equalsIgnoreCase("inverse")) {
            this.inverse = true;
            v.sendMessage(TextColors.GREEN, "Inverse jockey mode enabled");
        } else {
            this.inverse = false;
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.jockey";
    }
}
