/*
 * Copyright (c) 2015-2016 VoxelBox <http://engine.thevoxelbox.com>.
 * All Rights Reserved.
 */
package com.thevoxelbox.voxelsniper.util;

import java.nio.file.Path;

public class SchematicHelper {

    private static Path schematics;

    public static void setSchematicsDir(Path path) {
        schematics = path;
    }

    public static Path getSchematicsDir() {
        return schematics;
    }

}
