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

import static net.minecraftforge.fml.common.registry.EntityRegistry.*;

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.voxelplugineering.voxelsniper.config.BaseConfiguration;
import com.voxelplugineering.voxelsniper.config.VoxelSniperConfiguration;
import com.voxelplugineering.voxelsniper.forge.entity.ForgeEntityType;
import com.voxelplugineering.voxelsniper.forge.event.handler.ForgeEventProxy;
import com.voxelplugineering.voxelsniper.forge.service.ForgePermissionProxyService;
import com.voxelplugineering.voxelsniper.forge.service.ForgePlatformProxyService;
import com.voxelplugineering.voxelsniper.forge.service.ForgeSchedulerService;
import com.voxelplugineering.voxelsniper.forge.service.ForgeTextFormatParser;
import com.voxelplugineering.voxelsniper.forge.service.command.ForgeCommandRegistrar;
import com.voxelplugineering.voxelsniper.forge.util.ForgeMaterialAliases;
import com.voxelplugineering.voxelsniper.forge.world.biome.ForgeBiome;
import com.voxelplugineering.voxelsniper.forge.world.material.ForgeMaterial;
import com.voxelplugineering.voxelsniper.forge.world.material.ForgeMaterialState;
import com.voxelplugineering.voxelsniper.service.AnnotationScanner;
import com.voxelplugineering.voxelsniper.service.BiomeRegistryService;
import com.voxelplugineering.voxelsniper.service.Builder;
import com.voxelplugineering.voxelsniper.service.EntityRegistryService;
import com.voxelplugineering.voxelsniper.service.InitHook;
import com.voxelplugineering.voxelsniper.service.MaterialRegistryService;
import com.voxelplugineering.voxelsniper.service.PostInit;
import com.voxelplugineering.voxelsniper.service.ServicePriorities;
import com.voxelplugineering.voxelsniper.service.alias.GlobalAliasHandler;
import com.voxelplugineering.voxelsniper.service.command.CommandHandler;
import com.voxelplugineering.voxelsniper.service.config.Configuration;
import com.voxelplugineering.voxelsniper.service.eventbus.EventBus;
import com.voxelplugineering.voxelsniper.service.permission.PermissionProxy;
import com.voxelplugineering.voxelsniper.service.platform.PlatformProxy;
import com.voxelplugineering.voxelsniper.service.registry.BiomeRegistry;
import com.voxelplugineering.voxelsniper.service.registry.EntityRegistry;
import com.voxelplugineering.voxelsniper.service.registry.MaterialRegistry;
import com.voxelplugineering.voxelsniper.service.scheduler.Scheduler;
import com.voxelplugineering.voxelsniper.service.text.TextFormatParser;
import com.voxelplugineering.voxelsniper.util.Context;
import com.voxelplugineering.voxelsniper.world.material.Material;
import com.voxelplugineering.voxelsniper.world.material.MaterialStateCache;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

/**
 * A common proxy for operations common to both server and client side.
 */
@SuppressWarnings({ "checkstyle:javadocmethod", "javadoc" })
public abstract class CommonProxy
{

    @InitHook(target = AnnotationScanner.class)
    public void registerScannerExclusions(Context context, AnnotationScanner scanner)
    {
        scanner.addScannerExclusion("com/voxelplugineering/voxelsniper/bukkit/");
        scanner.addScannerExclusion("com/voxelplugineering/voxelsniper/sponge/");
    }

    @Builder(target = TextFormatParser.class,
            priority = 0)
    public TextFormatParser getFormatProxy(Context context)
    {
        return new ForgeTextFormatParser(context);
    }

    @Builder(target = PlatformProxy.class,
            priority = ServicePriorities.PLATFORM_PROXY_PRIORITY)
    public PlatformProxy getPlatformProxy(Context context)
    {
        return new ForgePlatformProxyService(context, VoxelSniperForge.voxelsniper.getConfigDir());
    }

    @InitHook(target = Configuration.class)
    public void registerConfiguration(Context context, Configuration config)
    {
        BaseConfiguration.defaultBiomeName = BiomeGenBase.plains.biomeName;
    }

    @Builder(target = MaterialRegistry.class,
            priority = ServicePriorities.MATERIAL_REGISTRY_PRIORITY)
    public MaterialRegistry<?> getMaterialRegistry(Context context)
    {
        return new MaterialRegistryService<net.minecraft.block.Block>(context);
    }

