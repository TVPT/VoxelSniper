/*
 * Copyright (c) 2015-2016 VoxelBox <http://engine.thevoxelbox.com>.
 * All Rights Reserved.
 */
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.util.TextException;

import org.spongepowered.api.text.Text;

public class BadUsageException extends TextException {

    private static final long serialVersionUID = 1L;

    public BadUsageException(Text text) {
        super(text);
    }

}
