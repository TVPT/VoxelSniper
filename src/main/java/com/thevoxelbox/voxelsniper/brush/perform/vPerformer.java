/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public abstract class vPerformer
{

    public String name = "Performer";
    protected Undo h;
    protected World w;
    protected String p;

    public abstract void info(Message vm);

    public abstract void init(com.thevoxelbox.voxelsniper.SnipeData v);

    public void setUndo()
    {
        h = new Undo();
    }

    public abstract void perform(Block b);

    public Undo getUndo()
    {
        Undo temp = h;
        h = null;
        return temp;
    }

    public boolean isUsingReplaceMaterial()
    {
        return false;
    }
}
