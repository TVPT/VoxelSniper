/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.event.SniperBrushChangedEvent;
import org.bukkit.Bukkit;

import java.util.Arrays;

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
        String handle = args[0];
        if (PerformerE.has(handle))
        {
            vPerformer p = PerformerE.getPerformer(handle);
            if (p != null)
            {
                current = p;
                SniperBrushChangedEvent event = new SniperBrushChangedEvent(v.owner(), v.owner().getCurrentToolId(), this, this);
                Bukkit.getPluginManager().callEvent(event);
                info(v.getVoxelMessage());
                current.info(v.getVoxelMessage());
                if (args.length > 1)
                {
                    String[] additionalArguments = Arrays.copyOfRange(args, 1, args.length);
                    parameters(hackTheArray(additionalArguments), v);
                }
            }
            else
            {
                parameters(hackTheArray(args), v);
            }
        }
        else
        {
            parameters(hackTheArray(args), v);
        }
    }

    /**
     * Padds an empty String to the front of the array.
     *
     * @param args Array to pad empty string in front of
     * @return padded array
     */
    private String[] hackTheArray(String[] args)
    {
        String[] returnValue = new String[args.length + 1];
        for (int i = 0, argsLength = args.length; i < argsLength; i++)
        {
            String arg = args[i];
            returnValue[i + 1] = arg;
        }
        return returnValue;
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
