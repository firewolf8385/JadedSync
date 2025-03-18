package net.jadedmc.jadedsync;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.jadedmc.jadedsync.database.Redis;
import net.jadedmc.jadedsync.listeners.DisconnectListener;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

@Plugin(
        id = "jadedsync",
        name = "JadedSync",
        version = "1.0",
        url = "https://www.jadedmc.net"
)
public class JadedSyncVelocityPlugin {
    private final Redis redis;
    private YamlDocument config;
    private final Logger logger;
    private final ProxyServer proxyServer;

    @Inject
    public JadedSyncVelocityPlugin(final ProxyServer proxyServer, final Logger logger, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.logger = logger;

        // Load the configuration file.
        try {
            config = YamlDocument.create(new File(dataDirectory.toFile(), "config.yml"),
                    Objects.requireNonNull(getClass().getResourceAsStream("/config.yml")),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS).build());
            config.update();
            config.save();
        }
        catch(IOException exception) {
            exception.printStackTrace();
        }

        // Connect to redis.
        redis = new Redis(this);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Register events.
        proxyServer.getEventManager().register(this, new DisconnectListener(this));
    }

    public YamlDocument getConfig() {
        return config;
    }

    public Redis getRedis() {
        return redis;
    }
}