package com.sxtanna.mc.acolytes.data.cost;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import com.sxtanna.mc.acolytes.lang.Lang;

public enum Type
{

	VAULT(Lang.COST__PREFIX__VAULT, Lang.COST__SUFFIX__VAULT),
	LEVEL(Lang.COST__PREFIX__LEVEL, Lang.COST__SUFFIX__LEVEL),
	TOKEN(Lang.COST__PREFIX__TOKEN, Lang.COST__SUFFIX__TOKEN);


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