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
import com.thevoxelbox.voxelsniper.SnipeAction;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Abstract implementation of the {@link IBrush} interface.
 */
public abstract class Brush implements IBrush {

    protected static int WORLD_HEIGHT =
            (Sponge.getServer().getChunkLayout().getSpaceMax().getY() + 1) * Sponge.getServer().getChunkLayout().getChunkSize().getY();

    public static Location<World> clampY(World world, int x, int y, int z) {
        if (y < 0) {
            y = 0;
        } else if (y > WORLD_HEIGHT) {
            y = WORLD_HEIGHT;
        }

        return new Location<>(world, x, y, z);
    }

    protected World world;
    protected Location<World> targetBlock;
    protected Location<World> lastBlock;
    protected Cause cause;
    protected Undo undo;
    private String name = "Undefined";

    @Override
    public void perform(SnipeAction action, SnipeData data, Location<World> targetBlock, Location<World> lastBlock) {
        this.world = targetBlock.getExtent();
        this.targetBlock = targetBlock;
        this.lastBlock = lastBlock;
        this.cause = VoxelSniper.plugin_cause.with(NamedCause.source(data.owner().getPlayer()));
        switch (action) {
            case ARROW:
                this.arrow(data);
                break;
            case GUNPOWDER:
                this.powder(data);
                break;
            default:
        }
        this.cause = null;
        this.world = null;
        this.targetBlock = null;
        this.lastBlock = null;
    }

    /**
     * The arrow action. Executed when a player RightClicks with an Arrow
     *
     * @param v Sniper caller
     */
    protected void arrow(final SnipeData v) {
    }

    /**
     * The powder action. Executed when a player RightClicks with Gunpowder
     *
     * @param v Sniper caller
     */
    protected void powder(final SnipeData v) {
    }

    @Override
    public abstract void info(Message vm);

    @Override
    public void parameters(final String[] par, final SnipeData v) {
        // @Usability support a --no-undo parameter flag
        v.sendMessage(TextColors.RED, "This brush does not accept additional parameters.");
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getBrushCategory() {
        return "General";
    }

    protected void setBlockType(int x, int y, int z, BlockType type) {
        setBlockType(x, y, z, type, true);
    }

    protected void setBlockType(int x, int y, int z, BlockType type, boolean flag) {
        // Don't store undos if we aren't changing the block
        if (this.world.getBlockType(x, y, z) == type) {
            return;
        }
        if (this.undo != null) {
            this.undo.put(new Location<World>(this.world, x, y, z));
        }
        this.world.setBlockType(x, y, z, type, flag, this.cause);
    }

    protected void setBlockState(int x, int y, int z, BlockState type) {
        setBlockState(x, y, z, type, true);
    }

    protected void setBlockState(int x, int y, int z, BlockState type, boolean flag) {
        // Don't store undos if we aren't changing the block
        if (this.world.getBlock(x, y, z) == type) {
            return;
        }
        if (this.undo != null) {
            this.undo.put(new Location<World>(this.world, x, y, z));
        }
        this.world.setBlock(x, y, z, type, flag, this.cause);
    }
}
