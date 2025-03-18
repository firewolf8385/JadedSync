/*
 * This file is part of JadedSync, licensed under the MIT License.
 *
 *  Copyright (c) JadedMC
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.jadedsync.api;

import net.jadedmc.jadedsync.JadedSyncBukkitPlugin;
import net.jadedmc.jadedsync.api.integration.Integration;
import net.jadedmc.jadedsync.api.player.SyncPlayer;
import net.jadedmc.jadedsync.api.player.SyncPlayerMap;
import net.jadedmc.jadedsync.database.Redis;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class JadedSyncAPI {
    private static JadedSyncBukkitPlugin plugin;

    public static void initialize(@NotNull final JadedSyncBukkitPlugin pl) {
        plugin = pl;
    }

    public static CompletableFuture<SyncPlayer> getJadedPlayer(@NotNull final Player player) {
        return CompletableFuture.supplyAsync(() -> {
            if(hasPlayer(player.getUniqueId())) {
                final Document document = Document.parse(plugin.getRedis().get("jadedsync:players:" + player.getUniqueId()));
                return new SyncPlayer(plugin, document);
            }
            else {
                return null;
            }
        });
    }

    /**
     * Check if a given player is already cached in Redis.
     * @param uuid UUID of the player to check.
     * @return true if they are in Redis, false if they are not.
     */
    public static boolean hasPlayer(@NotNull final UUID uuid) {
        return plugin.getRedis().exists("jadedsync:players:" + uuid.toString());
    }

    /**
     * Registers an Integration to be cached.
     * @param integration Integration to register.
     */
    public static void registerIntegration(@NotNull final Integration integration) {
        plugin.getIntegrationManager().registerIntegration(integration);
    }

    /**
     * Update a player's Redis cache.
     * @param player Player to update cache of.
     */
    public static void updatePlayer(@NotNull final Player player) {
        // Update the player's bSon document. Loops through all loaded integrations as well.
        final SyncPlayer syncPlayer = new SyncPlayer(plugin, player);

        // Sends that document to Redis.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getRedis().set("jadedsync:players:" + syncPlayer.getUniqueId(), syncPlayer.getDocument().toJson()));
    }

    public static Redis getRedis() {
        return plugin.getRedis();
    }

    public static SyncPlayerMap getSyncPlayers() {
        final SyncPlayerMap syncPlayers = new SyncPlayerMap();

        try(Jedis jedis = plugin.getRedis().jedisPool().getResource()) {
            final Set<String> keys = jedis.keys("jadedsync:players:*");

            for(@NotNull final String key : keys) {
                final String json = jedis.get(key);
                final Document document = Document.parse(json);
                final SyncPlayer syncPlayer = new SyncPlayer(plugin, document);
                syncPlayers.put(syncPlayer.getUniqueId(), syncPlayer);
            }
        }

        return syncPlayers;
    }

    public static Integration getIntegration(@NotNull final String id) {
        return plugin.getIntegrationManager().getIntegration(id);
    }
}