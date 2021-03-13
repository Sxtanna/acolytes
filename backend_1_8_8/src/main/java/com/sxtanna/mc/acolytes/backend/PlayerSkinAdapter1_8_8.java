package com.sxtanna.mc.acolytes.backend;

import org.jetbrains.annotations.NotNull;

import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.lang.reflect.Field;
import java.util.UUID;

public final class PlayerSkinAdapter1_8_8 implements PlayerSkinAdapter
{

	private static Field profileField;

	static
	{
		try
		{
			final Class<?> clazz = Class.forName("org.bukkit.craftbukkit.v1_8_R3.inventory.CraftMetaSkull");

			profileField = clazz.getDeclaredField("profile");
			profileField.setAccessible(true);

		}
		catch (final ClassNotFoundException | NoSuchFieldException ex)
		{
			ex.printStackTrace();
		}
	}


	@Override
	public void updateSkullMeta(@NotNull final SkullMeta meta, @NotNull final String skin)
	{
		final GameProfile profile = new GameProfile(UUID.randomUUID(), "acolytes");
		profile.getProperties().put("textures", new Property("textures", skin));

		try
		{
			profileField.set(meta, profile);
		}
		catch (final IllegalAccessException ex)
		{
			ex.printStackTrace();
		}
	}

}