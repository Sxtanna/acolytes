package com.sxtanna.mc.acolytes.backend;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import net.minecraft.server.v1_8_R3.EntityTypes;

import java.lang.reflect.Field;
import java.util.Map;

public final class PetEntityProvider1_8_8 implements PetEntityProvider
{

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void initialize()
	{
		try
		{
			final Field c = EntityTypes.class.getDeclaredField("c");
			c.setAccessible(true);

			final Field d = EntityTypes.class.getDeclaredField("d");
			d.setAccessible(true);

			final Field f = EntityTypes.class.getDeclaredField("f");
			f.setAccessible(true);

			((Map) c.get(null)).put("ArmorStand", PetEntity1_8_8.class);
			((Map) d.get(null)).put(PetEntity1_8_8.class, "ArmorStand");
			((Map) f.get(null)).put(PetEntity1_8_8.class, 30);
		}
		catch (final NoSuchFieldException | IllegalAccessException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public @NotNull PetEntity spawn(@NotNull final PetConfig config, @NotNull final Location origin)
	{
		final PetEntity1_8_8 entity = new PetEntity1_8_8(origin.getWorld(), config);
		entity.setPositionRotation(origin.getX(), origin.getY(), origin.getZ(), origin.getYaw(), origin.getPitch());

		((CraftWorld) origin.getWorld()).getHandle().addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);

		entity.setSmall(config.isSmallHead());
		entity.setInvisible(true);
		entity.setBasePlate(false);

		return entity;
	}

}