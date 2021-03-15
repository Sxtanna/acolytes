package com.sxtanna.mc.acolytes.menu.impl;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.conf.AcolytesConfig;
import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;
import com.sxtanna.mc.acolytes.lang.Lang;
import com.sxtanna.mc.acolytes.menu.Menu;
import com.sxtanna.mc.acolytes.util.bukkit.Stacks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.sxtanna.mc.acolytes.menu.Menu.Size.forCount;
import static com.sxtanna.mc.acolytes.util.bukkit.Colors.colorize;
import static java.util.Arrays.asList;

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
		final AtomicInteger next = new AtomicInteger();

		for (final Entry<String, Pet> entry : plugin.getModule().getController().getLoadedPets().entrySet())
		{
			final int slot = next.getAndIncrement();
			if (slot > getInventory().getSize())
			{
				break; // paginate?
			}

			final Pet loaded = entry.getValue();
			final Pet target = plugin.getModule().getController().getByUuid(player, loaded.getUuid()).orElse(null);

			final ItemStack item = Stacks.meta(loaded.createHeadItem(plugin.getModule().getAdapter()), meta ->
			{
				String custom = null;

				if (target != null)
				{
					custom = target.select(PetAttributes.NAME).orElse(null);
				}

				if (custom == null)
				{
					custom = loaded.select(PetAttributes.NAME).orElse(null);
				}

				Stacks.name(meta,
				            custom != null ? custom : loaded.getUuid());

				final List<String> lore = new ArrayList<>(asList((target != null ?
				                                                  plugin.lang(player, Lang.MENU__BUTTONS__HAS_GIVEN) :
				                                                  plugin.lang(player, Lang.MENU__BUTTONS__NOT_GIVEN)).split("\n")));
				if (target != null)
				{
					lore.addAll(asList((target.getEntity() != null ?
					                    plugin.lang(player, Lang.MENU__BUTTONS__REMOVE) :
					                    plugin.lang(player, Lang.MENU__BUTTONS__SUMMON)).split("\n")));
				}

				Stacks.lore(meta, lore);
			});


			slot(slot, item, event ->
			{
				if (target == null)
				{
					return;
				}


				final Optional<Pet> active = plugin.getModule().getController().getActive(player);
				if (active.isPresent())
				{
					plugin.getModule().getController().kill(player, active.get());

					plugin.send(player,
					            Lang.MENU__REMOVED,

					            "{pet_name}",
					            target.select(PetAttributes.NAME).orElse(target.select(PetAttributes.UUID).orElse("Pet")));

					if (active.get().equals(loaded))
					{
						return; // only despawn
					}
				}

				player.closeInventory();
				plugin.getModule().getController().load(player, target);

				plugin.send(player,
				            Lang.MENU__SPAWNED,

				            "{pet_name}",
				            target.select(PetAttributes.NAME).orElse(target.select(PetAttributes.UUID).orElse("Pet")));
			});
		}

	}

}