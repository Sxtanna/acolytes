package com.sxtanna.mc.acolytes.data.perk;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import com.sxtanna.mc.acolytes.util.bukkit.Format;

import xyz.xenondevs.particle.ParticleEffect;

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


	public abstract void give(@NotNull final Entity target);

	public abstract void take(@NotNull final Entity target);


	public static final class Effect extends Perk
	{

		@NotNull
		private final PotionEffect effect;


		public Effect(@NotNull final PotionEffect effect)
		{
			super(Format.format(effect));

			this.effect = effect;
		}


		@Contract(pure = true)
		public @NotNull PotionEffect getEffect()
		{
			return this.effect;
		}


		@Override
		public void give(@NotNull final Entity target)
		{
			if (!(target instanceof LivingEntity))
			{
				return;
			}

			((LivingEntity) target).addPotionEffect(getEffect(), true);
		}

		@Override
		public void take(@NotNull final Entity target)
		{
			if (!(target instanceof LivingEntity))
			{
				return;
			}

			((LivingEntity) target).removePotionEffect(getEffect().getType());
		}

	}

	public static final class EffectGroup extends Perk
	{

		@NotNull
		private final List<PotionEffect> effects;


		public EffectGroup(@NotNull final List<PotionEffect> effects)
		{
			super(effects.stream().map(Format::format).collect(Collectors.joining("\n")));

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
		public void give(@NotNull final Entity target)
		{
			if (!(target instanceof LivingEntity))
			{
				return;
			}

			getEffects().forEach(effect -> ((LivingEntity) target).addPotionEffect(effect, true));
		}

		@Override
		public void take(@NotNull final Entity target)
		{
			if (!(target instanceof LivingEntity))
			{
				return;
			}

			getEffects().forEach(effect -> ((LivingEntity) target).removePotionEffect(effect.getType()));
		}

	}


	public static final class Particles extends Perk
	{

		@NotNull
		private final ParticleEffect effect;


		public Particles(@NotNull final ParticleEffect effect)
		{
			super(effect.name());

			this.effect = effect;
		}


		@Contract(pure = true)
		public @NotNull ParticleEffect getEffect()
		{
			return effect;
		}


		@Override
		public void give(@NotNull final Entity target)
		{
			effect.display(!(target instanceof LivingEntity) ?
			               target.getLocation() :
			               ((LivingEntity) target).getEyeLocation().subtract(0.0, 0.4, 0.0));
		}

		@Override
		public void take(@NotNull final Entity target)
		{
			// nothing
		}

	}

}