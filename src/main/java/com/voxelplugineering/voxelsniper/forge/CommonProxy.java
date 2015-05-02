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
import com.voxelplugineering.voxelsniper.api.commands.CommandHandler;
import com.voxelplugineering.voxelsniper.api.config.Configuration;
import com.voxelplugineering.voxelsniper.api.event.bus.EventBus;
import com.voxelplugineering.voxelsniper.api.logging.LoggingDistributor;
import com.voxelplugineering.voxelsniper.api.permissions.PermissionProxy;
import com.voxelplugineering.voxelsniper.api.platform.PlatformProxy;
import com.voxelplugineering.voxelsniper.api.registry.BiomeRegistry;
import com.voxelplugineering.voxelsniper.api.registry.MaterialRegistry;
import com.voxelplugineering.voxelsniper.api.registry.RegistryProvider;
import com.voxelplugineering.voxelsniper.api.service.ServiceManager;
import com.voxelplugineering.voxelsniper.api.service.ServiceProvider;
import com.voxelplugineering.voxelsniper.api.service.scheduler.Scheduler;
import com.voxelplugineering.voxelsniper.api.util.text.TextFormatParser;
import com.voxelplugineering.voxelsniper.api.world.material.Material;
import com.voxelplugineering.voxelsniper.core.service.BiomeRegistryService;
import com.voxelplugineering.voxelsniper.core.service.logging.Log4jLogger;
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
public abstract class CommonProxy extends ServiceProvider
{

    /**
     * 
     */
    public CommonProxy()
    {
        super(Type.PLATFORM);
    }

    @Override
    public void registerNewServices(ServiceManager manager)
    {

    }

    /**
     * Init hook
     * 
     * @param logger The service
     */
    @InitHook(LoggingDistributor.class)
    public void getLogger(LoggingDistributor logger)
    {
        logger.registerLogger(new Log4jLogger(VoxelSniperForge.voxelsniper.getLogger()), "forge");
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(TextFormatParser.class)
    public TextFormatParser getFormatProxy()
    {
        return new ForgeTextFormatParser();
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(PlatformProxy.class)
    public PlatformProxy getPlatformProxy()
    {
        return new ForgePlatformProxyService();
    }

    /**
     * Init hook
     * 
     * @param config The service
     */
    @InitHook(Configuration.class)
    public void registerConfiguration(Configuration config)
    {
        config.registerContainer(ForgeConfiguration.class);
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(MaterialRegistry.class)
    public MaterialRegistry<?> getMaterialRegistry()
    {
        return new ProvidedMaterialRegistryService<net.minecraft.block.Block>(new MaterialProvider());
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(PermissionProxy.class)
    public PermissionProxy getPermissionProxy()
    {
        return new ForgePermissionProxyService();
    }

    /**
     * Init hook
     * 
     * @param service The service
     */
    @InitHook(EventBus.class)
    public void registerEventProxies(EventBus service)
    {
        ForgeEventProxy events = new ForgeEventProxy();
        MinecraftForge.EVENT_BUS.register(events);
        FMLCommonHandler.instance().bus().register(events);
    }

    /**
     * Init hook
     * 
     * @param cmd The service
     */
    @InitHook(CommandHandler.class)
    public void registerCommands(CommandHandler cmd)
    {
        cmd.setRegistrar(new ForgeCommandRegistrar());
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(Scheduler.class)
    public Scheduler getSchedulerProxy()
    {
        return new ForgeSchedulerService();
    }

    /**
     * Builder
     * 
     * @return The service
     */
    @Builder(BiomeRegistry.class)
    public BiomeRegistry<?> getBiomeRegistry()
    {
        return new BiomeRegistryService<net.minecraft.world.biome.BiomeGenBase>();
    }

    /**
     * Init hook
     * 
     * @param service The service
     */
    @InitHook(BiomeRegistry.class)
    public void registerBiomes(BiomeRegistry<?> service)
    {
        @SuppressWarnings("unchecked") BiomeRegistry<net.minecraft.world.biome.BiomeGenBase> reg =
                (BiomeRegistry<net.minecraft.world.biome.BiomeGenBase>) service;
        for (Object b : BiomeGenBase.BIOME_ID_MAP.keySet())
        {
            BiomeGenBase biome = (BiomeGenBase) BiomeGenBase.BIOME_ID_MAP.get(b);
            reg.registerBiome(b.toString(), biome, new ForgeBiome(biome));
        }
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
