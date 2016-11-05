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

public class VoxelReplaceCommand implements CommandExecutor {

    public static void setup(Object plugin) {
        Sponge.getCommandManager().register(plugin,
                CommandSpec.builder()
                        .arguments(GenericArguments.playerOrSource(Text.of("sniper")),
                                GenericArguments.optional(GenericArguments.string(Text.of("material"))))
                        .executor(new VoxelReplaceCommand()).permission(VoxelSniperConfiguration.PERMISSION_SNIPER)
                        .description(Text.of("VoxelSniper replace material selection")).build(),
                "vr");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext gargs) throws CommandException {
        Player player = (Player) gargs.getOne("sniper").get();
        Sniper sniper = SniperManager.get().getSniperForPlayer(player);
        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
        Optional<String> material = gargs.getOne("material");
        if (!material.isPresent()) {
            Location<World> targetBlock = null;
            BlockRayBuilder<World> rayBuilder = BlockRay.from(player).stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1));
            BlockRay<World> ray = rayBuilder.build();
            while (ray.hasNext()) {
                targetBlock = ray.next().getLocation();
            }
            snipeData.setReplaceId(targetBlock.getBlock());
            snipeData.getVoxelMessage().replace();
            return CommandResult.success();
        }
        String materialName = material.get();
        if (!materialName.contains(":")) {
            materialName = "minecraft:" + materialName;
        }
        Optional<BlockType> type = Sponge.getRegistry().getType(BlockType.class, materialName);
        if (type.isPresent()) {
            snipeData.setReplaceId(type.get().getDefaultState());
            snipeData.getVoxelMessage().replace();
        } else {
            Optional<BlockState> state = Sponge.getRegistry().getType(BlockState.class, materialName);
            if (state.isPresent()) {
                snipeData.setReplaceId(state.get());
                snipeData.getVoxelMessage().replace();
            } else {
                player.sendMessage(Text.of(TextColors.RED, "Material not found."));
            }
        }
        return CommandResult.success();
    }
}
