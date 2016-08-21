package com.thevoxelbox.voxelsniper.command;

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

public class VoxelUndoUserCommand implements CommandExecutor {

    public static void setup(Object plugin) {
        Sponge.getCommandManager().register(plugin,
                CommandSpec.builder()
                        .arguments(GenericArguments.playerOrSource(Text.of("sniper")),
                                GenericArguments.player(Text.of("target")))
                        .executor(new VoxelUndoUserCommand()).permission(VoxelSniperConfiguration.PERMISSION_COMMAND_UNDO_OTHER)
                        .description(Text.of("VoxelSniper undo other user")).build(),
                "uu");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext gargs) throws CommandException {
        Player player = (Player) gargs.getOne("sniper").get();
        Player target = (Player) gargs.getOne("target").get();
        SniperManager.get().getSniperForPlayer(target).undo(1);
        player.sendMessage(Text.of(TextColors.GREEN, "One action of player " + target.getName() + " has been undone"));
        return CommandResult.success();
    }
}
