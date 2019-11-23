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
import com.thevoxelbox.voxelsniper.brush.Brush;
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

public class VoxelPerformerCommand implements CommandExecutor {

    public static void setup(Object plugin) {
        Sponge.getCommandManager().register(plugin,
                CommandSpec.builder().arguments(GenericArguments.playerOrSource(Text.of("sniper")), GenericArguments.string(Text.of("performer")))
                        .executor(new VoxelPerformerCommand()).permission(VoxelSniperConfiguration.PERMISSION_SNIPER)
                        .description(usageText()).build(),
                "p");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext gargs) throws CommandException {
        Player player = gargs.<Player>getOne("sniper").get();
        Sniper sniper = SniperManager.get().getSniperForPlayer(player);
        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());

        String performer = (String) gargs.getOne("performer").get();

        Brush brush = sniper.getBrush(sniper.getCurrentToolId());
        if (brush instanceof PerformBrush) {
            PerformBrush pBrush = (PerformBrush) brush;
            pBrush.parse(new String[] { performer }, snipeData);
        } else {
            player.sendMessage(Text.of(TextColors.RED, "This brush is not a performer brush."));
        }
        return CommandResult.success();
    }

    private static Text usageText() {
        return Text.of(
                TextColors.RED,
                "Performer takes in a string of up to three characters.  The first\n" +
                        "is required and describes the placement method.  The second describes\n" +
                        "the replacement method and is optional.  The third letter must be a p\n" +
                        "and if present means that physics will not be applied when placing\n" +
                        "blocks.  The either two must be one of: \n\n" +
                        " - m for material.  Placing/replacing is solely done based on the base\n" +
                        "   block you have selected.\n\n" +
                        " - i for ink.  Blocks in the affected area will only have their properties\n" +
                        "   changed when placing or will only be replace if their properties match\n" +
                        "   the current ink\n\n" +
                        " - c for combo.  As the name suggests, placement and replacement will use\n" +
                        "   both the block type and ink values for placing/replacing."
        );
    }
}
