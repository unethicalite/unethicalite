package net.unethicalite.api.movement.pathfinder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Point;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.GameThread;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.game.Vars;
import net.unethicalite.api.game.Worlds;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.pathfinder.model.FairyRingLocation;
import net.unethicalite.api.movement.pathfinder.model.Transport;
import net.unethicalite.api.movement.pathfinder.model.dto.TransportDto;
import net.unethicalite.api.movement.pathfinder.model.requirement.Requirements;
import net.unethicalite.api.quests.Quests;
import net.unethicalite.api.widgets.Dialog;
import net.unethicalite.api.widgets.Widgets;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static net.unethicalite.api.movement.pathfinder.model.MovementConstants.MUSHTREES;
import static net.unethicalite.api.movement.pathfinder.model.MovementConstants.SPIRIT_TREES;

@Slf4j
public class TransportLoader
{
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static List<Transport> ALL_STATIC_TRANSPORTS = new ArrayList<>();
	private static List<Transport> LAST_TRANSPORT_LIST = new ArrayList<>();

	public static void init()
	{
		log.info("Loading transports");
		try (InputStream stream = Walker.class.getResourceAsStream("/transports.json"))
		{
			if (stream == null)
			{
				log.error("Failed to load transports.");
				return;
			}

			TransportDto[] json = GSON.fromJson(new String(stream.readAllBytes()), TransportDto[].class);

			List<Transport> list = Arrays.stream(json)
					.map(TransportDto::toTransport)
					.collect(Collectors.toList());
			ALL_STATIC_TRANSPORTS.addAll(list);
		}
		catch (IOException e)
		{
			log.error("Failed to load transports.", e);
		}

		log.info("Loaded {} transports", ALL_STATIC_TRANSPORTS.size());
	}

	public static List<Transport> buildTransports()
	{
		return LAST_TRANSPORT_LIST;
	}

