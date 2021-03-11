package com.sxtanna.mc.acolytes.pets;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.base.State;
import com.sxtanna.mc.acolytes.pets.controller.PetController;
import com.sxtanna.mc.acolytes.pets.controller.PetControllerLocal;
import com.sxtanna.mc.acolytes.pets.repository.PetRepository;
import com.sxtanna.mc.acolytes.pets.repository.PetRepositoryLocal;

import java.util.Objects;

public final class AcolytesModule implements State
{

	@NotNull
	private final AcolytesPlugin plugin;


	@Nullable
	private PetController controller;
	@Nullable
	private PetRepository repository;


	public AcolytesModule(@NotNull final AcolytesPlugin plugin)
	{
		this.plugin = plugin;
	}


	@Override
	public void load()
	{
		this.controller = new PetControllerLocal(this.plugin);
		this.repository = new PetRepositoryLocal(this.plugin);

		getRepository().load();
		getController().load();
	}

	@Override
	public void kill()
	{
		getController().kill();
		getRepository().kill();

		this.controller = null;
		this.repository = null;
	}


	@Contract(pure = true)
	public @NotNull PetController getController()
	{
		return Objects.requireNonNull(this.controller, "acolytes module not enabled");
	}

	@Contract(pure = true)
	public @NotNull PetRepository getRepository()
	{
		return Objects.requireNonNull(this.repository, "acolytes module not enabled");
	}

}