    @InitHook(target = MaterialRegistry.class)
    public void registerMaterials(Context context, MaterialRegistry<net.minecraft.block.Block> reg)
    {
        MaterialStateBuilder builder = new MaterialStateBuilder(reg);
        MaterialStateCache<IBlockState, ForgeMaterialState> cache = new MaterialStateCache<IBlockState, ForgeMaterialState>(builder);
        for (ResourceLocation rs : Block.blockRegistry.getKeys())
        {
            Block block = Block.blockRegistry.getObject(rs);
            Material material = new ForgeMaterial(block, cache);
            reg.registerMaterial((!rs.getResourceDomain().equals("minecraft") ? rs.getResourceDomain() + ":" : "") + rs.getResourcePath(), block,
                    material);
        }
    }

    @Builder(target = PermissionProxy.class,
            priority = ServicePriorities.PERMISSION_PROXY_PRIORITY)
    public PermissionProxy getPermissionProxy(Context context)
    {
        return new ForgePermissionProxyService(context);
    }

    @InitHook(target = EventBus.class)
    public void registerEventProxies(Context context, EventBus service)
    {
        ForgeEventProxy events = new ForgeEventProxy(context);
        MinecraftForge.EVENT_BUS.register(events);
    }

    @InitHook(target = CommandHandler.class)
    public void registerCommands(Context context, CommandHandler cmd)
    {
        cmd.setRegistrar(new ForgeCommandRegistrar(context));
    }

    @Builder(target = Scheduler.class,
            priority = ServicePriorities.SCHEDULER_PRIORITY)
    public Scheduler getSchedulerProxy(Context context)
    {
        return new ForgeSchedulerService(context);
    }

    @Builder(target = BiomeRegistry.class,
            priority = ServicePriorities.BIOME_REGISTRY_PRIORITY)
    public BiomeRegistry<?> getBiomeRegistry(Context context)
    {
        return new BiomeRegistryService<net.minecraft.world.biome.BiomeGenBase>(context);
    }

    @InitHook(target = BiomeRegistry.class)
    public void registerBiomes(Context context, BiomeRegistry<?> service)
    {
        @SuppressWarnings("unchecked")
        BiomeRegistry<net.minecraft.world.biome.BiomeGenBase> reg = (BiomeRegistry<net.minecraft.world.biome.BiomeGenBase>) service;
        for (String b : BiomeGenBase.BIOME_ID_MAP.keySet())
        {
            BiomeGenBase biome = BiomeGenBase.BIOME_ID_MAP.get(b);
            reg.registerBiome(b.toString(), biome, new ForgeBiome(biome));
        }
    }


    @Builder(target = EntityRegistry.class, priority = ServicePriorities.ENTITY_REGISTRY_PRIORITY)
    public EntityRegistry<?> getEntityRegistry(Context context)
    {
        return new EntityRegistryService<net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration>(context);
    }

    @SuppressWarnings("unchecked")
    @InitHook(target = EntityRegistry.class)
    public void registerEntities(Context context, EntityRegistry<?> service) {
        EntityRegistry<EntityRegistration> registry = (EntityRegistry<EntityRegistration>) service;
        net.minecraftforge.fml.common.registry.EntityRegistry forgeRegistry = instance();
        try {
            Field biMapField = forgeRegistry.getClass().getField("entityClassRegistrations");
            biMapField.setAccessible(true);
            BiMap<Class<? extends Entity>, EntityRegistration> map =
                    ((BiMap<Class<? extends Entity>, EntityRegistration>) biMapField.get(forgeRegistry));
            for (Map.Entry<Class<? extends Entity>, EntityRegistration> entry : map.entrySet()) {
                registry.registerEntityType(entry.getValue(), new ForgeEntityType(entry.getValue()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostInit
    public void postInit(Context c)
    {
        Optional<GlobalAliasHandler> aliases = c.get(GlobalAliasHandler.class);
        if (aliases.isPresent() && VoxelSniperConfiguration.generateDefaultAliases)
        {
            ForgeMaterialAliases.loadDefaultAliases(aliases.get());
        }
    }

    /**
     * Gets the count of currently online players.
     * 
     * @return The player count
     */
    public abstract int getOnlinePlayerCount();

}

class MaterialStateBuilder implements Function<IBlockState, ForgeMaterialState>
{

    private final MaterialRegistry<net.minecraft.block.Block> reg;

    public MaterialStateBuilder(MaterialRegistry<net.minecraft.block.Block> reg)
    {
        this.reg = reg;
    }

    @Override
    public ForgeMaterialState apply(IBlockState input)
    {
        ResourceLocation rs = Block.blockRegistry.getNameForObject(input.getBlock());
        return new ForgeMaterialState(
                this.reg.getMaterial((!rs.getResourceDomain().equals("minecraft") ? rs.getResourceDomain() + ":" : "") + rs.getResourcePath()).get(),
                input);
    }

}
