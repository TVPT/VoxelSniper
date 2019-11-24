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
package com.thevoxelbox.voxelsniper.util;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BlockTraitHelper {
    /* Attempts to parse all block traits in rawKeyValues. If a failure occurs, 
     * sends an error message to src and returns empty.  Otherwise, returns a 
     * mapping from the BlockTraits to the parsed values.
     */
    public static Optional<Map<BlockTrait<?>, Object>> parseKeyValues(Collection<String> rawKeyValues,
                                                                      BlockState target,
                                                                      CommandSource src) {
        assert rawKeyValues != null;
        Map<BlockTrait<?>, Object> traitMap = new HashMap<BlockTrait<?>, Object>();
        for (String rawKeyValue : rawKeyValues) {
            if (rawKeyValue.indexOf('=') == -1) {
                src.sendMessage(Text.of("Unable to parse"));
                return Optional.empty();
            }

            String[] params = rawKeyValue.split("=");
            String key = params[0];
            String value = params[1];

            Optional<BlockTrait<?>> optTrait = target.getTrait(key);
            if (!optTrait.isPresent()) {
                src.sendMessage(Text.of("Unknown block trait '", key, "'"));
                return Optional.empty();
            }

            BlockTrait<?> trait = optTrait.get();
            Optional<?> optValue = trait.parseValue(value);
            if (!optValue.isPresent()) {
                src.sendMessage(Text.of("Unknown value '", value, "' for key '", key, "'"));
                return Optional.empty();
            }
            traitMap.put(optTrait.get(), optValue.get());
        }

        return Optional.of(traitMap);
    }
}
