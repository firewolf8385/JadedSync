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
package net.jadedmc.jadedsync.database;

import net.jadedmc.jadedsync.JadedSyncBukkitPlugin;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

/**
 * Manages the connection process to Redis.
 */
public class Redis {
    private final JadedSyncBukkitPlugin plugin;
    private final JedisPool jedisPool;

    /**
     * Connects to Redis.
     * @param plugin Instance of the plugin.
     */
    public Redis(@NotNull final JadedSyncBukkitPlugin plugin) {
        this.plugin = plugin;

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(Integer.MAX_VALUE);

        String host = plugin.getConfigManager().getConfig().getString("Redis.host");
        int port = plugin.getConfigManager().getConfig().getInt("Redis.port");
        String username = plugin.getConfigManager().getConfig().getString("Redis.username");
        String password = plugin.getConfigManager().getConfig().getString("Redis.password");

        jedisPool = new JedisPool(jedisPoolConfig, host, port, username, password);

        subscribe();
    }

    public JedisPool jedisPool() {
        return jedisPool;
    }

    public void publish(String channel,  String message) {
        try(Jedis publisher = jedisPool.getResource()) {
            publisher.publish(channel, message);
        }
    }

    public void set(String key, String value) {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
        }
    }

    public void sadd(String key, String value) {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.sadd(key, value);
        }
    }

    public void del(String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    public boolean exists(@NotNull final String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        }
    }

    public String get(@NotNull final String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    public void subscribe() {
        new Thread("Redis Subscriber") {
            @Override
            public void run() {

                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String channel, String msg) {
                        }
                    }, "jadedsync");
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }.start();
    }
}