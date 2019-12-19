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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VoxelSniperCommand implements CommandExecutor {

    public static void setup(Object plugin) {
        Sponge.getCommandManager().register(plugin,
                CommandSpec.builder()
                        .arguments(GenericArguments.playerOrSource(Text.of("sniper")),
                                GenericArguments.choices(
                                                Text.of("action"), getChoicesMap(), true, false),
                                GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("args"))))
                        .executor(new VoxelSniperCommand()).permission(VoxelSniperConfiguration.PERMISSION_SNIPER)
                        .description(Text.of("VoxelSniper info and settings")).build(),
                "vs");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext gargs) {
        Player player = (Player) gargs.getOne("sniper").get();
        Sniper sniper = SniperManager.get().getSniperForPlayer(player);
        Optional<Action> oAction = gargs.getOne("action");

        Optional<String> oArguments = gargs.getOne("args");
        String[] args = new String[0];
        if (oArguments.isPresent()) {
            args = oArguments.get().split(" ");
        }

        if (oAction.isPresent()) {
            return handleAction(oAction.get(), sniper, args);
        }

        return CommandResult.success();
    }

    private CommandResult handleAction(Action action, Sniper sniper, String[] arguments) {
        switch (action) {
            case BRUSHES:
                sniper.getPlayer().sendMessage(Text.of(TextColors.AQUA, "All available brushes:"));
                sniper.getPlayer().sendMessage(Text.of(Brushes.getAllBrushes()));
                break;

            case VERSION:
                sniper.getPlayer().sendMessage(Text.of(TextColors.AQUA,
                                                        "VoxelSniper version ",
                                                        VoxelSniperConfiguration.PLUGIN_VERSION));
                break;

            case RANGE:
                SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
                if (arguments.length == 1) {
                    try {
                        int range = Integer.parseInt(arguments[0]);
                        if (range < 0) {
                            sniper.getPlayer().sendMessage(Text.of(TextColors.RED,
                                                                    "Negative range values are not allowed."));
                        }
                        snipeData.setRange(range);
                        snipeData.setRanged(true);
                        snipeData.getVoxelMessage().toggleRange();

                    } catch (NumberFormatException exception) {
                        sniper.getPlayer().sendMessage(Text.of(TextColors.RED,
                                                        "Failed to parse number for range '",
                                                        arguments[0],
                                                        "'"));
                    }

                    return CommandResult.success();
                }

                snipeData.setRanged(!snipeData.isRanged());
                snipeData.getVoxelMessage().toggleRange();
                break;

            case ENABLE:
                if (sniper.getPlayer().hasPermission(VoxelSniperConfiguration.PERMISSION_COMMAND_ENABLE)) {
                    sniper.setEnabled(true);
                    sniper.getPlayer().sendMessage(Text.of(TextColors.GREEN,
                                                    "VoxelSniper is ",
                                                    (sniper.isEnabled() ? "enabled" : "disabled")));
                }
                break;

            case DISABLE:
                if (sniper.getPlayer().hasPermission(VoxelSniperConfiguration.PERMISSION_COMMAND_ENABLE)) {
                    sniper.setEnabled(false);
                    sniper.getPlayer().sendMessage(Text.of(TextColors.GREEN,
                                                        "VoxelSniper is ",
                                                        (sniper.isEnabled() ? "enabled" : "disabled")));
                }
                break;

            case TOGGLE:
                if (sniper.getPlayer().hasPermission(VoxelSniperConfiguration.PERMISSION_COMMAND_ENABLE)) {
                    sniper.setEnabled(!sniper.isEnabled());
                    sniper.getPlayer().sendMessage(Text.of(TextColors.GREEN,
                                                        "VoxelSniper is ",
                                                        (sniper.isEnabled() ? "enabled" : "disabled")));
                }
                break;

            case INFO:
                sniper.getPlayer().sendMessage(Text.of(TextColors.DARK_RED,
                                                    "VoxelSniper - Current Brush Settings:"));
                sniper.displayInfo();
                break;

            default:
                sniper.getPlayer().sendMessage(Text.of(TextColors.RED,
                                                    "Unknown action \"", action, "\""));
                break;
        }

        return CommandResult.success();
    }

    private static Map<String, Action> getChoicesMap() {
        Map<String, Action> choices = new HashMap<>();
        for (Action a : Action.values()) {
            choices.put(a.toString(), a);
        }

        return choices;
    }

    private enum Action {
        BRUSHES,
        VERSION,
        RANGE,
        ENABLE,
        DISABLE,
        TOGGLE,
        INFO;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }

    }
}
