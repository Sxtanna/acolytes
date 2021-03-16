package com.sxtanna.mc.acolytes.conf;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import com.sxtanna.mc.acolytes.conf.mapper.MenuButtonMapper;
import com.sxtanna.mc.acolytes.menu.Menu.MenuButton;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManagerImpl;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.properties.MapProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.types.BeanPropertyType;
import ch.jalu.configme.resource.YamlFileResource;

import java.io.File;
import java.util.Optional;

import static ch.jalu.configme.properties.PropertyInitializer.mapProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public final class AcolytesConfig extends SettingsManagerImpl
{

	public AcolytesConfig(@NotNull final Plugin plugin)
	{
		super(new YamlFileResource(new File(plugin.getDataFolder(), "config.yml").toPath()),

		      ConfigurationDataBuilder.createConfiguration(Basic.class,
		                                                   Menus.class,
		                                                   Costs.class,
		                                                   Perks.class),

		      new PlainMigrationService());
	}


	public <T> @NotNull T get(@NotNull final Property<T> property)
	{
		return getOptional(property).orElse(property.getDefaultValue());
	}

	public <T> @NotNull Optional<T> getOptional(@NotNull final Property<T> property)
	{
		return Optional.ofNullable(getProperty(property));
	}


	public static final class Basic implements SettingsHolder
	{

		public static final Property<Integer> PLAYER_RESOLVER_FUZZY_DISTANCE =
				newProperty("plugin.player-resolve.fuzzy-distance", 3);

		public static final Property<Integer> PET_CUSTOM_NAME_LENGTH_MIN =
				newProperty("plugin.pet.custom-name.min", 3);

		public static final Property<Integer> PET_CUSTOM_NAME_LENGTH_MAX =
				newProperty("plugin.pet.custom-name.max", 16);


		public static final Property<Double> PET_SUMMON_OFFSET_X =
				newProperty("plugin.pet.summon.offset.x", 5.0);

		public static final Property<Double> PET_SUMMON_OFFSET_Y =
				newProperty("plugin.pet.summon.offset.y", 1.0);

		public static final Property<Double> PET_SUMMON_OFFSET_Z =
				newProperty("plugin.pet.summon.offset.z", 5.0);


		public static final Property<Boolean> PET_DETAILS_SMALL_HEAD =
				newProperty("plugin.pet.details.head.small", false);

		public static final Property<Boolean> PET_DETAILS_NAME_VISIBLE =
				newProperty("plugin.pet.details.name.visible", true);

		public static final Property<Boolean> PET_DETAILS_HEAD_LOOK           =
				newProperty("plugin.pet.details.head-look.enabled", true);
		public static final Property<Boolean> PET_DETAILS_HEAD_LOOK_AND_PITCH =
				newProperty("plugin.pet.details.head-look.and-pitch", true);


		public static final Property<Boolean> PET_DETAILS_BOBBING =
				newProperty("plugin.pet.details.bobbing.enabled", true);

		public static final Property<Double> PET_DETAILS_BOBBING_RANGE_SHIFT =
				newProperty("plugin.pet.details.bobbing.range.shift", 0.1);

		public static final Property<Double> PET_DETAILS_BOBBING_RANGE_LIMIT =
				newProperty("plugin.pet.details.bobbing.range.limit", 0.5);

		public static final Property<Double> PET_DETAILS_BOBBING_SPEED =
				newProperty("plugin.pet.details.bobbing.speed", 0.05);

		public static final Property<Double> PET_PATHING_SPEED =
				newProperty("plugin.pet.pathing.speed", 1.25);

		public static final Property<Double> PET_PATHING_RANGE_MIN =
				newProperty("plugin.pet.pathing.range.min", 10.0);

		public static final Property<Double> PET_PATHING_RANGE_MAX =
				newProperty("plugin.pet.pathing.range.max", 5.0);

		public static final Property<Double> PET_PATHING_TELEPORT_DISTANCE =
				newProperty("plugin.pet.pathing.teleport.distance", 20.0);

		public static final Property<Double> PET_PATHING_TELEPORT_OFFSET_X =
				newProperty("plugin.pet.pathing.teleport.offset.x", 5.0);

		public static final Property<Double> PET_PATHING_TELEPORT_OFFSET_Y =
				newProperty("plugin.pet.pathing.teleport.offset.y", 1.0);

		public static final Property<Double> PET_PATHING_TELEPORT_OFFSET_Z =
				newProperty("plugin.pet.pathing.teleport.offset.z", 5.0);

	}


	public static final class Menus implements SettingsHolder
	{

		public static final Property<String> PETS_MENU_TITLE =
				newProperty("menus.pets.title", "&3&lPets");


		public static final Property<String> PETS_MENU_LAYOUT =
				newProperty("menus.pets.layout", "╔═══╦═══╦═══╦═══╦═══╦═══╦═══╦═══╦═══╗\n" +
				                                 "║ F ║ F ║ F ║ F ║ F ║ F ║ F ║ F ║ F ║\n" +
				                                 "╠═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╣\n" +
				                                 "║ F ║ P ║ P ║ P ║ P ║ P ║ P ║ P ║ F ║\n" +
				                                 "╠═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╣\n" +
				                                 "║ F ║ P ║ P ║ P ║ P ║ P ║ P ║ P ║ F ║\n" +
				                                 "╠═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╣\n" +
				                                 "║ F ║ P ║ P ║ P ║ P ║ P ║ P ║ P ║ F ║\n" +
				                                 "╠═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╣\n" +
				                                 "║ F ║ F ║ F ║ F ║ F ║ F ║ F ║ F ║ F ║\n" +
				                                 "╚═══╩═══╩═══╩═══╩═══╩═══╩═══╩═══╩═══╝");

		@Comment("'P' is used as the placeholder for pet items, do not remove it.")
		public static final MapProperty<MenuButton> PETS_MENU_VALUES =
				mapProperty(BeanPropertyType.of(MenuButton.class, new MenuButtonMapper()))
						.path("menus.pets.values")
						.defaultEntry("P", MenuButton.builder()
						                             .type(Material.INK_SACK)
						                             .data(8)
						                             .name(" ")
						                             .build())
						.defaultEntry("F", MenuButton.builder()
						                             .type(Material.STAINED_GLASS_PANE)
						                             .data(15)
						                             .name(" ")
						                             .build())
						.build();


		public static final Property<String> OPTS_MENU_TITLE =
				newProperty("menus.opts.title", "&3&lPet Options");


		public static final Property<String> OPTS_MENU_LAYOUT =
				newProperty("menus.opts.layout", "╔═══╦═══╦═══╦═══╦═══╦═══╦═══╦═══╦═══╗\n" +
				                                 "║ F ║ F ║ F ║ F ║ F ║ F ║ F ║ F ║ F ║\n" +
				                                 "╠═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╣\n" +
				                                 "║ F ║ F ║ F ║ F ║ P ║ F ║ F ║ F ║ F ║\n" +
				                                 "╠═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╣\n" +
				                                 "║ F ║ F ║ F ║ F ║ F ║ F ║ F ║ F ║ F ║\n" +
				                                 "╚═══╩═══╩═══╩═══╩═══╩═══╩═══╩═══╩═══╝");

		public static final MapProperty<MenuButton> OPTS_MENU_VALUES =
				mapProperty(BeanPropertyType.of(MenuButton.class, new MenuButtonMapper()))
						.path("menus.opts.values")
						.defaultEntry("P", MenuButton.builder()
						                             .type(Material.PAINTING)
						                             .name("&6&lParticle Effects")
						                             .lore("",
						                                   "  &7Click to modify the pet's particle effects!")
						                             .build())
						.defaultEntry("F", MenuButton.builder()
						                             .type(Material.STAINED_GLASS_PANE)
						                             .data(15)
						                             .name(" ")
						                             .build())
						.build();

	}


	public static final class Costs implements SettingsHolder
	{

	}

	public static final class Perks implements SettingsHolder
	{

	}

}