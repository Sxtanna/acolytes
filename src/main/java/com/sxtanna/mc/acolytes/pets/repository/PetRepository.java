package com.sxtanna.mc.acolytes.pets.repository;

import org.jetbrains.annotations.NotNull;

import com.sxtanna.mc.acolytes.base.State;
import com.sxtanna.mc.acolytes.data.Pet;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PetRepository extends State
{

	@Override
	void load();

	@Override
	void kill();


	@NotNull CompletableFuture<Collection<Pet>> select(@NotNull final UUID uuid);

	@NotNull CompletableFuture<Void> delete(@NotNull final UUID uuid);

	@NotNull CompletableFuture<Void> insert(@NotNull final UUID uuid, @NotNull final Collection<Pet> pets);

}