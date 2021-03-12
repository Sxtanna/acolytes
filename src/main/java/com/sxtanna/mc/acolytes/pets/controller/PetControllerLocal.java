package com.sxtanna.mc.acolytes.pets.controller;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.data.Pet;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class PetControllerLocal implements PetController
{

	@NotNull
	private final AcolytesPlugin plugin;


	@NotNull
	private final Map<UUID, Pet>              active = new HashMap<>();
	@NotNull
	private final Map<UUID, Map<String, Pet>> cached = new HashMap<>();


	public PetControllerLocal(@NotNull final AcolytesPlugin plugin)
	{
		this.plugin = plugin;
	}


	@Override
	public void load()
	{

	}

	@Override
	public void kill()
	{

	}


	@Override
	public @NotNull Optional<Pet> getActive(@NotNull final Player player)
	{
		return Optional.ofNullable(this.active.get(player.getUniqueId()));
	}

	@Override
	public @NotNull Optional<Pet> getByName(@NotNull final Player player, @NotNull final String name)
	{
		return Optional.empty();
	}


	@Override
	public void load(final @NotNull Player player, final @NotNull Pet pet)
	{

	}

	@Override
	public void kill(final @NotNull Player player, final @NotNull Pet pet)
	{

	}

}