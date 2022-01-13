package net.runelite.client.plugins.unethicalite;

import com.google.inject.Provides;
import dev.hoot.api.entities.Players;
import dev.hoot.api.game.Game;
import dev.hoot.api.movement.Movement;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Point;
import net.runelite.api.RenderOverview;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.worldmap.WorldMapOverlay;

import javax.inject.Inject;
import javax.inject.Singleton;

import static net.runelite.api.widgets.WidgetInfo.MINIMAP_WORLDMAP_OPTIONS;

@PluginDescriptor(name = "Unethicalite")
public class UnethicalPlugin extends Plugin {

    @Inject
    private WorldMapOverlay worldMapOverlay;

    private Point lastMenuOpenedPoint;
    WorldPoint mapPoint;

    @Inject
    private UnethicalConfig config;

    @Provides
    @Singleton
    UnethicalConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(UnethicalConfig.class);
    }

    private static final String DESTINATION_MENU_TARGET = "<col=00ff00>Destination";


    @Subscribe
    public void onGameTick(GameTick event) {
        if (Movement.isWalking()) {
            return;
        }

        if (mapPoint == null || mapPoint.equals(Players.getLocal().getWorldLocation())) {
            mapPoint = null;
            return;
        }

        Movement.walkTo(mapPoint);
    }

    @Subscribe
    public void onMenuOpened(MenuOpened event) { //TODO: Event doesn't work
        lastMenuOpenedPoint = Game.getClient().getMouseCanvasPosition();
    }

    //Check if menu options have already been added
    private boolean menuContainsEntries() {
        MenuEntry[] entries = Game.getClient().getMenuEntries();
        if (entries != null) {
            for (MenuEntry entry : entries) {
                if (entry == null) {
                    continue;
                }
                if (entry.getTarget().equals(DESTINATION_MENU_TARGET)) {
                    return true;
                }
            }
        }
        return false;
    }


    @Subscribe
    public void onClientTick(ClientTick event) {

        final Widget map = Game.getClient().getWidget(WidgetInfo.WORLD_MAP_VIEW);

        // If user clicks on map
        if (map != null && map.getBounds()
                .contains(Game.getClient().getMouseCanvasPosition().getX(),
                        Game.getClient().getMouseCanvasPosition().getY())) {
            if (!menuContainsEntries()) {
                addMenuEntryFront("Walk to");
                addMenuEntry("Clear");
            }
        }
        // If user clicks on globe icon on minimap
        for (var menuEntry : Game.getClient().getMenuEntries()) {
            if (menuEntry.getActionParam1() == MINIMAP_WORLDMAP_OPTIONS.getId()) {
                if (!menuContainsEntries()) {
                    addMenuEntry("Clear");
                    return;
                }
            }
            if (mapPoint != null && menuEntry.getOption().equals("Walk here")) {
                if (!menuContainsEntries()) {
                    addMenuEntry("Clear");
                    return;
                }
            }
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        final Widget map = Game.getClient().getWidget(WidgetInfo.WORLD_MAP_VIEW);

        // If user clicks on map
        if (map != null && map.getBounds()
                .contains(Game.getClient().getMouseCanvasPosition().getX(),
                        Game.getClient().getMouseCanvasPosition().getY())) {
            if (!menuContainsEntries()) {
                addMenuEntry("Walk to");
                addMenuEntry("Clear");
            }
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked e) {
        if (e.getMenuTarget().equals(DESTINATION_MENU_TARGET)) {
            if (e.getMenuOption().equals("Walk to")) {
                mapPoint = calculateMapPoint(Game.getClient().isMenuOpen() ? lastMenuOpenedPoint
                                                                           : Game.getClient().getMouseCanvasPosition());
            }

            if (e.getMenuOption().equals("Clear")) {
                mapPoint = null;
            }
        }
    }

    private WorldPoint calculateMapPoint(Point point) {
        float zoom = Game.getClient().getRenderOverview().getWorldMapZoom();
        RenderOverview renderOverview = Game.getClient().getRenderOverview();
        final WorldPoint mapPoint = new WorldPoint(renderOverview.getWorldMapPosition().getX(),
                renderOverview.getWorldMapPosition().getY(), 0);
        final Point middle = worldMapOverlay.mapWorldPointToGraphicsPoint(mapPoint);

        final int dx = (int) ((point.getX() - middle.getX()) / zoom);
        final int dy = (int) ((-(point.getY() - middle.getY())) / zoom);

        return mapPoint.dx(dx).dy(dy);
    }

    private void addMenuEntry(String option) {
        Game.getClient()
                .createMenuEntry(-1)
                .setOption(option)
                .setTarget(DESTINATION_MENU_TARGET)
                .setOpcode(MenuAction.RUNELITE.getId());
    }

    private void addMenuEntryFront(String option) {
        Game.getClient().insertMenuItem(option, DESTINATION_MENU_TARGET, MenuAction.UNKNOWN.getId(), 0, 0, 0, false);
    }


}
