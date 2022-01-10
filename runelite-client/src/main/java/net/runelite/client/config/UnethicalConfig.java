package net.runelite.client.config;

import dev.hoot.api.movement.pathfinder.poh.JewelryBox;

@ConfigGroup(UnethicalConfig.CONFIG_GROUP)
public interface UnethicalConfig extends Config {

    String CONFIG_GROUP = "unethicalite";

    @ConfigSection(
            name = "Walker",
            description = "Configuration for the walker API",
            position = 0
    )
    String walkerSection = "Walker";

    @ConfigItem(
            keyName = "hasMountedGlory",
            name = "Mounted Glory",
            description = "",
            section = walkerSection
    )
    default boolean hasMountedGlory() { return false; }

    @ConfigItem(
            keyName = "hasMountedDigsitePendant",
            name = "Mounted Digsite Pendant",
            description = "",
            section = walkerSection
    )
    default boolean hasMountedDigsitePendant() { return false; }

    @ConfigItem(
            keyName = "hasMountedMythicalCape",
            name = "Mounted Mythical Cape",
            description = "",
            section = walkerSection
    )
    default boolean hasMountedMythicalCape() { return false; }

    @ConfigItem(
            keyName = "hasMountedXericsTalisman",
            name = "Mounted Xerics Talisman",
            description = "",
            section = walkerSection
    )
    default boolean hasMountedXericsTalisman() { return false; }

    @ConfigItem(
            keyName = "hasJewelryBox",
            name = "Jewelry Box",
            description = "",
            section = walkerSection
    )
    default JewelryBox hasJewelryBox() { return JewelryBox.NONE; }

}
