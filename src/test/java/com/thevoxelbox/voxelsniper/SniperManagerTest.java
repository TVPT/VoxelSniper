package com.thevoxelbox.voxelsniper;

import org.bukkit.entity.Player;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 */
public class SniperManagerTest
{
    private SniperManager sniperManager;

    @Before
    public void setUp() throws Exception
    {
        sniperManager = new SniperManager(Mockito.mock(VoxelSniper.class));
    }

    @Test
    public void testGetSniperForPlayer() throws Exception
    {
        Player player = Mockito.mock(Player.class);
        Sniper sniper = sniperManager.getSniperForPlayer(player);
        Assert.assertSame(player, sniper.getPlayer());
        Assert.assertSame(sniper, sniperManager.getSniperForPlayer(player));
    }
}
