package com.sxtanna.mc.acolytes.pets.controller;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.backend.PetConfig;
import com.sxtanna.mc.acolytes.backend.PetEntity;
import com.sxtanna.mc.acolytes.conf.AcolytesConfig;
import com.sxtanna.mc.acolytes.data.Pet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;

import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.stream.Collectors.toMap;

public final class PetControllerLocal implements PetController, Listener
{

	@NotNull
	private final AcolytesPlugin plugin;


	@NotNull
	private final Map<UUID, Pet>              active = new HashMap<>();
	@NotNull
	private final Map<String, Pet>            loaded = new HashMap<>();
	@NotNull
	private final Map<UUID, Map<String, Pet>> cached = new HashMap<>();


	public PetControllerLocal(@NotNull final AcolytesPlugin plugin)
	{
		this.plugin = plugin;
	}


	@Override
	public void load()
	{
		plugin.getModule().getRepository().select().whenComplete((pass, fail) -> {
			if (fail != null)
			{
				this.plugin.getLogger().log(Level.SEVERE, "failed to select default pets from repository", fail);
			}
			else if (pass != null && !pass.isEmpty())
			{
				this.loaded.putAll(pass.stream().collect(toMap(Pet::getUuid, Function.identity())));
			}
		});

		Bukkit.getOnlinePlayers().forEach(this::load);

		Bukkit.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@Override
	public void kill()
	{
		this.active.values().forEach(pet -> Optional.ofNullable(pet.setEntity(null)).map(PetEntity::getBukkitEntity).ifPresent(Entity::remove));
		this.active.clear();

		Bukkit.getOnlinePlayers().forEach(this::kill);

		HandlerList.unregisterAll(this);
	}


	@Override
	public @NotNull Map<String, Pet> getLoadedPets()
	{
		return this.loaded;
	}

	@Override
	public @NotNull Map<String, Pet> getPlayerPets(@NotNull final UUID player)
	{
		return Optional.ofNullable(this.cached.get(player)).orElse(Collections.emptyMap());
	}


	@Override
	public @NotNull Optional<Pet> getActive(@NotNull final UUID player)
	{
		return Optional.ofNullable(this.active.get(player));
	}


	@Override
	public @NotNull Optional<Pet> getByUuid(@NotNull final String uuid)
	{
		return Optional.ofNullable(this.loaded.get(uuid));
	}

	@Override
	public @NotNull Optional<Pet> getByUuid(@NotNull final UUID player, @NotNull final String uuid)
	{
		return Optional.ofNullable(this.cached.get(player)).map(cached -> cached.get(uuid));
	}


	@Override
	public void load(@NotNull final Player player, @NotNull final Pet pet)
	{
		final Location origin = player.getLocation();

		final double offX = plugin.getConfiguration().get(AcolytesConfig.Basic.PET_SUMMON_OFFSET_X);
		final double offY = plugin.getConfiguration().get(AcolytesConfig.Basic.PET_SUMMON_OFFSET_Y);
		final double offZ = plugin.getConfiguration().get(AcolytesConfig.Basic.PET_SUMMON_OFFSET_Z);
		origin.add(current().nextDouble(-offX, +offX),
		           current().nextDouble(-offY, +offY),
		           current().nextDouble(-offZ, +offZ));


		final PetEntity entity = plugin.getModule().getProvider().spawn(getPetConfig(pet), origin);
		entity.setTargetEntity(player);

		pet.setEntity(entity);
		pet.pushAttrs(plugin);

		this.active.put(player.getUniqueId(), pet);
	}

	@Override
	public void kill(@NotNull final Player player, @NotNull final Pet pet)
	{
		final Pet active = this.active.get(player.getUniqueId());
		if (!active.equals(pet))
		{
			return; // not active pet
		}

		this.active.remove(player.getUniqueId());


		final PetEntity entity = pet.setEntity(null);
		if (entity == null)
		{
			return; // not spawned
		}

		entity.setTargetEntity(null);
		entity.getBukkitEntity().remove();
	}


	@Override
	public boolean give(@NotNull final Player player, @NotNull final Pet pet)
	{
		final Map<String, Pet> pets = this.cached.computeIfAbsent(player.getUniqueId(), ($) -> new HashMap<>());
		if (pets.containsKey(pet.getUuid()))
		{
			return false;
		}

		pets.put(pet.getUuid(), pet.copy());

		save(player, false);

		return true;
	}

	@Override
	public boolean take(@NotNull final Player player, @NotNull final Pet pet)
	{
		final Map<String, Pet> pets = this.cached.get(player.getUniqueId());
		if (pets == null || pets.isEmpty())
		{
			return false;
		}

		pets.remove(pet.getUuid());

		save(player, false);

		return true;
	}


	private void load(@NotNull final Player player)
	{
		plugin.getModule()
		      .getRepository()
		      .select(player.getUniqueId())
		      .whenComplete((pass, fail) -> {
			      if (fail != null)
			      {
				      this.plugin.getLogger().log(Level.SEVERE, String.format("failed to select pets from repository for %s", player.getUniqueId()), fail);
			      }
			      else if (pass != null && !pass.isEmpty())
			      {
				      this.cached.put(player.getUniqueId(), pass.stream().collect(toMap(Pet::getUuid, Function.identity())));
			      }
		      });
	}

	private void kill(@NotNull final Player player)
	{
		save(player, true);
	}

	private void save(@NotNull final Player player, final boolean remove)
	{
		final Collection<Pet> values = Optional.ofNullable(!remove ? this.cached.get(player.getUniqueId()) : this.cached.remove(player.getUniqueId())).map(Map::values).orElse(null);

		if (values == null || values.isEmpty())
		{
			plugin.getModule()
			      .getRepository()
			      .delete(player.getUniqueId()); // maybe not a great idea?
		}
		else
		{
			plugin.getModule()
			      .getRepository()
			      .insert(player.getUniqueId(), values)
			      .whenComplete((pass, fail) -> {
				      if (fail != null)
				      {
					      this.plugin.getLogger().log(Level.SEVERE, String.format("failed to insert pets into repository for %s", player.getUniqueId()), fail);
				      }
			      });
		}
	}


	private @NotNull PetConfig getPetConfig(@NotNull final Pet pet)
	{
		return new PetConfig(plugin.getConfiguration().get(AcolytesConfig.Basic.PET_DETAILS_SMALL_HEAD),
		                     plugin.getConfiguration().get(AcolytesConfig.Basic.PET_DETAILS_HEAD_LOOK),
		                     plugin.getConfiguration().get(AcolytesConfig.Basic.PET_DETAILS_HEAD_LOOK_AND_PITCH),

		                     plugin.getConfiguration().get(AcolytesConfig.Basic.PET_DETAILS_BOBBING),
		                     plugin.getConfiguration().get(AcolytesConfig.Basic.PET_DETAILS_BOBBING_SPEED),
		                     plugin.getConfiguration().get(AcolytesConfig.Basic.PET_DETAILS_BOBBING_RANGE_SHIFT),
		                     plugin.getConfiguration().get(AcolytesConfig.Basic.PET_DETAILS_BOBBING_RANGE_LIMIT),

		                     plugin.getConfiguration().get(AcolytesConfig.Basic.PET_PATHING_SPEED),
		                     plugin.getConfiguration().get(AcolytesConfig.Basic.PET_PATHING_RANGE_MIN),
		                     plugin.getConfiguration().get(AcolytesConfig.Basic.PET_PATHING_RANGE_MAX),

		                     plugin.getConfiguration().get(AcolytesConfig.Basic.PET_PATHING_TELEPORT_DISTANCE),

		                     plugin.getConfiguration().get(AcolytesConfig.Basic.PET_PATHING_TELEPORT_OFFSET_X),
		                     plugin.getConfiguration().get(AcolytesConfig.Basic.PET_PATHING_TELEPORT_OFFSET_Y),
		                     plugin.getConfiguration().get(AcolytesConfig.Basic.PET_PATHING_TELEPORT_OFFSET_Z));
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