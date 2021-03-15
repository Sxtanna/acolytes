package com.sxtanna.mc.acolytes.pets.controller;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.sxtanna.mc.acolytes.base.State;
import com.sxtanna.mc.acolytes.data.Pet;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface PetController extends State
{

	@Override
	void load();

	@Override
	void kill();


	@NotNull Map<String, Pet> getLoadedPets();

	@NotNull Map<String, Pet> getPlayerPets(@NotNull final UUID player);

	default @NotNull Map<String, Pet> getPlayerPets(@NotNull final Player player)
	{
		return getPlayerPets(player.getUniqueId());
	}


	@NotNull Optional<Pet> getActive(@NotNull final UUID player);

	default @NotNull Optional<Pet> getActive(@NotNull final Player player)
	{
		return getActive(player.getUniqueId());
	}


	@NotNull Optional<Pet> getByUuid(@NotNull final String uuid);

	@NotNull Optional<Pet> getByUuid(@NotNull final UUID player, @NotNull final String uuid);

	default @NotNull Optional<Pet> getByUuid(@NotNull final Player player, @NotNull final String uuid)
	{
		return getByUuid(player.getUniqueId(), uuid);
	}


	void load(@NotNull final Player player, @NotNull final Pet pet);

	void kill(@NotNull final Player player, @NotNull final Pet pet);


	boolean give(@NotNull final Player player, @NotNull final Pet pet);

	boolean take(@NotNull final Player player, @NotNull final Pet pet);

}