package com.thevoxelbox.voxelsniper.util;

import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Inker {

    private static Cache<CacheTuple, BlockData> cache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).maximumSize(100).build();

    public static boolean ink(final Block existing, final String ink, boolean applyPhysics) {
        try {
            BlockData inkData = cache.get(new CacheTuple(existing.getType(), ink), new Callable<BlockData>() {

                @Override
                public BlockData call() {
                    return existing.getType().createBlockData(ink);
                }
            });

            BlockData existingData = existing.getBlockData();
            existing.setBlockData(existingData.merge(inkData), applyPhysics);
            return true;
        }
        catch (ExecutionException e) {
            return false;
        }
    }

    public static boolean ink(Block existing, String ink) {
        return ink(existing, ink, true);
    }

    public static BlockData inkMat(final Material mat, final String ink) {
        try {
            return cache.get(new CacheTuple(mat, ink), new Callable<BlockData>() {

                @Override
                public BlockData call() {
                    try
                    {
                        return mat.createBlockData(ink);
                    }
                    catch (IllegalArgumentException e)
                    {
                        return mat.createBlockData();
                    }
                }
            });
        }
        catch (ExecutionException e) {
            return mat.createBlockData();
        }
    }

    public static boolean matches(final Block block, final String ink) {
        try {
            BlockData match = cache.get(new CacheTuple(block.getType(), ink), new Callable<BlockData>() {

                @Override
                public BlockData call() {
                    return block.getType().createBlockData(ink);
                }
            });

            return block.getBlockData().matches(match);
        }
        catch (ExecutionException e) {
            return false;
        }
    }

    private static class CacheTuple {
        Material material;
        String ink;

        private CacheTuple(Material material, String ink) {
            this.material = material;
            this.ink = ink;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheTuple that = (CacheTuple)o;
            return material == that.material && Objects.equal(ink, that.ink);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(material, ink);
        }
    }
}
