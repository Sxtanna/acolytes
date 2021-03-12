package com.sxtanna.mc.acolytes.backend;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;

public interface PetEntityProvider
{

	void initialize();

	@NotNull PetEntity spawn(@NotNull final Location location, @NotNull final PetConfig config);

}