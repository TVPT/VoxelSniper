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

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.DataView.SafetyMode;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.DataFormats;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Updates stencils into schematics.
 */
public class StencilUpdater {

    public static void update(File dir) {
        File updated = new File(dir.getParentFile(), "stencils-updated");
        if (updated.exists()) {
            System.err.println("Directory for updated stencils already exists. (" + updated.getAbsolutePath() + ")");
            System.err.println("Either remove this directory or rename the stencils directory to disable this warning.");
            return;
        }
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                update(f);
            } else if (f.getName().endsWith(".vstencil")) {
                updateStencil(f);
            }
        }
        dir.renameTo(updated);
    }

    public static void updateStencil(File stencil) {
        String name = stencil.getName().replace(".vstencil", "");
        File schematicFile = SchematicHelper.getSchematicsDir().resolve(name + ".schem").toFile();
        if (schematicFile.exists()) {
            System.out.println("Schematic with name " + name + " already exists, skipping translating stencil with same name");
            return;
        }
        System.out.print("Translating " + stencil.getAbsolutePath() + " to schematic " + schematicFile.getAbsolutePath());
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(stencil)))) {
            // yes x z y is not a typo ...
            int w = in.readShort();
            int l = in.readShort();
            int h = in.readShort();

            short xRef = in.readShort();
            short zRef = in.readShort();
            short yRef = in.readShort();

            int runs = in.readInt();

            int[] blocks = new int[w * h * l];

            int cx = 0;
            int cy = 0;
            int cz = 0;

            for (int i = 0; i < runs; i++) {
                boolean multi = in.readBoolean();
                if (multi) {
                    int count = in.readByte() + 128;
                    int blockid = in.readByte() + 128;
                    int data = in.readByte() + 128;
                    int blockdata = ((blockid << 4) | (data & 0xF));
                    for (int c = 0; c < count; c++) {
                        blocks[cx + cy * w + cz * w * h] = blockdata;
                        cx++;
                        if (cx == w) {
                            cx = 0;
                            cz++;
                            if (cz == l) {
                                cz = 0;
                                cy++;
                            }
                        }
                    }
                } else {
                    int blockid = in.readByte() + 128;
                    int data = in.readByte() + 128;
                    int blockdata = ((blockid << 4) | (data & 0xF));
                    blocks[cx + cy * w + cz * w * h] = blockdata;
                    cx++;
                    if (cx == w) {
                        cx = 0;
                        cz++;
                        if (cz == l) {
                            cz = 0;
                            cy++;
                        }
                    }
                }
            }

            DataContainer schematic = new MemoryDataContainer(SafetyMode.NO_DATA_CLONED);
            schematic.set(DataQuery.of("Width"), w);
            schematic.set(DataQuery.of("Height"), h);
            schematic.set(DataQuery.of("Length"), l);

            schematic.set(DataQuery.of("Version"), 1);

            DataView metadata = schematic.createView(DataQuery.of("Metadata"));
            metadata.set(DataQuery.of("Name"), name);

            int[] offset = new int[] {xRef, yRef, zRef};
            schematic.set(DataQuery.of("Offset"), offset);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream(w * h * l);

            for (int y = 0; y < h; y++) {
                for (int z = 0; z < l; z++) {
                    for (int x = 0; x < w; x++) {
                        int id = blocks[x + y * w + z * w * h];
                        while ((id & -128) != 0) {
                            buffer.write(id & 127 | 128);
                            id >>>= 7;
                        }
                        buffer.write(id);
                    }
                }
            }

            schematic.set(DataQuery.of("BlockData"), buffer.toByteArray());

            try (GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(schematicFile))) {
                DataFormats.NBT.writeTo(out, schematic);
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }

}
