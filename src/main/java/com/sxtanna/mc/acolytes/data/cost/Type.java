package com.sxtanna.mc.acolytes.data.cost;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import com.sxtanna.mc.acolytes.lang.Lang;

public enum Type
{

	VAULT(Lang.COST__VAULT__PREFIX, Lang.COST__VAULT__SUFFIX),
	LEVEL(Lang.COST__LEVEL__PREFIX, Lang.COST__LEVEL__SUFFIX),
	TOKEN(Lang.COST__TOKEN__PREFIX, Lang.COST__TOKEN__SUFFIX);


	@NotNull
	private final Lang prefix;
	@NotNull
	private final Lang suffix;


	Type(@NotNull final Lang prefix, @NotNull final Lang suffix)
	{
		this.prefix = prefix;
		this.suffix = suffix;
	}


	@Contract(pure = true)
	public @NotNull Lang getPrefix()
	{
		return this.prefix;
	}

	@Contract(pure = true)
	public @NotNull Lang getSuffix()
	{
		return this.suffix;
	}

}