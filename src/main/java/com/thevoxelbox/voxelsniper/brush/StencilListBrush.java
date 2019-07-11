package com.thevoxelbox.voxelsniper.brush;

import com.google.common.io.Files;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.util.SpongeSchematic;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author Gavjenks
 */
public class StencilListBrush extends Brush
{
    private byte pasteOption = 1; // 0 = full, 1 = fill, 2 = replace
    private String filename = "NoFileLoaded";
    private short x;
    private short z;
    private short y;
    private short xRef;
    private short zRef;
    private short yRef;
    private byte pasteParam = 0;
    private List<SpongeSchematic> schematics = null;

    private final Random rand = new Random();

    private static final BlockVector[] ROTATIONS = new BlockVector[] {
            new BlockVector(1, 1, 1),
            new BlockVector(1, -1, 1),
            new BlockVector(-1, 1, 1),
            new BlockVector(-1, -1, 1),
    };

    /**
     *
     */
    public StencilListBrush()
    {
        this.setName("StencilList");
    }

    private File listFile() {
        return new File("plugins/VoxelSniper/stencilLists/" + this.filename + ".txt");
    }

    private void readStencilList(final String listname, final SnipeData v)
    {
        schematics = null;

        final File file = listFile();
        if (file.exists())
        {
            try
            {
                List<String> schemaFilesNames = Files.readLines(file, Charset.forName("UTF-8"));
                List<SpongeSchematic> accum = new ArrayList<SpongeSchematic>();
                for (String schemaFileName : schemaFilesNames) {
                    File schemaFile = new File(schemaFileName);

                    if(!schemaFile.exists()) {
                        v.sendMessage(ChatColor.RED + schemaFileName + " does not exist, skipping.");
                        continue;
                    }

                    accum.add(SpongeSchematic.read(schemaFile));
                }

                if(schematics.isEmpty()) {
                    v.sendMessage(ChatColor.RED + "No valid schematics found in " + listname + ".");
                    return;
                }

                schematics = accum;
            }
            catch (final Exception exception)
            {
                v.sendMessage(ChatColor.RED + "There was an issue loading the list " + file.getName() + ": " + exception.getMessage());
                exception.printStackTrace();
            }
        }
    }

    private void stencilPaste(final SnipeData v, BlockVector rotation) {
        if(schematics == null) {
            if (this.filename.matches("NoFileLoaded"))
            {
                v.sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
                return;
            }

            final File file = listFile();
            if(!file.exists()) {
                v.sendMessage(ChatColor.RED + "Sorry, no list was loaded.");
                return;
            }

            readStencilList(filename, v);

            if(schematics == null) {
                return;
            }
        }

        if(pasteOption != 0 && pasteOption != 1 && pasteOption != 2) {
            v.sendMessage(ChatColor.RED +  "Invalid paste option: " + pasteOption);
            return;
        }

        final Undo undo = new Undo();

        SpongeSchematic schematic = schematics.get(rand.nextInt(schematics.size()));

        Vector offset = schematic.getOffset().multiply(rotation);
        int offX = offset.getBlockX();
        int offY = offset.getBlockY();
        int offZ = offset.getBlockZ();
        int width = schematic.getWidth();
        int height = schematic.getHeight();
        int length = schematic.getLength();

        Block targetBlock = getTargetBlock();
        int posX = targetBlock.getX() + offX;
        int posY = targetBlock.getY() + offY;
        int posZ = targetBlock.getZ() + offZ;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    Vector rotated = new BlockVector(x, y, z).multiply(rotation);

                    Block block = this.clampY(posX + rotated.getBlockX(), posY + rotated.getBlockY(), posZ + rotated.getBlockZ());

                    boolean test = true;
                    if(pasteOption == 0) {
                        test = block.getType() == Material.AIR;
                    }
                    else if (pasteOption == 2) {
                        test = block.getType() != Material.AIR;
                    }

                    if(test) {
                        undo.put(block);
                        block.setBlockData(schematic.getBlockAt(x, y, z), false);
                    }
                }
            }
        }
    }

    private void stencilPasteRotation(final SnipeData v)
    {
        // just randomly chooses a rotation and then calls stencilPaste.
        this.stencilPaste(v, ROTATIONS[rand.nextInt(ROTATIONS.length)]);
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.stencilPaste(v, ROTATIONS[0]);
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.stencilPasteRotation(v);
    }


    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.custom("File loaded: " + this.filename);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        if (par[1].equalsIgnoreCase("info"))
        {
            v.sendMessage(ChatColor.GOLD + "Stencil List brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b schem [optional: 'full' 'fill' or 'replace', with fill as default] [name] -- Loads the specified stencil list.  Full/fill/replace must come first.  Full = paste all blocks, fill = paste only into air blocks, replace = paste full blocks in only, but replace anything in their way.");
            return;
        }
        else if (par[1].equalsIgnoreCase("full"))
        {
            this.pasteOption = 0;
            this.pasteParam = 1;
        }
        else if (par[1].equalsIgnoreCase("fill"))
        {
            this.pasteOption = 1;
            this.pasteParam = 1;
        }
        else if (par[1].equalsIgnoreCase("replace"))
        {
            this.pasteOption = 2;
            this.pasteParam = 1;
        }
        try
        {
            this.filename = par[1 + this.pasteParam];
            final File file = listFile();
            if (file.exists())
            {
                v.sendMessage(ChatColor.RED + "Stencil List '" + this.filename + "' exists and was loaded.");
                this.readStencilList(this.filename, v);
            }
            else
            {
                v.sendMessage(ChatColor.AQUA + "Stencil List '" + this.filename + "' does not exist.  This brush will not function without a valid stencil list.");
                this.filename = "NoFileLoaded";
            }
        }
        catch (final Exception exception)
        {
            v.sendMessage(ChatColor.RED + "You need to type a stencil name.");
        }
    }

    @Override
    public String getPermissionNode()
    {
        return "voxelsniper.brush.stencillist";
    }
}
