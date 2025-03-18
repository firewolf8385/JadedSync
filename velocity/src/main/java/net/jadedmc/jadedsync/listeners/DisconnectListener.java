package net.jadedmc.jadedsync.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import net.jadedmc.jadedsync.JadedSyncVelocityPlugin;

/**
 * This listens to the DisconnectEvent event, which is called every time a player leaves the server.
 */
public class DisconnectListener {
    private final JadedSyncVelocityPlugin plugin;

    /**
     * Creates the Listener.
     * @param plugin Instance of the plugin.
     */
    public DisconnectListener(final JadedSyncVelocityPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when a player disconnects from the proxy.
     * @param event DisconnectEvent.
     */
    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        Player player = event.getPlayer();
        plugin.getRedis().del("jadedsync:players:" + player.getUniqueId().toString());
    }
}