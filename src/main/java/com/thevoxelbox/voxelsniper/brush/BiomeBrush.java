package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

import org.bukkit.ChatColor;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

/**
 * @author Piotr <przerwap@gmail.com>
 */
public class BiomeBrush extends Brush
{
    private static int timesUsed = 0;
    private Biome selectedBiome = Biome.PLAINS;

    /**
     *
     */
    public BiomeBrush()
    {
        this.setName("Biome (currently not working)");
    }

    private void biome(final SnipeData v)
    {
        final int _bSize = v.getBrushSize();
        final double _bPow = Math.pow(_bSize, 2);

        for (int _x = -_bSize; _x <= _bSize; _x++)
        {
            final double _xPow = Math.pow(_x, 2);

            for (int _z = -_bSize; _z <= _bSize; _z++)
            {
                if ((_xPow + Math.pow(_z, 2)) <= _bPow)
                {
                    this.getWorld().setBiome(this.getBlockPositionX() + _x, this.getBlockPositionZ() + _z, this.selectedBiome);
                }
            }
        }

        final Block _b1 = this.getWorld().getBlockAt(this.getBlockPositionX() - _bSize, 0, this.getBlockPositionZ() - _bSize);
        final Block _b2 = this.getWorld().getBlockAt(this.getBlockPositionX() + _bSize, 0, this.getBlockPositionZ() + _bSize);

        final int _lowX = (_b1.getX() <= _b2.getX()) ? _b1.getChunk().getX() : _b2.getChunk().getX();
        final int _lowZ = (_b1.getZ() <= _b2.getZ()) ? _b1.getChunk().getZ() : _b2.getChunk().getZ();
        final int _highX = (_b1.getX() >= _b2.getX()) ? _b1.getChunk().getX() : _b2.getChunk().getX();
        final int _highZ = (_b1.getZ() >= _b2.getZ()) ? _b1.getChunk().getZ() : _b2.getChunk().getZ();

        for (int _x = _lowX; _x <= _highX; _x++)
        {
            for (int _z = _lowZ; _z <= _highZ; _z++)
            {
                this.getWorld().refreshChunk(_x, _z);
            }
        }
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        this.biome(v);
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        this.biome(v);
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
        vm.size();
        vm.custom(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN + this.selectedBiome.name());
    }

    @Override
    public final void parameters(final String[] args, final SnipeData v)
    {
        if (args[1].equalsIgnoreCase("info"))
        {
            v.sendMessage(ChatColor.GOLD + "Biome Brush Parameters:");
            String _availableBiomes = "";

            for (final org.bukkit.block.Biome _biome : org.bukkit.block.Biome.values())
            {
                if (_availableBiomes.isEmpty())
                {
                    _availableBiomes = ChatColor.DARK_GREEN + _biome.name();
                    continue;
                }

                _availableBiomes += ChatColor.RED + ", " + ChatColor.DARK_GREEN + _biome.name();

            }
            v.sendMessage(ChatColor.DARK_BLUE + "Available biomes: " + _availableBiomes);
        }
        else
        {
            // allows biome names with spaces in their name
            String biomeName = args[1];
            for(int i = 1; i < args.length; i++) {
                biomeName += " " + args[i];
            }

            for (final org.bukkit.block.Biome _bio : org.bukkit.block.Biome.values())
            {
                if (_bio.name().equalsIgnoreCase(biomeName))
                {
                    this.selectedBiome = _bio;
                    break;
                }
            }
            v.sendMessage(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN + this.selectedBiome.name());
        }
    }

    @Override
    public final int getTimesUsed()
    {
        return BiomeBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        BiomeBrush.timesUsed = tUsed;
    }
}
