package com.thevoxelbox.voxelsniper.brush;

import java.util.ArrayList;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * This brush only looks for solid blocks, and then changes those plus any air blocks touching them. If it works, this brush should be faster than the original
 * blockPositionY an amount proportional to the volume of a snipe selection area / the number of blocks touching air in the selection. This is because every solid block
 * surrounded blockPositionY others should take equally long to check and not change as it would take MC to change them and then check and find no lighting to update. For
 * air blocks surrounded blockPositionY other air blocks, this brush saves about 80-100 checks blockPositionY not updating them or their lighting. And for air blocks touching solids,
 * this brush is slower, because it replaces the air once per solid block it is touching. I assume on average this is about 2 blocks. So every air block
 * touching a solid negates one air block floating in air. Thus, for selections that have more air blocks surrounded blockPositionY air than air blocks touching solids,
 * this brush will be faster, which is almost always the case, especially for undeveloped terrain and for larger brush sizes (unlike the original brush, this
 * should only slow down blockPositionY the square of the brush size, not the cube of the brush size). For typical terrain, blockPositionY my calculations, overall speed increase is
 * about a factor of 5-6 for a size 20 brush. For a complicated city or ship, etc., this may be only a factor of about 2. In a hypothetical worst case scenario
 * of a 3d checkerboard of stone and air every other block, this brush should only be about 1.5x slower than the original brush. Savings increase for larger
 * brushes.
 *
 * @author GavJenks
 */
public class BlockResetSurfaceBrush extends Brush
{
    private static int timesUsed = 0;
    private static final ArrayList<Material> DENIED_UPDATES = new ArrayList<Material>();

    static
    {
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.SIGN);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.SIGN_POST);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.WALL_SIGN);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.CHEST);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.FURNACE);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.BURNING_FURNACE);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.REDSTONE_TORCH_OFF);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.REDSTONE_TORCH_ON);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.REDSTONE_WIRE);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.DIODE_BLOCK_OFF);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.DIODE_BLOCK_ON);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.WOODEN_DOOR);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.WOOD_DOOR);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.IRON_DOOR);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.IRON_DOOR_BLOCK);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.FENCE_GATE);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.AIR);
    }

    /**
     *
     */
    public BlockResetSurfaceBrush()
    {
        this.setName("Block Reset Brush Surface Only");
    }

    private void applyBrush(final SnipeData v)
    {
        final World _world = this.getWorld();

        for (int _z = -v.getBrushSize(); _z <= v.getBrushSize(); _z++)
        {
            for (int _x = -v.getBrushSize(); _x <= v.getBrushSize(); _x++)
            {
                for (int _y = -v.getBrushSize(); _y <= v.getBrushSize(); _y++)
                {

                    Block _block = _world.getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z);
                    if (BlockResetSurfaceBrush.DENIED_UPDATES.contains(_block.getType()))
                    {
                        continue;
                    }

                    boolean _airFound = false;

                    if (_world.getBlockAt(this.getBlockPositionX() + _x + 1, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z).getTypeId() == 0)
                    {
                        _block = _world.getBlockAt(this.getBlockPositionX() + _x + 1, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z);
                        final byte _oldData = _block.getData();
                        resetBlock(_block, _oldData);
                        _airFound = true;
                    }

                    if (_world.getBlockAt(this.getBlockPositionX() + _x - 1, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z).getTypeId() == 0)
                    {
                        _block = _world.getBlockAt(this.getBlockPositionX() + _x - 1, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z);
                        final byte _oldData = _block.getData();
                        resetBlock(_block, _oldData);
                        _airFound = true;
                    }

                    if (_world.getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y + 1, this.getBlockPositionZ() + _z).getTypeId() == 0)
                    {
                        _block = _world.getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y + 1, this.getBlockPositionZ() + _z);
                        final byte _oldData = _block.getData();
                        resetBlock(_block, _oldData);
                        _airFound = true;
                    }

                    if (_world.getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y - 1, this.getBlockPositionZ() + _z).getTypeId() == 0)
                    {
                        _block = _world.getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y - 1, this.getBlockPositionZ() + _z);
                        final byte _oldData = _block.getData();
                        resetBlock(_block, _oldData);
                        _airFound = true;
                    }

                    if (_world.getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z + 1).getTypeId() == 0)
                    {
                        _block = _world.getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z + 1);
                        final byte _oldData = _block.getData();
                        resetBlock(_block, _oldData);
                        _airFound = true;
                    }

                    if (_world.getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z - 1).getTypeId() == 0)
                    {
                        _block = _world.getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z - 1);
                        final byte _oldData = _block.getData();
                        resetBlock(_block, _oldData);
                        _airFound = true;
                    }

                    if (_airFound)
                    {
                        _block = _world.getBlockAt(this.getBlockPositionX() + _x, this.getBlockPositionY() + _y, this.getBlockPositionZ() + _z);
                        final byte _oldData = _block.getData();
                        resetBlock(_block, _oldData);
                    }
                }
            }
        }
    }

    private void resetBlock(Block _block, final byte _oldData)
    {
        _block.setTypeIdAndData(_block.getTypeId(), (byte) ((_block.getData() + 1) & 0xf), true);
        _block.setTypeIdAndData(_block.getTypeId(), _oldData, true);
    }

    @Override
    protected final void arrow(final SnipeData v)
    {
        applyBrush(v);
    }

    @Override
    protected final void powder(final SnipeData v)
    {
        applyBrush(v);
    }

    @Override
    public final void info(final Message vm)
    {
        vm.brushName(this.getName());
    }

    @Override
    public final int getTimesUsed()
    {
        return BlockResetSurfaceBrush.timesUsed;
    }

    @Override
    public final void setTimesUsed(final int tUsed)
    {
        BlockResetSurfaceBrush.timesUsed = tUsed;
    }
}
