/*
 * Copyright (c) 2016-2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.unethicalite.client.minimal;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.openosrs.client.OpenOSRS;
import net.unethicalite.api.account.GameAccount;
import net.unethicalite.api.game.Game;
import net.unethicalite.client.Static;
import net.unethicalite.client.config.UnethicaliteConfig;
import net.unethicalite.client.minimal.plugins.PluginEntry;
import net.unethicalite.client.minimal.ui.MinimalToolbar;
import net.unethicalite.client.minimal.ui.MinimalUI;
import net.unethicalite.client.managers.MinimalFpsManager;
import net.unethicalite.client.managers.MinimalPluginManager;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.client.ClassPreloader;
import net.runelite.client.RuneLite;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.RuntimeConfigLoader;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.rs.ClientLoader;
import net.runelite.client.rs.ClientUpdateCheckMode;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.ui.FatalErrorDialog;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.tooltip.TooltipOverlay;
import net.runelite.http.api.RuneLiteAPI;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.*;
import java.applet.Applet;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static net.runelite.client.RuneLite.OPENOSRS;
import static net.runelite.client.RuneLite.USER_AGENT;

@Singleton
@Slf4j
public class MinimalClient
{
	public static final File CLIENT_DIR = new File(System.getProperty("user.home"), ".openosrs");
	public static final File CACHE_DIR = new File(CLIENT_DIR, "cache");
	public static final File LOGS_DIR = new File(CLIENT_DIR, "logs");
	public static final File DEFAULT_CONFIG_FILE = new File(CLIENT_DIR, "settings.properties");
	public static final File DATA_DIR = new File(CLIENT_DIR, "data");
	public static final File SCRIPTS_DIR = new File(CLIENT_DIR, "scripts");

	private static final int MAX_OKHTTP_CACHE_SIZE = 20 * 1024 * 1024; // 20mb

	@Getter
	private static Injector injector;

	@Inject
	private EventBus eventBus;

	@Inject
	private ConfigManager configManager;

	@Inject
	private MinimalUI minimalUI;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Provider<TooltipOverlay> tooltipOverlay;

	@Inject
	@Nullable
	private Client client;

	@Inject
	private MinimalToolbar minimalToolbar;

	@Inject
	@Nullable
	private Applet applet;

	@Inject
	private MinimalPluginManager minimalPluginManager;

	@Inject
	private DrawManager drawManager;

	@Inject
	private MinimalFpsManager minimalFpsManager;

	@Inject
	private UnethicaliteConfig minimalConfig;

	@Inject
	private WorldService worldService;

	public static void main(String[] args) throws Exception
	{
		Locale.setDefault(Locale.ENGLISH);

		final OptionParser parser = new OptionParser();
		parser.accepts("developer-mode", "Enable developer tools");
		parser.accepts("debug", "Show extra debugging output");
		parser.accepts("insecure-skip-tls-verification", "Disables TLS verification");
		parser.accepts("jav_config", "jav_config url")
				.withRequiredArg()
				.defaultsTo(RuneLiteProperties.getJavConfig());

		final ArgumentAcceptingOptionSpec<String> proxyInfo = parser
				.accepts("proxy")
				.withRequiredArg().ofType(String.class);

		final ArgumentAcceptingOptionSpec<Integer> worldInfo = parser
				.accepts("world")
				.withRequiredArg().ofType(Integer.class);

		final ArgumentAcceptingOptionSpec<File> configfile = parser.accepts("runelite", "Use a specified config file")
				.withRequiredArg()
				.withValuesConvertedBy(new ConfigFileConverter())
				.defaultsTo(DEFAULT_CONFIG_FILE);

		OptionSet options = parseArgs(parser, args);

		if (options.has("debug"))
		{
			final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
			logger.setLevel(Level.DEBUG);
		}

		if (options.has("proxy"))
		{
			String[] proxy = options.valueOf(proxyInfo).split(":");

			if (proxy.length >= 2)
			{
				System.setProperty("socksProxyHost", proxy[0]);
				System.setProperty("socksProxyPort", proxy[1]);
			}

			if (proxy.length >= 4)
			{
				System.setProperty("java.net.socks.username", proxy[2]);
				System.setProperty("java.net.socks.password", proxy[3]);

				final String user = proxy[2];
				final char[] pass = proxy[3].toCharArray();

				Authenticator.setDefault(new Authenticator()
				{
					private final PasswordAuthentication auth = new PasswordAuthentication(user, pass);

					protected PasswordAuthentication getPasswordAuthentication()
					{
						return auth;
					}
				});
			}
		}

		if (options.has("world"))
		{
			int world = options.valueOf(worldInfo);
			System.setProperty("cli.world", String.valueOf(world));
		}

		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
		{
			log.error("Uncaught exception:", throwable);
			if (throwable instanceof AbstractMethodError)
			{
				log.error("Classes are out of date; Build with maven again.");
			}
		});

		OpenOSRS.preload();

		final OkHttpClient okHttpClient = buildHttpClient(options.has("insecure-skip-tls-verification"));
		RuneLiteAPI.CLIENT = okHttpClient;

		try
		{
			final RuntimeConfigLoader runtimeConfigLoader = new RuntimeConfigLoader(okHttpClient);
			final ClientLoader clientLoader = new ClientLoader(okHttpClient, ClientUpdateCheckMode.AUTO,
					runtimeConfigLoader,
					(String) options.valueOf(
							"jav_config")
			);

			new Thread(() ->
			{
				clientLoader.get();
				ClassPreloader.preload();
			}, "Preloader").start();

			log.info("OpenOSRS {} (RuneLite version {}, launcher version {}) starting up, args: {}",
					OpenOSRS.SYSTEM_VERSION, RuneLiteProperties.getVersion() == null ? "unknown" : RuneLiteProperties.getVersion(),
					RuneLiteProperties.getLauncherVersion(), args.length == 0 ? "none" : String.join(" ", args));

			final long start = System.currentTimeMillis();

			injector = Guice.createInjector(new MinimalModule(
					options.has("developer-mode"),
					okHttpClient,
					clientLoader,
					options.valueOf(configfile))
			);

			injector.getInstance(MinimalClient.class).start(options);

			RuneLite.setInjector(injector);

			final long end = System.currentTimeMillis();
			final RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
			final long uptime = rb.getUptime();
			log.info("Client initialization took {}ms. Uptime: {}ms", end - start, uptime);
		}
		catch (Exception e)
		{
			log.error("Failure during startup", e);
			SwingUtilities.invokeLater(() ->
					new FatalErrorDialog("OpenOSRS has encountered an unexpected error during startup.")
							.open());
		}
	}

	public void start(OptionSet options) throws Exception
	{
		// Load RuneLite or Vanilla client
		final boolean isOutdated = client == null;

		if (!isOutdated)
		{
			// Inject members into client
			injector.injectMembers(client);
		}

		// Start the applet
		if (applet != null)
		{
			copyJagexCache();

			// Client size must be set prior to init
			applet.setSize(Constants.GAME_FIXED_SIZE);

			// Change user.home so the client places jagexcache in the .runelite directory
			String oldHome = System.setProperty("user.home", getCacheDirectory().getAbsolutePath());
			try
			{
				applet.init();
			}
			finally
			{
				System.setProperty("user.home", oldHome);
			}

			applet.start();
		}

		// Load user configuration
		configManager.load();

		minimalToolbar.init();
		drawManager.registerEveryFrameListener(minimalFpsManager);
		minimalFpsManager.reloadConfig(minimalConfig.fpsLimit());
		eventBus.register(minimalToolbar);
		eventBus.register(minimalPluginManager);

		initArgs(options);

		minimalUI.init();

		eventBus.register(minimalUI);
		eventBus.register(overlayManager);
		eventBus.register(configManager);

		overlayManager.add(tooltipOverlay.get());

		minimalUI.show();

		if (options.has("script"))
		{
			quickLaunch(options);
		}
	}

	@VisibleForTesting
	public static void setInjector(Injector injector)
	{
		MinimalClient.injector = injector;
	}

	private static class ConfigFileConverter implements ValueConverter<File>
	{
		@Override
		public File convert(String fileName)
		{
			final File file;

			if (Paths.get(fileName).isAbsolute()
					|| fileName.startsWith("./")
					|| fileName.startsWith(".\\"))
			{
				file = new File(fileName);
			}
			else
			{
				file = new File(MinimalClient.CLIENT_DIR, fileName);
			}

			if (file.exists() && (!file.isFile() || !file.canWrite()))
			{
				throw new ValueConversionException(String.format("File %s is not accessible", file.getAbsolutePath()));
			}

			return file;
		}

		@Override
		public Class<? extends File> valueType()
		{
			return File.class;
		}

		@Override
		public String valuePattern()
		{
			return null;
		}
	}

	@VisibleForTesting
	static OkHttpClient buildHttpClient(boolean insecureSkipTlsVerification)
	{
		OkHttpClient.Builder builder = new OkHttpClient.Builder()
				.pingInterval(30, TimeUnit.SECONDS)
				.addNetworkInterceptor(chain ->
				{
					Request userAgentRequest = chain.request()
							.newBuilder()
							.header("User-Agent", USER_AGENT)
							.build();
					return chain.proceed(userAgentRequest);
				})
				// Setup cache
				.cache(new Cache(new File(CACHE_DIR, "okhttp"), MAX_OKHTTP_CACHE_SIZE))
				.addNetworkInterceptor(chain ->
				{
					// This has to be a network interceptor so it gets hit before the cache tries to store stuff
					Response res = chain.proceed(chain.request());
					if (res.code() >= 400 && "GET".equals(res.request().method()))
					{
						// if the request 404'd we don't want to cache it because its probably temporary
						res = res.newBuilder()
								.header("Cache-Control", "no-store")
								.build();
					}
					return res;
				});

		if (insecureSkipTlsVerification || RuneLiteProperties.isInsecureSkipTlsVerification())
		{
			setupInsecureTrustManager(builder);
		}

		return builder.build();
	}

	private static void setupInsecureTrustManager(OkHttpClient.Builder okHttpClientBuilder)
	{
		try
		{
			X509TrustManager trustManager = new X509TrustManager()
			{
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType)
				{
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType)
				{
				}

				@Override
				public X509Certificate[] getAcceptedIssuers()
				{
					return new X509Certificate[0];
				}
			};

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[]{trustManager}, new SecureRandom());
			okHttpClientBuilder.sslSocketFactory(sc.getSocketFactory(), trustManager);
		}
		catch (NoSuchAlgorithmException | KeyManagementException ex)
		{
			log.warn("unable to setup insecure trust manager", ex);
		}
	}

	static
	{
		//Fixes win10 scaling when not 100% while using Anti-Aliasing with GPU
		System.setProperty("sun.java2d.uiScale", "1.0");

		String launcherVersion = System.getProperty("launcher.version");
		System.setProperty("runelite.launcher.version", launcherVersion == null ? "unknown" : launcherVersion);

		CLIENT_DIR.mkdirs();
		SCRIPTS_DIR.mkdirs();
		DATA_DIR.mkdirs();
	}

	private static void copyJagexCache()
	{
		Path from = Paths.get(System.getProperty("user.home"), "jagexcache");
		Path to = Paths.get(getCacheDirectory().getAbsolutePath(), "jagexcache");
		if (Files.exists(to) || !Files.exists(from))
		{
			return;
		}

		log.info("Copying jagexcache from {} to {}", from, to);

		// Recursively copy path https://stackoverflow.com/a/50418060
		try (Stream<Path> stream = Files.walk(from))
		{
			stream.forEach(source ->
			{
				try
				{
					Files.copy(source, to.resolve(from.relativize(source)), COPY_ATTRIBUTES);
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			});
		}
		catch (Exception e)
		{
			log.warn("unable to copy jagexcache", e);
		}
	}

	private void initArgs(OptionSet options)
	{
		if (options.has("norender"))
		{
			configManager.setConfiguration("unethicalite", "renderOff", true);
		}
	}

	private void quickLaunch(OptionSet options)
	{
		if (options.has("script"))
		{
			String script = (String) options.valueOf("script");

			PluginEntry quickStartScript = minimalPluginManager.loadPlugins()
					.stream().filter(x -> x.getScriptClass().getAnnotation(PluginDescriptor.class).name().equals(script))
					.findFirst()
					.orElse(null);
			if (quickStartScript == null || !quickStartScript.isScript())
			{
				return;
			}

			minimalPluginManager.startPlugin(quickStartScript);
		}
	}

	private static String getCacheDir()
	{
		var dir = System.getProperty("unethicalite.cache-dir");
		if (dir != null)
		{
			return dir;
		}

		return OPENOSRS;
	}

	public static File getCacheDirectory()
	{
		var dir = getCacheDir();
		if (Objects.equals(dir, OPENOSRS))
		{
			return new File(CLIENT_DIR, "jagexcache");
		}

		var cacheDirs = new File(CLIENT_DIR, "custom-cache");
		return new File(cacheDirs, dir);
	}

	public static OptionSet parseArgs(OptionParser parser, String... args)
	{
		var accInfo = parser
				.accepts("account")
				.withRequiredArg().ofType(String.class);

		var cacheDirInfo = parser
				.accepts("cache-dir")
				.withOptionalArg().ofType(String.class);

		parser.accepts("norender");

		parser.accepts("script")
				.withRequiredArg().ofType(String.class);

		parser.accepts("scriptArgs")
				.withRequiredArg().ofType(String.class);

		var options = parser.parse(args);

		if (options.has("account"))
		{
			var details = options.valueOf(accInfo).split(":");
			GameAccount gameAccount = new GameAccount(details[0], details[1]);
			if (details.length >= 3)
			{
				gameAccount.setAuth(details[2]);
			}

			Game.setGameAccount(gameAccount);
		}

		if (options.has("scriptArgs"))
		{
			Static.setScriptArgs(((String) options.valueOf("scriptArgs")).split(","));
		}

		if (options.has("cache-dir"))
		{
			var cacheDir = options.valueOf(cacheDirInfo);

			if (cacheDir != null)
			{
				System.setProperty("unethicalite.cache-dir", cacheDir);
			}
			else
			{
				var acc = Game.getGameAccount();
				if (acc != null)
				{
					System.setProperty("unethicalite.cache-dir", acc.getUsername());
				}
				else
				{
					System.setProperty("unethicalite.cache-dir", UUID.randomUUID().toString());
				}
			}
		}

		return options;
	}
}
