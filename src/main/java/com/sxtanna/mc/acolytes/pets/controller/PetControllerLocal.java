package com.sxtanna.mc.acolytes.pets.controller;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;

import static java.util.stream.Collectors.toMap;

public final class PetControllerLocal implements PetController, Listener
{

	@NotNull
	private final AcolytesPlugin plugin;


	@NotNull
	private final Map<UUID, Pet>              active = new HashMap<>();
	@NotNull
	private final Map<UUID, Map<String, Pet>> cached = new HashMap<>();


	public PetControllerLocal(@NotNull final AcolytesPlugin plugin)
	{
		this.plugin = plugin;
	}


	@Override
	public void load()
	{
		Bukkit.getServer().getPluginManager().registerEvents(this, this.plugin);
		Bukkit.getOnlinePlayers().forEach(this::load);
	}

	@Override
	public void kill()
	{
		HandlerList.unregisterAll(this);
		Bukkit.getOnlinePlayers().forEach(this::kill);
	}


	@Override
	public @NotNull Optional<Pet> getActive(@NotNull final UUID player)
	{
		return Optional.ofNullable(this.active.get(player));
	}

	@Override
	public @NotNull Optional<Pet> getByUuid(@NotNull final UUID player, @NotNull final String uuid)
	{
		return Optional.ofNullable(this.cached.get(player)).map(cached -> cached.get(uuid));
	}


	@Override
	public void load(@NotNull final Player player, @NotNull final Pet pet)
	{

	}

	@Override
	public void kill(@NotNull final Player player, @NotNull final Pet pet)
	{

	}


	private void load(@NotNull final Player player)
	{
		plugin.getModule()
		      .getRepository()
		      .select(player.getUniqueId())
		      .whenComplete((pass, fail) -> {
			      if (fail != null)
			      {
				      plugin.getLogger().log(Level.SEVERE, String.format("failed to select pets from repository for %s", player.getUniqueId()), fail);
			      }
			      else if (pass != null)
			      {
				      cached.put(player.getUniqueId(), pass.stream()
				                                           .collect(toMap(pet -> pet.select(PetAttributes.UUID),
				                                                          Function.identity())));
			      }
		      });
	}

	private void kill(@NotNull final Player player)
	{
		plugin.getModule()
		      .getRepository()
		      .insert(player.getUniqueId(), this.cached.remove(player.getUniqueId()).values())
		      .whenComplete((pass, fail) -> {
			      if (fail != null)
			      {
				      plugin.getLogger().log(Level.SEVERE, String.format("failed to insert pets into repository for %s", player.getUniqueId()), fail);
			      }
		      });
	}


	@EventHandler
	public void onJoin(@NotNull final PlayerJoinEvent event)
	{
		load(event.getPlayer());
	}

	@EventHandler
	public void onQuit(@NotNull final PlayerQuitEvent event)
	{
		kill(event.getPlayer());
	}

}