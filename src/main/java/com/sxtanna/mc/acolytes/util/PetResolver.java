package com.sxtanna.mc.acolytes.util;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.data.Pet;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import co.aikar.commands.contexts.IssuerAwareContextResolver;

import java.util.Optional;

public enum PetResolver implements IssuerAwareContextResolver<Pet, BukkitCommandExecutionContext>
{
	INSTANCE;


	@Override
	public Pet getContext(@NotNull final BukkitCommandExecutionContext context) throws InvalidCommandArgument
	{
		final AcolytesPlugin plugin = AcolytesPlugin.get();

		final String uuid = context.popFirstArg();

		if (context.hasFlag("by_uuid"))
		{
			if (uuid == null)
			{
				throw new InvalidCommandArgument("You must supply the uuid of a pet", false);
			}

			return plugin.getModule()
			             .getController()
			             .getByUuid(uuid)
			             .orElseThrow(() -> new InvalidCommandArgument(String.format("could not find pet named %s!", uuid), false));
		}


		final Player player = Optional.ofNullable((OnlinePlayer) context.getResolvedArg(OnlinePlayer.class))
		                              .map(OnlinePlayer::getPlayer)
		                              .orElse((Player) context.getResolvedArg(Player.class));
		if (player == null)
		{
			throw new InvalidCommandArgument("No player context found!", false);
		}

		if (context.hasFlag("active"))
		{
			return plugin.getModule()
			             .getController()
			             .getActive(player)
			             .orElseThrow(() -> new InvalidCommandArgument("no active pet found!", false));
		}

		if (context.hasFlag("target"))
		{
			if (uuid == null)
			{
				throw new InvalidCommandArgument("You must supply the uuid of the player's pet", false);
			}

			return plugin.getModule()
			             .getController()
			             .getByUuid(player, uuid)
			             .orElseThrow(() -> new InvalidCommandArgument(String.format("could not find player's pet named %s!", uuid), false));
		}

		throw new InvalidCommandArgument("could not find pet!", false);
	}
}