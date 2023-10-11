package com.thevoxelbox.voxelsniper.brush;

import java.util.Random;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.PerlinNoiseGenerator;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Heat_Ray
 *
 * @author Gavjenks
 */
public class HeatRayBrush extends Brush
{

    private static final double REQUIRED_OBSIDIAN_DENSITY = 0.6;
    private static final double REQUIRED_COBBLE_DENSITY = 0.5;
    private static final double REQUIRED_FIRE_DENSITY = -0.25;
    private static final double REQUIRED_AIR_DENSITY = 0;

    private int octaves = 5;
    private double frequency = 1;

    private double amplitude = 0.3;


    /**
     * Default Constructor.
     */
    public HeatRayBrush()
    {
        this.setName("Heat Ray");
    }

    /**
     * Heat Ray executer.
     *
     * @param v
     */
    public final void heatRay(final SnipeData v)
    {
        final PerlinNoiseGenerator generator = new PerlinNoiseGenerator(new Random());

        final Vector targetLocation = this.getTargetBlock().getLocation().toVector();
        final Location currentLocation = new Location(this.getTargetBlock().getWorld(), 0, 0, 0);
        final Undo undo = new Undo();
        Block currentBlock;

        for (int z = v.getBrushSize(); z >= -v.getBrushSize(); z--)
        {
            for (int x = v.getBrushSize(); x >= -v.getBrushSize(); x--)
            {
                for (int y = v.getBrushSize(); y >= -v.getBrushSize(); y--)
                {
                    currentLocation.setX(this.getTargetBlock().getX() + x);
                    currentLocation.setY(this.getTargetBlock().getY() + y);
                    currentLocation.setZ(this.getTargetBlock().getZ() + z);

                    if (currentLocation.toVector().isInSphere(targetLocation, v.getBrushSize()))
                    {
                        currentBlock = currentLocation.getBlock();
                        if (currentBlock == null || currentBlock.getType() == Material.CHEST)
                        {
                            continue;
                        }

                        if (currentBlock.isLiquid())
                        {
                            undo.put(currentBlock);
                            currentBlock.setType(Material.AIR);
                            continue;
                        }

                        if (currentBlock.getType().isFlammable())
                        {
                            undo.put(currentBlock);
                            currentBlock.setType(Material.FIRE);
                            continue;
                        }

                        if (!currentBlock.getType().equals(Material.AIR))
                        {
                            final double airDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
                            final double fireDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
                            final double cobbleDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
                            final double obsidianDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);

                            if (obsidianDensity >= HeatRayBrush.REQUIRED_OBSIDIAN_DENSITY)
                            {
                                undo.put(currentBlock);
                                if (currentBlock.getType() != Material.OBSIDIAN)
                                {
                                    currentBlock.setType(Material.OBSIDIAN);
                                }
                            }
                            else if (cobbleDensity >= HeatRayBrush.REQUIRED_COBBLE_DENSITY)
                            {
                                undo.put(currentBlock);
                                if (currentBlock.getType() != Material.COBBLESTONE)
                                {
                                    currentBlock.setType(Material.COBBLESTONE);
                                }
                            }
                            else if (fireDensity >= HeatRayBrush.REQUIRED_FIRE_DENSITY)
                            {
                                undo.put(currentBlock);
                                if (currentBlock.getType() != Material.FIRE)
                                {
                                    currentBlock.setType(Material.FIRE);
                                }
                            }
                            else if (airDensity >= HeatRayBrush.REQUIRED_AIR_DENSITY)
                            {
                                undo.put(currentBlock);
                                if (currentBlock.getType() != Material.AIR)
                                {
                                    currentBlock.setType(Material.AIR);
                                }
                            }
                        }
                    }

                }
            }
        }

        v.owner().storeUndo(undo);
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.heatRay(v);
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.heatRay(v);
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.custom(ChatColor.GREEN + "Octaves: " + this.octaves);
        vm.custom(ChatColor.GREEN + "Amplitude: " + this.amplitude);
        vm.custom(ChatColor.GREEN + "Frequency: " + this.frequency);
        vm.size();
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        for (int i = 1; i < par.length; i++)
        {
            final String parameter = par[i].toLowerCase();

            if (parameter.equalsIgnoreCase("info"))
            {
                v.sendMessage(ChatColor.GOLD + "Heat Ray brush Parameters:");
                v.sendMessage(ChatColor.AQUA + "/b hr oct[int] -- Octaves parameter for the noise generator.");
                v.sendMessage(ChatColor.AQUA + "/b hr amp[float] -- Amplitude parameter for the noise generator.");
                v.sendMessage(ChatColor.AQUA + "/b hr freq[float] -- Frequency parameter for the noise generator.");
            }
            if (parameter.startsWith("oct"))
            {
                this.octaves = Integer.valueOf(parameter.replace("oct", ""));
                v.getVoxelMessage().custom(ChatColor.GREEN + "Octaves: " + this.octaves);
            }
            else if (parameter.startsWith("amp"))
            {
                this.amplitude = Double.valueOf(parameter.replace("amp", ""));
                v.getVoxelMessage().custom(ChatColor.GREEN + "Amplitude: " + this.amplitude);
            }
            else if (parameter.startsWith("freq"))
            {
                this.frequency = Double.valueOf(parameter.replace("freq", ""));
                v.getVoxelMessage().custom(ChatColor.GREEN + "Frequency: " + this.frequency);
            }
        }
    }

    @Override
    public String getPermissionNode()
    {
        return "voxelsniper.brush.heatray";
    }
}
