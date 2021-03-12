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
	public @NotNull Optional<Pet> getActive(@NotNull final UUID player)
	{
		return Optional.ofNullable(this.active.get(player));
	}

	@Override
	public @NotNull Optional<Pet> getByUuid(@NotNull final UUID player, @NotNull final String uuid)
	{
		return Optional.ofNullable(this.cached.get(player)).map(cached -> cached.get(uuid));
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