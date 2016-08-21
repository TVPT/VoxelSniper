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

public class VoxelInkCommand implements CommandExecutor {

    public static void setup(Object plugin) {
        Sponge.getCommandManager().register(plugin, CommandSpec.builder()
                .arguments(GenericArguments.playerOrSource(Text.of("sniper")), GenericArguments.string(Text.of("key")),
                        GenericArguments.literal(Text.of("equals"), "="), GenericArguments.string(Text.of("value")))
                .executor(new VoxelInkCommand()).permission(VoxelSniperConfiguration.PERMISSION_SNIPER)
                .description(Text.of("VoxelSniper Ink selection")).build(), "vi");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext gargs) throws CommandException {
        Player player = (Player) gargs.getOne("sniper").get();
        Sniper sniper = SniperManager.get().getSniperForPlayer(player);

        String key = (String) gargs.getOne("key").get();
        String value = (String) gargs.getOne("value").get();
        
        // @Spongify turn these into a key and value for a blockstate
//        if (args.length == 0) {
//            Block targetBlock = new RangeBlockHelper(player, player.getWorld()).getTargetBlock();
//            if (targetBlock != null) {
//                dataValue = targetBlock.getData();
//            } else {
//                return true;
//            }
//        } else {
//            try {
//                dataValue = Byte.parseByte(args[0]);
//            } catch (NumberFormatException exception) {
//                player.sendMessage("Couldn't parse input.");
//                return true;
//            }
//        }
//
//        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
//        snipeData.setData(dataValue);
//        snipeData.getVoxelMessage().data();
        return CommandResult.success();
    }
}
