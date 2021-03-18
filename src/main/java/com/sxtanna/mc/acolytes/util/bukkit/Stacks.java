package com.sxtanna.mc.acolytes.util.bukkit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public enum Stacks
{
	;

	public static @NotNull ItemStack item(@NotNull final Material material, final int amount)
	{
		return item(material, amount, 0);
	}

	public static @NotNull ItemStack item(@NotNull final Material material, final int amount, final int damage)
	{
		return item(material, amount, damage, ($) -> {});
	}


	public static @NotNull ItemStack item(@NotNull final Material material, final int amount, @NotNull final Consumer<ItemMeta> consumer)
	{
		return item(material, amount, 0, consumer);
	}

	public static @NotNull ItemStack item(@NotNull final Material material, final int amount, final int damage, @NotNull final Consumer<ItemMeta> consumer)
	{
		return meta(new ItemStack(material, amount, ((short) damage)), consumer);
	}


	public static @NotNull ItemStack meta(@NotNull final ItemStack item, @NotNull final Consumer<ItemMeta> consumer)
	{
		final ItemMeta meta = item.getItemMeta();
		consumer.accept(meta);

		item.setItemMeta(meta);

		return item;
	}


	public static void name(@NotNull final ItemMeta meta, @NotNull final String name)
	{
		meta.setDisplayName(Colors.colorize(name));
	}

	public static void lore(@NotNull final ItemMeta meta, @NotNull final String... lore)
	{
		meta.setLore(Stream.of(lore).map(Colors::colorize).collect(Collectors.toList()));
	}

	public static void lore(@NotNull final ItemMeta meta, @NotNull final Iterable<String> lore)
	{
		meta.setLore(StreamSupport.stream(lore.spliterator(), false).map(Colors::colorize).collect(Collectors.toList()));
	}

	public static void flag(@NotNull final ItemMeta meta, @NotNull final ItemFlag... flags)
	{
		meta.addItemFlags(flags);
	}


	public static void clean(@NotNull final ItemMeta meta)
	{
		name(meta, " ");
		lore(meta, Collections.emptyList());
		flag(meta, ItemFlag.values());
	}


	public static @Nullable String getName(@NotNull final ItemStack item)
	{
		if (!item.hasItemMeta())
		{
			return null;
		}

		final ItemMeta meta = item.getItemMeta();
		if (!meta.hasDisplayName())
		{
			return null;
		}

		return meta.getDisplayName();
	}

	public static @NotNull List<String> geLore(@NotNull final ItemStack item)
	{
		if (!item.hasItemMeta())
		{
			return Collections.emptyList();
		}

		final ItemMeta meta = item.getItemMeta();
		if (!meta.hasLore())
		{
			return Collections.emptyList();
		}

		return meta.getLore();
	}

}