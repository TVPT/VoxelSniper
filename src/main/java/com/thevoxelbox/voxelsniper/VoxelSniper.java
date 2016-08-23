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

import com.thevoxelbox.voxelsniper.brush.BallBrush;
import com.thevoxelbox.voxelsniper.brush.BiomeBrush;
import com.thevoxelbox.voxelsniper.brush.BlendBallBrush;
import com.thevoxelbox.voxelsniper.brush.BlendDiscBrush;
import com.thevoxelbox.voxelsniper.brush.BlendVoxelBrush;
import com.thevoxelbox.voxelsniper.brush.BlendVoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.BlobBrush;
import com.thevoxelbox.voxelsniper.brush.CanyonBrush;
import com.thevoxelbox.voxelsniper.brush.CanyonSelectionBrush;
import com.thevoxelbox.voxelsniper.brush.CheckerVoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.CleanSnowBrush;
import com.thevoxelbox.voxelsniper.brush.CometBrush;
import com.thevoxelbox.voxelsniper.brush.CylinderBrush;
import com.thevoxelbox.voxelsniper.brush.DiscBrush;
import com.thevoxelbox.voxelsniper.brush.DiscFaceBrush;
import com.thevoxelbox.voxelsniper.brush.DrainBrush;
import com.thevoxelbox.voxelsniper.brush.EllipseBrush;
import com.thevoxelbox.voxelsniper.brush.EllipsoidBrush;
import com.thevoxelbox.voxelsniper.brush.EntityBrush;
import com.thevoxelbox.voxelsniper.brush.EntityRemovalBrush;
import com.thevoxelbox.voxelsniper.brush.ErodeBrush;
import com.thevoxelbox.voxelsniper.brush.ExtrudeBrush;
import com.thevoxelbox.voxelsniper.brush.FillDownBrush;
import com.thevoxelbox.voxelsniper.brush.FlatOceanBrush;
import com.thevoxelbox.voxelsniper.brush.HeatRayBrush;
import com.thevoxelbox.voxelsniper.brush.JockeyBrush;
import com.thevoxelbox.voxelsniper.brush.LightningBrush;
import com.thevoxelbox.voxelsniper.brush.LineBrush;
import com.thevoxelbox.voxelsniper.brush.MoveBrush;
import com.thevoxelbox.voxelsniper.brush.OceanBrush;
import com.thevoxelbox.voxelsniper.brush.OverlayBrush;
import com.thevoxelbox.voxelsniper.brush.RandomErodeBrush;
import com.thevoxelbox.voxelsniper.brush.RegenerateChunkBrush;
import com.thevoxelbox.voxelsniper.brush.RingBrush;
import com.thevoxelbox.voxelsniper.brush.Rot3DBrush;
import com.thevoxelbox.voxelsniper.brush.RulerBrush;
import com.thevoxelbox.voxelsniper.brush.SetBrush;
import com.thevoxelbox.voxelsniper.brush.ShellBallBrush;
import com.thevoxelbox.voxelsniper.brush.ShellSetBrush;
import com.thevoxelbox.voxelsniper.brush.ShellVoxelBrush;
import com.thevoxelbox.voxelsniper.brush.SignOverwriteBrush;
import com.thevoxelbox.voxelsniper.brush.SnipeBrush;
import com.thevoxelbox.voxelsniper.brush.SnowConeBrush;
import com.thevoxelbox.voxelsniper.brush.SplatterBallBrush;
import com.thevoxelbox.voxelsniper.brush.SplatterDiscBrush;
import com.thevoxelbox.voxelsniper.brush.SplatterOverlayBrush;
import com.thevoxelbox.voxelsniper.brush.SplatterVoxelBrush;
import com.thevoxelbox.voxelsniper.brush.SplineBrush;
import com.thevoxelbox.voxelsniper.brush.StencilBrush;
import com.thevoxelbox.voxelsniper.brush.StencilListBrush;
import com.thevoxelbox.voxelsniper.brush.ThreePointCircleBrush;
import com.thevoxelbox.voxelsniper.brush.TreeSnipeBrush;
import com.thevoxelbox.voxelsniper.brush.TriangleBrush;
import com.thevoxelbox.voxelsniper.brush.UnderlayBrush;
import com.thevoxelbox.voxelsniper.brush.VoltMeterBrush;
import com.thevoxelbox.voxelsniper.brush.VoxelBrush;
import com.thevoxelbox.voxelsniper.brush.VoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.VoxelDiscFaceBrush;
import com.thevoxelbox.voxelsniper.brush.WarpBrush;
import com.thevoxelbox.voxelsniper.command.VoxelBrushCommand;
import com.thevoxelbox.voxelsniper.command.VoxelBrushToolCommand;
import com.thevoxelbox.voxelsniper.command.VoxelCenterCommand;
import com.thevoxelbox.voxelsniper.command.VoxelDefaultCommand;
import com.thevoxelbox.voxelsniper.command.VoxelHeightCommand;
import com.thevoxelbox.voxelsniper.command.VoxelInkCommand;
import com.thevoxelbox.voxelsniper.command.VoxelInkReplaceCommand;
import com.thevoxelbox.voxelsniper.command.VoxelListCommand;
import com.thevoxelbox.voxelsniper.command.VoxelPerformerCommand;
import com.thevoxelbox.voxelsniper.command.VoxelReplaceCommand;
import com.thevoxelbox.voxelsniper.command.VoxelSniperCommand;
import com.thevoxelbox.voxelsniper.command.VoxelUndoCommand;
import com.thevoxelbox.voxelsniper.command.VoxelUndoUserCommand;
import com.thevoxelbox.voxelsniper.command.VoxelVoxelCommand;
import com.thevoxelbox.voxelsniper.util.SniperStats;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.IOException;
import java.nio.file.Path;

