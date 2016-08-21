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
                        .executor(new VoxelBrushCommand()).permission(VoxelSniperConfiguration.PERMISSION_SNIPER)
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
            BlockRayBuilder<World> rayBuilder = BlockRay.from(player).filter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1));
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
