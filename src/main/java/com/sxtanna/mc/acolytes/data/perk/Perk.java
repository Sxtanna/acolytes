package com.sxtanna.mc.acolytes.data.perk;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Perk
{

	@NotNull
	private final String name;


	protected Perk(@NotNull final String name)
	{
		this.name = name;
	}


	@Contract(pure = true)
	public final @NotNull String getName()
	{
		return this.name;
	}


	public abstract void apply(@NotNull final LivingEntity target);


	public static final class Effect extends Perk
	{

		@NotNull
		private final PotionEffect effect;


		public Effect(@NotNull final PotionEffect effect)
		{
			super(String.format("%s %d", effect.getType().getName(), effect.getAmplifier() + 1));

			this.effect = effect;
		}


		@Contract(pure = true)
		public @NotNull PotionEffect getEffect()
		{
			return this.effect;
		}


		@Override
		public void apply(@NotNull final LivingEntity target)
		{
			target.addPotionEffect(getEffect(), true);
		}

	}

	public static final class EffectGroup extends Perk
	{

		@NotNull
		private final List<PotionEffect> effects;


		public EffectGroup(@NotNull final List<PotionEffect> effects)
		{
			super(effects.stream().map(effect -> String.format("%s %d", effect.getType().getName(), effect.getAmplifier() + 1)).collect(Collectors.joining("\n")));

			this.effects = effects;
		}

		public EffectGroup(@NotNull final PotionEffect... effects)
		{
			this(Arrays.asList(effects));
		}


		@Contract(pure = true)
		public @NotNull List<PotionEffect> getEffects()
		{
			return this.effects;
		}


		@Override
		public void apply(@NotNull final LivingEntity target)
		{
			getEffects().forEach(effect -> target.addPotionEffect(effect, true));
		}

	}

}