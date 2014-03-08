
package com.thevoxelbox.voxelsniper.util;

import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 *
 */
public class UndoDelegate implements BlockChangeDelegate
{
    private final World targetWorld;
    private Undo currentUndo;
        
    public Undo getUndo()
    {
        final Undo pastUndo = currentUndo;
        currentUndo = new Undo();
        return pastUndo;
    }

    public UndoDelegate(World targetWorld)
    {
        this.targetWorld = targetWorld;
        this.currentUndo = new Undo();
    }

    @SuppressWarnings("deprecation")
	@Override
    public boolean setRawTypeId(int x, int y, int z, int typeId)
    {
        this.currentUndo.put(targetWorld.getBlockAt(x, y, z));
        return this.targetWorld.getBlockAt(x, y, z).setTypeId(typeId, false);        
    }

    @SuppressWarnings("deprecation")
	@Override
    public boolean setRawTypeIdAndData(int x, int y, int z, int typeId, int data)
    {
        this.currentUndo.put(targetWorld.getBlockAt(x, y, z));
        return this.targetWorld.getBlockAt(x, y, z).setTypeIdAndData(typeId, (byte) data, false);
    }

    @SuppressWarnings("deprecation")
	@Override
    public boolean setTypeId(int x, int y, int z, int typeId)
    {
        this.currentUndo.put(targetWorld.getBlockAt(x, y, z));
        return this.targetWorld.getBlockAt(x, y, z).setTypeId(typeId);
    }

    @SuppressWarnings("deprecation")
	@Override
    public boolean setTypeIdAndData(int x, int y, int z, int typeId, int data)
    {
        this.currentUndo.put(targetWorld.getBlockAt(x, y, z));
        return this.targetWorld.getBlockAt(x, y, z).setTypeIdAndData(typeId, (byte) data, true);
    }
    
    @SuppressWarnings("deprecation")
	public boolean setBlock(Block b)
    {
        this.currentUndo.put(this.targetWorld.getBlockAt(b.getLocation()));
        return this.targetWorld.getBlockAt(b.getLocation()).setTypeIdAndData(b.getTypeId(), b.getData(), true);
    }
    

    @SuppressWarnings("deprecation")
	@Override
    public int getTypeId(int x, int y, int z)
    {
        return this.targetWorld.getBlockAt(x, y, z).getTypeId();
    }

    @Override
    public int getHeight()
    {
        return this.targetWorld.getMaxHeight();
    }

    @Override
    public boolean isEmpty(int x, int y, int z)
    {
        return this.targetWorld.getBlockAt(x, y, z).isEmpty();
    }
}
