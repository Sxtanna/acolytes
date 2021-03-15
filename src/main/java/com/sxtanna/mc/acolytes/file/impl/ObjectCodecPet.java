package com.sxtanna.mc.acolytes.file.impl;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.data.PetAttribute;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;
import com.sxtanna.mc.acolytes.data.impl.PetImpl;
import com.sxtanna.mc.acolytes.file.ObjectCodec;

import java.util.Locale;

public enum ObjectCodecPet implements ObjectCodec<Pet>
{
	INSTANCE;


	@Override
	public final @NotNull Pet pull(@NotNull final ConfigurationSection yaml)
	{
		final Pet pet = new PetImpl();

		for (final String name : yaml.getKeys(false))
		{
			//noinspection rawtypes
			final PetAttribute attr = PetAttributes.ATTRIBUTES.get(name.toLowerCase(Locale.ROOT));
			if (attr == null)
			{
				throw new IllegalStateException(String.format("unknown attribute in pet: %s", name));
			}

			//noinspection unchecked
			pet.update(attr, attr.pull(yaml));
		}

		return pet;
	}

	@Override
	public final void push(@NotNull final ConfigurationSection yaml, @NotNull final Pet value)
	{
		//noinspection rawtypes
		for (final PetAttribute attr : PetAttributes.ATTRIBUTES.values())
		{
			//noinspection unchecked
			value.select(attr).ifPresent(data -> attr.push(yaml, data));
		}
	}

}