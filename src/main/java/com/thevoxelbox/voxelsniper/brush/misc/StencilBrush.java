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
package com.thevoxelbox.voxelsniper.brush.misc;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.util.SchematicHelper;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntityArchetype;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.extent.ArchetypeVolume;
import org.spongepowered.api.world.schematic.BlockPaletteTypes;
import org.spongepowered.api.world.schematic.Schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StencilBrush extends Brush {

    public static enum PasteOption {
        FULL,
        FILL,
        REPLACE
    }

    public static enum SchematicType {
        LEGACY(DataTranslators.LEGACY_SCHEMATIC, ".schematic"),
        SPONGE(DataTranslators.SCHEMATIC, ".schem");

        public DataTranslator<Schematic> translator;
        public String fileEnding;

        SchematicType(DataTranslator<Schematic> translator, String ending) {
            this.translator = translator;
            this.fileEnding = ending;
        }
    }

    private String filename = null;
    private File file = null;
    private long lastMod;
    private Schematic schematic = null;
    private PasteOption pasteOption = PasteOption.FULL;
    private SchematicType schematicType = SchematicType.SPONGE;
    private UUID worldUid;
    private Vector3i pos1;
    private Vector3i pos2;

    public StencilBrush() {
        this.setName("Stencil");
    }

    @Override
    protected final void arrow(final SnipeData v) {
        if (this.file == null) {
            v.sendMessage(TextColors.RED, "You need to specify a schematic name.");
            return;
        }
        if (this.pos1 == null || !this.worldUid.equals(this.targetBlock.getExtent().getUniqueId())) {
            this.pos1 = this.targetBlock.getBlockPosition();
            this.pos2 = null;
            this.worldUid = this.targetBlock.getExtent().getUniqueId();
            v.sendMessage(TextColors.GRAY, "First point selected.");
        } else if (this.pos2 == null) {
            this.pos2 = this.targetBlock.getBlockPosition();
            v.sendMessage(TextColors.GRAY, "Second point selected.");
        } else {
            Vector3i origin = this.targetBlock.getBlockPosition();
            ArchetypeVolume volume = this.world.createArchetypeVolume(this.pos1, this.pos2, origin);
            this.schematic = Schematic.builder().paletteType(BlockPaletteTypes.LOCAL).volume(volume).metaValue("Name", this.filename)
                    .metaValue("Author", v.owner().getPlayer().getName()).metaValue("Date", System.currentTimeMillis()).build();

            this.file.getParentFile().mkdirs();
            try (GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(this.file))) {
                DataContainer data = this.schematicType.translator.translate(this.schematic);
                DataFormats.NBT.writeTo(out, data);
                v.sendMessage(TextColors.GREEN, "Schematic saved successfully. Ready for pasting.");
                this.lastMod = this.file.lastModified();
                this.pos1 = null;
                this.pos2 = null;
            } catch (IOException e) {
                e.printStackTrace();
                v.sendMessage(TextColors.RED, "Error Saving schematic, see console for details.");
                return;
            }
        }

    }

    @Override
    protected final void powder(final SnipeData v) {
        if (this.file == null) {
            v.sendMessage(TextColors.RED, "You need to specify a schematic name.");
            return;
        }
        if (!this.file.exists()) {
            v.sendMessage(TextColors.RED, "Scehmatic does not exist, must be saved to first.");
            return;
        }
        if (this.schematic == null || this.file.lastModified() != this.lastMod) {
            if (this.schematic != null) {
                v.sendMessage(TextColors.GREEN, "Reloading schematic from disk");
            }
            try (GZIPInputStream in = new GZIPInputStream(new FileInputStream(this.file))) {
                DataContainer data = DataFormats.NBT.readFrom(in);
                this.schematic = this.schematicType.translator.translate(data);
            } catch (IOException e) {
                e.printStackTrace();
                v.sendMessage(TextColors.RED, "Error loading schematic, see console for details.");
                return;
            }
            this.lastMod = this.file.lastModified();
        }
        this.undo = new Undo(this.schematic.getBlockSize().getX() * this.schematic.getBlockSize().getY() * this.schematic.getBlockSize().getZ());
        if (this.pasteOption == PasteOption.FULL) {
            this.schematic.getBlockWorker().iterate((e, x, y, z) -> {
                setBlockState(x + this.targetBlock.getBlockX(), y + this.targetBlock.getBlockY(), z + this.targetBlock.getBlockZ(),
                        e.getBlock(x, y, z));
            });
            for (Vector3i pos : this.schematic.getTileEntityArchetypes().keySet()) {
                TileEntityArchetype archetype = this.schematic.getTileEntityArchetypes().get(pos);
                archetype.apply(this.targetBlock.add(pos));
            }
        } else if (this.pasteOption == PasteOption.FILL) {
            this.schematic.getBlockWorker().iterate((e, x, y, z) -> {
                if (this.targetBlock.getExtent().getBlockType(x + this.targetBlock.getBlockX(), y + this.targetBlock.getBlockY(),
                        z + this.targetBlock.getBlockZ()) == BlockTypes.AIR) {
                    setBlockState(x + this.targetBlock.getBlockX(), y + this.targetBlock.getBlockY(), z + this.targetBlock.getBlockZ(),
                            e.getBlock(x, y, z));
                }
            });
            for (Vector3i pos : this.schematic.getTileEntityArchetypes().keySet()) {
                if (this.targetBlock.getExtent().getBlockType(pos.getX() + this.targetBlock.getBlockX(), pos.getY() + this.targetBlock.getBlockY(),
                        pos.getZ() + this.targetBlock.getBlockZ()) == BlockTypes.AIR) {
                    TileEntityArchetype archetype = this.schematic.getTileEntityArchetypes().get(pos);
                    archetype.apply(this.targetBlock.add(pos));
                }
            }
        } else { // replace
            this.schematic.getBlockWorker().iterate((e, x, y, z) -> {
                if (e.getBlockType(x, y, z) != BlockTypes.AIR) {
                    setBlockState(x + this.targetBlock.getBlockX(), y + this.targetBlock.getBlockY(), z + this.targetBlock.getBlockZ(),
                            e.getBlock(x, y, z));
                }
            });
            for (Vector3i pos : this.schematic.getTileEntityArchetypes().keySet()) {
                TileEntityArchetype archetype = this.schematic.getTileEntityArchetypes().get(pos);
                archetype.apply(this.targetBlock.add(pos));
            }
        }
        v.owner().storeUndo(this.undo);
        this.undo = null;
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.custom(TextColors.YELLOW, "Paste option: " + this.pasteOption.name().toLowerCase());
        vm.custom(TextColors.GREEN, "File loaded: " + this.filename);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        if (par.length == 0) {
            v.sendMessage(TextColors.RED, "You need to specify a stencil name.");
            return;
        }
        if (par[0].equalsIgnoreCase("info")) {
            v.sendMessage(TextColors.GOLD, "Stencil brush Parameters:");
            v.sendMessage(TextColors.AQUA, "/b st [name] [full|fill|replace] [--legacy] -- Loads the specified schematic. Full = paste all blocks, "
                    + "fill = paste only into air blocks, replace = paste full blocks in only, but replace anything in their way. The --legacy flag specifies that the MCEdit"
                    + "schematic format should be used rather than the sponge schematic format.");
            return;
        }
        SchematicType type = SchematicType.SPONGE;
        int next = 1;
        if(par.length > 1 && par[1].equalsIgnoreCase("--legacy")) {
            type = SchematicType.LEGACY;
            v.sendMessage(TextColors.AQUA, "Using lagacy schematic format.");
            next = 2;
        }
        if(par.length > 2 && par[2].equalsIgnoreCase("--legacy")) {
            type = SchematicType.LEGACY;
            v.sendMessage(TextColors.AQUA, "Using lagacy schematic format.");
        }
        this.schematicType = type;

        if (!par[0].equals(this.filename)) {
            this.lastMod = 0;
            this.schematic = null;
        }
        this.filename = par[0];
        File schematic = SchematicHelper.getSchematicsDir().resolve(this.filename + type.fileEnding).toFile();
        if (schematic.exists()) {
            v.sendMessage(TextColors.RED, "Stencil '" + this.filename + "' exists and was loaded. Paste with the powder, overwrite with the arrow.");
        } else {
            v.sendMessage(TextColors.AQUA, "Stencil '" + this.filename + "' does not exist.  Ready to be saved to, but cannot be pasted.");
        }
        this.file = schematic;
        if (par.length > next) {
            if (par[next].equalsIgnoreCase("full")) {
                this.pasteOption = PasteOption.FULL;
            } else if (par[next].equalsIgnoreCase("fill")) {
                this.pasteOption = PasteOption.FILL;
            } else if (par[next].equalsIgnoreCase("replace")) {
                this.pasteOption = PasteOption.REPLACE;
            } else {
                v.sendMessage(TextColors.RED, "Invalid paste option, choices are: full, fill, replace");
                return;
            }
            v.sendMessage(TextColors.YELLOW, "Paste option: " + this.pasteOption.name().toLowerCase());
        }
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.stencil";
    }
}
