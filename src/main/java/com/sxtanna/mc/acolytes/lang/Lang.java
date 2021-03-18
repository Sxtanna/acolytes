package com.sxtanna.mc.acolytes.lang;

import org.jetbrains.annotations.NotNull;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import java.util.Locale;

public enum Lang implements MessageKeyProvider
{

	CMDS__USERS__NAME_UPDATE,
	CMDS__ADMIN__NAME_UPDATE__SENDER,
	CMDS__ADMIN__NAME_UPDATE__TARGET,

	CMDS__ADMIN__GIVE__FAIL,
	CMDS__ADMIN__GIVE__SENDER,
	CMDS__ADMIN__GIVE__TARGET,

	CMDS__ADMIN__TAKE__FAIL,
	CMDS__ADMIN__TAKE__SENDER,
	CMDS__ADMIN__TAKE__TARGET,

	CMDS__ADMIN__RELOAD,

	MENU__SPAWNED,
	MENU__REMOVED,

	MENU__PURCHASE__SUCCESS,
	MENU__PURCHASE__FAILURE,

	MENU__BUTTONS__STATUS__NOT,
	MENU__BUTTONS__STATUS__HAS,

	MENU__BUTTONS__ACTION__SUMMON,
	MENU__BUTTONS__ACTION__REMOVE,
	MENU__BUTTONS__ACTION__OBTAIN,

	MENU__BUTTONS__EFFECT__FORMAT,

	COST__PREFIX__VAULT,
	COST__PREFIX__LEVEL,
	COST__PREFIX__TOKEN,

	COST__SUFFIX__VAULT,
	COST__SUFFIX__LEVEL,
	COST__SUFFIX__TOKEN,

	;


	@NotNull
	private final MessageKey messageKey = MessageKey.of(name().toLowerCase(Locale.ROOT).replace("__", ".").replace("_", "-"));

	@Override
	public final @NotNull MessageKey getMessageKey()
	{
		return this.messageKey;
	}
}