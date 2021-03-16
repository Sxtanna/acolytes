package com.sxtanna.mc.acolytes.file.impl;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.sxtanna.mc.acolytes.data.cost.Cost;
import com.sxtanna.mc.acolytes.data.cost.Type;
import com.sxtanna.mc.acolytes.file.ObjectCodec;

import java.math.BigDecimal;
import java.util.Locale;

public enum ObjectCodecCost implements ObjectCodec<Cost>
{
	INSTANCE;


	@Override
	public @NotNull Cost pull(@NotNull final ConfigurationSection yaml)
	{
		final String typeString = yaml.getString("cost.type");
		final String costString = yaml.getString("cost.cost");

		final Type       type;
		final BigDecimal cost;

		switch (typeString.toLowerCase(Locale.ROOT))
		{
			case "vault":
				type = Type.VAULT;
				break;
			case "level":
				type = Type.LEVEL;
				break;
			case "token":
				type = Type.TOKEN;
				break;
			default:
				throw new IllegalArgumentException(String.format("invalid cost type %s", typeString));
		}

		try
		{
			cost = new BigDecimal(costString);
		}
		catch (final NumberFormatException ex)
		{
			throw new IllegalArgumentException(String.format("invalid cost %s", costString));
		}

		return new Cost(type, cost);
	}

	@Override
	public void push(@NotNull final ConfigurationSection yaml, @NotNull final Cost value)
	{
		yaml.set("cost.type", value.getType().name());
		yaml.set("cost.cost", value.getCost().toPlainString());
	}
}