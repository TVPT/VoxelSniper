package com.thevoxelbox.voxelsniper.brush;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.util.InvalidFormatException;
import com.thevoxelbox.voxelsniper.util.schematic.BlockRegion;
import com.thevoxelbox.voxelsniper.util.schematic.BlockRegionMask;
import com.thevoxelbox.voxelsniper.util.schematic.BlockRegionMaskType;
import com.thevoxelbox.voxelsniper.util.schematic.BlockRegionOperations;
import com.thevoxelbox.voxelsniper.util.schematic.MCEditSchematic;

/**
 * Allows the copying and pasting of stencils. Stencils are stored using the Mcedit schematic format. Allows rotation and flipping of
 * stencils.
 * 
 * @author Deamon, Gavjenks
 */
public class StencilBrush extends Brush
{
    private byte        pasteOption = 0;         // 0 = full, 1 = fill, 2 = replace
    private String      filename    = "";
    private BlockRegion region      = null;
    private int[]       firstPoint  = new int[3];
    private int[]       secondPoint = new int[3];
    private int[]       pastePoint  = new int[3];
    private byte        point       = 1;
    private double      pitch       = 0;
    private double      yaw         = 0;
    private double      roll        = 0;
    private boolean     xFlipped    = false;
    private boolean     yFlipped    = false;
    private boolean     zFlipped    = false;

    public StencilBrush()
    {
        this.setName("Stencil");
    }

    private void stencilPaste(final SnipeData v)
    {
        if (this.region == null)
        {
            if (this.filename.equals(""))
            {
                v.sendMessage(ChatColor.RED + "No region has been copied yet, and no filename was specified.");
                return;
            }
            else
            {
                final File file = new File("plugins/VoxelSniper/stencils/" + this.filename + ".schematic");
                if (file.exists())
                {
                    if (region == null || file.lastModified() > region.getLastChanged())
                    {
                        try
                        {
                            region = MCEditSchematic.load(file);
                        }
                        catch (IOException e)
                        {
                            v.sendMessage(ChatColor.RED + "There was an issue loading the schematic " + file.getName() + ": " + e.getMessage());
                            return;
                        }
                        catch (InvalidFormatException e)
                        {
                            v.sendMessage(ChatColor.RED + "There was an issue loading the schematic " + file.getName() + ": " + e.getMessage());
                            return;
                        }
                    }
                }
                else
                {
                    v.sendMessage(ChatColor.RED + "Sorry, no region was loaded, and no stencil by that name was saved yet.");
                    return;
                }
            }
        }

        final Undo undo = new Undo();

        BlockRegionMask mask = null;
        if (this.pasteOption == 1) // Replace air
        {
            mask = new BlockRegionMask(BlockRegionMaskType.REPLACE);
            mask.add(new int[] { 0, -1 });
        }
        else if (this.pasteOption == 2) // Replace not air
        {
            mask = new BlockRegionMask(BlockRegionMaskType.NEGATIVE_REPLACE);
            mask.add(new int[] { 0, -1 });
        }
        else
        // Default to replacing all
        {
            mask = BlockRegionMask.NONE;
        }
        try
        {
            BlockRegionOperations.placeIntoWorldUnbuffered(region, v.getWorld(), getTargetBlock().getX(), getTargetBlock().getY(), getTargetBlock().getZ(), undo, mask, yaw, pitch, roll, xFlipped, yFlipped, zFlipped);
        }
        catch (InvalidFormatException e)
        {
            e.printStackTrace();
            v.sendMessage(ChatColor.DARK_RED + "Attempted to place invalid stencil!");
        }
        v.owner().storeUndo(undo);

    }

