package com.thevoxelbox.voxelsniper;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.mockito.InOrder;
import org.mockito.Mockito;

/**
 *
 */
public class UndoTest
{
    /*
    private Undo undo;

    @Before
    public void setUp() throws Exception
    {
        undo = new Undo();
    }

    @Test
    public void testGetSize() throws Exception
    {
        World world = Mockito.mock(World.class);
        for (int i = 0; i < 5; i++)
        {
            Block block = Mockito.mock(Block.class);
            BlockState blockState = Mockito.mock(BlockState.class);
            Location location = new Location(world, 0, 0, i);
            Mockito.when(block.getLocation())
                   .thenReturn(location);
            Mockito.when(block.getState())
                   .thenReturn(blockState);
            Mockito.when(blockState.getLocation())
                   .thenReturn(location);
            undo.put(block);
        }
        Assert.assertEquals(5, undo.getSize());
        Block block = Mockito.mock(Block.class);
        BlockState blockState = Mockito.mock(BlockState.class);
        Location location = new Location(world, 0, 0, 6);
        Mockito.when(block.getLocation())
               .thenReturn(location);
        Mockito.when(block.getState())
               .thenReturn(blockState);
        Mockito.when(blockState.getLocation())
               .thenReturn(location);
        undo.put(block);
        Assert.assertEquals(6, undo.getSize());
        undo.put(block);
        Assert.assertEquals(6, undo.getSize());

    }

    @Test
    public void testPut() throws Exception
    {
        World world = Mockito.mock(World.class);
        Block block = Mockito.mock(Block.class);
        BlockState blockState = Mockito.mock(BlockState.class);
        Location location = new Location(world, 0, 0, 0);
        Mockito.when(block.getLocation())
               .thenReturn(location);
        Mockito.when(block.getState())
               .thenReturn(blockState);
        Mockito.when(blockState.getLocation())
               .thenReturn(location);

        undo.put(block);
    }

    @Test
    public void testUndo() throws Exception
    {
        World world = Mockito.mock(World.class);

        Block normalBlock = Mockito.mock(Block.class);
        BlockState normalBlockState = Mockito.mock(BlockState.class);
        Location normalBlockLocation = new Location(world, 0, 0, 0);
        Mockito.when(normalBlock.getLocation())
               .thenReturn(normalBlockLocation);
        Mockito.when(normalBlock.getState())
               .thenReturn(normalBlockState);
        Mockito.when(normalBlock.getType())
               .thenReturn(Material.STONE);
        Mockito.when(normalBlockState.getLocation())
               .thenReturn(normalBlockLocation);
        Mockito.when(normalBlockState.getBlock())
               .thenReturn(normalBlock);

        Block fragileBlock = Mockito.mock(Block.class);
        BlockState fragileBlockState = Mockito.mock(BlockState.class);
        Location fragileBlockLocation = new Location(world, 0, 0, 1);
        Mockito.when(fragileBlock.getLocation())
               .thenReturn(fragileBlockLocation);
        Mockito.when(fragileBlock.getState())
               .thenReturn(fragileBlockState);
        Mockito.when(fragileBlock.getType())
               .thenReturn(Material.TORCH);
        Mockito.when(fragileBlockState.getLocation())
               .thenReturn(fragileBlockLocation);
        Mockito.when(fragileBlockState.getBlock())
               .thenReturn(fragileBlock);

        Block waterBlock = Mockito.mock(Block.class);
        BlockState waterBlockState = Mockito.mock(BlockState.class);
        Location waterBlockLocation = new Location(world, 0, 0, 2);
        Mockito.when(waterBlock.getLocation())
               .thenReturn(waterBlockLocation);
        Mockito.when(waterBlock.getState())
               .thenReturn(waterBlockState);
        Mockito.when(waterBlock.getType())
               .thenReturn(Material.WATER);
        Mockito.when(waterBlockState.getLocation())
               .thenReturn(waterBlockLocation);
        Mockito.when(waterBlockState.getBlock())
               .thenReturn(waterBlock);


        undo.put(waterBlock);
        undo.put(fragileBlock);
        undo.put(normalBlock);
        undo.undo();

        InOrder inOrder = Mockito.inOrder(normalBlockState, waterBlockState, fragileBlockState);
        inOrder.verify(normalBlockState).update(Mockito.anyBoolean(), Mockito.anyBoolean());
        inOrder.verify(fragileBlockState).update(Mockito.anyBoolean(), Mockito.anyBoolean());
        inOrder.verify(waterBlockState).update(Mockito.anyBoolean(), Mockito.anyBoolean());
    }
     */
}