/**
 * VoxelSniper main plugin class.
 */
@Plugin(id = VoxelSniperConfiguration.PLUGIN_ID, name = VoxelSniperConfiguration.PLUGIN_NAME, version = VoxelSniperConfiguration.PLUGIN_VERSION)
public class VoxelSniper {

    public static Cause plugin_cause;
    private static VoxelSniper instance;

    public static VoxelSniper getInstance() {
        return VoxelSniper.instance;
    }

    @Inject private Logger logger;
    @Inject private PluginContainer container;
    @ConfigDir(sharedRoot = false) @Inject private Path configDir;

    private final VoxelSniperListener voxelSniperListener = new VoxelSniperListener();
    private SniperStats stats;

    @Listener
    public void onInit(GameInitializationEvent event) {
        VoxelSniper.instance = this;
        plugin_cause = Cause.of(NamedCause.of("VoxelSniper", this.container));
        registerBrushes();
        this.logger.info("Registered " + Brushes.get().registeredSniperBrushes() + " Sniper Brushes with "
                + Brushes.get().registeredSniperBrushHandles() + " handles.");

        try {
            this.stats = new SniperStats(VoxelSniperConfiguration.PLUGIN_VERSION, this.configDir.resolve("metrics.properties").toFile());
        } catch (IOException e) {
            this.logger.error("Error setting up metrics", this.stats);
        }

        // @Spongify loadSniperConfiguration();

        Sponge.getEventManager().registerListeners(this, this.voxelSniperListener);

        registerCommands();
    }

    private void registerCommands() {
        // @Cleanup auto detect and load these?
        VoxelBrushCommand.setup(this);
        VoxelBrushToolCommand.setup(this);
        VoxelCenterCommand.setup(this);
        VoxelDefaultCommand.setup(this);
        VoxelHeightCommand.setup(this);
        VoxelInkCommand.setup(this);
        VoxelInkReplaceCommand.setup(this);
        VoxelListCommand.setup(this);
        VoxelPerformerCommand.setup(this);
        VoxelReplaceCommand.setup(this);
        VoxelSniperCommand.setup(this);
        VoxelUndoCommand.setup(this);
        VoxelUndoUserCommand.setup(this);
        VoxelVoxelCommand.setup(this);
    }

