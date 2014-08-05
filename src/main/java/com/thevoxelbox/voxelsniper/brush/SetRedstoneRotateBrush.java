package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.util.CoreProtectUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class SetRedstoneRotateBrush extends Brush
{
    private Block block = null;
    private Undo undo;

    /**
     *
     */
    public SetRedstoneRotateBrush()
    {
        this.setName("Set Redstone Rotate");
    }

    private boolean set(final Block bl, final SnipeData v)
    {
        if (this.block == null)
        {
            this.block = bl;
            return true;
        }
        else
        {
            this.undo = new Undo();
            final int lowX = (this.block.getX() <= bl.getX()) ? this.block.getX() : bl.getX();
            final int lowY = (this.block.getY() <= bl.getY()) ? this.block.getY() : bl.getY();
            final int lowZ = (this.block.getZ() <= bl.getZ()) ? this.block.getZ() : bl.getZ();
            final int highX = (this.block.getX() >= bl.getX()) ? this.block.getX() : bl.getX();
            final int highY = (this.block.getY() >= bl.getY()) ? this.block.getY() : bl.getY();
            final int highZ = (this.block.getZ() >= bl.getZ()) ? this.block.getZ() : bl.getZ();

            for (int y = lowY; y <= highY; y++)
            {
                for (int x = lowX; x <= highX; x++)
                {
                    for (int z = lowZ; z <= highZ; z++)
                    {
                        this.perform(this.clampY(x, y, z), v);
                    }
                }
            }
            this.block = null;
            return false;
        }
    }

    @SuppressWarnings("deprecation")
	private void perform(final Block bl, final SnipeData v)
    {
        if (bl.getType() == Material.DIODE_BLOCK_ON || bl.getType() == Material.DIODE_BLOCK_OFF)
        {
            this.undo.put(bl);
            CoreProtectUtils.logBlockRemove(bl, v.owner().getPlayer().getName());
            bl.setData((((bl.getData() % 4) + 1 < 5) ? (byte) (bl.getData() + 1) : (byte) (bl.getData() - 4)));
    	    CoreProtectUtils.logBlockPlace(bl, v.owner().getPlayer().getName());
        }
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        if (this.set(this.getTargetBlock(), v))
        {
            v.owner().getPlayer().sendMessage(ChatColor.GRAY + "Point one");
        }
        else
        {
            v.owner().storeUndo(this.undo);
        }
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        if (this.set(this.getLastBlock(), v))
        {
            v.owner().getPlayer().sendMessage(ChatColor.GRAY + "Point one");
        }
        else
        {
            v.owner().storeUndo(this.undo);
        }
    }

    @Override
    public final void info(final Message vm)
    {
        this.block = null;
        vm.brushName(this.getName());
    }

    @Override
    public final void parameters(final String[] par, final SnipeData v)
    {
        super.parameters(par, v);
    }

    @Override
    public String getPermissionNode()
    {
        return "voxelsniper.brush.setredstonerotate";
    }
}
