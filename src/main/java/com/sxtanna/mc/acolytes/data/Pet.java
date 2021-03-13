package com.sxtanna.mc.acolytes.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.sxtanna.mc.acolytes.backend.PetEntity;
import com.sxtanna.mc.acolytes.backend.PlayerSkinAdapter;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;

public interface Pet
{

	@Nullable PetEntity getEntity();

	@Nullable PetEntity setEntity(@Nullable PetEntity entity);


	@Nullable <T> T select(@NotNull final PetAttribute<T> attribute);

	@Nullable <T> T update(@NotNull final PetAttribute<T> attribute, @Nullable T value);


	default @NotNull ItemStack createHeadItem(@NotNull final PlayerSkinAdapter adapter)
	{
		final ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

		final String skin = select(PetAttributes.SKIN);
		if (skin != null)
		{
			final ItemMeta meta = head.getItemMeta();
			adapter.updateSkullMeta(((SkullMeta) meta), skin);

			head.setItemMeta(meta);
		}

		return head;
	}

}