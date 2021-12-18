package net.runelite.client.plugins.hootfighter;

import com.google.inject.Provides;
import dev.hoot.api.entities.Players;
import dev.hoot.api.entities.TileItems;
import dev.hoot.api.game.Combat;
import dev.hoot.api.game.Game;
import dev.hoot.api.items.Inventory;
import dev.hoot.api.magic.Magic;
import dev.hoot.api.movement.Movement;
import dev.hoot.api.movement.Reachable;
import dev.hoot.api.widgets.Dialog;
import dev.hoot.api.widgets.Prayers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "Hoot Fighter",
	description = "Weed",
	enabledByDefault = false
)
@Singleton
@Slf4j
public class HootFighterPlugin extends Plugin
{
	private ScheduledExecutorService executor;
	@Inject
	private HootFighterConfig config;

	@Inject
	private ItemManager itemManager;

	private WorldPoint startPoint;

	private List<TileItem> notOurItems = new ArrayList<>();

	@Override
	public void startUp()
	{
		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleWithFixedDelay(() -> {
			try
			{
				if (!Game.isLoggedIn())
				{
					return;
				}

				if (config.quickPrayer() && !Prayers.isQuickPrayerEnabled())
				{
					Prayers.toggleQuickPrayer(true);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}, 0, 100, TimeUnit.MILLISECONDS);

		if (Game.isLoggedIn())
		{
			startPoint = Players.getLocal().getWorldLocation();
		}
	}

	@SuppressWarnings("unused")
	@Subscribe
	private void onGameTick(GameTick e)
	{
		try
		{
			if (Movement.isWalking())
			{
				return;
			}

			if (config.flick() && Prayers.isQuickPrayerEnabled())
			{
				Prayers.toggleQuickPrayer(false);
			}

			if (config.eat() && Combat.getHealthPercent() <= config.healthPercent())
			{
				List<String> foods = List.of(config.foods().split(","));
				Item food = Inventory.getFirst(x -> (x.getName() != null && foods.stream().anyMatch(a -> x.getName().contains(a)))
					|| (foods.contains("Any") && x.hasAction("Eat")));
				if (food != null)
				{
					food.interact("Eat");
					return;
				}
			}

			if (config.restore() && Prayers.getPoints() < 5)
			{
				Item restorePotion = Inventory.getFirst(x -> x.hasAction("Drink")
					&& (x.getName().contains("Prayer potion") || x.getName().contains("Super restore")));
				if (restorePotion != null)
				{
					restorePotion.interact("Drink");
					return;
				}
			}

			if (config.buryBones())
			{
				Item bones = Inventory.getFirst(x -> x.hasAction("Bury") || x.hasAction("Scatter"));
				if (bones != null)
				{
					bones.interact(0);
					return;
				}
			}

			Player local = Players.getLocal();
			List<String> itemsToLoot = List.of(config.loot().split(","));
			if (!Inventory.isFull())
			{
				TileItem loot = TileItems.getNearest(x ->
					x.getTile().getWorldLocation().distanceTo(local.getWorldLocation()) < config.attackRange()
						&& !notOurItems.contains(x)
						&& ((x.getName() != null && itemsToLoot.contains(x.getName())
						|| (config.lootValue() > -1 && itemManager.getItemPrice(x.getId()) * x.getQuantity() > config.lootValue())
						|| (config.untradables() && (!x.isTradable()) || x.hasInventoryAction("Destroy"))))
				);
				if (loot != null)
				{
					if (!Reachable.isInteractable(loot.getTile()))
					{
						Movement.walkTo(loot.getTile().getWorldLocation());
						return;
					}

					loot.pickup();
					return;
				}
			}

			if (config.alching())
			{
				AlchSpell alchSpell = config.alchSpell();
				if (alchSpell.canCast())
				{
					List<String> alchItems = List.of(config.alchItems().split(","));
					Item alchItem = Inventory.getFirst(x -> x.getName() != null && alchItems.contains(x.getName()));
					if (alchItem != null)
					{
						Magic.cast(alchSpell.getSpell(), alchItem);
					}
				}
			}

			if (local.getInteracting() != null && !Dialog.canContinue())
			{
				return;
			}

			NPC mob = Combat.getAttackableNPC(x -> x.getName() != null
				&& x.getName().toLowerCase().contains(config.monster().toLowerCase()) && !x.isDead()
				&& x.getWorldLocation().distanceTo(local.getWorldLocation()) < config.attackRange()
			);
			if (mob == null)
			{
				if (startPoint == null)
				{
					log.info("No attackable monsters in area");
					return;
				}

				Movement.walkTo(startPoint);
				return;
			}

			if (!Reachable.isInteractable(mob))
			{
				Movement.walkTo(mob.getWorldLocation());
				return;
			}

			mob.interact("Attack");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Provides
	public HootFighterConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HootFighterConfig.class);
	}

	@Override
	public void shutDown()
	{
		if (executor != null)
		{
			executor.shutdown();
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage e)
	{
		String message = e.getMessage();
		if (message.contains("other players have dropped"))
		{
			var notOurs = TileItems.getAt(Players.getLocal().getWorldLocation(), x -> true);
			log.debug("{} are not our items", notOurs.stream().map(TileItem::getName).collect(Collectors.toList()));
			notOurItems.addAll(notOurs);
		}
	}
}
