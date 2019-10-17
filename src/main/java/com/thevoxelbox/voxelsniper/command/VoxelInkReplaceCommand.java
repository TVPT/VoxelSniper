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
import com.thevoxelbox.voxelsniper.util.BlockTraitHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.trait.BlockTrait;
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

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class VoxelInkReplaceCommand implements CommandExecutor {

    public static void setup(Object plugin) {
        Sponge.getCommandManager().register(plugin, CommandSpec.builder()
                .arguments(GenericArguments.playerOrSource(Text.of("sniper")),
                        GenericArguments.allOf(
                                GenericArguments.string(Text.of("key=value"))
                        ))
                .executor(new VoxelInkReplaceCommand()).permission(VoxelSniperConfiguration.PERMISSION_SNIPER)
                .description(Text.of("VoxelSniper Replace Ink selection")).build(), "vir");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext gargs) throws CommandException {
        Player player = (Player) gargs.getOne("sniper").get();
        Sniper sniper = SniperManager.get().getSniperForPlayer(player);
        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());

        Collection<String> keyValues = gargs.getAll("key=value");
        if (keyValues.size() == 0) {
            src.sendMessage(usageText());
            return CommandResult.success();
        }

        Optional<Map<BlockTrait<?>, Object>> optInkTraits =
                BlockTraitHelper.parseKeyValues(keyValues, snipeData.getReplaceState(), src);
        if (optInkTraits.isPresent()) {
            snipeData.setReplaceInkTraits(optInkTraits.get());
            snipeData.getVoxelMessage().replace();
        }
        return CommandResult.success();
    }

    private static Text usageText() {
        return Text.of(
                TextColors.RED,
                "Voxel replace ink selection\n" +
                        "Pass one or more block property in the form key=value separated by a space. " +
                        "These properties will used to check blocks when replacing them. E.g, passing " +
                        "color=blue will only replace blocks with blue color attribute.  To use the " +
                        "replace ink make sure you set the replace method to ink or combo."
        );
    }
}
