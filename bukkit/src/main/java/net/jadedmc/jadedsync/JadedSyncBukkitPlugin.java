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
package net.jadedmc.jadedsync;

import net.jadedmc.jadedsync.api.JadedSyncAPI;
import net.jadedmc.jadedsync.api.integration.IntegrationManager;
import net.jadedmc.jadedsync.config.ConfigManager;
import net.jadedmc.jadedsync.config.HookManager;
import net.jadedmc.jadedsync.database.Redis;
import net.jadedmc.jadedsync.listeners.PlayerJoinListener;
import org.bukkit.plugin.java.JavaPlugin;

public class JadedSyncBukkitPlugin extends JavaPlugin {
    private ConfigManager configManager;
    private HookManager hookManager;
    private IntegrationManager integrationManager;
    private Redis redis;

    @Override
    public void onEnable() {
        JadedSyncAPI.initialize(this);

        configManager = new ConfigManager(this);
        redis = new Redis(this);
        integrationManager = new IntegrationManager();
        hookManager = new HookManager(this);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public HookManager getHookManager() {
        return this.hookManager;
    }

    public IntegrationManager getIntegrationManager() {
        return this.integrationManager;
    }

    public Redis getRedis() {
        return this.redis;
    }
}