package com.sxtanna.mc.acolytes.data.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.backend.PetEntity;
import com.sxtanna.mc.acolytes.conf.AcolytesConfig;
import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.data.PetAttribute;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;
import com.sxtanna.mc.acolytes.util.Colors;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
	public @NotNull <T> Optional<T> select(@NotNull final PetAttribute<T> attribute)
	{
		return Optional.ofNullable(attribute.getType().cast(this.attributes.get(attribute.getName())));
	}

	@Override
	public @NotNull <T> Optional<T> update(@NotNull final PetAttribute<T> attribute, @Nullable final T value)
	{
		if (!attribute.isMutable() && this.attributes.containsKey(attribute.getName()))
		{
			throw new IllegalStateException(String.format("attribute %s is not mutable", attribute.getName()));
		}

		return Optional.ofNullable(attribute.getType().cast(this.attributes.put(attribute.getName(), value)));
	}


	@Override
	public void pushAttrs(@NotNull final AcolytesPlugin plugin)
	{
		final Entity entity = Optional.ofNullable(getEntity()).map(PetEntity::getBukkitEntity).orElse(null);
		if (entity == null)
		{
			return;
		}

		select(PetAttributes.UUID).ifPresent(uuid -> {
			entity.setMetadata("acolyte_uuid", new FixedMetadataValue(plugin, uuid));
		});

		select(PetAttributes.NAME).ifPresent(name -> {
			entity.setCustomName(Colors.colorize(name));
			entity.setCustomNameVisible(plugin.getConfiguration().get(AcolytesConfig.Basic.PET_DETAILS_NAME_VISIBLE));
		});

		if (entity instanceof LivingEntity)
		{
			((LivingEntity) entity).getEquipment().setHelmet(createHeadItem(plugin.getModule().getAdapter()));
		}
	}

}