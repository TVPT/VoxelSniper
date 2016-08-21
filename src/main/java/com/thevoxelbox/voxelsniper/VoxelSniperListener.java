package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.util.SniperStats;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

/**
 * VoxelSniper event listeners.
 */
public class VoxelSniperListener {

    public VoxelSniperListener() {
        SniperStats.setSnipeCounterInitTimeStamp(System.currentTimeMillis());
    }

    @Listener
    public final void onPlayerInteract(InteractBlockEvent.Secondary.MainHand event, @Root Player player) {

        if (!player.hasPermission(VoxelSniperConfiguration.PERMISSION_SNIPER)) {
            return;
        }

        Sniper sniper = SniperManager.get().getSniperForPlayer(player);
        if (sniper.isEnabled() && sniper.snipe(InteractionType.SECONDARY_MAINHAND, player.getItemInHand(HandTypes.MAIN_HAND).orElse(null),
                event.getTargetBlock().getLocation().orElse(null), event.getTargetSide())) {
            SniperStats.increaseSnipeCounter();
            event.setCancelled(true);
        }
    }

    @Listener
    public final void onPlayerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        Sniper sniper = SniperManager.get().getSniperForPlayer(player);

        if (VoxelSniperConfiguration.LOGIN_MESSAGE_ENABLED && player.hasPermission(VoxelSniperConfiguration.PERMISSION_SNIPER)) {
            sniper.displayInfo();
        }
    }
}
