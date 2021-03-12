package com.sxtanna.mc.acolytes.backend;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Entity;

public interface PetEntity
{

	void setTargetEntity(@NotNull final Entity entity);


	@NotNull Entity getBukkitEntity();

}