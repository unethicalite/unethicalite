package net.runelite.client.plugins.continueclicker;

import com.google.inject.Provides;
import dev.hoot.api.widgets.Dialog;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@PluginDescriptor(
        name = "Continue Clicker",
        description = "Presses continue on dialogue when available",
        tags = {"continue", "chat", "dialogue", "clicker"},
        enabledByDefault = false
)
public class ContinueClickerPlugin extends Plugin
{
    @Inject
    public Client client;

    @Inject
    public ClientThread clientThread;

    @Inject
    public ContinueClickerConfig config;

    @Provides
    public ContinueClickerConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ContinueClickerConfig.class);
    }

    @Override
    public void startUp()
    {
    }

    @Override
    public void shutDown()
    {
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (config.continueChat())
        {
            if (Dialog.canContinue())
            {
                Dialog.continueSpace();
            }
        }
        if (config.questHelper())
        {
            if (Dialog.isViewingOptions())
            {
                    Dialog.chooseOption("[");
            }
        }
    }
}
