package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.EnumMap;
import java.util.Map;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Blend_Brushes
 */
public class BlendBallBrush extends BlendBrushBase
{
    /**
     *
     */
    public BlendBallBrush()
    {
        this.setName("Blend Ball");
    }

    @Override
    protected final void blend(final SnipeData v)
    {
        final int brushSize = v.getBrushSize();
        final int brushSizeDoubled = 2 * brushSize;
        // Array that holds the original materials plus a buffer
        final Material[][][] oldMaterials = new Material[2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1];
        // Array that holds the blended materials
        final Material[][][] newMaterials = new Material[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];

        // Log current materials into oldmats
        for (int x = 0; x <= 2 * (brushSize + 1); x++)
        {
            for (int y = 0; y <= 2 * (brushSize + 1); y++)
            {
                for (int z = 0; z <= 2 * (brushSize + 1); z++)
                {
                    oldMaterials[x][y][z] = this.getBlockTypeAt(this.getTargetBlock().getX() - brushSize - 1 + x, this.getTargetBlock().getY() - brushSize - 1 + y, this.getTargetBlock().getZ() - brushSize - 1 + z);
                }
            }
        }

        // Log current materials into newmats
        for (int x = 0; x <= brushSizeDoubled; x++)
        {
            for (int y = 0; y <= brushSizeDoubled; y++)
            {
                for (int z = 0; z <= brushSizeDoubled; z++)
                {
                    newMaterials[x][y][z] = oldMaterials[x + 1][y + 1][z + 1];
                }
            }
        }

        // Blend materials
        for (int x = 0; x <= brushSizeDoubled; x++)
        {
            for (int y = 0; y <= brushSizeDoubled; y++)
            {
                for (int z = 0; z <= brushSizeDoubled; z++)
                {
                    // Map that tracks frequency of materials neighboring given block
                    final Map<Material, Integer> materialFrequency = new EnumMap<Material, Integer>(Material.class);
                    int modeMatCount = 0;
                    Material modeMat = Material.AIR;
                    boolean tiecheck = true;

                    for (int m = -1; m <= 1; m++)
                    {
                        for (int n = -1; n <= 1; n++)
                        {
                            for (int o = -1; o <= 1; o++)
                            {
                                if (!(m == 0 && n == 0 && o == 0))
                                {
                                    Material material = oldMaterials[x + 1 + m][y + 1 + n][z + 1 + o];
                                    materialFrequency.put(material, materialFrequency.get(material) + 1);
                                }
                            }
                        }
                    }

                    // Find most common neighboring material.
                    for (Material material : BLOCK_MATERIALS)
                    {
                        int freq = materialFrequency.get(material);
                        if (freq > modeMatCount && !(this.excludeAir && material == Material.AIR) && !(this.excludeWater && (material == Material.WATER)))
                        {
                            modeMatCount = freq;
                            modeMat = material;
                        }
                    }

                    // Make sure there'world not a tie for most common
                    for (Material material : BLOCK_MATERIALS) {
                        if (material == modeMat) {
                            break;
                        }

                        if (materialFrequency.get(material) == modeMatCount && !(this.excludeAir && material == Material.AIR) && !(this.excludeWater && (material == Material.WATER)))
                        {
                            tiecheck = false;
                        }
                    }

                    // Record most common neighbor material for this block
                    if (tiecheck)
                    {
                        newMaterials[x][y][z] = modeMat;
                    }
                }
            }
        }

        final Undo undo = new Undo();
        final double rSquared = Math.pow(brushSize + 1, 2);

        // Make the changes  
        for (int x = brushSizeDoubled; x >= 0; x--)
        {
            final double xSquared = Math.pow(x - brushSize - 1, 2);

            for (int y = 0; y <= brushSizeDoubled; y++)
            {
                final double ySquared = Math.pow(y - brushSize - 1, 2);

                for (int z = brushSizeDoubled; z >= 0; z--)
                {
                    if (xSquared + ySquared + Math.pow(z - brushSize - 1, 2) <= rSquared)
                    {
                        if (!(this.excludeAir && newMaterials[x][y][z] == Material.AIR) && !(this.excludeWater && (newMaterials[x][y][z] == Material.WATER)))
                        {
                            if (this.getBlockTypeAt(this.getTargetBlock().getX() - brushSize + x, this.getTargetBlock().getY() - brushSize + y, this.getTargetBlock().getZ() - brushSize + z) != newMaterials[x][y][z])
                            {
                                undo.put(this.clampY(this.getTargetBlock().getX() - brushSize + x, this.getTargetBlock().getY() - brushSize + y, this.getTargetBlock().getZ() - brushSize + z));
                            }
                            this.setBlockTypeAt(this.getTargetBlock().getZ() - brushSize + z, this.getTargetBlock().getX() - brushSize + x, this.getTargetBlock().getY() - brushSize + y, newMaterials[x][y][z]);
                        }
                    }
                }
            }
        }
        v.owner().storeUndo(undo);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        if (par[1].equalsIgnoreCase("info"))
        {
            v.sendMessage(ChatColor.GOLD + "Blend Ball Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b bb water -- toggle include or exclude (default: exclude) water");
            return;
        }

        super.parameters(par, v);
    }

    @Override
    public String getPermissionNode()
    {
        return "voxelsniper.brush.blendball";
    }
}
