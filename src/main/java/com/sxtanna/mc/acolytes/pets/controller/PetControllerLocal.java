package com.sxtanna.mc.acolytes.pets.controller;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.backend.PetConfig;
import com.sxtanna.mc.acolytes.backend.PetEntity;
import com.sxtanna.mc.acolytes.backend.PetEntityProvider1_8_8;
import com.sxtanna.mc.acolytes.conf.AcolytesConfig;
import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;

import java.util.Collection;
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
		Bukkit.getOnlinePlayers().forEach(this::load);

		Bukkit.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@Override
	public void kill()
	{
		Bukkit.getOnlinePlayers().forEach(this::kill);

		HandlerList.unregisterAll(this);
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
		final PetEntityProvider1_8_8 provider = new PetEntityProvider1_8_8();
		provider.initialize();

		final PetEntity entity = provider.spawn(player.getLocation(),
		                                        new PetConfig(plugin.getConfiguration().get(AcolytesConfig.Basic.PET_PATHING_SPEED),
		                                                      plugin.getConfiguration().get(AcolytesConfig.Basic.PET_PATHING_RANGE_MIN),
		                                                      plugin.getConfiguration().get(AcolytesConfig.Basic.PET_PATHING_RANGE_MAX)));
		entity.setTargetEntity(player);


		final Zombie zombie = (Zombie) entity.getBukkitEntity();
		zombie.setBaby(true);
		zombie.getEquipment().setHelmet(new ItemStack(Material.SKULL_ITEM, 1, (short) 3));
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));

		// pet.setEntity(entity);
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
				      this.plugin.getLogger().log(Level.SEVERE, String.format("failed to select pets from repository for %s", player.getUniqueId()), fail);
			      }
			      else if (pass != null)
			      {
				      this.cached.put(player.getUniqueId(), pass.stream()
				                                                .collect(toMap(pet -> pet.select(PetAttributes.UUID),
				                                                               Function.identity())));
			      }
		      });
	}

	private void kill(@NotNull final Player player)
	{
		final Collection<Pet> values = Optional.ofNullable(this.cached.remove(player.getUniqueId())).map(Map::values).orElse(null);
		if (values == null)
		{
			return;
		}

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