    private void stencilSave(final SnipeData v)
    {
        this.region = MCEditSchematic.createFromWorld(v.getWorld(), this.firstPoint, this.secondPoint, this.pastePoint);

        if (!this.filename.equals(""))
        {
            final File file = new File("plugins/VoxelSniper/stencils/" + this.filename + ".schematic");

            try
            {
                MCEditSchematic.save(region, file);
            }
            catch (IOException e)
            {
                v.sendMessage(ChatColor.DARK_RED + "There was an issue while saving your stencil " + e.getMessage());
                return;
            }
        }
        v.sendMessage(ChatColor.RED + "Region was successfully copied!");
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        if (this.point == 1)
        {
            this.firstPoint[0] = this.getTargetBlock().getX();
            this.firstPoint[1] = this.getTargetBlock().getY();
            this.firstPoint[2] = this.getTargetBlock().getZ();
            v.sendMessage(ChatColor.GRAY + "First point");
            v.sendMessage("X:" + this.firstPoint[0] + " Y:" + this.firstPoint[1] + " Z:" + this.firstPoint[2]);
            this.point = 2;
        }
        else if (this.point == 2)
        {
            this.secondPoint[0] = this.getTargetBlock().getX();
            this.secondPoint[1] = this.getTargetBlock().getY();
            this.secondPoint[2] = this.getTargetBlock().getZ();
            if ((Math.abs(this.firstPoint[0] - this.secondPoint[0]) * Math.abs(this.firstPoint[1] - this.secondPoint[1]) * Math.abs(this.firstPoint[2] - this.secondPoint[2])) > 5000000)
            {
                v.sendMessage(ChatColor.DARK_RED + "Area selected is too large. (Limit is 5,000,000 blocks)");
                this.point = 1;
            }
            else
            {
                v.sendMessage(ChatColor.GRAY + "Second point");
                v.sendMessage("X:" + this.secondPoint[0] + " Y:" + this.secondPoint[1] + " Z:" + this.secondPoint[2]);
                this.point = 3;
            }
        }
        else if (this.point == 3)
        {
            this.pastePoint[0] = this.getTargetBlock().getX();
            this.pastePoint[1] = this.getTargetBlock().getY();
            this.pastePoint[2] = this.getTargetBlock().getZ();
            v.sendMessage(ChatColor.GRAY + "Paste Reference point");
            v.sendMessage("X:" + this.pastePoint[0] + " Y:" + this.pastePoint[1] + " Z:" + this.pastePoint[2]);
            this.point = 1;

            this.stencilSave(v);
        }
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.stencilPaste(v);
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.custom("File loaded: " + (this.filename.equals("") ? "None; Using clipboard only." : this.filename));
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        int offset = 0;
        if (par[1].equalsIgnoreCase("info"))
        {
            v.sendMessage(ChatColor.GOLD + "Stencil brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b st (full|fill|replace) [name] (-r #) (-f N|S|E|W|NORTH|SOUTH|EAST|WEST) (p#) (y#) (r#) -- Loads the specified schematic. '-r #' or 'y#' specify yaw 'p#' specifies pitch and 'r#' specifies roll. -f specifies which axis to flip. All rotations are in degrees, not restricted to multiples of 90.");
            return;
        }
        else if (par[1].equalsIgnoreCase("full"))
        {
            this.pasteOption = 0;
            offset = 1;
        }
        else if (par[1].equalsIgnoreCase("fill"))
        {
            this.pasteOption = 1;
            offset = 1;
        }
        else if (par[1].equalsIgnoreCase("replace"))
        {
            this.pasteOption = 2;
            offset = 1;
        }

        if (par.length > 1 + offset)
        {
            this.filename = par[1 + offset];
            final File file = new File("plugins/VoxelSniper/stencils/" + this.filename + ".schematic");
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            try
            {
                MCEditSchematic.checkStencil("plugins/VoxelSniper/stencils/", this.filename);
            }
            catch (IOException ignore)
            {
            }

            if (file.exists())
            {
                try
                {
                    this.region = MCEditSchematic.load(file);
                }
                catch (IOException e)
                {
                    v.sendMessage(ChatColor.DARK_RED + "There was an issue loading " + this.filename + ".schematic " + e.getMessage());
                    v.sendMessage(ChatColor.AQUA + "Running in clipboard mode. Can copy/paste but will not save to file.");
                    this.region = null;
                    this.filename = "";
                    return;
                }
                catch (InvalidFormatException e)
                {
                    v.sendMessage(ChatColor.DARK_RED + "There was an issue loading " + this.filename + ".schematic " + e.getMessage());
                    v.sendMessage(ChatColor.AQUA + "Running in clipboard mode. Can copy/paste but will not save to file.");
                    this.region = null;
                    this.filename = "";
                    return;
                }
                v.sendMessage(ChatColor.RED + "Stencil '" + this.filename + "' exists and was loaded.  Make sure you are using powder if you do not want any chance of overwriting the file.");
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

        for (int i = 2 + offset; i < par.length; i++)
        {
            if ((par[i].equalsIgnoreCase("-r") || par[i].equalsIgnoreCase("-rotate")) && i < par.length - 1)
            {
                try
                {
                    double rot = Double.parseDouble(par[++i]);
                    this.yaw = (-rot) % 360;
                    v.sendMessage("Stencil yaw set to " + rot + " degrees.");
                }
                catch (NumberFormatException e)
                {
                    continue;
                }
            }
            if ((par[i].equalsIgnoreCase("-f") || par[i].equalsIgnoreCase("-flip")) && i < par.length - 1)
            {
                String dir = par[++i];
                while (dir.length() > 0)
                {
                    if (dir.length() >= 1 && (dir.substring(0, 1).equalsIgnoreCase("n") || dir.substring(0, 1).equalsIgnoreCase("s")))
                    {
                        dir = dir.substring(1);
                        xFlipped = true;
                        continue;
                    }
                    if (dir.length() >= 5 && (dir.substring(0, 5).equalsIgnoreCase("north") || dir.substring(0, 5).equalsIgnoreCase("south")))
                    {
                        dir = dir.substring(5);
                        xFlipped = true;
                        continue;
                    }

                    if (dir.length() >= 1 && (dir.substring(0, 1).equalsIgnoreCase("e") || dir.substring(0, 1).equalsIgnoreCase("w")))
                    {
                        dir = dir.substring(1);
                        zFlipped = true;
                        continue;
                    }
                    if (dir.length() >= 4 && (dir.substring(0, 4).equalsIgnoreCase("east") || dir.substring(0, 4).equalsIgnoreCase("west")))
                    {
                        dir = dir.substring(4);
                        zFlipped = true;
                        continue;
                    }

                    if (dir.length() >= 1 && (dir.substring(0, 1).equalsIgnoreCase("u") || dir.substring(0, 1).equalsIgnoreCase("d")))
                    {
                        dir = dir.substring(1);
                        yFlipped = true;
                        continue;
                    }
                    if (dir.length() >= 2 && (dir.substring(0, 2).equalsIgnoreCase("up")))
                    {
                        dir = dir.substring(2);
                        yFlipped = true;
                        continue;
                    }
                    if (dir.length() >= 4 && (dir.substring(0, 4).equalsIgnoreCase("down")))
                    {
                        dir = dir.substring(4);
                        yFlipped = true;
                        continue;
                    }
                    dir = dir.substring(1);
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
            if (par[i].startsWith("p"))
            {
                try
                {
                    double rot = Double.parseDouble(par[i].substring(1));
                    this.pitch = rot % 360;
                    v.sendMessage("Stencil pitch set to " + rot + " degrees.");
                }
                catch (NumberFormatException e)
                {
                    continue;
                }
            }
            if (par[i].startsWith("y"))
            {
                try
                {
                    double rot = Double.parseDouble(par[i].substring(1));
                    this.yaw = (-rot) % 360;
                    v.sendMessage("Stencil yaw set to " + rot + " degrees.");
                }
                catch (NumberFormatException e)
                {
                    continue;
                }
            }
            if (par[i].startsWith("r"))
            {
                try
                {
                    double rot = Double.parseDouble(par[i].substring(1));
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
