package com.thevoxelbox.voxelsniper.brush;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.util.SpongeSchematic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

/**
 * Allows the copying and pasting of stencils. Stencils are stored using the Mcedit schematic format. Allows rotation and flipping of
 * stencils.
 *
 * @author Katrix, Gavjenks
 */
public class StencilBrush extends Brush
{
    private byte pasteOption = 1; // 0 = full, 1 = fill, 2 = replace
    private String filename = "";
    private SpongeSchematic schematic;
    private BlockVector firstPoint = new BlockVector(0, 0, 0);
    private BlockVector secondPoint = new BlockVector(0, 0, 0);
    private BlockVector pastePoint = new BlockVector(0, 0, 0);
    private byte point = 1;
    private double pitch = 0;
    private double yaw = 0;
    private double roll = 0;
    private boolean xFlipped = false;
    private boolean yFlipped = false;
    private boolean zFlipped = false;

    public StencilBrush()
    {
        this.setName("Stencil");
    }

    private void stencilPaste(final SnipeData v)
    {
        if (schematic == null)
        {
            if (this.filename.isEmpty())
            {
                v.sendMessage(ChatColor.RED + "No region has been copied yet, and no filename was specified.");
                return;
            }

            final File file = getSchematicFile();

            if(!file.exists()) {
                v.sendMessage(ChatColor.RED + "Sorry, no region was loaded, and no stencil by that name was saved yet.");
                return;
            }

            try {
                schematic = SpongeSchematic.read(file);
            }
            catch (IOException e) {
                v.sendMessage(ChatColor.RED + "There was an issue loading the schematic " + file.getName() + ": " + e.getMessage());
                return;
            }
        }

        if(pasteOption != 0 && pasteOption != 1 && pasteOption != 2) {
            v.sendMessage(ChatColor.RED +  "Invalid paste option: " + pasteOption);
            return;
        }

        final Undo undo = new Undo();

        Vector offset = schematic.getOffset();
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
                    Block block = this.clampY(posX + x, posY + y, posZ + z);

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

    private void stencilSave(final SnipeData v)
    {
        schematic = SpongeSchematic.createFromWorld(v.getWorld(), this.firstPoint, this.secondPoint, this.pastePoint);

        if (!filename.isEmpty())
        {
            final File file = getSchematicFile();
            try
            {
                schematic.writeTo(file);
            }
            catch (IOException e)
            {
                v.sendMessage(ChatColor.RED + "There was an issue while saving your schematic  " + file.getName() + ": " + e.getMessage());
            }
        }

        v.sendMessage(ChatColor.RED + "Region was successfully copied!");
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        BlockVector vec = this.getTargetBlock().getLocation().toVector().toBlockVector();
        if (this.point == 1)
        {
            this.firstPoint = vec;
            v.sendMessage(ChatColor.GRAY + "First point");
            v.sendMessage(this.formatVector(this.firstPoint));
            this.point = 2;
        }
        else if (this.point == 2)
        {
            this.secondPoint = vec;
            if (this.vecArea(this.firstPoint, this.secondPoint) > 5000000)
            {
                v.sendMessage(ChatColor.DARK_RED + "Area selected is too large. (Limit is 5,000,000 blocks)");
                this.point = 1;
            }
            else
            {
                v.sendMessage(ChatColor.GRAY + "Second point");
                v.sendMessage(this.formatVector(this.secondPoint));
                this.point = 3;
            }
        }
        else if (this.point == 3)
        {
            this.pastePoint = vec;
            v.sendMessage(ChatColor.GRAY + "Paste Reference point");
            v.sendMessage(this.formatVector(this.pastePoint));
            this.point = 1;

            this.stencilSave(v);
        }
    }

    private File getSchematicFile() {
        return new File("plugins/VoxelSniper/stencils/" + this.filename + ".schem");
    }

    private String formatVector(BlockVector vec) {
        return "X:" + vec.getBlockX() + " Z:" + vec.getBlockZ() + " Y:" + vec.getBlockZ();
    }

    private int vecArea(BlockVector v1, BlockVector v2) {
        return (Math.abs(v1.getBlockX() - v2.getBlockX()) * Math.abs(v1.getBlockY() - v2.getBlockY()) * Math.abs(v1.getBlockZ() - v2.getBlockZ()));
    }

    @Override
    protected final void powder(final SnipeData v)
    { // will be used to paste later on
        this.stencilPaste(v);
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.custom("File loaded: " + (this.filename.isEmpty() ? "None; Using clipboard only." : this.filename));
    }

    @Override
    public final void parameters(final String[] pars, final SnipeData v)
    {
        int parN = 1;
        String par = pars[parN];
        if (par.equalsIgnoreCase("info"))
        {
            v.sendMessage(ChatColor.GOLD + "Stencil brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b st (full|fill|replace) [name] (-r #) (-f N|S|E|W|NORTH|SOUTH|EAST|WEST) (p#) (y#) (r#) -- Loads the specified schematic. '-r #' or 'y#' specify yaw 'p#' specifies pitch and 'r#' specifies roll. -f specifies which axis to flip. All rotations are in degrees, not restricted to multiples of 90.");
            return;
        }
        else if (par.equalsIgnoreCase("full"))
        {
            this.pasteOption = 0;
            parN++;
        }
        else if (par.equalsIgnoreCase("fill"))
        {
            this.pasteOption = 1;
            parN++;
        }
        else if (par.equalsIgnoreCase("replace"))
        {
            this.pasteOption = 2;
            parN++;
        }

        if (pars.length > parN)
        {
            this.filename = pars[parN++];
            final File file = getSchematicFile();
            if (!file.getParentFile().exists())
            {
                file.getParentFile().mkdirs();
            }

            if (file.exists())
            {
                try {
                    schematic = SpongeSchematic.read(file);
                    v.sendMessage(ChatColor.RED + "Stencil '" + this.filename + "' exists and was loaded.  Make sure you are using powder if you do not want any chance of overwriting the file.");
                }
                catch (IOException e) {
                    v.sendMessage(ChatColor.DARK_RED + "There was an issue loading " + this.filename + ".schem " + e.getMessage());
                    v.sendMessage(ChatColor.AQUA + "Running in clipboard mode. Can copy/paste but will not save to file.");
                    this.schematic = null;
                    this.filename = "";
                    return;
                }
            }
            else
            {
                v.sendMessage(ChatColor.AQUA + "Stencil '" + this.filename + "' does not exist.  Ready to be saved to, but cannot be pasted.");
            }
        }
        else
        {
            this.filename = "";
            v.sendMessage(ChatColor.AQUA + "No filename specified, running in clipboard mode. Can copy/paste but will not save to file.");
        }

        xFlipped = yFlipped = zFlipped = false;
        yaw = pitch = roll = 0;

        for (int i = parN; i < pars.length; i++)
        {
            if ((pars[i].equalsIgnoreCase("-r") || pars[i].equalsIgnoreCase("-rotate")) && i < pars.length - 1)
            {
                try
                {
                    double rot = Double.parseDouble(pars[++i]);
                    this.yaw = (-rot) % 360;
                    v.sendMessage("Stencil yaw set to " + rot + " degrees.");
                }
                catch (NumberFormatException e)
                {
                    continue;
                }
            }

            if ((pars[i].equalsIgnoreCase("-f") || pars[i].equalsIgnoreCase("-flip")) && i < pars.length - 1)
            {
                String dir = pars[++i];
                int start = 0;
                Pattern pat = Pattern.compile("(north|n|south|s)|(east|e|west|w)|(up|u|down|d)");
                Matcher matcher = pat.matcher(dir);
                int end = dir.length();
                while (start < end)
                {
                    matcher.region(start, end);
                    if (matcher.find()) {
                        start = matcher.end();

                        if (matcher.group(1) != null)
                        {
                            xFlipped = true;
                        }

                        if (matcher.group(2) != null)
                        {
                            zFlipped = true;
                        }

                        if (matcher.group(3) != null)
                        {
                            yFlipped = true;
                        }
                    }
                    else
                    {
                        start++;
                    }
                }

                if (xFlipped)
                {
                    v.sendMessage("Stencil set to flip on the X axis.");
                }
                if (yFlipped)
                {
                    v.sendMessage("Stencil set to flip on the Y axis.");
                }
                if (zFlipped)
                {
                    v.sendMessage("Stencil set to flip on the Z axis.");
                }
            }

            if (pars[i].startsWith("p"))
            {
                try
                {
                    double rot = Double.parseDouble(pars[i].substring(1));
                    this.pitch = rot % 360;
                    v.sendMessage("Stencil pitch set to " + rot + " degrees.");
                }
                catch (NumberFormatException e)
                {
                    continue;
                }
            }

            if (pars[i].startsWith("y"))
            {
                try
                {
                    double rot = Double.parseDouble(pars[i].substring(1));
                    this.yaw = (-rot) % 360;
                    v.sendMessage("Stencil yaw set to " + rot + " degrees.");
                }
                catch (NumberFormatException e)
                {
                    continue;
                }
            }

            if (pars[i].startsWith("r"))
            {
                try
                {
                    double rot = Double.parseDouble(pars[i].substring(1));
                    this.roll = rot % 360;
                    v.sendMessage("Stencil roll set to " + rot + " degrees.");
                }
                catch (NumberFormatException e)
                {
                    continue;
                }
            }
        }
    }

    @Override
    public String getPermissionNode()
    {
        return "voxelsniper.brush.stencil";
    }
}
