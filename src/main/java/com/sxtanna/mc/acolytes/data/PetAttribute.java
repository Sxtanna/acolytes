package com.sxtanna.mc.acolytes.data;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface PetAttribute<T>
{

	@NotNull T getDefaultAttr();

	@NotNull T getCopiedValue(@NotNull final T value);

	@NotNull String getName();

	@NotNull Class<T> getType();

	default boolean isMutable()
	{
		return true;
	}


	static <T> PetAttribute<T> make(@NotNull final Class<T> type, @NotNull final String name, @NotNull final T defaultAttr)
	{
		return make(type, name, defaultAttr, true, Function.identity());
	}

	static <T> PetAttribute<T> make(@NotNull final Class<T> type, @NotNull final String name, @NotNull final T defaultAttr, @NotNull final Function<T, T> copy)
	{
		return make(type, name, defaultAttr, true, copy);
	}

	static <T> PetAttribute<T> make(@NotNull final Class<T> type, @NotNull final String name, @NotNull final T defaultAttr, final boolean mutable)
	{
		return make(type, name, defaultAttr, mutable, Function.identity());
	}

	static <T> PetAttribute<T> make(@NotNull final Class<T> type, @NotNull final String name, @NotNull final T defaultAttr, final boolean mutable, @NotNull final Function<T, T> copy)
	{
		return new PetAttribute<T>()
		{
			@Override
			public @NotNull T getDefaultAttr()
			{
				return defaultAttr;
			}

			@Override
			public @NotNull T getCopiedValue(@NotNull final T value)
			{
				return copy.apply(value);
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