package com.sxtanna.mc.acolytes.data;

import org.jetbrains.annotations.NotNull;

public interface PetAttribute<T>
{

	@NotNull T getDefaultAttr();

	@NotNull String getName();

	@NotNull Class<T> getType();

	default boolean isMutable()
	{
		return true;
	}


	static <T> PetAttribute<T> make(@NotNull final Class<T> type, @NotNull final String name, @NotNull final T defaultAttr)
	{
		return make(type, name, defaultAttr, true);
	}

	static <T> PetAttribute<T> make(@NotNull final Class<T> type, @NotNull final String name, @NotNull final T defaultAttr, final boolean mutable)
	{
		return new PetAttribute<T>()
		{
			@Override
			public @NotNull T getDefaultAttr()
			{
				return defaultAttr;
			}


			@Override
			public @NotNull String getName()
			{
				return name;
			}

			@Override
			public @NotNull Class<T> getType()
			{
				return type;
			}

			@Override
			public boolean isMutable()
			{
				return mutable;
			}
		};
	}

}