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
package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.spongepowered.api.text.format.TextColors;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_OCEANATOR_5000
 *
 * @author Voxel
 */
public class OceanBrush extends Brush {

    // @Spongify
    private static final int WATER_LEVEL_DEFAULT = 62; // y=63 -- we are using
                                                       // array indices here
    private static final int WATER_LEVEL_MIN = 12;
    private static final int LOW_CUT_LEVEL = 12;
//    private static final List<Material> EXCLUDED_MATERIALS = new LinkedList<Material>();
//
//    static
//    {
//        EXCLUDED_MATERIALS.add(Material.AIR);
//        EXCLUDED_MATERIALS.add(Material.SAPLING);
//        EXCLUDED_MATERIALS.add(Material.WATER);
//        EXCLUDED_MATERIALS.add(Material.STATIONARY_WATER);
//        EXCLUDED_MATERIALS.add(Material.LAVA);
//        EXCLUDED_MATERIALS.add(Material.STATIONARY_LAVA);
//        EXCLUDED_MATERIALS.add(Material.LOG);
//        EXCLUDED_MATERIALS.add(Material.LEAVES);
//        EXCLUDED_MATERIALS.add(Material.YELLOW_FLOWER);
//        EXCLUDED_MATERIALS.add(Material.RED_ROSE);
//        EXCLUDED_MATERIALS.add(Material.RED_MUSHROOM);
//        EXCLUDED_MATERIALS.add(Material.BROWN_MUSHROOM);
//        EXCLUDED_MATERIALS.add(Material.MELON_BLOCK);
//        EXCLUDED_MATERIALS.add(Material.MELON_STEM);
//        EXCLUDED_MATERIALS.add(Material.PUMPKIN);
//        EXCLUDED_MATERIALS.add(Material.PUMPKIN_STEM);
//        EXCLUDED_MATERIALS.add(Material.COCOA);
//        EXCLUDED_MATERIALS.add(Material.SNOW);
//        EXCLUDED_MATERIALS.add(Material.SNOW_BLOCK);
//        EXCLUDED_MATERIALS.add(Material.ICE);
//        EXCLUDED_MATERIALS.add(Material.SUGAR_CANE_BLOCK);
//        EXCLUDED_MATERIALS.add(Material.LONG_GRASS);
//        EXCLUDED_MATERIALS.add(Material.SNOW);
//    }

    private int waterLevel = WATER_LEVEL_DEFAULT;
    private boolean coverFloor = false;

    /**
     *
     */
    public OceanBrush() {
        this.setName("OCEANATOR 5000(tm)");
    }

    private int getHeight(final int bx, final int bz) {
//        for (int y = this.getWorld().getHighestBlockYAt(bx, bz); y > 0; y--)
//        {
//            final Material material = this.clampY(bx, y, bz).getType();
//            if (!EXCLUDED_MATERIALS.contains(material))
//            {
//                return y;
//            }
//        }
        return 0;
    }

