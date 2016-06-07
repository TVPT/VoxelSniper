/*
 * Copyright (c) 2015-2016 VoxelBox <http://engine.thevoxelbox.com>.
 * All Rights Reserved.
 */
package com.thevoxelbox.voxelsniper.util;

import org.spongepowered.api.text.Text;

public class TextException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final Text        text;

    public TextException(Text text) {
        super(text.toPlain());
        this.text = text;
    }

    public TextException(Text text, Throwable cause) {
        super(text.toPlain(), cause);
        this.text = text;
    }

    public Text getText() {
        return this.text;
    }

}
