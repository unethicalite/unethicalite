package net.runelite.client.plugins.unethicalite;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import javax.inject.Singleton;

@PluginDescriptor(name = "Unethicalite")
public class UnethicalPlugin extends Plugin {

    @Inject
    private UnethicalConfig config;

    @Provides
    @Singleton
    UnethicalConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(UnethicalConfig.class);
    }

}
