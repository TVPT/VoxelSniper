package com.thevoxelbox.voxelsniper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;


@TestInstance(Lifecycle.PER_CLASS)
public class SniperManagerTest
{
    private SniperManager sniperManager;

    @BeforeEach
    public void setUp() throws Exception
    {
        sniperManager = new SniperManager(Mockito.mock(VoxelSniper.class));
    }

}
