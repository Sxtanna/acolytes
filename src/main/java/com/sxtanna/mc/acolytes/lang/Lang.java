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

	MENU__SPAWNED,
	MENU__REMOVED,

	MENU__PURCHASE_SUCCESS,
	MENU__PURCHASE_FAILURE,

	MENU__BUTTONS__NOT_GIVEN,
	MENU__BUTTONS__HAS_GIVEN,

	MENU__BUTTONS__SUMMON,
	MENU__BUTTONS__REMOVE,

	COST__VAULT__PREFIX,
	COST__LEVEL__PREFIX,
	COST__TOKEN__PREFIX,

	COST__VAULT__SUFFIX,
	COST__LEVEL__SUFFIX,
	COST__TOKEN__SUFFIX,

	;


	@NotNull
	private final MessageKey messageKey = MessageKey.of(name().toLowerCase(Locale.ROOT).replace("__", ".").replace("_", "-"));

	@Override
	public final @NotNull MessageKey getMessageKey()
	{
		return this.messageKey;
	}
}