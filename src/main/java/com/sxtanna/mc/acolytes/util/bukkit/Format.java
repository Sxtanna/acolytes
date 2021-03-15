package com.sxtanna.mc.acolytes.util.bukkit;

import org.jetbrains.annotations.NotNull;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.potion.PotionEffectType.*;

public enum Format
{
	;

	private static final Map<PotionEffectType, String> NAMES = new HashMap<>();

	static
	{
		NAMES.put(SPEED, "Speed");
		NAMES.put(SLOW, "Slowness");
		NAMES.put(FAST_DIGGING, "Haste");
		NAMES.put(SLOW_DIGGING, "Mining Fatigue");
		NAMES.put(INCREASE_DAMAGE, "Strength");
		NAMES.put(HEAL, "Instant Health");
		NAMES.put(HARM, "Instant Damage");
		NAMES.put(JUMP, "Jump Boost");
		NAMES.put(CONFUSION, "Nausea");
		NAMES.put(REGENERATION, "Regeneration");
		NAMES.put(DAMAGE_RESISTANCE, "Resistance");
		NAMES.put(FIRE_RESISTANCE, "Fire Resistance");
		NAMES.put(WATER_BREATHING, "Water Breathing");
		NAMES.put(INVISIBILITY, "Invisibility");
		NAMES.put(BLINDNESS, "Blindness");
		NAMES.put(NIGHT_VISION, "Night Vision");
		NAMES.put(HUNGER, "Hunger");
		NAMES.put(WEAKNESS, "Weakness");
		NAMES.put(POISON, "Poison");
		NAMES.put(WITHER, "Wither");
		NAMES.put(HEALTH_BOOST, "Health Boost");
		NAMES.put(ABSORPTION, "Absorption");
		NAMES.put(SATURATION, "Saturation");
	}


	public static @NotNull String format(@NotNull final PotionEffect effect)
	{
		return String.format("&7%s &a%d",
		                     NAMES.getOrDefault(effect.getType(), effect.getType().getName()),
		                     (effect.getAmplifier() + 1));
	}

}