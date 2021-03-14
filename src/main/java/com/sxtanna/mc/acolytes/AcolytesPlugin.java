package com.sxtanna.mc.acolytes;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.sxtanna.mc.acolytes.cmds.CommandAcolytes;
import com.sxtanna.mc.acolytes.cmds.CommandAcolytesAdmin;
import com.sxtanna.mc.acolytes.conf.AcolytesConfig;
import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.pets.AcolytesModule;
import com.sxtanna.mc.acolytes.util.Colors;
import com.sxtanna.mc.acolytes.util.OnlinePlayerResolver;
import com.sxtanna.mc.acolytes.util.PetResolver;

import co.aikar.commands.ACFUtil;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import co.aikar.locales.MessageKeyProvider;

import java.util.Objects;

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

		initializeCommandManager();
		// todo: load lang files?
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

}