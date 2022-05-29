package net.unethicalite.client;

import net.unethicalite.api.movement.pathfinder.GlobalCollisionMap;
import net.unethicalite.api.script.paint.Paint;
import net.unethicalite.client.config.UnethicaliteConfig;
import net.unethicalite.client.managers.DefinitionManager;
import net.unethicalite.client.managers.NativeInputManager;
import net.unethicalite.client.managers.NeverLogManager;
import net.unethicalite.client.managers.interaction.InteractionManager;
import net.runelite.api.Client;
import net.runelite.api.packets.ClientPacket;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.WorldService;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.PluginManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Static
{
	private static String[] scriptArgs = new String[0];

	@Inject
	private static EventBus eventBus;

	@Inject
	private static ClientThread clientThread;

	@Inject
	private static Client client;

	@Inject
	private static ItemManager itemManager;

	@Inject
	private static WorldService worldService;

	@Inject
	private static ChatMessageManager chatMessageManager;

	@Inject
	private static ClientPacket clientPacket;

	@Inject
	private static GlobalCollisionMap globalCollisionMap;

	@Inject
	private static PluginManager pluginManager;

	@Inject
	private static ConfigManager configManager;

	@Inject
	private static KeyManager keyManager;

	@Inject
	private static DefinitionManager definitionManager;

	@Inject
	private static InteractionManager interactionManager;

	@Inject
	private static NativeInputManager nativeInputManager;

	@Inject
	private static Paint paint;

	@Inject
	private static UnethicaliteConfig unethicaliteConfig;

	@Inject
	private static NeverLogManager neverLogManager;

	public static void setScriptArgs(String[] scriptArgs)
	{
		Static.scriptArgs = scriptArgs;
	}

	public static String[] getScriptArgs()
	{
		return scriptArgs;
	}

	public static EventBus getEventBus()
	{
		return eventBus;
	}

	public static ClientThread getClientThread()
	{
		return clientThread;
	}

	public static Client getClient()
	{
		return client;
	}

	public static ItemManager getItemManager()
	{
		return itemManager;
	}

	public static WorldService getWorldService()
	{
		return worldService;
	}

	public static ChatMessageManager getChatMessageManager()
	{
		return chatMessageManager;
	}

	public static ClientPacket getClientPacket()
	{
		return clientPacket;
	}

	public static GlobalCollisionMap getGlobalCollisionMap()
	{
		return globalCollisionMap;
	}

	public static PluginManager getPluginManager()
	{
		return pluginManager;
	}

	public static ConfigManager getConfigManager()
	{
		return configManager;
	}

	public static KeyManager getKeyManager()
	{
		return keyManager;
	}

	public static DefinitionManager getDefinitionManager()
	{
		return definitionManager;
	}

	public static InteractionManager getInteractionManager()
	{
		return interactionManager;
	}

	public static NativeInputManager getNativeInputManager()
	{
		return nativeInputManager;
	}

	public static Paint getPaint()
	{
		return paint;
	}

	public static UnethicaliteConfig getUnethicaliteConfig()
	{
		return unethicaliteConfig;
	}

	public static NeverLogManager getNeverLogManager()
	{
		return neverLogManager;
	}
}
