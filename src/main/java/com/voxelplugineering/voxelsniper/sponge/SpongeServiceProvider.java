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
package com.voxelplugineering.voxelsniper.sponge;

import com.google.common.base.Function;
import com.voxelplugineering.voxelsniper.Gunsmith;
import com.voxelplugineering.voxelsniper.entity.Player;
import com.voxelplugineering.voxelsniper.service.Builder;
import com.voxelplugineering.voxelsniper.service.InitHook;
import com.voxelplugineering.voxelsniper.service.PostInit;
import com.voxelplugineering.voxelsniper.service.ServicePriorities;
import com.voxelplugineering.voxelsniper.service.alias.AnnotationScanner;
import com.voxelplugineering.voxelsniper.service.command.CommandHandler;
import com.voxelplugineering.voxelsniper.service.command.CommandHandlerService;
import com.voxelplugineering.voxelsniper.service.config.Configuration;
import com.voxelplugineering.voxelsniper.service.eventbus.EventBus;
import com.voxelplugineering.voxelsniper.service.permission.PermissionProxy;
import com.voxelplugineering.voxelsniper.service.platform.PlatformProxy;
import com.voxelplugineering.voxelsniper.service.registry.BiomeRegistry;
import com.voxelplugineering.voxelsniper.service.registry.BiomeRegistryService;
import com.voxelplugineering.voxelsniper.service.registry.MaterialRegistry;
import com.voxelplugineering.voxelsniper.service.registry.MaterialRegistryService;
import com.voxelplugineering.voxelsniper.service.registry.PlayerRegistry;
import com.voxelplugineering.voxelsniper.service.registry.PlayerRegistryService;
import com.voxelplugineering.voxelsniper.service.registry.RegistryProvider;
import com.voxelplugineering.voxelsniper.service.registry.WorldRegistry;
import com.voxelplugineering.voxelsniper.service.registry.WorldRegistryService;
import com.voxelplugineering.voxelsniper.service.scheduler.Scheduler;
import com.voxelplugineering.voxelsniper.service.text.TextFormatParser;
import com.voxelplugineering.voxelsniper.sponge.entity.SpongePlayer;
import com.voxelplugineering.voxelsniper.sponge.event.handler.SpongeEventHandler;
import com.voxelplugineering.voxelsniper.sponge.service.SpongePermissionProxyService;
import com.voxelplugineering.voxelsniper.sponge.service.SpongePlatformProxyService;
import com.voxelplugineering.voxelsniper.sponge.service.SpongeSchedulerService;
import com.voxelplugineering.voxelsniper.sponge.service.SpongeTextFormatService;
import com.voxelplugineering.voxelsniper.sponge.service.command.SpongeCommandRegistrar;
import com.voxelplugineering.voxelsniper.sponge.service.command.SpongeConsoleProxy;
import com.voxelplugineering.voxelsniper.sponge.world.SpongeWorld;
import com.voxelplugineering.voxelsniper.sponge.world.biome.SpongeBiome;
import com.voxelplugineering.voxelsniper.sponge.world.material.SpongeMaterial;
import com.voxelplugineering.voxelsniper.sponge.world.material.SpongeMaterialState;
import com.voxelplugineering.voxelsniper.util.Context;
import com.voxelplugineering.voxelsniper.util.Pair;
import com.voxelplugineering.voxelsniper.world.World;
import com.voxelplugineering.voxelsniper.world.material.MaterialStateCache;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.biome.BiomeType;

import java.io.File;
import java.util.Optional;

/**
 * A provider for bukkit's initialization values.
 */
@SuppressWarnings({"checkstyle:javadocmethod", "javadoc"})
public class SpongeServiceProvider
{

    private final PluginContainer plugin;
    private final File root;

    /**
     * Creates a new {@link SpongeServiceProvider}.
     * 
     * @param game The game instance
     * @param plugin The plugin container
     * @param logger The logger
     */
    public SpongeServiceProvider(PluginContainer plugin, File root)
    {
        this.plugin = plugin;
        this.root = root;
    }

    @InitHook(target = AnnotationScanner.class)
    public void registerScannerExclusions(Context context, AnnotationScanner scanner)
    {
        scanner.addScannerExclusion("com/voxelplugineering/voxelsniper/forge/");
        scanner.addScannerExclusion("com/voxelplugineering/voxelsniper/bukkit/");
    }

    @PostInit
    public void postInit(Context c)
    {
    }

    @Builder(target = TextFormatParser.class, priority = ServicePriorities.TEXT_FORMAT_PRIORITY)
    public TextFormatParser getFormatProxy(Context context)
    {
        return new SpongeTextFormatService(context);
    }

    @Builder(target = PlatformProxy.class, priority = ServicePriorities.PLATFORM_PROXY_PRIORITY)
    public PlatformProxy getPlatformProxy(Context context)
    {
        return new SpongePlatformProxyService(context, this.root);
    }

    @InitHook(target = Configuration.class)
    public void registerConfiguration(Context context, Configuration config)
    {
        // config overrides go here
    }

    @Builder(target = MaterialRegistry.class, priority = ServicePriorities.MATERIAL_REGISTRY_PRIORITY)
    public MaterialRegistry<?> getMaterialRegistry(Context context)
    {
        return new MaterialRegistryService<org.spongepowered.api.block.BlockType>(context);
    }

