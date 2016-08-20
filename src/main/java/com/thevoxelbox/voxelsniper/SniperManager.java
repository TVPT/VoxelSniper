package com.thevoxelbox.voxelsniper;

import com.google.common.collect.Maps;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;
import java.util.UUID;

public class SniperManager {

    private static final SniperManager instance = new SniperManager();

    public static SniperManager get() {
        return instance;
    }

    private Map<UUID, Sniper> sniperInstances = Maps.newHashMap();

    private SniperManager() {

    }

    public Sniper getSniperForPlayer(Player player) {
        Sniper sniper = this.sniperInstances.get(player.getUniqueId());
        if (sniper == null) {
            this.sniperInstances.put(player.getUniqueId(), sniper = new Sniper(player));
        }
        return sniper;
    }
}
