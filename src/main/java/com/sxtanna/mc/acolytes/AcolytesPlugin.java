package com.sxtanna.mc.acolytes;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import com.sxtanna.mc.acolytes.cmds.CommandAcolytes;
import com.sxtanna.mc.acolytes.cmds.CommandAcolytesAdmin;
import com.sxtanna.mc.acolytes.conf.AcolytesConfig;
import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.pets.AcolytesModule;
import com.sxtanna.mc.acolytes.util.bukkit.Colors;
import com.sxtanna.mc.acolytes.util.OnlinePlayerResolver;
import com.sxtanna.mc.acolytes.util.PetResolver;

import co.aikar.commands.ACFUtil;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import co.aikar.locales.MessageKeyProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;

import static com.google.common.io.Files.getFileExtension;
import static com.google.common.io.Files.getNameWithoutExtension;

public final class AcolytesPlugin extends JavaPlugin
{

	{
		saveDefaultConfig();
	}


	@Nullable
	private static AcolytesPlugin INSTANCE;

	public static @NotNull AcolytesPlugin get()
	{
		return Objects.requireNonNull(INSTANCE, "acolytes is not enabled");
	}


	@NotNull
	private final AcolytesConfig config = new AcolytesConfig(this);
	@NotNull
	private final AcolytesModule module = new AcolytesModule(this);

	@Nullable
	private PaperCommandManager manager;


	@Override
	public void onLoad()
	{
		INSTANCE = this;

		// todo: save lang files?
	}

	@Override
	public void onEnable()
	{
		getModule().load();

		saveDefaultLanguageFiles();
		initializeCommandManager();
		loadStorageLanguageFiles();
	}

	@Override
	public void onDisable()
	{
		getModule().kill();

		INSTANCE = null;
	}


	@Contract(pure = true)
	public @NotNull AcolytesConfig getConfiguration()
	{
		return this.config;
	}


	@Contract(pure = true)
	public @NotNull AcolytesModule getModule()
	{
		return this.module;
	}


	public void send(@NotNull final CommandSender sender, @NotNull final MessageKeyProvider provider, @NotNull final String... replacements)
	{
		if (manager == null)
		{
			return;
		}

		final BukkitCommandIssuer issuer = manager.getCommandIssuer(sender);

		String message = manager.getLocales().getMessage(issuer, provider.getMessageKey());
		if (replacements.length != 0)
		{
			message = ACFUtil.replaceStrings(message, replacements);
		}

		issuer.sendMessage(Colors.colorize(message));
	}


	private void initializeCommandManager()
	{
		manager = new PaperCommandManager(this);
		manager.enableUnstableAPI("help");
		manager.enableUnstableAPI("brigadier");
		manager.usePerIssuerLocale(true, true);


		manager.getCommandContexts().registerContext(OnlinePlayer.class,
		                                             OnlinePlayerResolver.INSTANCE);

		manager.getCommandContexts().registerIssuerAwareContext(Pet.class,
		                                                        PetResolver.INSTANCE);

		manager.getCommandCompletions().registerCompletion("pets",
		                                                   context -> getModule().getController()
		                                                                         .getLoadedPets()
		                                                                         .keySet());

		manager.getCommandCompletions().registerCompletion("target_pets",
		                                                   context -> getModule().getController()
		                                                                         .getPlayerPets(context.getContextValue(OnlinePlayer.class).getPlayer())
		                                                                         .keySet());

		manager.registerCommand(new CommandAcolytes(this));
		manager.registerCommand(new CommandAcolytesAdmin(this));
	}

	private void saveDefaultLanguageFiles()
	{
		final File path = new File(getDataFolder(), "lang");
		if (!path.exists() && !path.mkdirs())
		{
			return;
		}

		final String[] langs = {"en-US.yml"};

		for (final String lang : langs)
		{
			final File file = new File(path, lang);
			if (file.exists())
			{
				continue;
			}

			try (final InputStream resource = getResource("lang/" + lang))
			{
				if (resource == null)
				{
					continue;
				}

				Files.copy(resource, file.toPath());
			}
			catch (IOException ex)
			{
				getLogger().log(Level.WARNING, "could not save language file for " + lang, ex);
			}
		}
	}

	private void loadStorageLanguageFiles()
	{
		if (manager == null)
		{
			return;
		}

		//noinspection UnstableApiUsage
		final File[] langs = new File(getDataFolder(), "lang").listFiles(($, name) -> getFileExtension(name).equalsIgnoreCase("yml"));
		if (langs == null || langs.length <= 0)
		{
			return;
		}


		for (final File lang : langs)
		{
			//noinspection UnstableApiUsage
			final Locale locale = Locale.forLanguageTag(getNameWithoutExtension(lang.getName()));

			try
			{
				manager.addSupportedLanguage(locale);
				manager.getLocales().loadYamlLanguageFile(lang, locale);

				getLogger().info("loaded locale: " + locale.getDisplayName());
			}
			catch (final IOException | InvalidConfigurationException ex)
			{
				getLogger().log(Level.WARNING, "could not load language file for " + lang, ex);
			}
		}
	}

}