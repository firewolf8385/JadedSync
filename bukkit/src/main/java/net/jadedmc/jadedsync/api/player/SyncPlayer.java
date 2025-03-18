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
package net.jadedmc.jadedsync.api.player;

import net.jadedmc.jadedsync.JadedSyncBukkitPlugin;
import net.jadedmc.jadedsync.api.integration.Integration;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a player being synced across the network.
 * Data is stored in Redis
 */
public class SyncPlayer {
    private final JadedSyncBukkitPlugin plugin;
    private final UUID uuid;
    private final String displayName;
    private Document document;
    private boolean updated = false;

    /**
     * Creates a SyncPlayer from a bSon document.
     * @param plugin Instance of the plugin.
     * @param document bSon document representing the player.
     */
    public SyncPlayer(@NotNull final JadedSyncBukkitPlugin plugin, @NotNull final Document document) {
        this.plugin = plugin;
        this.uuid = UUID.fromString(document.getString("uuid"));
        this.displayName = document.getString("username");
        this.document = document;
    }

    /**
     * Creates a SyncPlayer from an online player.
     * @param plugin Instance of the plugin.
     * @param player Player to get the SyncPlayer of.
     */
    public SyncPlayer(@NotNull final JadedSyncBukkitPlugin plugin, @NotNull final Player player) {
        this.plugin = plugin;
        this.uuid = player.getUniqueId();
        this.displayName = player.getName();
        this.document = updateDocument();
    }

    /**
     * Converts the player into its bSon document form.
     * @return Serialized form of the SyncPlayer.
     */
    public Document getDocument() {
        return this.document;
    }

    public Document getIntegrationDocument(@NotNull final String integrationID) {
        return this.document.get("integrations", Document.class).get(integrationID, Document.class);
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    public String getName() {
        return this.displayName;
    }

    public Document updateDocument() {
        final Player player = plugin.getServer().getPlayer(uuid);

        if(player == null) {
            plugin.getServer().getLogger().severe("tried to update offline player " + this.uuid + "!");
            return null;
        }

        updated = false;
        final Document document = new Document();
        document.append("uuid", this.uuid.toString());
        document.append("username", this.displayName);

        final Document integrationsDocument = new Document();

        for(final Integration integration : plugin.getIntegrationManager().getIntegrations()) {
            integrationsDocument.append(integration.getId(), integration.getPlayerDocument(player));
        }

        document.append("integrations", integrationsDocument);
        this.document = document;
        updated = true;
        return document;
    }
}