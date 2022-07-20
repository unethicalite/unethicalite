package net.unethicalite.mixins;

import net.runelite.api.mixins.Copy;
import net.runelite.api.mixins.Mixin;
import net.runelite.api.mixins.Replace;
import net.runelite.api.mixins.Shadow;
import net.runelite.rs.api.RSClient;
import net.runelite.rs.api.RSDesktopPlatformInfoProvider;
import net.runelite.rs.api.RSPlatformInfo;

import java.util.Random;

@Mixin(RSDesktopPlatformInfoProvider.class)
public class HDesktopPlatformInfoProviderMixin
{
	@Shadow("client")
	private static RSClient client;

	@Copy("get")
	@Replace("get")
	public RSPlatformInfo rl$get()
	{
		Random random = new Random();

		String[] archArray = {"ia64", "amd64", "x86", "unknown", ""};
		int index = random.nextInt(archArray.length);
		String arch = archArray[index];

		boolean arch_bit = arch.startsWith("amd64") || arch.startsWith("x86_64");

		byte os = (byte) (random.nextInt(4 - 1 + 1) + 1);
		byte os_version = 0;
		if (os == 1)
		{
			os_version = (byte) (random.nextInt(11 - 1 + 1) + 1);
		}
		else if (os == 2)
		{
			os_version = (byte) (random.nextInt(29 - 20 + 1) + 20);
		}

		byte provider = (byte) (random.nextInt(5 - 1 + 1) + 1);
		int a = (random.nextInt(20 - 1 + 1) + 1);
		String b = (a < 10 ? "." + (random.nextInt(20 - 1 + 1) + 1) : "");
		String java_version = (random.nextInt(8 - 1 + 1) + 1) + "." + (random.nextInt(20 - 0 + 1) + 0) + b;

		//rs$someMethod(java_version);
		int field4080 = 0;
		int field4077 = 0;
		int field4081 = 0;
		if (java_version.startsWith("1."))
		{
			String[] var2 = java_version.split("\\.");

			try
			{
				field4080 = Integer.parseInt(var2[1]);
				field4077 = Integer.parseInt(var2[0]);
				field4081 = Integer.parseInt(var2[1]);
			}
			catch (Exception var4)
			{
			}
		}
		else
		{
			String[] var2 = java_version.split("\\.");

			try
			{
				field4080 = Integer.parseInt(var2[0]);
				field4077 = Integer.parseInt(var2[1]);
				field4081 = Integer.parseInt(var2[2]);
			}
			catch (Exception var4)
			{
			}
		}
		int memory = ((int) (Runtime.getRuntime().maxMemory() / 1048576L) + 1) / (random.nextInt(4 - 1 + 1) + 1);
		int processors;
		if ((random.nextInt(10 - 1 + 1) + 1) > 5)
		{
			processors = Runtime.getRuntime().availableProcessors();
		}
		else
		{
			processors = 0;
		}

		System.out.println("[Tonic] New Randomized System Info:");
		System.out.println("[os] " + os);
		System.out.println("[os_version]" + os_version);
		System.out.println("[provider] " + provider);
		System.out.println("[memory] " + memory);
		System.out.println("[processors] " + processors);
		System.out.println("[java_version] " + java_version);
		return client.createPlatformInfo(os, arch_bit, os_version, provider, field4080, field4077, field4081, false, memory, processors, 0, 0, "", "", "", "", 0, 0, 0, 0, "", "", (new int[3]), 0, "");
	}
}

