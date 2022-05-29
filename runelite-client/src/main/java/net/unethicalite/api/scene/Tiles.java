package net.unethicalite.api.scene;

import net.unethicalite.api.coords.RegionPoint;
import net.unethicalite.api.coords.ScenePoint;
import net.unethicalite.api.widgets.Widgets;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.Point;
import net.runelite.api.RenderOverview;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.unethicalite.client.Static;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class Tiles
{
	public static List<Tile> getAll(Predicate<Tile> filter)
	{
		List<Tile> out = new ArrayList<>();

		for (int x = 0; x < Constants.SCENE_SIZE; x++)
		{
			for (int y = 0; y < Constants.SCENE_SIZE; y++)
			{
				Tile tile = Static.getClient().getScene().getTiles()[Static.getClient().getPlane()][x][y];
				if (tile != null && filter.test(tile))
				{
					out.add(tile);
				}
			}
		}

		return out;
	}

	public static List<Tile> getAll()
	{
		return getAll(x -> true);
	}

	public static Tile getAt(WorldPoint worldPoint)
	{
		return getAt(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane());
	}

	public static Tile getAt(LocalPoint localPoint)
	{
		return Static.getClient().getScene().getTiles()[Static.getClient().getPlane()][localPoint.getSceneX()][localPoint.getSceneY()];
	}

	public static Tile getAt(int worldX, int worldY, int plane)
	{
		Client client = Static.getClient();
		int correctedX = worldX < Constants.SCENE_SIZE ? worldX + client.getBaseX() : worldX;
		int correctedY = worldY < Constants.SCENE_SIZE ? worldY + client.getBaseY() : worldY;

		if (!WorldPoint.isInScene(client, correctedX, correctedY))
		{
			return null;
		}

		int x = correctedX - client.getBaseX();
		int y = correctedY - client.getBaseY();

		return client.getScene().getTiles()[plane][x][y];
	}

	public static Tile getAt(RegionPoint regionPoint)
	{
		return getAt(regionPoint.toWorld());
	}

	public static Tile getAt(ScenePoint scenePoint)
	{
		return Static.getClient().getScene().getTiles()[scenePoint.getPlane()][scenePoint.getX()][scenePoint.getY()];
	}

	public static List<Tile> getSurrounding(WorldPoint worldPoint, int radius)
	{
		List<Tile> out = new ArrayList<>();
		for (int x = -radius; x <= radius; x++)
		{
			for (int y = -radius; y <= radius; y++)
			{
				out.add(getAt(worldPoint.dx(x).dx(y)));
			}
		}

		return out;
	}

	public static Tile getHoveredTile()
	{
		return Static.getClient().getSelectedSceneTile();
	}

	public static List<WorldPoint> getWorldMapTiles(int plane)
	{
		Widget worldMap = Widgets.get(WidgetInfo.WORLD_MAP_VIEW);
		if (worldMap == null)
		{
			return Collections.emptyList();
		}

		List<WorldPoint> out = new ArrayList<>();
		RenderOverview ro = Static.getClient().getRenderOverview();

		Rectangle worldMapRect = worldMap.getBounds();

		float pixelsPerTile = ro.getWorldMapZoom();
		int widthInTiles = (int) Math.ceil(worldMapRect.getWidth() / pixelsPerTile);
		int heightInTiles = (int) Math.ceil(worldMapRect.getHeight() / pixelsPerTile);

		Point worldMapPosition = ro.getWorldMapPosition();
		int leftX = worldMapPosition.getX() - (widthInTiles / 2);
		int rightX = leftX + widthInTiles;
		int topY = worldMapPosition.getY() + (heightInTiles / 2);
		int bottomY = topY - heightInTiles;

		for (int x = leftX; x < rightX; x++)
		{
			for (int y = topY; y >= bottomY; y--)
			{
				out.add(new WorldPoint(x, y, plane));
			}
		}

		return out;
	}
}
