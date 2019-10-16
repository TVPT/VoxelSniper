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

import com.flowpowered.math.vector.Vector3d;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.Brush;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.explosive.fireball.LargeFireball;
import org.spongepowered.api.entity.projectile.explosive.fireball.SmallFireball;

@Brush.BrushInfo(
    name = "Comet",
    aliases = {"com", "comet"},
    permission = "voxelsniper.brush.comet",
    category = Brush.BrushCategory.MISC
)
public class CometBrush extends Brush {

    public CometBrush() {
    }

    private void doFireball(final SnipeData v) {
        Player pl = v.owner().getPlayer();
        Vector3d target = this.targetBlock.getPosition().sub(pl.getLocation().getPosition().add(0, 1.72, 0));
        pl.launchProjectile(SmallFireball.class, target.normalize());
    }

    private void doLargeFireball(final SnipeData v) {
        Player pl = v.owner().getPlayer();
        Vector3d target = this.targetBlock.getPosition().sub(pl.getLocation().getPosition().add(0, 1.72, 0));
        pl.launchProjectile(LargeFireball.class, target.normalize());
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.doLargeFireball(v);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.doFireball(v);
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.info.name());
    }
}