    /**
     * @param v
     * @param undo
     */
    @SuppressWarnings("deprecation")
    protected final void oceanator(final SnipeData v, final Undo undo) {
//        final World world = this.getWorld();
//
//        final int minX = (int) Math.floor((this.getTargetBlock().getX() - v.getBrushSize()));
//        final int minZ = (int) Math.floor((this.getTargetBlock().getZ() - v.getBrushSize()));
//        final int maxX = (int) Math.floor((this.getTargetBlock().getX() + v.getBrushSize()));
//        final int maxZ = (int) Math.floor((this.getTargetBlock().getZ() + v.getBrushSize()));
//
//        for (int x = minX; x <= maxX; x++)
//        {
//            for (int z = minZ; z <= maxZ; z++)
//            {
//                final int currentHeight = getHeight(x, z);
//                final int wLevelDiff = currentHeight - (this.waterLevel - 1);
//                final int newSeaFloorLevel = ((this.waterLevel - wLevelDiff) >= LOW_CUT_LEVEL) ? this.waterLevel - wLevelDiff : LOW_CUT_LEVEL;
//
//                final int highestY = this.getWorld().getHighestBlockYAt(x, z);
//
//                // go down from highest Y block down to new sea floor
//                for (int y = highestY; y > newSeaFloorLevel; y--)
//                {
//                    final Block block = world.getBlockAt(x, y, z);
//                    if (!block.getType().equals(Material.AIR))
//                    {
//                        undo.put(block);
//                        block.setType(Material.AIR);
//                    }
//                }
//
//                // go down from water level to new sea level
//                for (int y = this.waterLevel; y > newSeaFloorLevel; y--)
//                {
//                    final Block block = world.getBlockAt(x, y, z);
//                    if (!block.getType().equals(Material.STATIONARY_WATER))
//                    {
//                        // do not put blocks into the undo we already put into
//                        if (!block.getType().equals(Material.AIR))
//                        {
//                            undo.put(block);
//                        }
//                        block.setType(Material.STATIONARY_WATER);
//                    }
//                }
//
//                // cover the sea floor of required
//                if (this.coverFloor && (newSeaFloorLevel < this.waterLevel))
//                {
//                    Block block = world.getBlockAt(x, newSeaFloorLevel, z);
//                    if (block.getTypeId() != v.getVoxelId())
//                    {
//                        undo.put(block);
//                        block.setTypeId(v.getVoxelId());
//                    }
//                }
//            }
//        }
    }

    @Override
    protected final void arrow(final SnipeData v) {
//        Undo undo = new Undo();
//        this.oceanator(v, undo);
//        v.owner().storeUndo(undo);
    }

    @Override
    protected final void powder(final SnipeData v) {
        arrow(v);
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v) {
        for (int i = 0; i < par.length; i++) {
            final String parameter = par[i];

            try {
                if (parameter.equalsIgnoreCase("info")) {
                    v.sendMessage(TextColors.BLUE, "Parameters:");
                    v.sendMessage(TextColors.GREEN, "-wlevel #  ", TextColors.BLUE, "--  Sets the water level (e.g. -wlevel 64)");
                    v.sendMessage(TextColors.GREEN, "-cfloor [y|n]  ", TextColors.BLUE,
                            "--  Enables or disables sea floor cover (e.g. -cfloor y) (Cover material will be your voxel material)");
                } else if (parameter.equalsIgnoreCase("-wlevel")) {
                    if ((i + 1) >= par.length) {
                        v.sendMessage(TextColors.RED, "Missing parameter. Correct syntax: -wlevel [#] (e.g. -wlevel 64)");
                        continue;
                    }

                    int temp = Integer.parseInt(par[++i]);

                    if (temp <= WATER_LEVEL_MIN) {
                        v.sendMessage(TextColors.RED, "Error: Your specified water level was below 12.");
                        continue;
                    }

                    this.waterLevel = temp - 1;
                    v.sendMessage(TextColors.BLUE, "Water level set to ", TextColors.GREEN, (waterLevel + 1));
                } else if (parameter.equalsIgnoreCase("-cfloor") || parameter.equalsIgnoreCase("-coverfloor")) {
                    if ((i + 1) >= par.length) {
                        v.sendMessage(TextColors.RED, "Missing parameter. Correct syntax: -cfloor [y|n] (e.g. -cfloor y)");
                        continue;
                    }

                    this.coverFloor = par[++i].equalsIgnoreCase("y");
                    v.sendMessage(TextColors.BLUE, String.format("Floor cover %s.", TextColors.GREEN, (this.coverFloor ? "enabled" : "disabled")));
                }
            } catch (Exception exception) {
                v.sendMessage(TextColors.RED, String.format("Error while parsing parameter: %s", parameter));
                exception.printStackTrace();
            }
        }
    }

    @Override
    public final void info(final Message vm) {
        vm.brushName(this.getName());
        vm.custom(TextColors.BLUE, "Water level set to ", TextColors.GREEN, (waterLevel + 1));
        vm.custom(TextColors.BLUE, "Floor cover ", TextColors.GREEN, (this.coverFloor ? "enabled" : "disabled") + ".");
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.ocean";
    }
}
