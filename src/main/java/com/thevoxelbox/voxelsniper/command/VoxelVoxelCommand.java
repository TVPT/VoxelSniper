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

public class VoxelVoxelCommand implements CommandExecutor {

    public static void setup(Object plugin) {
        Sponge.getCommandManager().register(plugin,
                CommandSpec.builder()
                        .arguments(GenericArguments.playerOrSource(Text.of("sniper")),
                                GenericArguments.optional(GenericArguments.string(Text.of("material"))))
                        .executor(new VoxelBrushCommand()).permission(VoxelSniperConfiguration.PERMISSION_SNIPER)
                        .description(Text.of("VoxelSniper material selection")).build(),
                "v");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext gargs) throws CommandException {
        Player player = (Player) gargs.getOne("sniper").get();
        Sniper sniper = SniperManager.get().getSniperForPlayer(player);
        SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
        Optional<String> material = gargs.getOne("material");
        if (!material.isPresent()) {
            Location<World> targetBlock = null;
            BlockRayBuilder<World> rayBuilder = BlockRay.from(player).filter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1));
            BlockRay<World> ray = rayBuilder.build();
            while (ray.hasNext()) {
                targetBlock = ray.next().getLocation();
            }
            snipeData.setVoxelId(targetBlock.getBlock());
            snipeData.getVoxelMessage().voxel();
            return CommandResult.success();
        }
        Optional<BlockType> type = Sponge.getRegistry().getType(BlockType.class, material.get());
        if (type.isPresent()) {
            snipeData.setVoxelId(type.get().getDefaultState());
            snipeData.getVoxelMessage().voxel();
        } else {
            Optional<BlockState> state = Sponge.getRegistry().getType(BlockState.class, material.get());
            if (state.isPresent()) {
                snipeData.setVoxelId(state.get());
                snipeData.getVoxelMessage().voxel();
            } else {
                player.sendMessage(Text.of(TextColors.RED, "Material not found."));
            }
        }
        return CommandResult.success();
    }
}
