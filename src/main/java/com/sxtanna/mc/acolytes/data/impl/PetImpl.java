package com.sxtanna.mc.acolytes.data.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.sxtanna.mc.acolytes.backend.PetEntity;
import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.data.PetAttribute;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public final class PetImpl implements Pet
{

	public static @NotNull Pet defaultPet()
	{
		final Pet pet = new PetImpl();

		pet.update(PetAttributes.UUID, "default");
		pet.update(PetAttributes.NAME, "&3&lKiggy");
		pet.update(PetAttributes.SKIN, PetAttributes.SKIN.getDefaultAttr());

		return pet;
	}


	@NotNull
	private final Map<String, Object>        attributes = new HashMap<>();
	@NotNull
	private final AtomicReference<PetEntity> liveEntity = new AtomicReference<>();


	@Override
	public @Nullable PetEntity getEntity()
	{
		return this.liveEntity.get();
	}

	@Override
	public @Nullable PetEntity setEntity(@Nullable final PetEntity entity)
	{
		return this.liveEntity.getAndSet(entity);
	}


	@Override
	public <T> @Nullable T select(@NotNull final PetAttribute<T> attribute)
	{
		return attribute.getType().cast(this.attributes.get(attribute.getName()));
	}

	@Override
	public <T> @Nullable T update(@NotNull final PetAttribute<T> attribute, @Nullable final T value)
	{
		if (!attribute.isMutable() && this.attributes.containsKey(attribute.getName()))
		{
			throw new IllegalStateException(String.format("attribute %s is not mutable", attribute.getName()));
		}

		return attribute.getType().cast(this.attributes.put(attribute.getName(), value));
	}

}