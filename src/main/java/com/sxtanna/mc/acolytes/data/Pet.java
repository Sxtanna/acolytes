package com.sxtanna.mc.acolytes.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.sxtanna.mc.acolytes.backend.PetEntity;

public interface Pet
{

	@Nullable PetEntity getEntity();

	void setEntity(@Nullable PetEntity entity);


	@Nullable <T> T select(@NotNull final PetAttribute<T> attribute);

	@Nullable <T> T update(@NotNull final PetAttribute<T> attribute, @Nullable T value);

}