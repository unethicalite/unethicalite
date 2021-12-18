package net.runelite.client.plugins.continueclicker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("ContinueClicker")
public interface ContinueClickerConfig extends Config
{

    @ConfigItem(
            keyName = "continueChat",
            name = "Continue all chats",
            description = "Progress through all click here to continue... chats",
            position = 0
    )
    default boolean continueChat()
    {
        return true;
    }

    @ConfigItem(
            keyName = "questHelper",
            name = "Select Quest Helper options",
            description = "Enable to auto select highlighted quest helper options",
            position = 5
    )
    default boolean questHelper()
    {
        return true;
    }
}
