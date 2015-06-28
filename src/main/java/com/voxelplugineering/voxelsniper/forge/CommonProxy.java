/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 The Voxel Plugineering Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.voxelplugineering.voxelsniper.forge;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

import com.google.common.base.Optional;
import com.voxelplugineering.voxelsniper.api.service.Builder;
import com.voxelplugineering.voxelsniper.api.service.InitHook;
import com.voxelplugineering.voxelsniper.api.service.PostInit;
import com.voxelplugineering.voxelsniper.api.service.command.CommandHandler;
import com.voxelplugineering.voxelsniper.api.service.config.Configuration;
import com.voxelplugineering.voxelsniper.api.service.event.EventBus;
import com.voxelplugineering.voxelsniper.api.service.permission.PermissionProxy;
import com.voxelplugineering.voxelsniper.api.service.platform.PlatformProxy;
import com.voxelplugineering.voxelsniper.api.service.registry.BiomeRegistry;
import com.voxelplugineering.voxelsniper.api.service.registry.MaterialRegistry;
import com.voxelplugineering.voxelsniper.api.service.registry.RegistryProvider;
import com.voxelplugineering.voxelsniper.api.service.scheduler.Scheduler;
import com.voxelplugineering.voxelsniper.api.service.text.TextFormatParser;
import com.voxelplugineering.voxelsniper.api.world.material.Material;
import com.voxelplugineering.voxelsniper.core.GunsmithLogger;
import com.voxelplugineering.voxelsniper.core.service.BiomeRegistryService;
import com.voxelplugineering.voxelsniper.core.service.logging.Log4jLogger;
import com.voxelplugineering.voxelsniper.core.util.Context;
import com.voxelplugineering.voxelsniper.core.util.Pair;
import com.voxelplugineering.voxelsniper.forge.config.ForgeConfiguration;
import com.voxelplugineering.voxelsniper.forge.event.handler.ForgeEventProxy;
import com.voxelplugineering.voxelsniper.forge.service.ForgePermissionProxyService;
import com.voxelplugineering.voxelsniper.forge.service.ForgePlatformProxyService;
import com.voxelplugineering.voxelsniper.forge.service.ForgeSchedulerService;
import com.voxelplugineering.voxelsniper.forge.service.ForgeTextFormatParser;
import com.voxelplugineering.voxelsniper.forge.service.ProvidedMaterialRegistryService;
import com.voxelplugineering.voxelsniper.forge.service.command.ForgeCommandRegistrar;
import com.voxelplugineering.voxelsniper.forge.world.biome.ForgeBiome;
import com.voxelplugineering.voxelsniper.forge.world.material.ForgeMaterial;

/**
 * A common proxy for operations common to both server and client side.
 */
public abstract class CommonProxy
{

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(target = TextFormatParser.class, priority = 0)
    public TextFormatParser getFormatProxy(Context context)
    {
        return new ForgeTextFormatParser(context);
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(target = PlatformProxy.class, priority = 4000)
    public PlatformProxy getPlatformProxy(Context context)
    {
        return new ForgePlatformProxyService(context, VoxelSniperForge.voxelsniper.getConfigDir());
    }

    /**
     * Init hook
     * 
     * @param config The service
     */
    @InitHook(target = Configuration.class)
    public void registerConfiguration(Context context, Configuration config)
    {
        config.registerContainer(ForgeConfiguration.class);
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(target = MaterialRegistry.class, priority = 5000)
    public MaterialRegistry<?> getMaterialRegistry(Context context)
    {
        return new ProvidedMaterialRegistryService<net.minecraft.block.Block>(context, new MaterialProvider());
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(target = PermissionProxy.class, priority = 7000)
    public PermissionProxy getPermissionProxy(Context context)
    {
        return new ForgePermissionProxyService(context);
    }

    /**
     * Init hook
     * 
     * @param service The service
     */
    @InitHook(target = EventBus.class)
    public void registerEventProxies(Context context, EventBus service)
    {
        ForgeEventProxy events = new ForgeEventProxy(context);
        MinecraftForge.EVENT_BUS.register(events);
        FMLCommonHandler.instance().bus().register(events);
    }

    /**
     * Init hook
     * 
     * @param cmd The service
     */
    @InitHook(target = CommandHandler.class)
    public void registerCommands(Context context, CommandHandler cmd)
    {
        cmd.setRegistrar(new ForgeCommandRegistrar(context));
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(target = Scheduler.class, priority = 11000)
    public Scheduler getSchedulerProxy(Context context)
    {
        return new ForgeSchedulerService(context);
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(target = BiomeRegistry.class, priority = 12000)
    public BiomeRegistry<?> getBiomeRegistry(Context context)
    {
        return new BiomeRegistryService<net.minecraft.world.biome.BiomeGenBase>(context);
    }

    /**
     * Init hook
     * 
     * @param service The service
     */
    @InitHook(target = BiomeRegistry.class)
    public void registerBiomes(Context context, BiomeRegistry<?> service)
    {
        @SuppressWarnings("unchecked")
        BiomeRegistry<net.minecraft.world.biome.BiomeGenBase> reg =
                (BiomeRegistry<net.minecraft.world.biome.BiomeGenBase>) service;
        for (Object b : BiomeGenBase.BIOME_ID_MAP.keySet())
        {
            BiomeGenBase biome = (BiomeGenBase) BiomeGenBase.BIOME_ID_MAP.get(b);
            reg.registerBiome(b.toString(), biome, new ForgeBiome(biome));
        }
    }

    @PostInit
    public void postInit(Context c) {
        GunsmithLogger.getLogger().registerLogger(new Log4jLogger(VoxelSniperForge.voxelsniper.getLogger()), "forge");
    }

    /**
     * Gets the count of currently online players.
     * 
     * @return The player count
     */
    public abstract int getOnlinePlayerCount();

}

class MaterialProvider implements RegistryProvider<net.minecraft.block.Block, Material>
{

    @Override
    public Optional<Pair<Block, Material>> get(String name)
    {
        Block block = (Block) Block.blockRegistry.getObject(new ResourceLocation(name));
        if (block == null)
        {
            return Optional.absent();
        }
        Material material = new ForgeMaterial(block);
        return Optional.<Pair<Block, Material>>of(new Pair<Block, Material>(block, material));
    }

}
