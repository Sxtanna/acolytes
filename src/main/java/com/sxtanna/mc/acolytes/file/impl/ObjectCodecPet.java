package com.sxtanna.mc.acolytes.file.impl;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.data.PetAttribute;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;
import com.sxtanna.mc.acolytes.data.impl.PetImpl;
import com.sxtanna.mc.acolytes.file.ObjectCodec;

import java.util.Locale;

public final class ObjectCodecPet implements ObjectCodec<Pet>
{

	@NotNull
	public static final ObjectCodec<Pet> INSTANCE = new ObjectCodecPet();


	@Override
	public @NotNull Pet pull(@NotNull final ConfigurationSection yaml)
	{
		final Pet pet = new PetImpl();

		for (final String name : yaml.getKeys(false))
		{
			switch (name.toLowerCase(Locale.ROOT))
			{
				case "uuid":
					pet.update(PetAttributes.UUID, PetAttributes.UUID.getType().cast(yaml.get(name)));
					break;
				case "name":
					pet.update(PetAttributes.NAME, PetAttributes.NAME.getType().cast(yaml.get(name)));
					break;
				case "skin":
					pet.update(PetAttributes.SKIN, PetAttributes.SKIN.getType().cast(yaml.get(name)));
					break;
				default:
					throw new IllegalStateException(String.format("unknown attribute in pet: %s", name));
			}
		}

		return pet;
	}

	@Override
	public void push(@NotNull final ConfigurationSection yaml, @NotNull final Pet value)
	{
		for (final PetAttribute<?> attr : PetAttributes.ATTRIBUTES.values())
		{
			value.select(attr).ifPresent(data -> yaml.set(attr.getName(), data));
		}
	}

}