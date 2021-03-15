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

	MENU__SPAWNED,
	MENU__REMOVED,

	MENU__BUTTONS__NOT_GIVEN,
	MENU__BUTTONS__HAS_GIVEN,

	MENU__BUTTONS__SUMMON,
	MENU__BUTTONS__REMOVE,

	;


	@NotNull
	private final MessageKey messageKey = MessageKey.of(name().toLowerCase(Locale.ROOT).replace("__", ".").replace("_", "-"));

	@Override
	public final @NotNull MessageKey getMessageKey()
	{
		return this.messageKey;
	}
}