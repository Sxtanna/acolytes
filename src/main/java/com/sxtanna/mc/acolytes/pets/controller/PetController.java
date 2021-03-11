package com.sxtanna.mc.acolytes.pets.controller;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.sxtanna.mc.acolytes.base.State;
import com.sxtanna.mc.acolytes.data.Pet;

import java.util.Optional;

public interface PetController extends State
{

	@Override
	void load();

	@Override
	void kill();



	@NotNull Optional<Pet> getActive(@NotNull final Player player);

	@NotNull Optional<Pet> getByName(@NotNull final Player player, @NotNull final String name);


	void load(@NotNull final Player player, @NotNull final Pet pet);

	void kill(@NotNull final Player player, @NotNull final Pet pet);

}