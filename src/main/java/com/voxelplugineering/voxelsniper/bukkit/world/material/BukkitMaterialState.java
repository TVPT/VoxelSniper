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
package com.voxelplugineering.voxelsniper.bukkit.world.material;

import com.voxelplugineering.voxelsniper.world.material.Material;
import com.voxelplugineering.voxelsniper.world.material.MaterialState;

/**
 * A {@link MaterialState} wrapping a byte data value.
 */
public class BukkitMaterialState implements MaterialState
{

    private final byte state;
    private final Material type;

    /**
     * Creates a new {@link BukkitMaterialState}.
     * 
     * @param type The base material
     * @param state The data value
     */
    public BukkitMaterialState(Material type, byte state)
    {
        this.type = type;
        this.state = state;
    }

    @Override
    public Material getType()
    {
        return this.type;
    }

    /**
     * Gets the data value.
     * 
     * @return The data value
     */
    public byte getState()
    {
        return this.state;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        if (!(o instanceof BukkitMaterialState))
        {
            return false;
        }
        BukkitMaterialState sms = (BukkitMaterialState) o;
        return sms.state == this.state;
    }

    @Override
    public int hashCode()
    {
        int r = 1;
        r = r * 31 + this.state;
        return r;
    }

}
