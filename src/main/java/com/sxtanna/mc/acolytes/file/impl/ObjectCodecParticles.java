package com.sxtanna.mc.acolytes.file.impl;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.sxtanna.mc.acolytes.data.perk.Perk;
import com.sxtanna.mc.acolytes.file.ObjectCodec;

import xyz.xenondevs.particle.ParticleEffect;

public enum ObjectCodecParticles implements ObjectCodec<Perk.Particles>
{
	INSTANCE;


	@Override
	public @NotNull Perk.Particles pull(@NotNull final ConfigurationSection yaml)
	{
		final String name = yaml.getString("name");

		return new Perk.Particles(ParticleEffect.valueOf(name));
	}

	@Override
	public void push(@NotNull final ConfigurationSection yaml, @NotNull final Perk.Particles value)
	{
		yaml.set("name", value.getEffect().name());
	}

}