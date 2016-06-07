/*
 * Copyright (c) 2015-2016 VoxelBox <http://engine.thevoxelbox.com>.
 * All Rights Reserved.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.player.PlayerData;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

public abstract class ModeBrush<B extends ModeBrush<B>> implements Brush<B> {

    private Mode place   = Mode.TYPE;
    private Mode replace = Mode.NONE;

    @Override
    public B create(String args) {
        if (args == null || args.isEmpty()) {
            throw new BadUsageException(Text.of("Brush requires a performer"));
        }
        String otherArgs = "";
        if (args.indexOf(' ') != -1) {
            otherArgs = args.substring(args.indexOf(' ') + 1);
            args = args.substring(0, args.indexOf(' '));
        }
        Mode place = charToMode(args.charAt(0));
        Mode replace = Mode.NONE;
        if (args.length() > 1) {
            replace = charToMode(args.charAt(1));
        }
        B brush = consumeArgs(otherArgs);
        ((ModeBrush<?>) brush).place = place;
        ((ModeBrush<?>) brush).replace = replace;
        return brush;
    }

    private Mode charToMode(char c) {
        if (c == 'm') {
            return Mode.TYPE;
        } else if (c == 'i') {
            return Mode.STATE;
        } else if (c == 'c') {
            return Mode.COMBO;
        }
        return Mode.NONE;
    }

    protected abstract B consumeArgs(String args);

    public void perform(PlayerData data, World world, int x, int y, int z, Cause cause) {
        if (x < -30000000 || x > 29999999 || y < 0 || y > 255 || z < -30000000 || z > 29999999) {
            return;
        }
        if (this.replace != Mode.NONE) {
            BlockState current = world.getBlock(x, y, z);
            if (this.replace == Mode.TYPE) {
                if (!current.getType().equals(data.getReplaceMaterial().getType())) {
                    return;
                }
            } else if (this.replace == Mode.COMBO) {
                if (!current.equals(data.getReplaceMaterial())) {
                    return;
                }
            } else if (this.replace == Mode.STATE) {
                // TODO check only keys not type
            }
            if (this.place == Mode.STATE) {
                // TODO change only keys not type
                return;
            }
        }
        if (this.place == Mode.TYPE) {
            world.setBlockType(x, y, z, data.getMaterial().getType());
            return;
        } else if (this.place == Mode.COMBO) {
            world.setBlock(x, y, z, data.getMaterial());
            return;
        } else if (this.place == Mode.STATE) {
            // TODO change only keys not type
            return;
        } else if (this.place == Mode.NONE) {
            throw new IllegalStateException("Place mode cannot be none");
        }

    }

    private static enum Mode {
        NONE,
        TYPE,
        STATE,
        COMBO;
    }

}