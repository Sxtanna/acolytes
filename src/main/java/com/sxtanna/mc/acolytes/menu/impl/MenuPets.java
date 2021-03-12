package com.sxtanna.mc.acolytes.menu.impl;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.conf.AcolytesConfig;
import com.sxtanna.mc.acolytes.menu.Menu;

import static com.sxtanna.mc.acolytes.menu.Menu.Size.forCount;
import static com.sxtanna.mc.acolytes.util.Colors.colorize;

public final class MenuPets extends Menu
{

	@NotNull
	private final Player         player;
	@NotNull
	private final AcolytesPlugin plugin;


	public MenuPets(@NotNull final AcolytesPlugin plugin, @NotNull final Player player)
	{
		super(forCount(plugin.getConfiguration().get(AcolytesConfig.Menus.PETS_MENU_ROW_COUNT)),
		      colorize(plugin.getConfiguration().get(AcolytesConfig.Menus.PETS_MENU_INV_TITLE)));

		this.player = player;
		this.plugin = plugin;
	}


	@Override
	protected void make()
	{

	}

}