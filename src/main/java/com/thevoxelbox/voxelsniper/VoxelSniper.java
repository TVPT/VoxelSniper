/*
 * This file is part of VoxelSniper, licensed under the MIT License (MIT).
 *
 * Copyright (c) The VoxelBox <http://thevoxelbox.com>
 * Copyright (c) contributors
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
package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.genesis.common.config.ConfigurationManager;
import com.thevoxelbox.genesis.logging.Log;
import com.thevoxelbox.genesis.logging.LogLevel;
import com.thevoxelbox.genesis.logging.target.Slf4jTarget;
import com.thevoxelbox.voxelsniper.brush.BrushInfo;
import com.thevoxelbox.voxelsniper.brush.BrushManager;
import com.thevoxelbox.voxelsniper.change.ChangeQueue;
import com.thevoxelbox.voxelsniper.command.BrushCommand;
import com.thevoxelbox.voxelsniper.player.PlayerData;
import com.thevoxelbox.voxelsniper.util.AnnotationHelper;

import com.google.inject.Inject;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Optional;
import java.util.function.Predicate;

@Plugin(id = "voxelsniper", name = "VoxelSniper", version = "8.0.0")
public class VoxelSniper {

    public static NamedCause   plugin_cause;
    private static VoxelSniper instance;

    public static ChangeQueue getChangeQueue() {
        return instance.queue;
    }

    @Inject
    private Logger          logger;
    @Inject
    @ConfigDir(sharedRoot = false)
    private File            configDir;
    @Inject
    private PluginContainer container;

    private ItemType        primary   = Sponge.getRegistry().getType(ItemType.class, VoxelSniperConfig.primary_material).get();
    private ItemType        secondary = Sponge.getRegistry().getType(ItemType.class, VoxelSniperConfig.secondary_material).get();
    private ChangeQueue     queue;

    @Listener
    public void onInit(GameInitializationEvent event) {
        instance = this;
        plugin_cause = NamedCause.of("plugin", this.container);
        Log.getDefaultTargets().clear();
        Log.getDefaultTargets().add(new Slf4jTarget(this.logger));
        Log.getDefaultFormats().clear();
        Log.setDefaultLevel(LogLevel.DEBUG);
        Log.reloadFromDefaults();
        Log.get("Config").setLevel(LogLevel.INFO);

        ConfigurationManager config = new ConfigurationManager(this.configDir);
        config.initStaticConfig(VoxelSniperConfig.class);

        AnnotationHelper.registerConsumer(Type.getType(BrushInfo.class), new BrushManager.BrushConsumer());
        AnnotationHelper.scanClassPath((URLClassLoader) getClass().getClassLoader());

        Log.GLOBAL.info("Found and loaded " + BrushManager.get().getLoadedBrushes().size() + " brushes.");

        setupCommands();

        this.queue = new ChangeQueue();
        Sponge.getScheduler().createTaskBuilder().intervalTicks(1).execute(this.queue).name("VoxelSniper change queue").submit(this);
    }

    @Listener
    public void onSnipe(InteractBlockEvent.Secondary event, @First Player player) {
        // TODO permission check
        Optional<ItemStack> otype = player.getItemInHand(HandTypes.MAIN_HAND);
        if (!otype.isPresent()) {
            return;
        }
        ItemType type = otype.get().getItem();
        if (type.equals(this.primary) || type.equals(this.secondary)) {
            PlayerData data = PlayerData.get(player.getUniqueId());
//            if (System.currentTimeMillis() - data.getLastSnipeTime() < VoxelSniperConfig.snipe_cooldown) {
//                return;
//            }
            data.updateLastSnipeTime();
            Predicate<BlockRayHit<World>> filter;
            if (type.equals(this.primary)) {
                filter = BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1);
            } else {
                filter = BlockRay.onlyAirFilter();
            }
            Optional<BlockRayHit<World>> targetHit = BlockRay.from(player).filter(filter).build().end();
            if (!targetHit.isPresent()) {
                Log.GLOBAL.debug("No target");
                return;
            }
            Location<World> target = targetHit.get().getLocation();
            data.getCurrentBrush().execute(data, target);
        }
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Login event) {
        PlayerData.create(event.getTargetUser().getUniqueId());
    }

    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event) {
        PlayerData.clear(event.getTargetEntity().getUniqueId());
    }

    private void setupCommands() {
        // @formatter:off
        Sponge.getCommandManager().register(this, new BrushCommand(), "brush", "b");
        // @formatter:on
    }
}
