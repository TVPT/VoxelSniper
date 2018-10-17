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
    private static final ArrayList<Material> DENIED_UPDATES = new ArrayList<Material>();

    static
    {
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.SIGN);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.WALL_SIGN);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.CHEST);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.FURNACE);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.REDSTONE_TORCH);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.REDSTONE_WIRE);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.REPEATER);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.SPRUCE_DOOR);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.OAK_DOOR);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.JUNGLE_DOOR);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.BIRCH_DOOR);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.ACACIA_DOOR);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.DARK_OAK_DOOR);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.IRON_DOOR);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.ACACIA_FENCE_GATE);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.BIRCH_FENCE_GATE);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.JUNGLE_FENCE_GATE);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.OAK_FENCE_GATE);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.SPRUCE_FENCE_GATE);
        BlockResetSurfaceBrush.DENIED_UPDATES.add(Material.DARK_OAK_FENCE_GATE);
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
        final World world = this.getWorld();

        for (int z = -v.getBrushSize(); z <= v.getBrushSize(); z++)
        {
            for (int x = -v.getBrushSize(); x <= v.getBrushSize(); x++)
            {
                for (int y = -v.getBrushSize(); y <= v.getBrushSize(); y++)
                {

                    Block block = world.getBlockAt(this.getTargetBlock().getX() + x, this.getTargetBlock().getY() + y, this.getTargetBlock().getZ() + z);
                    if (BlockResetSurfaceBrush.DENIED_UPDATES.contains(block.getType()))
                    {
                        continue;
                    }

                    boolean airFound = false;

                    if (world.getBlockAt(this.getTargetBlock().getX() + x + 1, this.getTargetBlock().getY() + y, this.getTargetBlock().getZ() + z).getType() == Material.AIR)
                    {
                        block = world.getBlockAt(this.getTargetBlock().getX() + x + 1, this.getTargetBlock().getY() + y, this.getTargetBlock().getZ() + z);
                        resetBlock(block);
                        airFound = true;
                    }

                    if (world.getBlockAt(this.getTargetBlock().getX() + x - 1, this.getTargetBlock().getY() + y, this.getTargetBlock().getZ() + z).getType() == Material.AIR)
                    {
                        block = world.getBlockAt(this.getTargetBlock().getX() + x - 1, this.getTargetBlock().getY() + y, this.getTargetBlock().getZ() + z);
                        resetBlock(block);
                        airFound = true;
                    }

                    if (world.getBlockAt(this.getTargetBlock().getX() + x, this.getTargetBlock().getY() + y + 1, this.getTargetBlock().getZ() + z).getType() == Material.AIR)
                    {
                        block = world.getBlockAt(this.getTargetBlock().getX() + x, this.getTargetBlock().getY() + y + 1, this.getTargetBlock().getZ() + z);
                        resetBlock(block);
                        airFound = true;
                    }

                    if (world.getBlockAt(this.getTargetBlock().getX() + x, this.getTargetBlock().getY() + y - 1, this.getTargetBlock().getZ() + z).getType() == Material.AIR)
                    {
                        block = world.getBlockAt(this.getTargetBlock().getX() + x, this.getTargetBlock().getY() + y - 1, this.getTargetBlock().getZ() + z);
                        resetBlock(block);
                        airFound = true;
                    }

                    if (world.getBlockAt(this.getTargetBlock().getX() + x, this.getTargetBlock().getY() + y, this.getTargetBlock().getZ() + z + 1).getType() == Material.AIR)
                    {
                        block = world.getBlockAt(this.getTargetBlock().getX() + x, this.getTargetBlock().getY() + y, this.getTargetBlock().getZ() + z + 1);
                        resetBlock(block);
                        airFound = true;
                    }

                    if (world.getBlockAt(this.getTargetBlock().getX() + x, this.getTargetBlock().getY() + y, this.getTargetBlock().getZ() + z - 1).getType() == Material.AIR)
                    {
                        block = world.getBlockAt(this.getTargetBlock().getX() + x, this.getTargetBlock().getY() + y, this.getTargetBlock().getZ() + z - 1);
                        resetBlock(block);
                        airFound = true;
                    }

                    if (airFound)
                    {
                        block = world.getBlockAt(this.getTargetBlock().getX() + x, this.getTargetBlock().getY() + y, this.getTargetBlock().getZ() + z);
                        resetBlock(block);
                    }
                }
            }
        }
    }

	private void resetBlock(Block block)
    {
    	final byte oldData = block.getData();
        block.setTypeIdAndData(block.getTypeId(), (byte) ((block.getData() + 1) & 0xf), true);
        block.setTypeIdAndData(block.getTypeId(), oldData, true);
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
    public String getPermissionNode()
    {
        return "voxelsniper.brush.blockresetsurface";
    }
}
