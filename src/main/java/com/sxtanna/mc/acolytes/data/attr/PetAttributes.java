package com.sxtanna.mc.acolytes.data.attr;

import org.jetbrains.annotations.Unmodifiable;

import com.sxtanna.mc.acolytes.data.PetAttribute;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum PetAttributes
{
	;


	public static final PetAttribute<String> UUID = PetAttribute.make(String.class, "uuid", "", false);
	public static final PetAttribute<String> NAME = PetAttribute.make(String.class, "name", "");
	public static final PetAttribute<String> SKIN = PetAttribute.make(String.class, "skin", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTAyOGUzYzZmNDM1MGIzZTQyNGIzNzJmM2M5NDZmNTdiNzgzYjlhYTBlNGUyOTg5MWZmNTc4NDQxZWYwMTMwOCJ9fX0=");


	@Unmodifiable
	public static final Map<String, PetAttribute<?>> ATTRIBUTES = Collections.unmodifiableMap(Stream.of(UUID, NAME, SKIN).collect(Collectors.toMap(PetAttribute::getName, Function.identity())));

}