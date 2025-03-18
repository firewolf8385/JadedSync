package net.jadedmc.jadedsync.database;

import net.jadedmc.jadedsync.JadedSyncVelocityPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Manages the connection process to Redis.
 */
public class Redis {
    private final JadedSyncVelocityPlugin plugin;
    private final JedisPool jedisPool;

    /**
     * Connects to Redis.
     * @param plugin Instance of the plugin.
     */
    public Redis(final JadedSyncVelocityPlugin plugin) {
        this.plugin = plugin;

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(Integer.MAX_VALUE);

        String host = plugin.getConfig().getString("Redis.host");
        int port = plugin.getConfig().getInt("Redis.port");
        String username = plugin.getConfig().getString("Redis.username");
        String password = plugin.getConfig().getString("Redis.password");

        jedisPool = new JedisPool(jedisPoolConfig, host, port, username, password);
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
}