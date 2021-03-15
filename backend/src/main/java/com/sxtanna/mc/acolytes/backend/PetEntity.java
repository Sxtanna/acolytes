package com.sxtanna.mc.acolytes.backend;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Entity;

public interface PetEntity
{

	void setTargetEntity(@Nullable final Entity entity);

	@Nullable Entity getTargetEntity();


	@NotNull Entity getBukkitEntity();

}