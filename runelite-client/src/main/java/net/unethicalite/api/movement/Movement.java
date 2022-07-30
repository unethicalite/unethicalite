package net.unethicalite.api.movement;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Locatable;
import net.runelite.api.MenuAction;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.game.Vars;
import net.unethicalite.api.movement.pathfinder.Walker;
import net.unethicalite.api.movement.pathfinder.model.BankLocation;
import net.unethicalite.api.scene.Tiles;
import net.unethicalite.api.widgets.Widgets;
import net.unethicalite.client.Static;

import java.util.Comparator;

@Slf4j
public class Movement
{
	private static final int STAMINA_VARBIT = 25;
	private static final int RUN_VARP = 173;

	public static void setDestination(int sceneX, int sceneY)
	{
		Static.getClient().setSelectedSceneTileX(sceneX);
		Static.getClient().setSelectedSceneTileY(sceneY);
		Static.getClient().setViewportWalking(true);
	}

	public static WorldPoint getDestination()
	{
		Client client = Static.getClient();
		if (client.getDestinationX() == 0 && client.getDestinationY() == 0)
		{
			return null;
		}

		return new WorldPoint(
				client.getDestinationX() + client.getBaseX(),
				client.getDestinationY() + client.getBaseY(),
				client.getPlane()
		);
	}

	public static boolean isWalking()
	{
		Player local = Players.getLocal();
		WorldPoint destination = getDestination();
		return local.isMoving()
				&& destination != null
				&& destination.distanceTo(local) > 4;
	}

	public static void walk(WorldPoint worldPoint)
	{
		Client client = Static.getClient();
		Player local = client.getLocalPlayer();
		if (local == null)
		{
			return;
		}

		WorldPoint walkPoint = worldPoint;
		Tile destinationTile = Tiles.getAt(worldPoint);
		// Check if tile is in loaded client scene
		if (destinationTile == null)
		{
			log.debug("Destination {} is not in scene", worldPoint);
			Tile nearestInScene = Tiles.getAll()
					.stream()
					.min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo(local.getWorldLocation())))
					.orElse(null);
			if (nearestInScene == null)
			{
				log.debug("Couldn't find nearest walkable tile");
				return;
			}

			walkPoint = nearestInScene.getWorldLocation();
		}

		int sceneX = walkPoint.getX() - client.getBaseX();
		int sceneY = walkPoint.getY() - client.getBaseY();
		Point canv = Perspective.localToCanvas(client, LocalPoint.fromScene(sceneX, sceneY), client.getPlane());
		int x = canv != null ? canv.getX() : -1;
		int y = canv != null ? canv.getY() : -1;

		client.interact(
				0,
				MenuAction.WALK.getId(),
				sceneX,
				sceneY,
				x,
				y
		);
	}

	public static void walk(Locatable locatable)
	{
		walk(locatable.getWorldLocation());
	}

	public static boolean walkTo(WorldPoint worldPoint)
	{
		return Walker.walkTo(worldPoint);
	}

	public static boolean walkTo(Locatable locatable)
	{
		return walkTo(locatable.getWorldLocation());
	}

	public static boolean walkTo(BankLocation bankLocation)
	{
		return walkTo(bankLocation.getArea().getCenter());
	}

	public static boolean walkTo(int x, int y)
	{
		return walkTo(x, y, Static.getClient().getPlane());
	}

	public static boolean walkTo(int x, int y, int plane)
	{
		return walkTo(new WorldPoint(x, y, plane));
	}

	public static boolean isRunEnabled()
	{
		return Vars.getVarp(RUN_VARP) == 1;
	}

	public static void toggleRun()
	{
		Widget widget = Widgets.get(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB);
		if (widget != null)
		{
			widget.interact("Toggle Run");
		}
	}

	public static boolean isStaminaBoosted()
	{
		return Vars.getBit(STAMINA_VARBIT) == 1;
	}

	public static int getRunEnergy()
	{
		return Static.getClient().getEnergy();
	}

	public static int calculateDistance(WorldPoint destination)
	{
		return Walker.calculatePath(destination).size();
	}
}
