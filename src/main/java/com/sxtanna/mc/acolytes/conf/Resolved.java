package com.sxtanna.mc.acolytes.conf;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import com.sxtanna.mc.acolytes.AcolytesPlugin;

import ch.jalu.configme.properties.Property;

@FunctionalInterface
public interface Resolved<T>
{

	@NotNull T get();


	@Contract(pure = true)
	static <T> @NotNull Resolved<T> from(@NotNull final Property<T> property)
	{
		return () -> AcolytesPlugin.get().getConfiguration().get(property);
	}

}