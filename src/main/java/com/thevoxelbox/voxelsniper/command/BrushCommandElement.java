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
package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.Brushes;
import com.thevoxelbox.voxelsniper.brush.Brush;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class BrushCommandElement extends CommandElement {
    private List<String> brushes;

    protected BrushCommandElement(Text t) {
        super(t);
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        CommandArgs.Snapshot argSnapshot = args.getSnapshot();
        String brushInput = args.next();
        Class<? extends Brush> brush = Brushes.getBrushForHandle(brushInput);

        if (brush == null) {
            args.applySnapshot(argSnapshot);
            throw args.createError(Text.of("Cannot find brush \"" + brushInput + "\""));
        }

        return brush;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        if (brushes == null) {
            brushes = new ArrayList<>(Brushes.getAllBrushes());
        }

        String nextArg = "";
        try {
            nextArg = args.peek();
        } catch (ArgumentParseException ignored) {}

        List<String> possibleBrushes = new ArrayList<>();
        for (String brush : brushes) {
            if (brush.startsWith(nextArg)) {
                possibleBrushes.add(brush);
            }
        }

        return possibleBrushes;
    }
}
