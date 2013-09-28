package com.thevoxelbox.voxelsniper.util;

import com.thevoxelbox.voxelsniper.Brushes;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import com.thevoxelbox.voxelsniper.common.BrushInfo;

import java.util.TreeSet;

/**
 *
 */
public final class BrushInfoFactory
{
    public static BrushInfo createBrushInfo(IBrush brush)
    {
        TreeSet<String> sniperBrushHandles = new TreeSet<String>(Brushes.getAllBrushHandles(brush.getClass()));
        return new BrushInfo(brush.getName(), brush.getBrushCategory(), sniperBrushHandles.first());
    }
}
