package com.thevoxelbox.voxelsniper;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import java.util.Set;

@TestInstance(Lifecycle.PER_CLASS)
public class BrushesTest
{
    private Brushes brushes;

    @BeforeEach
    public void setUp() throws Exception
    {
        brushes = new Brushes();
    }

    @Test
    public void testRegisterSniperBrush() throws Exception
    {
        IBrush brush = Mockito.mock(IBrush.class);
        brushes.registerSniperBrush(brush.getClass(), "mockhandle", "testhandle");
    }

    @Test
    public void testGetBrushForHandle() throws Exception
    {
        IBrush brush = Mockito.mock(IBrush.class);
        brushes.registerSniperBrush(brush.getClass(), "mockhandle", "testhandle");
        Assertions.assertEquals(brush.getClass(), brushes.getBrushForHandle("mockhandle"));
        Assertions.assertEquals(brush.getClass(), brushes.getBrushForHandle("testhandle"));
        Assertions.assertNull(brushes.getBrushForHandle("notExistant"));
    }

    @Test
    public void testRegisteredSniperBrushes() throws Exception
    {
        Assertions.assertEquals(0, brushes.registeredSniperBrushes());
        IBrush brush = Mockito.mock(IBrush.class);
        brushes.registerSniperBrush(brush.getClass(), "mockhandle", "testhandle");
        Assertions.assertEquals(1, brushes.registeredSniperBrushes());
    }

    @Test
    public void testRegisteredSniperBrushHandles() throws Exception
    {
        Assertions.assertEquals(0, brushes.registeredSniperBrushHandles());
        IBrush brush = Mockito.mock(IBrush.class);
        brushes.registerSniperBrush(brush.getClass(), "mockhandle", "testhandle");
        Assertions.assertEquals(2, brushes.registeredSniperBrushHandles());
    }

    @Test
    public void testGetSniperBrushHandles() throws Exception
    {
        IBrush brush = Mockito.mock(IBrush.class);
        brushes.registerSniperBrush(brush.getClass(), "mockhandle", "testhandle");
        Set<String> sniperBrushHandles = brushes.getSniperBrushHandles(brush.getClass());
        Assertions.assertTrue(sniperBrushHandles.contains("mockhandle"));
        Assertions.assertTrue(sniperBrushHandles.contains("testhandle"));
        Assertions.assertFalse(sniperBrushHandles.contains("notInSet"));
    }

    @Test
    public void testGetRegisteredBrushesMultimap() throws Exception
    {
        IBrush brush = Mockito.mock(IBrush.class);
        brushes.registerSniperBrush(brush.getClass(), "mockhandle", "testhandle");
        Multimap<Class<? extends IBrush>,String> registeredBrushesMultimap = brushes.getRegisteredBrushesMultimap();
        Assertions.assertTrue(registeredBrushesMultimap.containsKey(brush.getClass()));
        Assertions.assertFalse(registeredBrushesMultimap.containsKey(IBrush.class));
        Assertions.assertTrue(registeredBrushesMultimap.containsEntry(brush.getClass(), "mockhandle"));
        Assertions.assertTrue(registeredBrushesMultimap.containsEntry(brush.getClass(), "testhandle"));
        Assertions.assertFalse(registeredBrushesMultimap.containsEntry(brush.getClass(), "notAnEntry"));
    }
}
