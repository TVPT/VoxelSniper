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

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperConfiguration;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
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
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRay.BlockRayBuilder;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class VoxelListCommand implements CommandExecutor {

    public static void setup(Object plugin) {
        Sponge.getCommandManager().register(plugin,
                CommandSpec.builder()
                        .arguments(GenericArguments.playerOrSource(Text.of("sniper")),
                                GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("args"))))
                        .executor(new VoxelListCommand()).permission(VoxelSniperConfiguration.PERMISSION_SNIPER)
                        .description(Text.of("VoxelSniper material list selection")).build(),
                "vl");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext gargs) throws CommandException {
        Player player = (Player) gargs.getOne("sniper").get();
        Sniper sniper = SniperManager.get().getSniperForPlayer(player);
        Optional<String> oargs = gargs.getOne("args");
        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
        if (!oargs.isPresent()) {
            Location<World> targetBlock = null;
            BlockRayBuilder<World> rayBuilder = BlockRay.from(player).stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1));
            BlockRay<World> ray = rayBuilder.build();
            while (ray.hasNext()) {
                targetBlock = ray.next().getLocation();
            }
            snipeData.getVoxelList().add(targetBlock.getBlock());
            snipeData.getVoxelMessage().voxelList();
            return CommandResult.success();
        }
        String[] args = oargs.get().split(" ");
        if (args[0].equalsIgnoreCase("clear")) {
            snipeData.getVoxelList().clear();
            snipeData.getVoxelMessage().voxelList();
            return CommandResult.success();
        }

        for (String arg : args) {
            boolean remove = arg.startsWith("-");
            if (remove) {
                arg = arg.substring(1);
            }
            Optional<BlockType> type = Sponge.getRegistry().getType(BlockType.class, arg);
            if (type.isPresent()) {
                if (remove) {
                    snipeData.getVoxelList().remove(type.get());
                } else {
                    snipeData.getVoxelList().add(type.get());
                }
            } else {
                Optional<BlockState> state = Sponge.getRegistry().getType(BlockState.class, arg);
                if (state.isPresent()) {
                    if (remove) {
                        snipeData.getVoxelList().remove(state.get());
                    } else {
                        snipeData.getVoxelList().add(state.get());
                    }
                } else {
                    player.sendMessage(Text.of(TextColors.RED, "Material not found."));
                }
            }
        }
        snipeData.getVoxelMessage().voxelList();
        return CommandResult.success();
    }
}
