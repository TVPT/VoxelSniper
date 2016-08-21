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
import com.thevoxelbox.voxelsniper.brush.IBrush;
import com.thevoxelbox.voxelsniper.brush.PerformBrush;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
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
                                        GenericArguments.optional(GenericArguments.string(Text.of("brush"))),
                                        GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("brush_args"))))
                                .executor(new VoxelBrushCommand())
                                .permission(VoxelSniperConfiguration.PERMISSION_SNIPER)
                                .description(Text.of("VoxelSniper brush settings")).build(),
                        "b", "brush");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("Player only."));
            return CommandResult.success();
        }
        Player player = (Player) src;
        Sniper sniper = SniperManager.get().getSniperForPlayer(player);
        String currentToolId = sniper.getCurrentToolId();
        SnipeData snipeData = sniper.getSnipeData(currentToolId);

        Optional<String> brush_selection = args.getOne("brush");
        if (!brush_selection.isPresent()) {
            sniper.displayInfo();
            return CommandResult.success();
        }

        try {
            double newBrushSize = Double.parseDouble(brush_selection.get());
            if (!player.hasPermission(VoxelSniperConfiguration.PERMISSION_IGNORE_SIZE_LIMITS)
                    && newBrushSize > VoxelSniperConfiguration.LITESNIPER_MAX_BRUSH_SIZE) {
                player.sendMessage(
                        Text.of(TextColors.RED, "Size is restricted to " + VoxelSniperConfiguration.LITESNIPER_MAX_BRUSH_SIZE + " for you."));
                newBrushSize = VoxelSniperConfiguration.LITESNIPER_MAX_BRUSH_SIZE;
            }
//            int originalSize = snipeData.getBrushSize();
            snipeData.setBrushSize(newBrushSize);
            // @Spongify create new events
//            SniperBrushSizeChangedEvent event = new SniperBrushSizeChangedEvent(sniper, currentToolId, originalSize, snipeData.getBrushSize());
//            Bukkit.getPluginManager().callEvent(event);
            snipeData.getVoxelMessage().size();
            return CommandResult.success();
        } catch (NumberFormatException ingored) {
        }
        Optional<String> brush_args = args.getOne("brush_args");
        Class<? extends IBrush> brush = Brushes.get().getBrushForHandle(brush_selection.get());
        if (brush != null) {
//            IBrush orignalBrush = sniper.getBrush(currentToolId);
            sniper.setBrush(currentToolId, brush);

            if (brush_args.isPresent()) {
                String[] bargs = brush_args.get().split(" ");
                IBrush currentBrush = sniper.getBrush(currentToolId);
                if (currentBrush instanceof PerformBrush) {
                    ((PerformBrush) currentBrush).parse(bargs, snipeData);
                } else {
                    // @Cleanup parse out flags and pass as separate set
                    currentBrush.parameters(bargs, snipeData);
                }
            }
            // @Spongify add new event
//            SniperBrushChangedEvent event = new SniperBrushChangedEvent(sniper, currentToolId, orignalBrush, sniper.getBrush(currentToolId));
            sniper.displayInfo();
        } else {
            player.sendMessage(Text.of(TextColors.RED, "Couldn't find Brush for brush handle \"" + brush_selection.get() + "\""));
        }
        return CommandResult.success();
    }
}
