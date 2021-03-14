package com.sxtanna.mc.acolytes.util.bukkit;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import net.md_5.bungee.api.ChatColor;

public enum Colors
{
	;


	@Contract("null -> null; !null -> !null")
	public static @Nullable String colorize(@Nullable final String text)
	{
		return text == null ? null : ChatColor.translateAlternateColorCodes('&', text);
	}

}