package com.sxtanna.mc.acolytes.backend;

import org.jetbrains.annotations.NotNull;

import org.bukkit.inventory.meta.SkullMeta;

public interface PlayerSkinAdapter
{

	void updateSkullMeta(@NotNull final SkullMeta meta, @NotNull final String skin);

}