package com.sxtanna.mc.acolytes.data.attr;

import org.jetbrains.annotations.Unmodifiable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sxtanna.mc.acolytes.data.PetAttribute;
import com.sxtanna.mc.acolytes.data.perk.Perk;
import com.sxtanna.mc.acolytes.file.impl.ObjectCodecPotionEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum PetAttributes
{
	;


	public static final PetAttribute<String> UUID = PetAttribute.builder(String.class, "uuid")
	                                                            .defaultAttr("")
	                                                            .mutable(false)
	                                                            .build();


	public static final PetAttribute<String> NAME = PetAttribute.builder(String.class, "name")
	                                                            .defaultAttr("")
	                                                            .build();


	public static final PetAttribute<String> SKIN = PetAttribute.builder(String.class, "skin")
	                                                            .defaultAttr("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTAyOGUzYzZmNDM1MGIzZTQyNGIzNzJmM2M5NDZmNTdiNzgzYjlhYTBlNGUyOTg5MWZmNTc4NDQxZWYwMTMwOCJ9fX0=")
	                                                            .build();


	public static final PetAttribute<Perk.Effect> PERK_EFFECT = PetAttribute.builder(Perk.Effect.class, "perk_effect")
	                                                                        .defaultAttr(new Perk.Effect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false)))
	                                                                        .copier((perk) -> new Perk.Effect(new PotionEffect(perk.getEffect().getType(),
	                                                                                                                           perk.getEffect().getDuration(),
	                                                                                                                           perk.getEffect().getAmplifier(),
	                                                                                                                           perk.getEffect().isAmbient(),
	                                                                                                                           perk.getEffect().hasParticles())))
	                                                                        .writer((yaml, value) -> {
		                                                                        ObjectCodecPotionEffect.INSTANCE.push(yaml.createSection("perk.perk_effect"), value.getEffect());
	                                                                        })
	                                                                        .reader(yaml -> {
		                                                                        return new Perk.Effect(ObjectCodecPotionEffect.INSTANCE.pull(yaml.getConfigurationSection("perk.perk_effect")));
	                                                                        })
	                                                                        .build();

	public static final PetAttribute<Perk.EffectGroup> PERK_EFFECT_GROUP = PetAttribute.builder(Perk.EffectGroup.class, "perk_effect_group")
	                                                                                   .defaultAttr(new Perk.EffectGroup(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false),
	                                                                                                                     new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1, false, false)))
	                                                                                   .copier((perk) -> {
		                                                                                   final List<PotionEffect> effects = new ArrayList<>();

		                                                                                   for (final PotionEffect effect : perk.getEffects())
		                                                                                   {
			                                                                                   effects.add(new PotionEffect(effect.getType(),
			                                                                                                                effect.getDuration(),
			                                                                                                                effect.getAmplifier(),
			                                                                                                                effect.isAmbient(),
			                                                                                                                effect.hasParticles()));
		                                                                                   }

		                                                                                   return new Perk.EffectGroup(effects);
	                                                                                   })
	                                                                                   .writer((yaml, value) -> {
		                                                                                   final ConfigurationSection section = yaml.createSection("perk.perk_effect_group");

		                                                                                   for (int i = 0; i < value.getEffects().size(); i++)
		                                                                                   {
			                                                                                   ObjectCodecPotionEffect.INSTANCE.push(section.createSection(String.valueOf(i)), value.getEffects().get(i));
		                                                                                   }
	                                                                                   })
	                                                                                   .reader(yaml -> {

		                                                                                   final ConfigurationSection section = yaml.getConfigurationSection("perk.perk_effect_group");

		                                                                                   final List<PotionEffect> effects = new ArrayList<>();

		                                                                                   for (final String key : section.getKeys(false))
		                                                                                   {
			                                                                                   effects.add(ObjectCodecPotionEffect.INSTANCE.pull(section.getConfigurationSection(key)));
		                                                                                   }

		                                                                                   return new Perk.EffectGroup(effects);
	                                                                                   })
	                                                                                   .build();


	@Unmodifiable
	public static final Map<String, PetAttribute<?>> ATTRIBUTES = Collections.unmodifiableMap(Stream.of(UUID, NAME, SKIN, PERK_EFFECT, PERK_EFFECT_GROUP)
	                                                                                                .collect(toMap(PetAttribute::getName,
	                                                                                                               Function.identity())));

}