    @InitHook(target = MaterialRegistry.class)
    public void registerMaterials(Context context, MaterialRegistry<?> service)
    {
        @SuppressWarnings("unchecked")
        MaterialRegistry<org.spongepowered.api.block.BlockType> registry = (MaterialRegistry<org.spongepowered.api.block.BlockType>) service;
        MaterialStateBuilder builder = new MaterialStateBuilder(registry);
        MaterialStateCache<BlockState, SpongeMaterialState> cache = new MaterialStateCache<BlockState, SpongeMaterialState>(builder);
        for (org.spongepowered.api.block.BlockType m : Sponge.getRegistry().getAllOf(BlockType.class))
        {
            registry.registerMaterial(m.getId().replace("minecraft:", ""), m, new SpongeMaterial(m, cache));
        }
    }

    @Builder(target = WorldRegistry.class, priority = ServicePriorities.WORLD_REGISTRY_PRIORITY)
    public WorldRegistry<?> getWorldRegistry(Context context)
    {
        WorldRegistryProvider provider = new WorldRegistryProvider(context);
        return new WorldRegistryService<org.spongepowered.api.world.World>(context, provider);
    }

    @Builder(target = PermissionProxy.class, priority = ServicePriorities.PERMISSION_PROXY_PRIORITY)
    public PermissionProxy getPermissionProxy(Context context)
    {
        return new SpongePermissionProxyService(context);
    }

    @Builder(target = PlayerRegistry.class, priority = ServicePriorities.PLAYER_REGISTRY_PRIORITY)
    public PlayerRegistry<?> getPlayerRegistry(Context context)
    {
        return new PlayerRegistryService<org.spongepowered.api.entity.living.player.Player>(context, new PlayerRegistryProvider(context),
                new SpongeConsoleProxy());
    }

    @InitHook(target = EventBus.class)
    public void registerEventProxies(Context context, EventBus service)
    {
        Sponge.getEventManager().registerListeners(this.plugin, new SpongeEventHandler(context));
    }

    @InitHook(target = CommandHandler.class)
    public void registerCommands(Context context, CommandHandler service)
    {
        CommandHandlerService cmd = (CommandHandlerService) service;
        cmd.setRegistrar(new SpongeCommandRegistrar(context));
    }

    @Builder(target = Scheduler.class, priority = ServicePriorities.SCHEDULER_PRIORITY)
    public Scheduler getSchedulerProxy(Context context)
    {
        return new SpongeSchedulerService(context, this.plugin);
    }

    @Builder(target = BiomeRegistry.class, priority = ServicePriorities.BIOME_REGISTRY_PRIORITY)
    public BiomeRegistry<?> getBiomeRegistry(Context context)
    {
        return new BiomeRegistryService<org.spongepowered.api.world.biome.BiomeType>(context);
    }

    @InitHook(target = BiomeRegistry.class)
    public void registerBiomes(Context context, BiomeRegistry<?> service)
    {
        @SuppressWarnings("unchecked")
        BiomeRegistry<org.spongepowered.api.world.biome.BiomeType> reg = (BiomeRegistry<org.spongepowered.api.world.biome.BiomeType>) service;
        for (org.spongepowered.api.world.biome.BiomeType b : Sponge.getRegistry().getAllOf(BiomeType.class))
        {
            reg.registerBiome(b.getName(), b, new SpongeBiome(b));
        }
    }

    public static class MaterialStateBuilder implements Function<BlockState, SpongeMaterialState>
    {

        private final MaterialRegistry<org.spongepowered.api.block.BlockType> reg;

        public MaterialStateBuilder(MaterialRegistry<org.spongepowered.api.block.BlockType> reg)
        {
            this.reg = reg;
        }

        @Override
        public SpongeMaterialState apply(BlockState input)
        {
            return new SpongeMaterialState(this.reg.getMaterial(input.getType().getId().replace("minecraft:", "")).get(), input);
        }

    }

    /**
     * A world registry provider for sponge.
     */
    public static class WorldRegistryProvider implements RegistryProvider<org.spongepowered.api.world.World, World>
    {

        private final Context context;

        /**
         * Creates a new {@link com.voxelplugineering.voxelsniper.service.registry.RegistryProvider}
         *
         * @param game The game instance
         */
        public WorldRegistryProvider(Context context)
        {
            this.context = context;
        }

        @Override
        public Optional<Pair<org.spongepowered.api.world.World, World>> get(String name)
        {
            Optional<org.spongepowered.api.world.World> world = Sponge.getServer().getWorld(name);
            if (world.isPresent())
            {
                SpongeWorld spongeWorld = new SpongeWorld(this.context, world.get(), Gunsmith.getMainThread());
                return Optional.of(new Pair<org.spongepowered.api.world.World, World>(world.get(), spongeWorld));
            }
            return Optional.empty();
        }
    }

    /**
     * A player registry provider for forge.
     */
    public static class PlayerRegistryProvider implements RegistryProvider<org.spongepowered.api.entity.living.player.Player, Player>
    {

        private final Context context;

        /**
         * Creates a new {@link PlayerRegistryProvider}
         *
         * @param g The game instance
         */
        public PlayerRegistryProvider(Context context)
        {
            this.context = context;
        }

        @Override
        public Optional<Pair<org.spongepowered.api.entity.living.player.Player, Player>> get(String name)
        {
            Optional<org.spongepowered.api.entity.living.player.Player> player = Sponge.getServer().getPlayer(name);
            if (player.isPresent())
            {
                SpongePlayer splayer = new SpongePlayer(this.context, player.get());
                splayer.init(this.context);
                return Optional.of(new Pair<org.spongepowered.api.entity.living.player.Player, Player>(player.get(), splayer));
            }
            return Optional.empty();
        }
    }
}
