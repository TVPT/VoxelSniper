package com.thevoxelbox.voxelsniper.brush;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.util.InvalidFormatException;
import com.thevoxelbox.voxelsniper.util.schematic.BlockRegion;
import com.thevoxelbox.voxelsniper.util.schematic.BlockRegionMask;
import com.thevoxelbox.voxelsniper.util.schematic.BlockRegionMaskType;
import com.thevoxelbox.voxelsniper.util.schematic.BlockRegionOperations;
import com.thevoxelbox.voxelsniper.util.schematic.MCEditSchematic;

/**
 * Allows the formation of stencils into lists. Lists can be created or modified from ingame. A random rotation or axis to flip across can
 * be specified and calculated fresh for every past action.
 * 
 * @author Deamon, Gavjenks
 */
public class StencilListBrush extends Brush
{
    private byte         pasteOption      = 1;                      // 0 = full, 1 = fill, 2 = replace
    private String       filename         = "";
    private List<String> stencilList      = new ArrayList<String>();
    private boolean      randRot          = false;
    private boolean      randFlipX        = false;
    private boolean      randFlipY        = false;
    private boolean      randFlipZ        = false;
    private double       rotationInterval = 90;

    /**
     *
     */
    public StencilListBrush()
    {
        this.setName("StencilList");
    }

    private String readRandomStencil(final SnipeData v)
    {
        double rand = Math.random() * (this.stencilList.size());
        final int choice = (int) rand;
        return this.stencilList.get(choice);
    }

    private void readStencilList(final SnipeData v)
    {
        final File file = new File("plugins/VoxelSniper/stencilLists/" + this.filename + ".txt");
        this.stencilList.clear();
        if (file.exists())
        {
            try
            {
                final Scanner scanner = new Scanner(file);
                while (scanner.hasNext())
                {
                    this.stencilList.add(scanner.nextLine());
                }
                scanner.close();
            }
            catch (final Exception exception)
            {
                exception.printStackTrace();
                v.sendMessage(ChatColor.DARK_RED + "Failed to load stencilList!");
                return;
            }
        }
    }

    private void saveStencilList(final SnipeData v)
    {
        if (stencilList.size() == 0)
        {
            return;
        }
        final File file = new File("plugins/VoxelSniper/stencilLists/" + this.filename + ".txt");
        if (!file.getParentFile().exists())
        {
            file.getParentFile().mkdirs();
        }
        if (file.exists())
        {
            file.delete();
        }
        try
        {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            for (int i = 0; i < this.stencilList.size(); i++)
            {
                String s = this.stencilList.get(i);
                writer.write(s);
                if (i < this.stencilList.size() - 1)
                {
                    writer.write(System.getProperty("line.separator"));
                }
            }
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            v.sendMessage(ChatColor.DARK_RED + "Failed to save stencilList!");
            return;
        }

    }

