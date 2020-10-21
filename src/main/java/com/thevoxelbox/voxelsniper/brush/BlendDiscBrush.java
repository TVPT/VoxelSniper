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
public class BlendDiscBrush extends BlendBrushBase
{
    /**
     *
     */
    public BlendDiscBrush()
    {
        this.setName("Blend Disc");
    }

    @Override
    protected final void blend(final SnipeData v)
    {
        final int brushSize = v.getBrushSize();
        final int brushSizeDoubled = 2 * brushSize;
        final Material[][] oldMaterials = new Material[2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1]; // Array that holds the original materials plus a buffer
        final Material[][] newMaterials = new Material[brushSizeDoubled + 1][brushSizeDoubled + 1]; // Array that holds the blended materials

        // Log current materials into oldmats
        for (int x = 0; x <= 2 * (brushSize + 1); x++)
        {
            for (int z = 0; z <= 2 * (brushSize + 1); z++)
            {
                oldMaterials[x][z] = this.getBlockTypeAt(this.getTargetBlock().getX() - brushSize - 1 + x, this.getTargetBlock().getY(), this.getTargetBlock().getZ() - brushSize - 1 + z);
            }
        }

        // Log current materials into newmats
        for (int x = 0; x <= brushSizeDoubled; x++)
        {
            for (int z = 0; z <= brushSizeDoubled; z++)
            {
                newMaterials[x][z] = oldMaterials[x + 1][z + 1];
            }
        }

        // Blend materials
        for (int x = 0; x <= brushSizeDoubled; x++)
        {
            for (int z = 0; z <= brushSizeDoubled; z++)
            {
                final Map<Material, Integer> materialFrequency = new EnumMap<Material, Integer>(Material.class);
                int modeMatCount = 0;
                Material modeMat = Material.AIR;
                boolean tiecheck = true;

                for (int m = -1; m <= 1; m++)
                {
                    for (int n = -1; n <= 1; n++)
                    {
                        if (!(m == 0 && n == 0))
                        {
                            Material material = oldMaterials[x + 1 + m][z + 1 + n];
                            materialFrequency.put(material, materialFrequency.get(material) + 1);
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
                for (Material material : BLOCK_MATERIALS)
                {
                    if (material == modeMat) {
                        break;
                    }

                    if (materialFrequency.get(material) == modeMatCount && !(this.excludeAir && material == Material.AIR) && !(excludeWater && (material == Material.WATER)))
                    {
                        tiecheck = false;
                    }
                }

                // Record most common neighbor material for this block
                if (tiecheck)
                {
                    newMaterials[x][z] = modeMat;
                }
            }
        }

        final Undo undo = new Undo();
        final double rSquared = Math.pow(brushSize + 1, 2);

        // Make the changes
        for (int x = brushSizeDoubled; x >= 0; x--)
        {
            final double xSquared = Math.pow(x - brushSize - 1, 2);

            for (int z = brushSizeDoubled; z >= 0; z--)
            {
                if (xSquared + Math.pow(z - brushSize - 1, 2) <= rSquared)
                {
                    if (!(this.excludeAir && newMaterials[x][z] == Material.AIR) && !(this.excludeWater && (newMaterials[x][z] == Material.WATER)))
                    {
                        if (this.getBlockTypeAt(this.getTargetBlock().getX() - brushSize + x, this.getTargetBlock().getY(), this.getTargetBlock().getZ() - brushSize + z) != newMaterials[x][z])
                        {
                            undo.put(this.clampY(this.getTargetBlock().getX() - brushSize + x, this.getTargetBlock().getY(), this.getTargetBlock().getZ() - brushSize + z));
                        }
                        this.setBlockTypeAt(this.getTargetBlock().getZ() - brushSize + z, this.getTargetBlock().getX() - brushSize + x, this.getTargetBlock().getY(), newMaterials[x][z]);
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
            v.sendMessage(ChatColor.GOLD + "Blend Disc Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b bd water -- toggle include or exclude (default) water");
            return;
        }

        super.parameters(par, v);
    }

    @Override
    public String getPermissionNode()
    {
        return "voxelsniper.brush.blenddisc";
    }
}
