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

import com.google.inject.Inject;
import com.thevoxelbox.voxelsniper.brush.chunk.CanyonBrush;
import com.thevoxelbox.voxelsniper.brush.chunk.CanyonSelectionBrush;
import com.thevoxelbox.voxelsniper.brush.chunk.EntityRemovalBrush;
import com.thevoxelbox.voxelsniper.brush.chunk.FlatOceanBrush;
import com.thevoxelbox.voxelsniper.brush.chunk.OceanBrush;
import com.thevoxelbox.voxelsniper.brush.chunk.RegenerateChunkBrush;
import com.thevoxelbox.voxelsniper.brush.misc.BiomeBrush;
import com.thevoxelbox.voxelsniper.brush.misc.CleanSnowBrush;
import com.thevoxelbox.voxelsniper.brush.misc.CometBrush;
import com.thevoxelbox.voxelsniper.brush.misc.DrainBrush;
import com.thevoxelbox.voxelsniper.brush.misc.EntityBrush;
import com.thevoxelbox.voxelsniper.brush.misc.HeatRayBrush;
import com.thevoxelbox.voxelsniper.brush.misc.JockeyBrush;
import com.thevoxelbox.voxelsniper.brush.misc.LightningBrush;
import com.thevoxelbox.voxelsniper.brush.misc.Rot3DBrush;
import com.thevoxelbox.voxelsniper.brush.misc.RulerBrush;
import com.thevoxelbox.voxelsniper.brush.misc.SignOverwriteBrush;
import com.thevoxelbox.voxelsniper.brush.misc.SnowConeBrush;
import com.thevoxelbox.voxelsniper.brush.misc.StencilBrush;
import com.thevoxelbox.voxelsniper.brush.misc.StencilListBrush;
import com.thevoxelbox.voxelsniper.brush.misc.TreeSnipeBrush;
import com.thevoxelbox.voxelsniper.brush.misc.VoltMeterBrush;
import com.thevoxelbox.voxelsniper.brush.misc.WarpBrush;
import com.thevoxelbox.voxelsniper.brush.shape.BallBrush;
import com.thevoxelbox.voxelsniper.brush.shape.BlobBrush;
import com.thevoxelbox.voxelsniper.brush.shape.CheckerVoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.shape.CylinderBrush;
import com.thevoxelbox.voxelsniper.brush.shape.DiscBrush;
import com.thevoxelbox.voxelsniper.brush.shape.DiscFaceBrush;
import com.thevoxelbox.voxelsniper.brush.shape.EllipseBrush;
import com.thevoxelbox.voxelsniper.brush.shape.EllipsoidBrush;
import com.thevoxelbox.voxelsniper.brush.shape.ExtrudeBrush;
import com.thevoxelbox.voxelsniper.brush.shape.FillDownBrush;
import com.thevoxelbox.voxelsniper.brush.shape.LineBrush;
import com.thevoxelbox.voxelsniper.brush.shape.OverlayBrush;
import com.thevoxelbox.voxelsniper.brush.shape.RingBrush;
import com.thevoxelbox.voxelsniper.brush.shape.SetBrush;
import com.thevoxelbox.voxelsniper.brush.shape.ShellBallBrush;
import com.thevoxelbox.voxelsniper.brush.shape.ShellSetBrush;
import com.thevoxelbox.voxelsniper.brush.shape.ShellVoxelBrush;
import com.thevoxelbox.voxelsniper.brush.shape.SnipeBrush;
import com.thevoxelbox.voxelsniper.brush.shape.ThreePointCircleBrush;
import com.thevoxelbox.voxelsniper.brush.shape.TriangleBrush;
import com.thevoxelbox.voxelsniper.brush.shape.UnderlayBrush;
import com.thevoxelbox.voxelsniper.brush.shape.VoxelBrush;
import com.thevoxelbox.voxelsniper.brush.shape.VoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.shape.VoxelDiscFaceBrush;
import com.thevoxelbox.voxelsniper.brush.terrain.BlendBallBrush;
import com.thevoxelbox.voxelsniper.brush.terrain.BlendDiscBrush;
import com.thevoxelbox.voxelsniper.brush.terrain.BlendVoxelBrush;
import com.thevoxelbox.voxelsniper.brush.terrain.BlendVoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.terrain.ErodeBrush;
import com.thevoxelbox.voxelsniper.brush.terrain.RandomErodeBrush;
import com.thevoxelbox.voxelsniper.brush.terrain.SplatterBallBrush;
import com.thevoxelbox.voxelsniper.brush.terrain.SplatterDiscBrush;
import com.thevoxelbox.voxelsniper.brush.terrain.SplatterOverlayBrush;
import com.thevoxelbox.voxelsniper.brush.terrain.SplatterVoxelBrush;
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
import com.thevoxelbox.voxelsniper.util.SchematicHelper;
import com.thevoxelbox.voxelsniper.util.StencilUpdater;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.nio.file.Path;

/**
 * VoxelSniper main plugin class.
 */
@Plugin(id = VoxelSniperConfiguration.PLUGIN_ID, name = VoxelSniperConfiguration.PLUGIN_NAME, version = VoxelSniperConfiguration.PLUGIN_VERSION, description = VoxelSniperConfiguration.PLUGIN_DESC)
public class VoxelSniper {

    private static VoxelSniper instance;

    public static VoxelSniper getInstance() {
        return VoxelSniper.instance;
    }

    public static Logger getLogger() {
        return instance.logger;
    }

    @Inject private Logger logger;
    @ConfigDir(sharedRoot = false) @Inject private Path configDir;

    private final VoxelSniperListener voxelSniperListener = new VoxelSniperListener();

    @Listener
    public void onInit(GameInitializationEvent event) {
        VoxelSniper.instance = this;

        this.logger.info("Loading VoxelSniper configuration");
        Path config = this.configDir.resolve("voxelsniper.conf");
        VoxelSniperConfiguration.init(config);

        registerBrushes();
        this.logger.info("Registered " + Brushes.get().registeredSniperBrushes() + " Sniper Brushes with "
                + Brushes.get().registeredSniperBrushHandles() + " handles.");

        SchematicHelper.setSchematicsDir(this.configDir.resolve("schematics"));
        File stencils = this.configDir.resolve("stencils").toFile();
        if (stencils.exists() && stencils.isDirectory()) {
            this.logger.info("Found a stencils directory, porting all stencils inside to schematics.");
            StencilUpdater.update(stencils);
        }

        Sponge.getEventManager().registerListeners(this, this.voxelSniperListener);

        registerCommands();
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        this.logger.info("Reloading VoxelSniper configuration");
        Path config = this.configDir.resolve("voxelsniper.conf");
        VoxelSniperConfiguration.init(config);
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