    private void stencilPaste(final SnipeData v, double rot, boolean xFlipped, boolean yFlipped, boolean zFlipped)
    {
        if (this.filename.equals(""))
        {
            v.sendMessage(ChatColor.RED + "Please select a stencil list before attempting to paste.");
            return;
        }

        if (this.stencilList.isEmpty())
        {
            v.sendMessage(ChatColor.RED + "Your current stencil list is empty, please add some stencils to it.");
            return;
        }

        final String stencilName = this.readRandomStencil(v);
        v.sendMessage(stencilName + " rot: " + rot + "flipped on x: " + xFlipped + " y: " + yFlipped + " z: " + zFlipped);

        BlockRegion region = null;
        final File file = new File("plugins/VoxelSniper/stencils/" + stencilName + ".schematic");
        if (file.exists())
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
            BlockRegionOperations.placeIntoWorldUnbuffered(region, v.getWorld(), getTargetBlock().getX(), getTargetBlock().getY(), getTargetBlock().getZ(), undo, mask, rot, 0, 0, xFlipped, yFlipped, zFlipped);
        }
        catch (InvalidFormatException e)
        {
            e.printStackTrace();
            v.sendMessage(ChatColor.DARK_RED + "Attempted to place invalid stencil!");
        }
        v.owner().storeUndo(undo);
    }

    private void stencilPasteRotation(final SnipeData v)
    {
        double r = this.randRot ? Math.floor(Math.random() * (360 / this.rotationInterval)) * this.rotationInterval : 0;
        boolean xf = this.randFlipX ? Math.random() < 0.5 : false;
        boolean yf = this.randFlipY ? Math.random() < 0.5 : false;
        boolean zf = this.randFlipZ ? Math.random() < 0.5 : false;
        this.stencilPaste(v, r, xf, yf, zf);
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.stencilPaste(v, 0, false, false, false);
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
        int offset = 0;
        if (par[1].equalsIgnoreCase("info"))
        {
            v.sendMessage(ChatColor.GOLD + "Stencil List brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b sl (full|fill|replace) [list] (+|-[name])* (rotate|r #) (flip|f N|S|E|W|NORTH|SOUTH|EAST|WEST) -- Loads the specified stencil list. the names specified in [name] may use wildcards. The degrees specified after rotate are the interval, each time a stencil is pasted it will be rotated by a random multiple of this interval.");
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
            final File file = new File("plugins/VoxelSniper/stencilLists/" + this.filename + ".txt");
            if (file.exists())
            {
                v.sendMessage(ChatColor.RED + "Stencil List '" + this.filename + "' exists and was loaded.");
                this.readStencilList(v);
            }
            else
            {
                v.sendMessage(ChatColor.RED + "That stencil list does not exist, you'll need to add some stencils before you can start pasting.");
                this.stencilList.clear();
            }
        }
        boolean changed = false;

        randFlipX = randFlipY = randFlipZ = randRot = false;

        for (int i = 1 + offset; i < par.length; i++)
        {
            if (par[i].startsWith("-"))
            {
                String arg = par[i].substring(1);
                String[] stencils = expandWildcardStencil(arg);
                for (String fn: stencils)
                {
                    this.stencilList.remove(fn);
                    v.sendMessage(ChatColor.DARK_AQUA + "Removed: " + ChatColor.RED + fn);
                    changed = true;
                }
                if (stencils.length == 0)
                {
                    v.sendMessage(ChatColor.DARK_AQUA + "Your stencilList did not contain any stencils matching that name");
                }
            }
            else if (par[i].startsWith("+"))
            {
                String arg = par[i].substring(1);
                String[] stencils = expandWildcardStencil(arg);
                for (String fn: stencils)
                {
                    final File file = new File("plugins/VoxelSniper/stencils/" + fn + ".schematic");
                    try
                    {
                        MCEditSchematic.checkStencil("plugins/VoxelSniper/stencils/", fn);
                    }
                    catch (IOException ignore)
                    {
                    }

                    if (file.exists() && !this.stencilList.contains(fn))
                    {
                        this.stencilList.add(fn);
                        v.sendMessage(ChatColor.DARK_AQUA + "Added: " + ChatColor.RED + fn);
                        changed = true;
                    }
                    else
                    {
                        v.sendMessage(ChatColor.RED + "Could not find the stencil " + fn);
                    }
                }

            }
            else
            {
                if ((par[i].equalsIgnoreCase("r") || par[i].equalsIgnoreCase("rotate")) && i < par.length - 1)
                {
                    double rot = Double.parseDouble(par[++i]);
                    this.rotationInterval = rot % 360;
                    randRot = true;
                    v.sendMessage("Stencil random rotation interval set to " + rot + " degrees.");
                }
                else if ((par[i].equalsIgnoreCase("r") || par[i].equalsIgnoreCase("rotate")) && i >= par.length - 1)
                {
                    this.rotationInterval = 90;
                    randRot = true;
                    v.sendMessage("Stencil random rotation interval set to 90 degrees.");
                }
                if ((par[i].equalsIgnoreCase("f") || par[i].equalsIgnoreCase("flip")) && i < par.length - 1)
                {
                    randFlipX = randFlipY = randFlipZ = false;

                    String dir = par[++i];
                    while (dir.length() > 0)
                    {
                        if (dir.length() >= 1 && (dir.substring(0, 1).equalsIgnoreCase("n") || dir.substring(0, 1).equalsIgnoreCase("s")))
                        {
                            dir = dir.substring(1);
                            randFlipX = true;
                            continue;
                        }
                        if (dir.length() >= 5 && (dir.substring(0, 5).equalsIgnoreCase("north") || dir.substring(0, 5).equalsIgnoreCase("south")))
                        {
                            dir = dir.substring(5);
                            randFlipX = true;
                            continue;
                        }

                        if (dir.length() >= 1 && (dir.substring(0, 1).equalsIgnoreCase("e") || dir.substring(0, 1).equalsIgnoreCase("w")))
                        {
                            dir = dir.substring(1);
                            randFlipZ = true;
                            continue;
                        }
                        if (dir.length() >= 4 && (dir.substring(0, 4).equalsIgnoreCase("east") || dir.substring(0, 4).equalsIgnoreCase("west")))
                        {
                            dir = dir.substring(4);
                            randFlipZ = true;
                            continue;
                        }

                        if (dir.length() >= 1 && (dir.substring(0, 1).equalsIgnoreCase("u") || dir.substring(0, 1).equalsIgnoreCase("d")))
                        {
                            dir = dir.substring(1);
                            randFlipY = true;
                            continue;
                        }
                        if (dir.length() >= 2 && (dir.substring(0, 2).equalsIgnoreCase("up")))
                        {
                            dir = dir.substring(2);
                            randFlipY = true;
                            continue;
                        }
                        if (dir.length() >= 4 && (dir.substring(0, 4).equalsIgnoreCase("down")))
                        {
                            dir = dir.substring(4);
                            randFlipY = true;
                            continue;
                        }
                        dir = dir.substring(1);
                    }

                    if (randFlipZ)
                    {
                        v.sendMessage("Stencil set to flip randomly on the X axis.");
                    }
                    if (randFlipZ)
                    {
                        v.sendMessage("Stencil set to flip randomly on the Y axis.");
                    }
                    if (randFlipZ)
                    {
                        v.sendMessage("Stencil set to flip randomly on the Z axis.");
                    }
                }
            }
        }
        if (changed)
        {
            this.saveStencilList(v);
        }
    }

    private String[] expandWildcardStencil(final String arg)
    {
        if (arg.contains("*"))
        {
            final Pattern p = Pattern.compile(arg.replace("*", ".*"));
            File dir = new File(VoxelSniper.getInstance().getDataFolder(), "stencils");
            FileFilter fileFilter = new FileFilter(){

                @Override
                public boolean accept(File f)
                {
                    if(p.matcher(f.getName()).matches())
                    {
                        return true;
                    }
                    return false;
                }
                
            };
            File[] files = dir.listFiles(fileFilter);
            String[] matches = new String[files.length];
            for (int i = 0; i < files.length; i++)
            {
                matches[i] = files[i].getName().replace(".schematic", "");
            }
            return matches;
        }
        return new String[] { arg };
    }

    @Override
    public String getPermissionNode()
    {
        return "voxelsniper.brush.stencillist";
    }
}
