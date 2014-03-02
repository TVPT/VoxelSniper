package com.thevoxelbox.voxelsniper;

import com.google.common.collect.Multimap;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Set;

/**
 *
 */
public class BrushesTest
{
    private Brushes brushes;

    @Before
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
        Assert.assertEquals(brush.getClass(), brushes.getBrushForHandle("mockhandle"));
        Assert.assertEquals(brush.getClass(), brushes.getBrushForHandle("testhandle"));
        Assert.assertNull(brushes.getBrushForHandle("notExistant"));
    }

    @Test
    public void testRegisteredSniperBrushes() throws Exception
    {
        Assert.assertEquals(0, brushes.registeredSniperBrushes());
        IBrush brush = Mockito.mock(IBrush.class);
        brushes.registerSniperBrush(brush.getClass(), "mockhandle", "testhandle");
        Assert.assertEquals(1, brushes.registeredSniperBrushes());
    }

    @Test
    public void testRegisteredSniperBrushHandles() throws Exception
    {
        Assert.assertEquals(0, brushes.registeredSniperBrushHandles());
        IBrush brush = Mockito.mock(IBrush.class);
        brushes.registerSniperBrush(brush.getClass(), "mockhandle", "testhandle");
        Assert.assertEquals(2, brushes.registeredSniperBrushHandles());
    }

    @Test
    public void testGetSniperBrushHandles() throws Exception
    {
        IBrush brush = Mockito.mock(IBrush.class);
        brushes.registerSniperBrush(brush.getClass(), "mockhandle", "testhandle");
        Set<String> sniperBrushHandles = brushes.getSniperBrushHandles(brush.getClass());
        Assert.assertTrue(sniperBrushHandles.contains("mockhandle"));
        Assert.assertTrue(sniperBrushHandles.contains("testhandle"));
        Assert.assertFalse(sniperBrushHandles.contains("notInSet"));
    }

    @Test
    public void testGetRegisteredBrushesMultimap() throws Exception
    {
        IBrush brush = Mockito.mock(IBrush.class);
        brushes.registerSniperBrush(brush.getClass(), "mockhandle", "testhandle");
        Multimap<Class<? extends IBrush>,String> registeredBrushesMultimap = brushes.getRegisteredBrushesMultimap();
        Assert.assertTrue(registeredBrushesMultimap.containsKey(brush.getClass()));
        Assert.assertFalse(registeredBrushesMultimap.containsKey(IBrush.class));
        Assert.assertTrue(registeredBrushesMultimap.containsEntry(brush.getClass(), "mockhandle"));
        Assert.assertTrue(registeredBrushesMultimap.containsEntry(brush.getClass(), "testhandle"));
        Assert.assertFalse(registeredBrushesMultimap.containsEntry(brush.getClass(), "notAnEntry"));
    }
}
