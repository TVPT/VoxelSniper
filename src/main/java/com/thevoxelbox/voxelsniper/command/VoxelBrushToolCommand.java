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

import com.thevoxelbox.voxelsniper.SnipeAction;
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
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class VoxelBrushToolCommand implements CommandExecutor {

    public static void setup(Object plugin) {
        Sponge.getCommandManager()
                .register(plugin,
                        CommandSpec.builder()
                                .arguments(GenericArguments.playerOrSource(Text.of("sniper")),
                                        GenericArguments.remainingJoinedStrings(Text.of("args")))
                                .executor(new VoxelBrushToolCommand())
                                .permission(VoxelSniperConfiguration.PERMISSION_SNIPER)
                                .description(Text.of("VoxelSniper brush tool settings")).build(),
                        "btool");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext gargs) throws CommandException {
        Player player = (Player) gargs.getOne("sniper").get();
        Sniper sniper = SniperManager.get().getSniperForPlayer(player);

        Optional<String> oargs = gargs.getOne("args");

        if (oargs.isPresent() && !oargs.get().isEmpty()) {
            String[] args = oargs.get().split(" ");
            if (args[0].equalsIgnoreCase("assign")) {
                if (args.length == 1) {
                    player.sendMessage(Text.of(TextColors.RED, "Usage: /btool assign <arrow|powder> <toolid>"));
                    return CommandResult.success();
                }
                SnipeAction action;
                if (args[1].equalsIgnoreCase("arrow")) {
                    action = SnipeAction.ARROW;
                } else if (args[1].equalsIgnoreCase("powder")) {
                    action = SnipeAction.GUNPOWDER;
                } else {
                    player.sendMessage(Text.of(TextColors.RED, "Usage: /btool assign <arrow|powder> <toolid>"));
                    return CommandResult.success();
                }

                if (args.length == 3) {
                    Optional<ItemStack> itemInHand = player.getItemInHand(HandTypes.MAIN_HAND);
                    if (!itemInHand.isPresent()) {
                        player.sendMessage(Text.of(TextColors.RED, "You must have an item in your main hand to assign a brush tool."));
                        return CommandResult.success();
                    }
                    if (sniper.setTool(args[2], action, itemInHand.get().getType())) {
                        player.sendMessage(Text.of(TextColors.GREEN,
                                itemInHand.get().getType().getId() + " has been assigned to '" + args[2] + "' as action " + action.name() + "."));
                    } else {
                        player.sendMessage(Text.of(TextColors.RED, "Couldn't assign tool."));
                    }
                    return CommandResult.success();
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (args.length == 2) {
                    sniper.removeTool(args[1]);
                    return CommandResult.success();
                }
                ItemType itemInHand =
                        (player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) ? player.getItemInHand(HandTypes.MAIN_HAND).get().getType() : null;
                if (itemInHand == null) {
                    player.sendMessage(Text.of(TextColors.RED, "Can't unassign empty hands."));
                    return CommandResult.success();
                }
                if (sniper.getCurrentToolId() == null) {
                    player.sendMessage(Text.of(TextColors.RED, "Can't unassign default tool."));
                    return CommandResult.success();
                }
                sniper.removeTool(sniper.getCurrentToolId(), itemInHand);
                return CommandResult.success();
            }
        }
        player.sendMessage(Text.of(TextColors.RED, "Usage: /btool assign <arrow|powder> <toolid>"));
        player.sendMessage(Text.of(TextColors.RED, "Usage: /btool remove [toolid]"));
        return CommandResult.success();
    }
}
