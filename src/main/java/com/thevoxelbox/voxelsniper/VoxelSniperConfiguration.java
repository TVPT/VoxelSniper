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
package com.thevoxelbox.voxelsniper;

/**
 * Configuration storage defining global configurations for VoxelSniper.
 */
public class VoxelSniperConfiguration {

    public static final String PLUGIN_ID = "voxelsniper";
    public static final String PLUGIN_NAME = "VoxelSniper";
    public static final String PLUGIN_VERSION = "8.2.0";
    public static final String PLUGIN_DESC = "Long range terrain editing";

    // Constants
    public static final int CHUNK_SIZE = 16;
    public static final String PERMISSION_SNIPER = "voxelsniper.sniper";
    public static final String PERMISSION_IGNORE_SIZE_LIMITS = "voxelsniper.ignorelimitations";
    public static final String PERMISSION_COMMAND_ENABLE = "voxelsniper.command.vs.enable";
    public static final String PERMISSION_COMMAND_UNDO_OTHER = "voxelsniper.command.uu";

    // Config
    public static double LITESNIPER_MAX_BRUSH_SIZE = 10.5;
    public static int UNDO_CACHE_SIZE = 10;
    public static boolean LOGIN_MESSAGE_ENABLED = true;
    public static int BRUSH_SIZE_WARNING_THRESHOLD = 25;
    public static int SNIPER_CACHE_EXPIRY = -1;

    // @Spongify load from hocon container
}
