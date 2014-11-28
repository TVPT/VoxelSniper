/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 The Voxel Plugineering Team
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
package com.voxelplugineering.voxelsniper.bukkit;

import com.voxelplugineering.voxelsniper.api.Gunsmith;
import com.voxelplugineering.voxelsniper.common.CommonBlock;
import com.voxelplugineering.voxelsniper.common.CommonLocation;
import com.voxelplugineering.voxelsniper.common.CommonMaterial;

/**
 * A bukkit wrapper for {@link CommonBlock}s.
 */
public class BukkitBlock extends CommonBlock
{

    /**
     * Creates a new {@link BukkitBlock}.
     * 
     * @param location the location of the block, cannot be null
     * @param material the material of the block, cannot be null
     */
    public BukkitBlock(CommonLocation location, BukkitMaterial material)
    {
        super(location, material);
    }

    /**
     * Creates a new {@link BukkitBlock}. The material is set to the current material at the given location.
     * 
     * @param location the location of the block, cannot be null
     */
    public BukkitBlock(CommonLocation location)
    {
        super(location, Gunsmith.getMaterialFactory()
                .getMaterial(
                        ((BukkitWorld) location.getWorld())
                                .localGetMaterialAt(location.getFlooredX(), location.getFlooredY(), location.getFlooredZ()).name()));
    }

    /**
     * {@inheritDoc}
     */
    public CommonMaterial<?> getMaterial()
    {
        CommonMaterial<?> mat =
                Gunsmith.getMaterialFactory().getMaterial(
                        ((BukkitWorld) this.getLocation().getWorld()).localGetMaterialAt(this.getLocation().getFlooredX(),
                                this.getLocation().getFlooredY(), this.getLocation().getFlooredZ()).name());
        localSetMaterial(mat);
        return mat;
    }
}
