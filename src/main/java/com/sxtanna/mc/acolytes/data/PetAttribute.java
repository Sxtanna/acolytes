package com.sxtanna.mc.acolytes.data;

import org.jetbrains.annotations.NotNull;

public interface PetAttribute<T>
{

	@NotNull T getDefaultAttr();

	@NotNull Class<T> getType();


	static <T> PetAttribute<T> make(@NotNull final Class<T> type, @NotNull final T defaultAttr)
	{
		return new PetAttribute<T>()
		{
			@Override
			public @NotNull T getDefaultAttr()
			{
				return defaultAttr;
			}

			@Override
			public @NotNull Class<T> getType()
			{
				return type;
			}
		};
	}

}