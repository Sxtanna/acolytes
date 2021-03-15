package com.sxtanna.mc.acolytes.file.impl;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sxtanna.mc.acolytes.file.ObjectCodec;

import com.google.common.primitives.Ints;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public enum ObjectCodecPotionEffect implements ObjectCodec<PotionEffect>
{
	INSTANCE;


	@Override
	public final @NotNull PotionEffect pull(@NotNull final ConfigurationSection yaml)
	{
		final String name = yaml.getString("name");

		final int time;
		final int tier = yaml.getInt("tier");

		final String timeString = yaml.getString("time");
		if (timeString.equalsIgnoreCase("infinite"))
		{
			time = Integer.MAX_VALUE;
		}
		else
		{
			//noinspection UnstableApiUsage
			time = Optional.ofNullable(Ints.tryParse(timeString)).orElse(Integer.MAX_VALUE);
		}

		final boolean lessParticles = yaml.getBoolean("less-particles");
		final boolean showParticles = yaml.getBoolean("show-particles");

		final PotionEffectType type = checkNotNull(PotionEffectType.getByName(name),
		                                           String.format("could not find particle effect named %s", name));

		return new PotionEffect(type, time, tier, lessParticles, showParticles);
	}

	@Override
	public final void push(@NotNull final ConfigurationSection yaml, @NotNull final PotionEffect value)
	{
		yaml.set("name", value.getType().getName());
		yaml.set("time", value.getDuration() != Integer.MAX_VALUE ? value.getDuration() : "infinite");
		yaml.set("tier", value.getAmplifier());
		yaml.set("less-particles", value.isAmbient());
		yaml.set("show-particles", value.hasParticles());
	}
}