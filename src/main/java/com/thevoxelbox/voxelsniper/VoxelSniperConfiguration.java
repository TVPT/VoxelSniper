package com.thevoxelbox.voxelsniper;

/**
 * Configuration storage defining global configurations for VoxelSniper.
 */
public class VoxelSniperConfiguration {

    // Constants
    public static final int CHUNK_SIZE = 16;
    public static final String PERMISSION_SNIPER = "voxelsniper.sniper";
    public static final String PERMISSION_IGNORE_SIZE_LIMITS = "voxelsniper.ignorelimitations";
    public static final String PERMISSION_COMMAND_ENABLE = "voxelsniper.command.vs.enable";
    public static final String PERMISSION_COMMAND_UNDO_OTHER = "voxelsniper.command.uu";

    // Config
    public static int LITESNIPER_MAX_BRUSH_SIZE = 10;
    public static int UNDO_CACHE_SIZE = 10;
    public static boolean LOGIN_MESSAGE_ENABLED = true;

    // @Spongify load from hocon container
}
