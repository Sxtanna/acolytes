package com.sxtanna.mc.acolytes.menu.impl;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.conf.AcolytesConfig;
import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.menu.Menu;

import java.util.List;
import java.util.Map;

import static com.sxtanna.mc.acolytes.util.bukkit.Colors.colorize;

public final class MenuOpts extends Menu
{

	@NotNull
	private final Pet            pet;
	@NotNull
	private final Player         player;
	@NotNull
	private final AcolytesPlugin plugin;
	@NotNull
	private final MenuDecoded    decode;


	public MenuOpts(@NotNull final AcolytesPlugin plugin, @NotNull final Pet pet, @NotNull final Player player, @NotNull final MenuDecoded decode)
	{
		super(decode.getRequiredSize(),
		      colorize(plugin.getConfiguration().get(AcolytesConfig.Menus.OPTS_MENU_TITLE)));

		this.pet    = pet;
		this.player = player;
		this.plugin = plugin;
		this.decode = decode;
	}


	@Override
	protected void make()
	{
		if (decode.getCreationData().isEmpty())
		{
			return;
		}

		final Map<String, MenuButton> buttons = plugin.getConfiguration().get(AcolytesConfig.Menus.OPTS_MENU_VALUES);
		if (buttons.isEmpty())
		{
			return;
		}


		for (final Map.Entry<String, MenuButton> button : buttons.entrySet())
		{
			final List<MenuCreationFunction> functions = decode.getCreationData().get(button.getKey().charAt(0));
			if (functions == null || functions.isEmpty())
			{
				continue;
			}

			for (final MenuCreationFunction function : functions)
			{
				function.accept(this, button.getValue().getItemStack(), event -> {

				});
			}
		}
	}

}