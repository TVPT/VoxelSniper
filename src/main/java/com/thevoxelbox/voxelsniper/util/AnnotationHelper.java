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

import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AnnotationHelper {

    private static final Map<String, Consumer<String>> consumers = Maps.newHashMap();

    public static void registerConsumer(Type annotation, Consumer<String> consumer) {
        consumers.put(annotation.getDescriptor(), consumer);
    }

    private static final String JAVA_HOME          = StandardSystemProperty.JAVA_HOME.value();
    private static final String CLASS_EXTENSION    = ".class";

    private static Set<String>  SCANNER_EXCLUSIONS = Sets.newHashSet();

    static {
        SCANNER_EXCLUSIONS.add("java/");
        SCANNER_EXCLUSIONS.add("javax/");
        SCANNER_EXCLUSIONS.add("sun/");
        SCANNER_EXCLUSIONS.add("com/sun/");
        SCANNER_EXCLUSIONS.add("org/lwjgl/");
        SCANNER_EXCLUSIONS.add("io/netty/");
        SCANNER_EXCLUSIONS.add("org/spongepowered/");
        SCANNER_EXCLUSIONS.add("net/minecraft/");
        SCANNER_EXCLUSIONS.add("net/minecraftforge/");
        SCANNER_EXCLUSIONS.add("com/google/");
        SCANNER_EXCLUSIONS.add("org/objectweb/asm/");
    }

    public static void scanClassPath(URLClassLoader loader) {
        Set<URI> sources = Sets.newHashSet();

        for (URL url : loader.getURLs()) {
            if (!url.getProtocol().equals("file")) {
                continue;
            }

            if (url.getPath().startsWith(JAVA_HOME)) {
                continue;
            }

            URI source;
            try {
                source = url.toURI();
            } catch (URISyntaxException e) {
                continue;
            }

            if (sources.add(source)) {
                try {
                    scanFile(new File(source));
                } catch (IOException e) {
                }
            }
        }
    }

    public static void scanFile(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                scanDirectory(file);
            } else {
                scanZip(file);
            }
        }
    }

    public static void scanDirectory(File dir) throws IOException {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scanDirectory(file);
            } else if (file.isFile() && file.getName().endsWith(".class")) {
                try (InputStream in = new FileInputStream(file)) {
                    findAnnotations(in);
                }
            }
        }
    }

    private static void scanZip(File file) {
        if (!file.getName().endsWith(".zip") || !file.getName().endsWith(".jar")) {
            return;
        }
        try {
            try (ZipFile zip = new ZipFile(file)) {
                Enumeration<? extends ZipEntry> entries = zip.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (entry.isDirectory() || !entry.getName().endsWith(CLASS_EXTENSION)) {
                        continue;
                    }

                    try (InputStream in = zip.getInputStream(entry)) {
                        findAnnotations(in);
                    }
                }
            }
        } catch (IOException e) {
        }
    }

    @SuppressWarnings("unchecked")
    private static void findAnnotations(InputStream in) throws IOException {
        ClassReader reader = new ClassReader(in);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        for (String exc : SCANNER_EXCLUSIONS) {
            if (classNode.name.startsWith(exc)) {
                return;
            }
        }
        if (classNode.visibleAnnotations != null) {
            for (AnnotationNode anno : (List<AnnotationNode>) classNode.visibleAnnotations) {
                Consumer<String> consumer = consumers.get(anno.desc);
                if (consumer != null) {
                    consumer.accept(classNode.name.replace("/", "."));
                }
            }
        }
        if (classNode.invisibleAnnotations != null) {
            for (AnnotationNode anno : (List<AnnotationNode>) classNode.invisibleAnnotations) {
                Consumer<String> consumer = consumers.get(anno.desc);
                if (consumer != null) {
                    consumer.accept(classNode.name.replace("/", "."));
                }
            }
        }
        if (classNode.visibleTypeAnnotations != null) {
            for (AnnotationNode anno : (List<AnnotationNode>) classNode.visibleTypeAnnotations) {
                Consumer<String> consumer = consumers.get(anno.desc);
                if (consumer != null) {
                    consumer.accept(classNode.name.replace("/", "."));
                }
            }
        }
        if (classNode.invisibleTypeAnnotations != null) {
            for (AnnotationNode anno : (List<AnnotationNode>) classNode.invisibleTypeAnnotations) {
                Consumer<String> consumer = consumers.get(anno.desc);
                if (consumer != null) {
                    consumer.accept(classNode.name.replace("/", "."));
                }
            }
        }

    }
}
