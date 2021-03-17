package com.sxtanna.mc.acolytes.hook;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.OptionalLong;

public final class Manager
{

	public static @NotNull Optional<BigDecimal> get(@NotNull final Player player)
	{
		try
		{
			final OptionalLong tokens = me.realized.tokenmanager.TokenManagerPlugin.getInstance().getTokens(player);
			if (!tokens.isPresent())
			{
				return Optional.empty();
			}

			return Optional.of(BigDecimal.valueOf(tokens.getAsLong()));
		}
		catch (final NoClassDefFoundError ignored)
		{
			return Optional.empty();
		}
	}


	public static @NotNull Economy.Response take(@NotNull final Player player, @NotNull final BigDecimal amount)
	{
		try
		{
			final OptionalLong tokens = me.realized.tokenmanager.TokenManagerPlugin.getInstance().getTokens(player);
			if (!tokens.isPresent())
			{
				return Economy.Response.failing("balance unavailable");
			}

			if (tokens.getAsLong() < amount.longValueExact())
			{
				return Economy.Response.failing("not enough funds");
			}

			me.realized.tokenmanager.TokenManagerPlugin.getInstance().setTokens(player, tokens.getAsLong() - amount.longValueExact());

			return Economy.Response.passing();
		}
		catch (final NoClassDefFoundError error)
		{
			return Economy.Response.failing("no token economy");
		}
	}


	public static @NotNull Economy.Response give(@NotNull final Player player, @NotNull final BigDecimal amount)
	{
		try
		{
			if (!me.realized.tokenmanager.TokenManagerPlugin.getInstance().addTokens(player, amount.longValueExact()))
			{
				return Economy.Response.failing("could not deposit");
			}

			return Economy.Response.passing();
		}
		catch (final NoClassDefFoundError error)
		{
			return Economy.Response.failing("no token economy");
		}
	}

}