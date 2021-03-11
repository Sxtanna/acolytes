package com.sxtanna.mc.acolytes.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Pet
{

	@Nullable <T> T select(@NotNull final PetAttribute<T> attribute);

	@Nullable <T> T update(@NotNull final PetAttribute<T> attribute, @Nullable T value);

}