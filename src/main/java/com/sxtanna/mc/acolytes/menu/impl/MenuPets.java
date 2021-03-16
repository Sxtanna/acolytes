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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sxtanna.mc.acolytes.util.bukkit.Colors.colorize;
import static java.util.Arrays.asList;

public final class MenuPets extends Menu
{

	public static final String PETS_KEY = "P";


	@NotNull
	private final Player         player;
	@NotNull
	private final AcolytesPlugin plugin;
	@NotNull
	private final MenuDecoded    decode;


	public MenuPets(@NotNull final AcolytesPlugin plugin, @NotNull final Player player, final @NotNull MenuDecoded decode)
	{
		super(decode.getRequiredSize(),
		      colorize(plugin.getConfiguration().get(AcolytesConfig.Menus.PETS_MENU_TITLE)));

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

		final Map<String, MenuButton> buttons = plugin.getConfiguration().get(AcolytesConfig.Menus.PETS_MENU_VALUES);
		if (buttons.isEmpty())
		{
			return;
		}


		final Iterator<Entry<String, Pet>> pets = plugin.getModule().getController().getLoadedPets().entrySet().iterator();

		for (final Entry<String, MenuButton> button : buttons.entrySet())
		{
			final List<MenuCreationFunction> functions = decode.getCreationData().get(button.getKey().charAt(0));
			if (functions == null || functions.isEmpty())
			{
				continue;
			}

			for (final MenuCreationFunction function : functions)
			{
				if (!button.getKey().equals(PETS_KEY) || !pets.hasNext())
				{
					function.accept(this, button.getValue().getItemStack(), event -> { });
					continue;
				}

				final Pet loaded = pets.next().getValue();
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

						target.select(PetAttributes.PERK_EFFECT).ifPresent(perk -> {
							lore.add(" ");
							lore.add("&7" + perk.getName());
						});

						target.select(PetAttributes.PERK_EFFECT_GROUP).ifPresent(perk -> {
							lore.add(" ");
							lore.addAll(Arrays.stream(perk.getName().split("\n")).map(name -> "&7" + name).collect(Collectors.toList()));
						});
					}

					Stacks.lore(meta, lore);
				});


				function.accept(this, item, event -> {
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
							redo();
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

}