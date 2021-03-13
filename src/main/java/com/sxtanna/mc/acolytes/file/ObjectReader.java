package com.sxtanna.mc.acolytes.file;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

@FunctionalInterface
public interface ObjectReader<T>
{

	@NotNull T pull(@NotNull final ConfigurationSection yaml);

}