package com.sxtanna.mc.acolytes.menu.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.conf.AcolytesConfig;
import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;
import com.sxtanna.mc.acolytes.data.cost.Cost;
import com.sxtanna.mc.acolytes.hook.Replace;
import com.sxtanna.mc.acolytes.lang.Lang;
import com.sxtanna.mc.acolytes.menu.Menu;
import com.sxtanna.mc.acolytes.pets.controller.PetController;
import com.sxtanna.mc.acolytes.util.bukkit.Format;
import com.sxtanna.mc.acolytes.util.bukkit.Stacks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static com.sxtanna.mc.acolytes.util.bukkit.Colors.colorize;

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


		final List<Pet> entries = new ArrayList<>(plugin.getModule().getController().getLoadedPets().values());
		entries.sort(plugin.getConfiguration().get(AcolytesConfig.Menus.PETS_MENU_SORTER));

		final Iterator<Pet> pets = entries.iterator();

		for (final Entry<String, MenuButton> button : buttons.entrySet())
		{
			final List<MenuCreationFunction> functions = decode.getCreationData().get(button.getKey().charAt(0));
			if (functions == null || functions.isEmpty())
			{
				continue;
			}

			for (final MenuCreationFunction function : functions)
			{
				final ItemStack buttonItem = button.getValue().getItemStack();

				if (!button.getKey().equals(PETS_KEY) || !pets.hasNext())
				{
					function.accept(this, Stacks.meta(buttonItem, Stacks::clean), event -> { });
					continue;
				}

				final Pet loaded = pets.next();
				final Pet target = plugin.getModule().getController().getByUuid(player, loaded.getUuid()).orElse(null);

				final ItemStack item = Stacks.meta(loaded.createHeadItem(plugin.getModule().getAdapter()), meta ->
				{
					final String name = Replace.set(player, resolvePetItemName(buttonItem, loaded, target));
					Stacks.name(meta, name);

					final List<String> lore = resolvePetItemLore(buttonItem, target);
					lore.replaceAll(line -> Replace.set(player, line));

					Stacks.lore(meta, lore);
				});


				function.accept(this, item, event -> {
					if (target == null)
					{
						final PetController controller = plugin.getModule().getController();

						controller.purchase(player, loaded)
						          .handle(() ->
						                  {
							                  controller.give(player, loaded);

							                  plugin.send(player, Lang.MENU__PURCHASE__SUCCESS,

							                              "{pet_name}",
							                              loaded.select(PetAttributes.NAME).orElse(loaded.select(PetAttributes.UUID).orElse("Pet")),

							                              "{cost}",
							                              loaded.select(PetAttributes.COST).orElse(Cost.FREE).toStringFormatted(player));

						                  },
						                  reason ->
						                  {
							                  plugin.send(player, Lang.MENU__PURCHASE__FAILURE,

							                              "{pet_name}",
							                              loaded.select(PetAttributes.NAME).orElse(loaded.select(PetAttributes.UUID).orElse("Pet")),

							                              "{cost}",
							                              loaded.select(PetAttributes.COST).orElse(Cost.FREE).toStringFormatted(player),

							                              "{reason}",
							                              reason);
						                  });
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


	private @NotNull String resolvePetItemName(@NotNull final ItemStack buttonItem, @NotNull final Pet loaded, @Nullable final Pet target)
	{
		final String buttonName = Stacks.getName(buttonItem);

		String custom = null;

		if (target != null)
		{
			custom = target.select(PetAttributes.NAME).orElse(null);
		}

		if (custom == null)
		{
			custom = loaded.select(PetAttributes.NAME).orElse(loaded.getUuid());
		}

		if (buttonName != null)
		{
			custom = buttonName.replace("{pet_name}", custom);
		}

		return custom;
	}

	private @NotNull List<String> resolvePetItemLore(@NotNull final ItemStack buttonItem, @Nullable final Pet target)
	{
		final List<String> custom = new ArrayList<>();

		for (final String line : Stacks.geLore(buttonItem))
		{
			final String lower = line.toLowerCase(Locale.ROOT);

			if (lower.contains("{pet_status}"))
			{
				final String status = resolve(player, target == null ? Lang.MENU__BUTTONS__STATUS__NOT : Lang.MENU__BUTTONS__STATUS__HAS);
				custom.addAll(Arrays.asList(line.replace("{pet_status}", status).split("\n")));
			}

			if (target != null)
			{
				if (lower.contains("{pet_action}"))
				{
					final String action = resolve(player, target.getEntity() == null ? Lang.MENU__BUTTONS__ACTION__SUMMON : Lang.MENU__BUTTONS__ACTION__REMOVE);
					custom.addAll(Arrays.asList(line.replace("{pet_action}", action).split("\n")));
				}

				if (lower.contains("{pet_effect}"))
				{
					final String effect = resolve(player, Lang.MENU__BUTTONS__EFFECT__FORMAT);

					target.select(PetAttributes.PERK_EFFECT).ifPresent(perk -> {
						custom.add(" ");
						custom.addAll(Arrays.asList(effect.replace("{effect_name}",
						                                           Format.name(perk.getEffect().getType()))
						                                  .replace("{effect_level}",
						                                           String.valueOf(perk.getEffect().getAmplifier() + 1)).split("\n")));
					});

					target.select(PetAttributes.PERK_EFFECT_GROUP).ifPresent(perk -> {
						custom.add(" ");
						perk.getEffects().forEach(each -> {
							custom.addAll(Arrays.asList(effect.replace("{effect_name}",
							                                           Format.name(each.getType()))
							                                  .replace("{effect_level}",
							                                           String.valueOf(each.getAmplifier() + 1)).split("\n")));
						});
					});
				}
			}
		}

		return custom;
	}


	private @NotNull String resolve(@NotNull final Player player, @NotNull final Lang lang)
	{
		return plugin.lang(player, lang);
	}

}