package net.jadedmc.jadedsync.api.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class SyncPlayerMap extends HashMap<UUID, SyncPlayer> {

    public boolean contains(@NotNull final String username) {
        for(final SyncPlayer syncPlayer : this.values()) {
            if(syncPlayer.getName().equalsIgnoreCase(username)) {
                return true;
            }
        }

        return false;
    }

    public boolean contains(@NotNull final UUID uuid) {
        return containsKey(uuid);
    }

    public boolean contains(@NotNull final Player player) {
        return contains(player.getUniqueId());
    }

    public SyncPlayer get(@NotNull final String username) {
        for(final SyncPlayer syncPlayer : this.values()) {
            if(syncPlayer.getName().equalsIgnoreCase(username)) {
                return syncPlayer;
            }
        }

        return null;
    }
}