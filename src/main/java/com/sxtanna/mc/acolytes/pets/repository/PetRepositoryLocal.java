package com.sxtanna.mc.acolytes.pets.repository;

import org.jetbrains.annotations.NotNull;

import com.sxtanna.mc.acolytes.AcolytesPlugin;

public final class PetRepositoryLocal implements PetRepository
{

	@NotNull
	private final AcolytesPlugin plugin;


	public PetRepositoryLocal(@NotNull final AcolytesPlugin plugin)
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

}