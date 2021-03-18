package com.sxtanna.mc.acolytes.menu.impl;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.conf.AcolytesConfig;
import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;
import com.sxtanna.mc.acolytes.data.perk.Perk;
import com.sxtanna.mc.acolytes.menu.Menu;
import com.sxtanna.mc.acolytes.util.bukkit.Stacks;

import xyz.xenondevs.particle.ParticleEffect;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sxtanna.mc.acolytes.util.bukkit.Colors.colorize;

public final class MenuParticles extends Menu
{

	public static final String PARTICLES_KEY = "P";


	@NotNull
	private final Pet            pet;
	@NotNull
	private final Player         player;
	@NotNull
	private final AcolytesPlugin plugin;
	@NotNull
	private final MenuDecoded    decode;


	public MenuParticles(@NotNull final AcolytesPlugin plugin, @NotNull final Pet pet, @NotNull final Player player, @NotNull final MenuDecoded decode)
	{
		super(decode.getRequiredSize(),
		      colorize(plugin.getConfiguration().get(AcolytesConfig.Menus.PARTICLES_MENU_TITLE)));

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


		final Optional<Perk.Particles> current = pet.select(PetAttributes.PARTICLES);

		final List<ParticleEffect> entries = Arrays.stream(ParticleEffect.values()).filter(effect -> !effect.getFieldName().equals("NONE")).collect(Collectors.toList());
		System.out.println("available particles " + entries.size());

		final Iterator<ParticleEffect> effects = entries.iterator();

		for (final Map.Entry<String, MenuButton> button : buttons.entrySet())
		{
			final List<MenuCreationFunction> functions = decode.getCreationData().get(button.getKey().charAt(0));
			if (functions == null || functions.isEmpty())
			{
				continue;
			}

			for (final MenuCreationFunction function : functions)
			{
				if (!button.getKey().equals(PARTICLES_KEY) || !effects.hasNext())
				{
					function.accept(this, button.getValue().getItemStack(), event -> { });
					continue;
				}

				final ParticleEffect effect = effects.next();

				final MenuButton effButton = plugin.getConfiguration().get(AcolytesConfig.Perks.PARTICLE_EFFECT_ICONS).get(effect.name());
				final MenuButton defButton = plugin.getConfiguration().get(AcolytesConfig.Perks.PARTICLE_EFFECT_ICONS).get("DEFAULT");

				ItemStack item;

				if (effButton != null)
				{
					item = effButton.getItemStack();
				}
				else if (defButton != null)
				{
					item = Stacks.meta(defButton.getItemStack(), meta ->
					{
						Stacks.name(meta, meta.getDisplayName().replace("{effect_name}", effect.name()));
					});
				}
				else
				{
					final ItemStack fall = MenuButton.builder()
					                                 .type(Material.INK_SACK)
					                                 .data(8)
					                                 .name("&9{effect_name}")
					                                 .build().getItemStack();

					item = Stacks.meta(fall, meta ->
					{
						Stacks.name(meta, meta.getDisplayName().replace("{effect_name}", effect.name()));
					});
				}

				item = Stacks.meta(item, meta ->
				{
					Stacks.flag(meta, ItemFlag.values());

					if (current.isPresent() && current.get().getEffect() == effect)
					{
						meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
					}
				});

				function.accept(this, item, event -> {
					pet.update(PetAttributes.PARTICLES, new Perk.Particles(effect));
					redo();
				});
			}
		}
	}

}