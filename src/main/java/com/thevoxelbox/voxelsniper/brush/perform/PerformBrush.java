/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.event.SniperBrushChangedEvent;
import org.bukkit.Bukkit;

/**
 * @author Voxel
 */
public abstract class PerformBrush extends Brush implements Performer
{

    protected vPerformer current = new pMaterial();

    public vPerformer getCurrentPerformer()
    {
        return current;
    }

    @Override
    public void parse(String[] args, com.thevoxelbox.voxelsniper.SnipeData v)
    {
        if (PerformerE.has(args[1]))
        {
            vPerformer p = PerformerE.getPerformer(args[1]);
            if (p != null)
            {
                current = p;
                SniperBrushChangedEvent event = new SniperBrushChangedEvent(v.owner(), this, this);
                Bukkit.getPluginManager().callEvent(event);
                info(v.getVoxelMessage());
                current.info(v.getVoxelMessage());
                if (args.length > 2)
                {
                    String[] t = new String[args.length - 1];
                    t[0] = args[0];
                    for (int x = 2; x < args.length; x++)
                    {
                        t[x - 1] = args[x];
                    }
                    parameters(t, v);
                }
            }
            else
            {
                parameters(args, v);
            }
        }
        else
        {
            parameters(args, v);
        }
    }

    public void initP(com.thevoxelbox.voxelsniper.SnipeData v)
    {
        current.init(v);
        current.setUndo();
    }

    @Override
    public void showInfo(Message vm)
    {
        current.info(vm);
    }
}
