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
    private BrushInfoFactory()
    {
    }

    public static BrushInfo createBrushInfo(Brushes brushes, IBrush brush)
    {
        TreeSet<String> sniperBrushHandles = new TreeSet<String>(brushes.getSniperBrushHandles(brush.getClass()));
        return new BrushInfo(brush.getName(), brush.getBrushCategory(), sniperBrushHandles.first());
    }
}
