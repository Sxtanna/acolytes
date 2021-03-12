package com.sxtanna.mc.acolytes.pets.repository;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.data.Pet;

import java.io.File;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Preconditions.checkState;

public final class PetRepositoryLocal implements PetRepository
{

	@NotNull
	private final AcolytesPlugin plugin;
	@NotNull
	private final File           folder;


	public PetRepositoryLocal(@NotNull final AcolytesPlugin plugin)
	{
		this.plugin = plugin;
		this.folder = new File(plugin.getDataFolder(), "pet-repository");
	}


	@Override
	public void load()
	{
		checkState(this.folder.exists() || this.folder.mkdirs(), "could not create pet-repository folder");
	}

	@Override
	public void kill()
	{

	}


	@Contract("_ -> new")
	private @NotNull File file(@NotNull final UUID uuid)
	{
		return new File(this.folder, String.format("%s.json", uuid));
	}


	@Override
	public @NotNull CompletableFuture<Collection<Pet>> select(@NotNull final UUID uuid)
	{
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public @NotNull CompletableFuture<Void> delete(@NotNull final UUID uuid)
	{
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public @NotNull CompletableFuture<Void> insert(@NotNull final UUID uuid, @NotNull final Collection<Pet> pets)
	{
		return CompletableFuture.completedFuture(null);
	}

}