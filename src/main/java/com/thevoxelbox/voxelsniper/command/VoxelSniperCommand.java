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

import java.util.Optional;

public class VoxelSniperCommand implements CommandExecutor {

    public static void setup(Object plugin) {
        Sponge.getCommandManager().register(plugin,
                CommandSpec.builder()
                        .arguments(GenericArguments.playerOrSource(Text.of("sniper")),
                                GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("args"))))
                        .executor(new VoxelBrushCommand()).permission(VoxelSniperConfiguration.PERMISSION_SNIPER)
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
                player.sendMessage(Text.of(Brushes.get().getAllBrushes()));
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
}
