package net.unethicalite.client.managers;

import lombok.Getter;
import net.runelite.api.Item;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class InventoryManager
{
	@Getter
	private static final Map<Integer, Item[]> cachedContainers = new ConcurrentHashMap<>();

	@Inject
	InventoryManager(EventBus eventBus)
	{
		eventBus.register(this);
	}

	@Subscribe(priority = Integer.MAX_VALUE)
	private void onItemContainerChanged(ItemContainerChanged e)
	{
		cachedContainers.put(e.getContainerId(), e.getItemContainer().getItems());
	}
}
