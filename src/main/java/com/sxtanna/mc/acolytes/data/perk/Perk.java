package com.sxtanna.mc.acolytes.data.perk;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

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

}