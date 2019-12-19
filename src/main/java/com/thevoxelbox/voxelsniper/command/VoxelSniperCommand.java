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

import java.util.Iterator;
import java.util.Optional;

public class VoxelSniperCommand implements CommandExecutor {

    public static void setup(Object plugin) {
        Sponge.getCommandManager().register(plugin,
                CommandSpec.builder()
                        .arguments(GenericArguments.playerOrSource(Text.of("sniper")),
                                GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("args"))))
                        .executor(new VoxelSniperCommand()).permission(VoxelSniperConfiguration.PERMISSION_SNIPER)
                        .description(Text.of("VoxelSniper material list selection")).build(),
                "vs");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext gargs) throws CommandException {
        Player player = (Player) gargs.getOne("sniper").get();
        Sniper sniper = SniperManager.get().getSniperForPlayer(player);
        Optional<String> oargs = gargs.getOne("args");
        if (oargs.isPresent()) {
            String[] args = oargs.get().split(" ");
            if (args[0].equalsIgnoreCase("brushes")) {
                player.sendMessage(Text.of(TextColors.AQUA, "All available brushes:"));
                player.sendMessage(Text.of(getBrushListString()));
                return CommandResult.success();
            } else if (args[0].equalsIgnoreCase("version")) {
                player.sendMessage(Text.of(TextColors.AQUA, "VoxelSniper version " + VoxelSniperConfiguration.PLUGIN_VERSION));
                return CommandResult.success();
            } else if (args[0].equalsIgnoreCase("range")) {
                SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
                if (args.length == 2) {
                    try {
                        int range = Integer.parseInt(args[1]);
                        if (range < 0) {
                            player.sendMessage(Text.of(TextColors.RED, "Negative range values are not allowed."));
                        }
                        snipeData.setRange(range);
                        snipeData.setRanged(true);
                        snipeData.getVoxelMessage().toggleRange();

                    } catch (NumberFormatException exception) {
                        player.sendMessage(Text.of(TextColors.RED, "Failed to parse number for range '" + args[1] + "'"));
                    }
                    return CommandResult.success();
                }
                snipeData.setRanged(!snipeData.isRanged());
                snipeData.getVoxelMessage().toggleRange();
                return CommandResult.success();
            } else if (args[0].equalsIgnoreCase("enable") && player.hasPermission(VoxelSniperConfiguration.PERMISSION_COMMAND_ENABLE)) {
                sniper.setEnabled(true);
                player.sendMessage(Text.of(TextColors.GREEN, "VoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled")));
                return CommandResult.success();
            } else if (args[0].equalsIgnoreCase("disable") && player.hasPermission(VoxelSniperConfiguration.PERMISSION_COMMAND_ENABLE)) {
                sniper.setEnabled(false);
                player.sendMessage(Text.of(TextColors.GREEN, "VoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled")));
                return CommandResult.success();
            } else if (args[0].equalsIgnoreCase("toggle") && player.hasPermission(VoxelSniperConfiguration.PERMISSION_COMMAND_ENABLE)) {
                sniper.setEnabled(!sniper.isEnabled());
                player.sendMessage(Text.of(TextColors.GREEN, "VoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled")));
                return CommandResult.success();
            }
        }
        player.sendMessage(Text.of(TextColors.DARK_RED, "VoxelSniper - Current Brush Settings:"));
        sniper.displayInfo();
        return CommandResult.success();
    }

    private String getBrushListString() {
        StringBuilder sb = new StringBuilder();
        Iterator<String> brushes = Brushes.getAllBrushes().iterator();
        if (!brushes.hasNext()) {
            return sb.toString();
        }

        sb.append(brushes.next());
        while (brushes.hasNext()) {
            sb.append(", ");
            sb.append(brushes.next());
        }

        return sb.toString();
    }
}
