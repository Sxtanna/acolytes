package com.sxtanna.mc.acolytes.file;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

@FunctionalInterface
public interface ObjectWriter<T>
{

	void push(@NotNull final ConfigurationSection yaml, @NotNull final T value);

}