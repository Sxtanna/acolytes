package com.sxtanna.mc.acolytes.conf.mapper;

import org.bukkit.Material;

import com.sxtanna.mc.acolytes.menu.Menu;

import ch.jalu.configme.beanmapper.leafvaluehandler.AbstractLeafValueHandler;
import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class MenuButtonLeafHandler extends AbstractLeafValueHandler
{

	@Override
	protected Object convert(final Class<?> clazz, final Object value)
	{
		if (clazz.equals(Menu.MenuButton.class) && value instanceof Map)
		{
			final Menu.MenuButton button = new Menu.MenuButton();

			final Object typeInput = ((Map<?, ?>) value).get("type");
			final Object dataInput = ((Map<?, ?>) value).get("data");
			final Object nameInput = ((Map<?, ?>) value).get("name");
			final Object loreInput = ((Map<?, ?>) value).get("lore");

			if (typeInput != null)
			{
				final Material type = typeInput instanceof Material ? (Material) typeInput : Material.matchMaterial(typeInput.toString());
				button.setType(type);
			}

			if (dataInput != null)
			{
				//noinspection UnstableApiUsage
				Optional.ofNullable(Ints.tryParse(dataInput.toString())).ifPresent(button::setData);
			}

			if (nameInput != null)
			{
				button.setName(nameInput.toString());
			}

			if (loreInput != null)
			{
				final List<String> lore = loreInput instanceof List ?
				                          ((List<?>) loreInput).stream().map(Object::toString).collect(Collectors.toList()) :
				                          loreInput instanceof String ?
				                          Arrays.asList(((String) loreInput).split("\b")) :
				                          new ArrayList<>();

				button.setLore(lore);
			}

			return button;
		}

		return null;
	}

	@Override
	public Object toExportValue(final Object value)
	{
		if (value instanceof Menu.MenuButton)
		{
			final Menu.MenuButton button = (Menu.MenuButton) value;

			final Map<String, Object> values = new LinkedHashMap<>();

			Optional.ofNullable(button.getType()).ifPresent(type -> values.put("type", type.name()));
			Optional.ofNullable(button.getData()).ifPresent(data -> values.put("data", data));
			Optional.ofNullable(button.getName()).ifPresent(name -> values.put("name", name));
			Optional.ofNullable(button.getLore()).ifPresent(lore -> values.put("lore", lore));

			return values;
		}

		return null;
	}

}