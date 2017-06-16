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

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuration storage defining global configurations for VoxelSniper.
 */
public class VoxelSniperConfiguration {

    public static final String PLUGIN_ID = "voxelsniper";
    public static final String PLUGIN_NAME = "VoxelSniper";
    public static final String PLUGIN_VERSION = "8.5.0-SNAPSHOT";
    public static final String PLUGIN_DESC = "Long range terrain editing";

    // Constants
    public static final int CHUNK_SIZE = 16;
    public static final String PERMISSION_SNIPER = "voxelsniper.sniper";
    public static final String PERMISSION_IGNORE_SIZE_LIMITS = "voxelsniper.ignorelimitations";
    public static final String PERMISSION_COMMAND_ENABLE = "voxelsniper.command.vs.enable";
    public static final String PERMISSION_COMMAND_UNDO_OTHER = "voxelsniper.command.uu";

    // @formatter:off
    // Config
    @ConfigValue(name = "litesniper.max_brush_size", comment = "Maximum brush size for litesniper users")
    public static double LITESNIPER_MAX_BRUSH_SIZE = 10.5;
    @ConfigValue(name = "undo_cache_size", comment = "The maximum number of undo actions stored per player")
    public static int UNDO_CACHE_SIZE = 10;
    @ConfigValue(name = "login_message_enabled", comment = "Whether a player's current sniper settings are displayed to them on login")
    public static boolean LOGIN_MESSAGE_ENABLED = true;
    @ConfigValue(name = "brush_size_warning_threshold", comment = "A theshold for displaying a warning to a player when they select a large brush size (default: 25)")
    public static int BRUSH_SIZE_WARNING_THRESHOLD = 25;
    @ConfigValue(name = "sniper_cache_expiry", comment = "A player who has not been active will have their settings reset after this time in seconds (default: -1 (no expiry))")
    public static int SNIPER_CACHE_EXPIRY = -1;

    @ConfigValue(name = "defaults.cylinder_center", comment = "The default cylinder center value (default: 0)")
    public static int DEFAULT_CYLINDER_CENTER = 0;
    @ConfigValue(name = "defaults.voxel_height", comment = "The default voxel height value (default: 1)")
    public static int DEFAULT_VOXEL_HEIGHT = 1;
    @ConfigValue(name = "defaults.brush_size", comment = "The default brush size (default: 3.5)")
    public static double DEFAULT_BRUSH_SIZE = 3.5;
    @ConfigValue(name = "defaults.replace_material", comment = "The default replace material (default: air)")
    public static String DEFAULT_REPLACE_ID = "air";
    @ConfigValue(name = "defaults.voxel_material", comment = " The default voxel material (default: air)")
    public static String DEFAULT_VOXEL_ID = "air";

    @ConfigValue(name = "messages.no_brush")
    public static String MESSAGE_NO_BRUSH = "&4No brush selected.";
    @ConfigValue(name = "messages.current_tool")
    public static String MESSAGE_CURRENT_TOOL = "&9Current tool: {0}";
    @ConfigValue(name = "messages.undo_successful")
    public static String MESSAGE_UNDO_SUCCESSFUL = "&aUndo successful: &c{0} &ablocks have been replaced.";
    @ConfigValue(name = "messages.nothing_to_undo")
    public static String MESSAGE_NOTHING_TO_UNDO = "&aThere's nothing to undo.";
    @ConfigValue(name = "messages.brush_error")
    public static String MESSAGE_BRUSH_ERROR = "&4Error performing brush operation, see console for details.";
    @ConfigValue(name = "messages.snipe_target_missed")
    public static String MESSAGE_SNIPE_TARGET_NOT_VISIBLE = "&cSnipe target block must be visible.";
    @ConfigValue(name = "messages.brush_permission_error")
    public static String MESSAGE_BRUSH_PERMISSION_ERROR = "&cYou are not allowed to use this brush. You're missing the permission node '{0}'";
    // @formatter:on

    public static void createDefaults(CommentedConfigurationNode config) {
        for (Field field : VoxelSniperConfiguration.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigValue.class)) {
                ConfigValue anno = field.getAnnotation(ConfigValue.class);
                CommentedConfigurationNode node = config.getNode((Object[]) anno.name().split("\\."));
                node.setComment(anno.comment());
                try {
                    node.setValue(field.get(null));
                    VoxelSniper.getLogger().debug("Created " + anno.name() + " = " + node.getValue());
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    VoxelSniper.getLogger().error("Failed to extract default value for configuration field " + field.toGenericString());
                    e.printStackTrace();
                }
            }
        }
    }

    public static void init(Path config) {
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setPath(config).build();
        if (!Files.exists(config)) {
            CommentedConfigurationNode node = loader.createEmptyNode();
            createDefaults(node);
            try {
                Files.createDirectories(config.toAbsolutePath().getParent());
                loader.save(node);
            } catch (IOException e) {
                VoxelSniper.getLogger().error("Failed to save default configuration");
                e.printStackTrace();
            }
        } else {
            try {
                CommentedConfigurationNode node = loader.load();
                boolean modified = false;
                for (Field field : VoxelSniperConfiguration.class.getDeclaredFields()) {
                    if (field.isAnnotationPresent(ConfigValue.class)) {
                        ConfigValue anno = field.getAnnotation(ConfigValue.class);
                        if (anno.hidden()) {
                            continue;
                        }
                        CommentedConfigurationNode n = node.getNode((Object[]) anno.name().split("\\."));
                        if (!n.isVirtual()) {
                            try {
                                VoxelSniper.getLogger().debug("Loaded " + anno.name() + " = " + n.getValue());
                                field.set(null, n.getValue());
                            } catch (IllegalArgumentException | IllegalAccessException e) {
                                VoxelSniper.getLogger().error("Error loading configuration value " + anno.name());
                                e.printStackTrace();
                            }
                        } else if (!anno.hidden()) {
                            n.setComment(anno.comment());
                            try {
                                n.setValue(field.get(null));
                                VoxelSniper.getLogger().debug("Created " + anno.name() + " = " + n.getValue());
                            } catch (IllegalArgumentException | IllegalAccessException e) {
                                VoxelSniper.getLogger().error("Failed to extract default value for configuration field " + field.toGenericString());
                                e.printStackTrace();
                            }
                            modified = true;
                        }
                    }
                }
                if (modified) {
                    try {
                        loader.save(node);
                    } catch (IOException e) {
                        VoxelSniper.getLogger().error("Failed to save updated configuration");
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                VoxelSniper.getLogger().error("Failed to load configuration");
                e.printStackTrace();
            }
        }
        VoxelSniperMessages.reload();
    }

    private VoxelSniperConfiguration() {
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface ConfigValue {

        String name();

        String comment() default "";

        boolean hidden() default false;

    }

}
