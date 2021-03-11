package com.sxtanna.mc.acolytes.util;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sxtanna.mc.acolytes.conf.AcolytesConfig;
import com.sxtanna.mc.acolytes.conf.Resolved;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import co.aikar.commands.contexts.ContextResolver;

import java.util.Optional;
import java.util.UUID;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static org.apache.commons.lang.StringUtils.getLevenshteinDistance;

public enum OnlinePlayerResolver implements ContextResolver<OnlinePlayer, BukkitCommandExecutionContext>
{
	INSTANCE;


	private static final int NAME_LENGTH = 16;
	private static final int UUID_LENGTH = 36;

	private static final Resolved<Integer> DIST_SEARCH = Resolved.from(AcolytesConfig.Basic.PLAYER_RESOLVER_FUZZY_DISTANCE);


	@Override
	public OnlinePlayer getContext(@NotNull final BukkitCommandExecutionContext context) throws InvalidCommandArgument
	{
		System.out.println("resolving player");
		if (context.hasFlag("sender"))
		{
			final CommandSender sender = context.getSender();
			if (!(sender instanceof Player))
			{
				throw new InvalidCommandArgument("You must be a player to run this command", false);
			}

			return new OnlinePlayer((Player) sender);
		}

		final String argument = context.popFirstArg();
		if (argument == null)
		{
			throw new InvalidCommandArgument("failed to resolve player without argument", false);
		}


		Player player = null;


		if (argument.length() <= NAME_LENGTH) // argument is within minecraft name length constraint
		{

			// attempt to resolve player by exact name
			player = Bukkit.getPlayerExact(argument);


			if (player == null)
			{
				// attempt to resolve player by fuzzy name match

				final Optional<? extends Player> closest = Bukkit.getOnlinePlayers()
				                                                 .stream()
				                                                 .min(comparing(Player::getName,
				                                                                comparingInt(name -> getLevenshteinDistance(name, argument))));

				if (closest.isPresent())
				{
					if (getLevenshteinDistance(closest.get().getName(), argument) <= DIST_SEARCH.get())
					{
						player = closest.get();
					}
				}
			}
		}


		if (argument.length() <= UUID_LENGTH && player == null) // argument is withing uuid length constraint
		{
			try
			{
				// attempt to resolve player by uuid, parsed from the argument
				player = Bukkit.getPlayer(UUID.fromString(ensureUUIDIsDashed(argument)));
			}
			catch (final IllegalArgumentException | ArrayIndexOutOfBoundsException ignored)
			{
				// ignored exception, uuid parse failed.
			}
		}

		if (player == null)
		{
			throw new InvalidCommandArgument(String.format("failed to resolve player using %s", argument), false);
		}

		return new OnlinePlayer(player);
	}


	@NotNull
	private static String ensureUUIDIsDashed(@NotNull final String uuid)
	{
		final StringBuilder builder = new StringBuilder(uuid.replace("-", ""));

		builder.insert(20, '-');
		builder.insert(16, '-');
		builder.insert(12, '-');
		builder.insert(8, '-');

		return builder.toString();
	}

}