package com.sxtanna.mc.acolytes.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.backend.PetEntity;
import com.sxtanna.mc.acolytes.backend.PlayerSkinAdapter;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;
import com.sxtanna.mc.acolytes.util.bukkit.Stacks;

import java.util.Optional;

public interface Pet
{

	@NotNull Pet copy();


	@Nullable PetEntity getEntity();

	@Nullable PetEntity setEntity(@Nullable PetEntity entity);


	@NotNull <T> Optional<T> select(@NotNull final PetAttribute<T> attribute);

	@NotNull <T> Optional<T> update(@NotNull final PetAttribute<T> attribute, @Nullable T value);


	void pushAttrs(@NotNull final AcolytesPlugin plugin);


	default @NotNull String getUuid()
	{
		return select(PetAttributes.UUID).orElseThrow(() -> new IllegalStateException("pet does not have a uuid?"));
	}

	default @NotNull ItemStack createHeadItem(@NotNull final PlayerSkinAdapter adapter)
	{
		return Stacks.item(Material.SKULL_ITEM, 1, 3, meta ->
		{
			Stacks.name(meta, " ");
			Stacks.flag(meta, ItemFlag.values());

			select(PetAttributes.SKIN).ifPresent(skin -> {
				adapter.updateSkullMeta(((SkullMeta) meta), skin);
			});
		});
	}

}