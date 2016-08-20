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

public class VoxelInkReplaceCommand implements CommandExecutor {

    public static void setup(Object plugin) {
        Sponge.getCommandManager().register(plugin, CommandSpec.builder()
                .arguments(GenericArguments.playerOrSource(Text.of("sniper")), GenericArguments.string(Text.of("key")),
                        GenericArguments.literal(Text.of("equals"), "="), GenericArguments.string(Text.of("value")))
                .executor(new VoxelBrushCommand()).permission(VoxelSniperConfiguration.PERMISSION_SNIPER)
                .description(Text.of("VoxelSniper Ink selection")).build(), "vir");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext gargs) throws CommandException {
        Player player = (Player) gargs.getOne("sniper").get();
        Sniper sniper = SniperManager.get().getSniperForPlayer(player);

        String key = (String) gargs.getOne("key").get();
        String value = (String) gargs.getOne("value").get();

        // @Spongify turn these into a key and value for block state
//        if (args.length == 0)
//        {
//            Block targetBlock = new RangeBlockHelper(player, player.getWorld()).getTargetBlock();
//            if (targetBlock != null)
//            {
//                dataValue = targetBlock.getData();
//            }
//            else
//            {
//                return true;
//            }
//        }
//        else
//        {
//            try
//            {
//                dataValue = Byte.parseByte(args[0]);
//            }
//            catch (NumberFormatException exception)
//            {
//                player.sendMessage("Couldn't parse input.");
//                return true;
//            }
//        }
//
//        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
//        snipeData.setReplaceData(dataValue);
//        snipeData.getVoxelMessage().replaceData();
        return CommandResult.success();
    }
}
