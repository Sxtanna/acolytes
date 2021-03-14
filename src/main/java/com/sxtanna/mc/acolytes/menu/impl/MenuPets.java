package com.sxtanna.mc.acolytes.menu.impl;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.conf.AcolytesConfig;
import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;
import com.sxtanna.mc.acolytes.menu.Menu;
import com.sxtanna.mc.acolytes.util.bukkit.Stacks;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.sxtanna.mc.acolytes.menu.Menu.Size.forCount;
import static com.sxtanna.mc.acolytes.util.bukkit.Colors.colorize;

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
		final Map<String, Pet> pets = plugin.getModule().getController().getLoadedPets();

		final AtomicInteger next = new AtomicInteger();

		for (final Map.Entry<String, Pet> entry : pets.entrySet())
		{
			final int slot = next.getAndIncrement();
			if (slot > getInventory().getSize())
			{
				break; // paginate?
			}


			final ItemStack item = Stacks.meta(entry.getValue().createHeadItem(plugin.getModule().getAdapter()), meta ->
			{
				entry.getValue().select(PetAttributes.NAME).ifPresent(name -> {
					Stacks.name(meta, name);
				});
			});


			slot(slot, item, event ->
			{
				// todo: check if they own this pet first

				player.closeInventory();
				plugin.getModule().getController().load(player, entry.getValue());
			});
		}

	}

}