    /**
     * Registers all brushes.
     */
    private void registerBrushes() {
        // @Cleanup load these by scanning for annotations
        Brushes.get().registerSniperBrush(BallBrush.class, "b", "ball");
        Brushes.get().registerSniperBrush(BiomeBrush.class, "bio", "biome");
        Brushes.get().registerSniperBrush(BlendBallBrush.class, "bb", "blendball");
        Brushes.get().registerSniperBrush(BlendDiscBrush.class, "bd", "blenddisc");
        Brushes.get().registerSniperBrush(BlendVoxelBrush.class, "bv", "blendvoxel");
        Brushes.get().registerSniperBrush(BlendVoxelDiscBrush.class, "bvd", "blendvoxeldisc");
        Brushes.get().registerSniperBrush(BlobBrush.class, "blob", "splatblob");
        Brushes.get().registerSniperBrush(CanyonBrush.class, "ca", "canyon");
        Brushes.get().registerSniperBrush(CanyonSelectionBrush.class, "cas", "canyonselection");
        Brushes.get().registerSniperBrush(CheckerVoxelDiscBrush.class, "cvd", "checkervoxeldisc");
        Brushes.get().registerSniperBrush(CleanSnowBrush.class, "cls", "cleansnow");
        Brushes.get().registerSniperBrush(CometBrush.class, "com", "comet");
        Brushes.get().registerSniperBrush(CylinderBrush.class, "c", "cylinder");
        Brushes.get().registerSniperBrush(DiscBrush.class, "d", "disc");
        Brushes.get().registerSniperBrush(DiscFaceBrush.class, "df", "discface");
        Brushes.get().registerSniperBrush(DrainBrush.class, "drain");
        Brushes.get().registerSniperBrush(EllipseBrush.class, "el", "ellipse");
        Brushes.get().registerSniperBrush(EllipsoidBrush.class, "elo", "ellipsoid");
        Brushes.get().registerSniperBrush(EntityBrush.class, "en", "entity");
        Brushes.get().registerSniperBrush(EntityRemovalBrush.class, "er", "entityremoval");
        Brushes.get().registerSniperBrush(ErodeBrush.class, "e", "erode");
        Brushes.get().registerSniperBrush(ExtrudeBrush.class, "ex", "extrude");
        Brushes.get().registerSniperBrush(FillDownBrush.class, "fd", "filldown");
        Brushes.get().registerSniperBrush(FlatOceanBrush.class, "fo", "flatocean");
        Brushes.get().registerSniperBrush(HeatRayBrush.class, "hr", "heatray");
        Brushes.get().registerSniperBrush(JockeyBrush.class, "jockey");
        Brushes.get().registerSniperBrush(LightningBrush.class, "light", "lightning");
        Brushes.get().registerSniperBrush(LineBrush.class, "l", "line");
        Brushes.get().registerSniperBrush(MoveBrush.class, "mv", "move");
        Brushes.get().registerSniperBrush(OceanBrush.class, "o", "ocean");
        Brushes.get().registerSniperBrush(OverlayBrush.class, "over", "overlay");
        Brushes.get().registerSniperBrush(RandomErodeBrush.class, "re", "randomerode");
        Brushes.get().registerSniperBrush(RegenerateChunkBrush.class, "gc", "generatechunk");
        Brushes.get().registerSniperBrush(RingBrush.class, "ri", "ring");
        Brushes.get().registerSniperBrush(Rot3DBrush.class, "rot3", "rotation3d", "rot", "rotation");
        Brushes.get().registerSniperBrush(RulerBrush.class, "r", "ruler");
        Brushes.get().registerSniperBrush(SetBrush.class, "set");
        Brushes.get().registerSniperBrush(ShellBallBrush.class, "shb", "shellball");
        Brushes.get().registerSniperBrush(ShellSetBrush.class, "shs", "shellset");
        Brushes.get().registerSniperBrush(ShellVoxelBrush.class, "shv", "shellvoxel");
        Brushes.get().registerSniperBrush(SignOverwriteBrush.class, "sio", "signoverwriter");
        Brushes.get().registerSniperBrush(SnipeBrush.class, "s", "snipe");
        Brushes.get().registerSniperBrush(SnowConeBrush.class, "snow", "snowcone");
        Brushes.get().registerSniperBrush(SplatterBallBrush.class, "sb", "splatball");
        Brushes.get().registerSniperBrush(SplatterDiscBrush.class, "sd", "splatdisc");
        Brushes.get().registerSniperBrush(SplatterOverlayBrush.class, "sover", "splatteroverlay");
        Brushes.get().registerSniperBrush(SplatterVoxelBrush.class, "sv", "splattervoxel");
        Brushes.get().registerSniperBrush(SplatterDiscBrush.class, "svd", "splatvoxeldisc");
        Brushes.get().registerSniperBrush(SplineBrush.class, "sp", "spline");
        Brushes.get().registerSniperBrush(StencilBrush.class, "st", "stencil");
        Brushes.get().registerSniperBrush(StencilListBrush.class, "sl", "stencillist");
        Brushes.get().registerSniperBrush(ThreePointCircleBrush.class, "tpc", "threepointcircle");
        Brushes.get().registerSniperBrush(TreeSnipeBrush.class, "t", "tree", "treesnipe");
        Brushes.get().registerSniperBrush(TriangleBrush.class, "tri", "triangle");
        Brushes.get().registerSniperBrush(UnderlayBrush.class, "under", "underlay");
        Brushes.get().registerSniperBrush(VoltMeterBrush.class, "volt", "voltmeter");
        Brushes.get().registerSniperBrush(VoxelBrush.class, "v", "voxel");
        Brushes.get().registerSniperBrush(VoxelDiscBrush.class, "vd", "voxeldisc");
        Brushes.get().registerSniperBrush(VoxelDiscFaceBrush.class, "vdf", "voxeldiscface");
        Brushes.get().registerSniperBrush(WarpBrush.class, "w", "warp");
    }
}
