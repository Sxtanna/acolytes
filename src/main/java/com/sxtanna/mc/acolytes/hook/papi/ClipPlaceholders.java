package com.sxtanna.mc.acolytes.hook.papi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;
import com.sxtanna.mc.acolytes.data.cost.Cost;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.util.Locale;

public final class ClipPlaceholders extends PlaceholderExpansion
{

	@NotNull
	private final AcolytesPlugin plugin;


	public ClipPlaceholders(@NotNull final AcolytesPlugin plugin)
	{
		this.plugin = plugin;
	}


	@Override
	public @NotNull String getIdentifier()
	{
		return "pets";
	}

	@Override
	public @NotNull String getAuthor()
	{
		return "Sxtanna";
	}

	@Override
	public @NotNull String getVersion()
	{
		return plugin.getDescription().getVersion();
	}

	@Override
	public boolean persist()
	{
		return true;
	}


	@Override
	public @Nullable String onRequest(@Nullable final OfflinePlayer player, @NotNull final String params)
	{
		final String[] args = params.split("_");
		if (args.length != 1 || player == null || !player.isOnline())
		{
			return null;
		}

		return plugin.getModule()
		             .getController()
		             .getActive(player.getPlayer())
		             .map(pet -> request(pet, args[0], player.getPlayer()))
		             .orElse(null);
	}



	private @Nullable String request(@NotNull final Pet pet, @NotNull final String param, @Nullable final Player player)
	{
		switch (param.toLowerCase(Locale.ROOT))
		{
			case "uuid":
				return pet.getUuid();
			case "name":
				return pet.select(PetAttributes.NAME).orElse(pet.getUuid());
			case "cost":
				return player == null ? null : pet.select(PetAttributes.COST).orElse(Cost.FREE).toStringFormatted(player.getPlayer());
		}

		return null;
	}

}