	public static void refreshTransports()
	{
		GameThread.invoke(() ->
		{
			List<Transport> filteredStatic = ALL_STATIC_TRANSPORTS.stream()
					.filter(it -> it.getRequirements().fulfilled())
					.collect(Collectors.toList());

			List<Transport> transports = new ArrayList<>();

			boolean princeAliCompleted = Quests.isFinished(Quest.PRINCE_ALI_RESCUE);
			int gold = Inventory.getFirst(995) != null ? Inventory.getFirst(995).getQuantity() : 0;
			if (gold >= 10 || princeAliCompleted)
			{
				// The door here is weird, the transform actions and name return null
				transports.add(objectTransport(
						new WorldPoint(3267, 3228, 0),
						new WorldPoint(3268, 3228, 0),
						TileObjects.getFirstAt(3268, 3228, 0, 44599),
						princeAliCompleted ? 0 : 3)
				);
				transports.add(objectTransport(
						new WorldPoint(3268, 3228, 0),
						new WorldPoint(3267, 3228, 0),
						TileObjects.getFirstAt(3268, 3228, 0, 44599),
						princeAliCompleted ? 0 : 3)
				);
				transports.add(objectTransport(
						new WorldPoint(3267, 3227, 0),
						new WorldPoint(3268, 3227, 0),
						TileObjects.getFirstAt(3268, 3227, 0, 44598),
						princeAliCompleted ? 0 : 3)
				);
				transports.add(objectTransport(
						new WorldPoint(3268, 3227, 0),
						new WorldPoint(3267, 3227, 0),
						TileObjects.getFirstAt(3268, 3227, 0, 44598),
						princeAliCompleted ? 0 : 3)
				);
			}

			if (Worlds.inMembersWorld())
			{
				boolean ringOfCharos = Equipment.contains(ItemID.RING_OF_CHAROS, ItemID.RING_OF_CHAROSA);

				//morytania
				if (Quests.isFinished(Quest.IN_SEARCH_OF_THE_MYREQUE) && (ringOfCharos || gold >= 10))
				{
					transports.add(objectTransport(new WorldPoint(3522, 3285, 0), new WorldPoint(3498, 3380, 0), 6969,
							"Quick-board"));
				}
				transports.add(objectTransport(new WorldPoint(3498, 3380, 0), new WorldPoint(3522, 3285, 0), 6970,
						"Board"));

				//Shamans
				transports.add(objectTransport(new WorldPoint(1312, 3685, 0), new WorldPoint(1312, 10086, 0), 34405, "Enter"));
				/**
				 * Doors for shamans
				 */
				transports.add(objectTransport(new WorldPoint(1293, 10090, 0), new WorldPoint(1293, 10093, 0), 34642, "Pass"));
				transports.add(objectTransport(new WorldPoint(1293, 10093, 0), new WorldPoint(1293, 10091, 0), 34642, "Pass"));
				transports.add(objectTransport(new WorldPoint(1296, 10096, 0), new WorldPoint(1298, 10096, 0), 34642, "Pass"));
				transports.add(objectTransport(new WorldPoint(1298, 10096, 0), new WorldPoint(1296, 10096, 0), 34642, "Pass"));
				transports.add(objectTransport(new WorldPoint(1307, 10096, 0), new WorldPoint(1309, 10096, 0), 34642, "Pass"));
				transports.add(objectTransport(new WorldPoint(1309, 10096, 0), new WorldPoint(1307, 10096, 0), 34642, "Pass"));
				transports.add(objectTransport(new WorldPoint(1316, 10096, 0), new WorldPoint(1318, 10096, 0), 34642, "Pass"));
				transports.add(objectTransport(new WorldPoint(1318, 10096, 0), new WorldPoint(1316, 10096, 0), 34642, "Pass"));
				transports.add(objectTransport(new WorldPoint(1324, 10096, 0), new WorldPoint(1326, 10096, 0), 34642, "Pass"));
				transports.add(objectTransport(new WorldPoint(1326, 10096, 0), new WorldPoint(1324, 10096, 0), 34642, "Pass"));

				// Crabclaw island
				if (gold >= 10_000)
				{
					transports.add(npcTransport(new WorldPoint(1782, 3458, 0), new WorldPoint(1778, 3417, 0), 7483, "Travel"));
				}

				transports.add(npcTransport(new WorldPoint(1779, 3418, 0), new WorldPoint(1784, 3458, 0), 7484, "Travel"));

				// Port sarim
				if (Vars.getBit(4897) == 0)
				{
					if (Vars.getBit(8063) >= 7)
					{
						transports.add(npcDialogTransport(new WorldPoint(3054, 3245, 0),
								new WorldPoint(1824, 3691, 0),
								8484,
								"Can you take me to Great Kourend?"));
					}
					else
					{
						transports.add(npcDialogTransport(new WorldPoint(3054, 3245, 0),
								new WorldPoint(1824, 3691, 0),
								8484,
								"That's great, can you take me there please?"));
					}
				}
				else
				{
					transports.add(npcTransport(new WorldPoint(3054, 3245, 0),
							new WorldPoint(1824, 3695, 1),
							"Veos",
							"Port Piscarilius"));
				}

				if (Quests.getState(Quest.LUNAR_DIPLOMACY) != QuestState.NOT_STARTED)
				{
					transports.add(npcTransport(new WorldPoint(2222, 3796, 2), new WorldPoint(2130, 3899, 2), NpcID.CAPTAIN_BENTLEY_6650, "Travel"));
					transports.add(npcTransport(new WorldPoint(2130, 3899, 2), new WorldPoint(2222, 3796, 2), NpcID.CAPTAIN_BENTLEY_6650, "Travel"));
				}

				// Spirit Trees
				if (Quests.isFinished(Quest.TREE_GNOME_VILLAGE))
				{
					for (var source : SPIRIT_TREES)
					{
						if (source.location.equals("Gnome Stronghold") && !Quests.isFinished(Quest.THE_GRAND_TREE))
						{
							continue;
						}
						for (var target : SPIRIT_TREES)
						{
							if (source == target)
							{
								continue;
							}

							transports.add(spritTreeTransport(source.position, target.position, target.location));
						}
					}
				}

				if (Quests.isFinished(Quest.THE_LOST_TRIBE))
				{
					transports.add(npcTransport(new WorldPoint(3229, 9610, 0), new WorldPoint(3316, 9613, 0), NpcID.KAZGAR_7301, "Mines"));
					transports.add(npcTransport(new WorldPoint(3316, 9613, 0), new WorldPoint(3229, 9610, 0), NpcID.MISTAG_7299, "Cellar"));
				}

				// Tree Gnome Village
				if (Quests.getState(Quest.TREE_GNOME_VILLAGE) != QuestState.NOT_STARTED)
				{
					transports.add(npcTransport(new WorldPoint(2504, 3192, 0), new WorldPoint(2515, 3159, 0), 4968, "Follow"));
					transports.add(npcTransport(new WorldPoint(2515, 3159, 0), new WorldPoint(2504, 3192, 0), 4968, "Follow"));
				}

				// Eagles peak cave
				if (Vars.getVarp(934) >= 15)
				{
					// Entrance
					transports.add(objectTransport(new WorldPoint(2328, 3496, 0), new WorldPoint(1994, 4983, 3), 19790,
							"Enter"));
					transports.add(objectTransport(new WorldPoint(1994, 4983, 3), new WorldPoint(2328, 3496, 0), 19891,
							"Exit"));
				}

				// Waterbirth island
				if (Quests.isFinished(Quest.THE_FREMENNIK_TRIALS) || gold >= 1000)
				{
					transports.add(npcTransport(new WorldPoint(2544, 3760, 0), new WorldPoint(2620, 3682, 0), 10407, "Rellekka"));
					transports.add(npcTransport(new WorldPoint(2620, 3682, 0), new WorldPoint(2547, 3759, 0), 5937, "Waterbirth Island"));
				}

				// Pirates cove
				transports.add(npcTransport(new WorldPoint(2620, 3692, 0), new WorldPoint(2213, 3794, 0), NpcID.LOKAR_SEARUNNER, "Pirate's Cove"));
				transports.add(npcTransport(new WorldPoint(2213, 3794, 0), new WorldPoint(2620, 3692, 0), NpcID.LOKAR_SEARUNNER_9306, "Rellekka"));

				// Corsair's Cove
				if (Skills.getBoostedLevel(Skill.AGILITY) >= 10)
				{
					transports.add(objectTransport(new WorldPoint(2546, 2871, 0), new WorldPoint(2546, 2873, 0), 31757,
							"Climb"));
					transports.add(objectTransport(new WorldPoint(2546, 2873, 0), new WorldPoint(2546, 2871, 0), 31757,
							"Climb"));
				}

				// Lumbridge castle dining room, ignore if RFD is in progress.
				if (Quests.getState(Quest.RECIPE_FOR_DISASTER) != QuestState.IN_PROGRESS)
				{
					transports.add(objectTransport(new WorldPoint(3213, 3221, 0), new WorldPoint(3212, 3221, 0), 12349, "Open"));
					transports.add(objectTransport(new WorldPoint(3212, 3221, 0), new WorldPoint(3213, 3221, 0), 12349, "Open"));
					transports.add(objectTransport(new WorldPoint(3213, 3222, 0), new WorldPoint(3212, 3222, 0), 12350, "Open"));
					transports.add(objectTransport(new WorldPoint(3212, 3222, 0), new WorldPoint(3213, 3222, 0), 12350, "Open"));
					transports.add(objectTransport(new WorldPoint(3207, 3218, 0), new WorldPoint(3207, 3217, 0), 12348, "Open"));
					transports.add(objectTransport(new WorldPoint(3207, 3217, 0), new WorldPoint(3207, 3218, 0), 12348, "Open"));
				}

				// Digsite gate
				if (Vars.getBit(3637) >= 153)
				{
					transports.add(objectTransport(new WorldPoint(3295, 3429, 0), new WorldPoint(3296, 3429, 0), 24561,
							"Open"));
					transports.add(objectTransport(new WorldPoint(3296, 3429, 0), new WorldPoint(3295, 3429, 0), 24561,
							"Open"));
					transports.add(objectTransport(new WorldPoint(3295, 3428, 0), new WorldPoint(3296, 3428, 0), 24561,
							"Open"));
					transports.add(objectTransport(new WorldPoint(3296, 3428, 0), new WorldPoint(3295, 3428, 0), 24561,
							"Open"));
				}

				// Fairy Rings
				if (Equipment.contains(ItemID.DRAMEN_STAFF, ItemID.LUNAR_STAFF)
						&& Quests.getState(Quest.FAIRYTALE_II__CURE_A_QUEEN) != QuestState.NOT_STARTED)
				{
					for (FairyRingLocation sourceRing : FairyRingLocation.values())
					{
						for (FairyRingLocation destRing : FairyRingLocation.values())
						{
							if (sourceRing != destRing)
							{
								transports.add(fairyRingTransport(sourceRing, destRing));
							}
						}
					}
				}
			}

			// Entrana
			transports.add(npcTransport(new WorldPoint(3041, 3237, 0), new WorldPoint(2834, 3331, 1), 1166, "Take-boat"));
			transports.add(npcTransport(new WorldPoint(2834, 3335, 0), new WorldPoint(3048, 3231, 1), 1170, "Take-boat"));
			transports.add(npcDialogTransport(new WorldPoint(2821, 3374, 0),
					new WorldPoint(2822, 9774, 0),
					1164,
					"Well that is a risk I will have to take."));

			// Fossil Island
			transports.add(npcTransport(new WorldPoint(3362, 3445, 0),
					new WorldPoint(3724, 3808, 0),
					8012,
					"Quick-Travel"));

			transports.add(objectDialogTransport(new WorldPoint(3724, 3808, 0),
					new WorldPoint(3362, 3445, 0),
					30914,
					new String[]{"Travel"},
					"Row to the barge and travel to the Digsite."));

			// Magic Mushtrees
			for (var source : MUSHTREES)
			{
				for (var target : MUSHTREES)
				{
					if (source.position != target.position)
					{
						transports.add(mushtreeTransport(source.position, target.position, target.widget));
					}
				}
			}

			// Gnome stronghold
			transports.add(objectDialogTransport(new WorldPoint(2461, 3382, 0),
					new WorldPoint(2461, 3385, 0),
					190,
					new String[]{"Open"},
					"Sorry, I'm a bit busy."));

			// Paterdomus
			transports.add(trapDoorTransport(new WorldPoint(3405, 3506, 0), new WorldPoint(3405, 9906, 0), 1579, 1581));
			transports.add(trapDoorTransport(new WorldPoint(3423, 3485, 0), new WorldPoint(3440, 9887, 0), 3432, 3433));
			transports.add(trapDoorTransport(new WorldPoint(3422, 3484, 0), new WorldPoint(3440, 9887, 0), 3432, 3433));

			// Port Piscarilius
			transports.add(npcTransport(new WorldPoint(1824, 3691, 0), new WorldPoint(3055, 3242, 1), 10727, "Port Sarim"));

			// Glarial's tomb
			transports.add(itemUseTransport(new WorldPoint(2557, 3444, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
			transports.add(itemUseTransport(new WorldPoint(2557, 3445, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
			transports.add(itemUseTransport(new WorldPoint(2558, 3443, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
			transports.add(itemUseTransport(new WorldPoint(2559, 3443, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
			transports.add(itemUseTransport(new WorldPoint(2560, 3444, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
			transports.add(itemUseTransport(new WorldPoint(2560, 3445, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
			transports.add(itemUseTransport(new WorldPoint(2558, 3446, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
			transports.add(itemUseTransport(new WorldPoint(2559, 3446, 0), new WorldPoint(2555, 9844, 0), 294, 1992));

			// Waterfall Island
			transports.add(itemUseTransport(new WorldPoint(2512, 3476, 0), new WorldPoint(2513, 3468, 0), 954, 1996));
			transports.add(itemUseTransport(new WorldPoint(2512, 3466, 0), new WorldPoint(2511, 3463, 0), 954, 2020));

			LAST_TRANSPORT_LIST.clear();
			LAST_TRANSPORT_LIST.addAll(filteredStatic);
			LAST_TRANSPORT_LIST.addAll(transports);
		});
	}

	public static Transport trapDoorTransport(
			WorldPoint source,
			WorldPoint destination,
			int closedId,
			int openedId
	)
	{
		return new Transport(source, destination, Integer.MAX_VALUE, 0, () ->
		{
			TileObject openedTrapdoor = TileObjects.getFirstSurrounding(source, 5, openedId);
			if (openedTrapdoor != null)
			{
				openedTrapdoor.interact(0);
				return;
			}

			TileObject closedTrapDoor = TileObjects.getFirstSurrounding(source, 5, closedId);
			if (closedTrapDoor != null)
			{
				closedTrapDoor.interact(0);
			}
		});
	}

	public static Transport fairyRingTransport(
			FairyRingLocation source,
			FairyRingLocation destination
	)
	{
		return new Transport(source.getLocation(), destination.getLocation(), Integer.MAX_VALUE, 0, () ->
		{
			log.debug("Looking for fairy ring at {} to {}", source.getLocation(), destination.getLocation());
			TileObject ring = TileObjects.getFirstSurrounding(source.getLocation(), 5, "Fairy ring");

			if (ring == null)
			{
				log.debug("Fairy ring at {} is null", source.getLocation());
				return;
			}

			if (destination == FairyRingLocation.ZANARIS)
			{
				ring.interact("Zanaris");
				return;
			}

			if (ring.hasAction(a -> a != null && a.contains(destination.getCode())))
			{
				ring.interact(a -> a != null && a.contains(destination.getCode()));
				return;
			}

			if (Widgets.isVisible(Widgets.get(WidgetInfo.FAIRY_RING)))
			{
				destination.travel();
				return;
			}

			ring.interact("Configure");
		});
	}

	public static Transport itemUseTransport(
			WorldPoint source,
			WorldPoint destination,
			int itemId,
			int objId
	)
	{
		return new Transport(source, destination, Integer.MAX_VALUE, 0, () ->
		{
			Item item = Inventory.getFirst(itemId);
			if (item == null)
			{
				return;
			}

			TileObject transport = TileObjects.getFirstSurrounding(source, 5, objId);
			if (transport != null)
			{
				item.useOn(transport);
			}
		});
	}

	public static Transport npcTransport(
			WorldPoint source,
			WorldPoint destination,
			int npcId,
			String... actions
	)
	{
		return new Transport(source, destination, 10, 0, () ->
		{
			NPC npc = NPCs.getNearest(x -> x.getWorldLocation().distanceTo(source) <= 10 && x.getId() == npcId);
			if (npc != null)
			{
				npc.interact(actions);
			}
		});
	}

	public static Transport npcTransport(
			WorldPoint source,
			WorldPoint destination,
			String npcName,
			String... actions
	)
	{
		return new Transport(source, destination, 10, 0, () ->
		{
			NPC npc = NPCs.getNearest(x -> x.getWorldLocation().distanceTo(source) <= 10 && x.getName().equalsIgnoreCase(npcName));
			if (npc != null)
			{
				npc.interact(actions);
			}
		});
	}

	public static Transport npcDialogTransport(
			WorldPoint source,
			WorldPoint destination,
			int npcId,
			String... chatOptions
	)
	{
		return new Transport(source, destination, 10, 0, () ->
		{
			if (Dialog.isViewingOptions())
			{
				if (Dialog.canContinue())
				{
					Dialog.continueSpace();
					return;
				}

				if (Dialog.chooseOption(chatOptions))
				{
					return;
				}

				return;
			}

			NPC npc = NPCs.getNearest(x -> x.getWorldLocation().distanceTo(source) <= 10 && x.getId() == npcId);
			if (npc != null)
			{
				npc.interact(0);
			}
		});
	}

	public static Transport objectTransport(
			WorldPoint source,
			WorldPoint destination,
			int objId,
			String actions
	)
	{
		return new Transport(source, destination, Integer.MAX_VALUE, 0, () ->
		{
			TileObject first = TileObjects.getFirstAt(source, objId);
			if (first != null)
			{
				first.interact(actions);
				return;
			}

			TileObjects.getSurrounding(source, 5, x -> x.getId() == objId).stream()
					.min(Comparator.comparingInt(o -> o.distanceTo(source)))
					.ifPresent(obj -> obj.interact(actions));
		});
	}

	public static Transport objectTransport(
			WorldPoint source,
			WorldPoint destination,
			int objId,
			String actions,
			Requirements requirements
	)
	{
		return new Transport(source, destination, Integer.MAX_VALUE, 0, () ->
		{
			TileObject first = TileObjects.getFirstAt(source, objId);
			if (first != null)
			{
				log.debug("Transport found {}", first.getWorldLocation());
				first.interact(actions);
				return;
			}

			log.debug("Transport not found {}, {}", source, objId);
			TileObjects.getSurrounding(source, 5, x -> x.getId() == objId).stream()
					.min(Comparator.comparingInt(o -> o.distanceTo(source)))
					.ifPresent(obj -> obj.interact(actions));
		}, requirements);
	}

	public static Transport objectTransport(
			WorldPoint source,
			WorldPoint destination,
			TileObject tileObject,
			int actionIndex
	)
	{
		return new Transport(source, destination, Integer.MAX_VALUE, 0, () ->
		{
			if (tileObject == null)
			{
				return;
			}

			tileObject.interact(actionIndex);
		});
	}

	public static Transport objectDialogTransport(
			WorldPoint source,
			WorldPoint destination,
			int objId,
			String[] actions,
			String... chatOptions
	)
	{
		return new Transport(source, destination, Integer.MAX_VALUE, 0, () ->
		{
			if (Dialog.isViewingOptions())
			{
				if (Dialog.canContinue())
				{
					Dialog.continueSpace();
					return;
				}

				if (Dialog.chooseOption(chatOptions))
				{
					return;
				}

				return;
			}

			TileObject transport = TileObjects.getFirstSurrounding(source, 5, objId);
			if (transport != null)
			{
				transport.interact(actions);
			}
		});
	}

	private static Transport spritTreeTransport(WorldPoint source, WorldPoint target, String location)
	{
		return new Transport(
				source,
				target,
				5,
				0,
				() ->
				{
					Widget treeWidget = Widgets.get(187, 3);
					if (Widgets.isVisible(treeWidget))
					{
						Arrays.stream(treeWidget.getDynamicChildren())
								.filter(child -> child.getText().toLowerCase().contains(location.toLowerCase()))
								.findFirst()
								.ifPresent(child -> child.interact(0, MenuAction.WIDGET_CONTINUE.getId(), child.getIndex(), child.getId()));
						return;
					}

					TileObject tree = TileObjects.getFirstSurrounding(source, 5, 1293, 1294, 1295);
					if (tree != null)
					{
						final Point point = tree.menuPoint();
						tree.interact(tree.getId(), MenuAction.GAME_OBJECT_FIRST_OPTION.getId(), point.getX(), point.getY());
					}

				});
	}

	private static Transport mushtreeTransport(WorldPoint source, WorldPoint target, WidgetInfo widget)
	{
		return new Transport(
				source,
				target,
				5,
				0,
				() ->
				{
					Widget treeWidget = Widgets.get(widget);
					if (Widgets.isVisible(treeWidget))
					{
						treeWidget.interact(0, MenuAction.WIDGET_CONTINUE.getId(), treeWidget.getIndex(), treeWidget.getId());
						return;
					}

					TileObject tree = TileObjects.getFirstSurrounding(source, 5, "Magic Mushtree");
					if (tree != null)
					{
						tree.interact("Use");
					}
				});
	}

	public static class MagicMushtree
	{
		private final WorldPoint position;
		private final WidgetInfo widget;

		public MagicMushtree(WorldPoint position, WidgetInfo widget)
		{
			this.position = position;
			this.widget = widget;
		}
	}

	public static class SpiritTree
	{
		private final WorldPoint position;
		private final String location;

		public SpiritTree(WorldPoint position, String location)
		{
			this.position = position;
			this.location = location;
		}
	}
}
