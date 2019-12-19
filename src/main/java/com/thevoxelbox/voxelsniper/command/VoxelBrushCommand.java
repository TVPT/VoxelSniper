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
package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.Brushes;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperConfiguration;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.event.sniper.ChangeBrushEvent;
import com.thevoxelbox.voxelsniper.event.sniper.ChangeBrushSizeEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class VoxelBrushCommand implements CommandExecutor {

    public static void setup(Object plugin) {
        Sponge.getCommandManager()
                .register(plugin,
                        CommandSpec.builder()
                                .arguments(
                                        GenericArguments.playerOrSource(Text.of("sniper")),
                                        GenericArguments.optional(new BrushCommandElement(Text.of("brush"))),
                                        GenericArguments.optionalWeak(GenericArguments.doubleNum(Text.of("brush_size"))),
                                        GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("brush_args"))))
                                .executor(new VoxelBrushCommand())
                                .permission(VoxelSniperConfiguration.PERMISSION_SNIPER)
                                .description(Text.of("Set VoxelSniper brush")).build(),
                        "b", "brush");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Player player = (Player) args.getOne("sniper").get();
        Sniper sniper = SniperManager.get().getSniperForPlayer(player);
        String currentToolId = sniper.getCurrentToolId();
        SnipeData snipeData = sniper.getSnipeData(currentToolId);

        Optional<Class<? extends Brush>> optBrush = args.getOne("brush");
        if (optBrush.isPresent()) {
            Class<? extends Brush> newBrushClass = optBrush.get();
            Brush oldBrush = sniper.getBrush(currentToolId);

            if (!oldBrush.getClass().equals(newBrushClass)) {
                sniper.setBrush(currentToolId, newBrushClass);
                Brush newBrush = sniper.getBrush(currentToolId);

                ChangeBrushEvent event = new ChangeBrushEvent(Sponge.getCauseStackManager().getCurrentCause(), snipeData, newBrush);
                Sponge.getEventManager().post(event);
            }
        }

        Optional<Double> optBrushSize = args.getOne("brush_size");
        if (optBrushSize.isPresent()) {
            double newBrushSize = optBrushSize.get();
            if (!player.hasPermission(VoxelSniperConfiguration.PERMISSION_IGNORE_SIZE_LIMITS)
                    && newBrushSize > VoxelSniperConfiguration.LITESNIPER_MAX_BRUSH_SIZE) {
                player.sendMessage(
                        Text.of(TextColors.RED, "Size is restricted to " + VoxelSniperConfiguration.LITESNIPER_MAX_BRUSH_SIZE + " for you."));
                newBrushSize = VoxelSniperConfiguration.LITESNIPER_MAX_BRUSH_SIZE;
            }

            ChangeBrushSizeEvent event = new ChangeBrushSizeEvent(Sponge.getCauseStackManager().getCurrentCause(), snipeData, newBrushSize);
            Sponge.getEventManager().post(event);
            snipeData.setBrushSize(newBrushSize);
            snipeData.getVoxelMessage().size();
        }

        Optional<String> brush_args = args.getOne("brush_args");
        if (brush_args.isPresent()) {
            String[] bargs = brush_args.get().split(" ");
            Brush currentBrush = sniper.getBrush(currentToolId);
            currentBrush.parameters(bargs, snipeData);
        }

        sniper.displayInfo();
        return CommandResult.success();
    }
}
