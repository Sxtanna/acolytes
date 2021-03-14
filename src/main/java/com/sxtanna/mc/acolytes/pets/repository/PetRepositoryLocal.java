package com.sxtanna.mc.acolytes.pets.repository;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.file.YamlConfiguration;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;
import com.sxtanna.mc.acolytes.data.impl.PetImpl;
import com.sxtanna.mc.acolytes.file.impl.ObjectCodecPet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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

		final File defaults = new File(this.folder, "defaults");
		if (defaults.exists() || !defaults.mkdirs())
		{
			return;
		}

		final Pet pet = PetImpl.defaultPet();

		try
		{
			final YamlConfiguration yaml = new YamlConfiguration();

			ObjectCodecPet.INSTANCE.push(yaml, pet);

			yaml.save(new File(defaults, String.format("%s.yml", pet.select(PetAttributes.UUID).orElseThrow(() -> new IllegalStateException("pet has no uuid")))));
		}
		catch (final IOException ex)
		{
			ex.printStackTrace();
		}
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
	public @NotNull CompletableFuture<Collection<Pet>> select()
	{
		return CompletableFuture.supplyAsync(() -> {

			final File root = new File(this.folder, "defaults");
			if (!root.exists())
			{
				return Collections.emptyList();
			}

			final File[] files = root.listFiles(($, name) -> name.toLowerCase(Locale.ROOT).endsWith(".yml"));
			if (files == null || files.length == 0)
			{
				return Collections.emptyList();
			}

			final List<Pet> pets = new ArrayList<>();

			for (final File file : files)
			{
				try
				{
					pets.add(ObjectCodecPet.INSTANCE.pull(YamlConfiguration.loadConfiguration(file)));
				}
				catch (final IllegalStateException ex)
				{
					throw new CompletionException(String.format("failed to read pet from file: %s", file), ex);
				}
			}

			return pets;
		});
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