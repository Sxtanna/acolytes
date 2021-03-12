package com.sxtanna.mc.acolytes;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sxtanna.mc.acolytes.cmds.CommandAcolytes;
import com.sxtanna.mc.acolytes.cmds.CommandAcolytesAdmin;
import com.sxtanna.mc.acolytes.conf.AcolytesConfig;
import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.pets.AcolytesModule;
import com.sxtanna.mc.acolytes.util.OnlinePlayerResolver;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;

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


	private void initializeCommandManager()
	{
		final PaperCommandManager manager = new PaperCommandManager(this);
		manager.enableUnstableAPI("help");
		manager.enableUnstableAPI("brigadier");
		manager.usePerIssuerLocale(true, true);


		manager.getCommandContexts().registerContext(OnlinePlayer.class,
		                                             OnlinePlayerResolver.INSTANCE);

		manager.getCommandContexts().registerContext(Pet.class, context ->
		{
			final Player player = ((OnlinePlayer) context.getResolvedArg(OnlinePlayer.class)).getPlayer();

			if (context.hasFlag("active"))
			{
				return getModule().getController()
				                  .getActive(player)
				                  .orElseThrow(() -> new InvalidCommandArgument("no active pet found!", false));
			}

			if (context.hasFlag("target"))
			{
				final String uuid = context.popFirstArg();

				return getModule().getController()
				                  .getByUuid(player, uuid)
				                  .orElseThrow(() -> new InvalidCommandArgument(String.format("could not find pet named %s!", uuid), false));
			}

			return null;
		});


		manager.registerCommand(new CommandAcolytes(this));
		manager.registerCommand(new CommandAcolytesAdmin(this));
	}

}